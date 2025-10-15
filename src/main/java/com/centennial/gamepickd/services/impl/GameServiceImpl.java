package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.dtos.GameDTO;
import com.centennial.gamepickd.dtos.RenamedMultipartFile;
import com.centennial.gamepickd.dtos.SearchGameDTO;
import com.centennial.gamepickd.entities.Contributor;
import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.entities.Genre;
import com.centennial.gamepickd.repository.contracts.ContributorDAO;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import com.centennial.gamepickd.repository.contracts.GenreDAO;
import com.centennial.gamepickd.services.contracts.GameService;
import com.centennial.gamepickd.services.contracts.StorageService;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import com.centennial.gamepickd.util.enums.GenreType;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final GameDAO gameDAO;
    private final GenreDAO genreDAO;
    private final ContributorDAO contributorDAO;
    private final StorageService storageService;
    private final Mapper mapper;
    private static final int MAX_PAGE_SIZE = 100;

    public GameServiceImpl(
            @Qualifier("gameDAOJpaImpl") GameDAO gameDAO,
            @Qualifier("genreDAOJpaImpl") GenreDAO genreDAO,
            @Qualifier("contributorDAOJpaImpl") ContributorDAO contributorDAO,
            @Qualifier("s3StorageService") StorageService storageService,
            Mapper mapper
    ) {
        this.gameDAO = gameDAO;
        this.genreDAO = genreDAO;
        this.contributorDAO = contributorDAO;
        this.storageService = storageService;
        this.mapper = mapper;
    }

    @Transactional
    @CacheEvict(value = "gamesCache", allEntries = true)
    @Override
    public void add(AddGameDTO addGameDTO) throws Exceptions.GameAlreadyExistsException, Exceptions.StorageException, Exceptions.ContributorNotFoundException {
        // Check if the game exists
        Optional<Game> existingGame = gameDAO.findByTitle(addGameDTO.title());
        if(existingGame.isPresent())
            throw new Exceptions.GameAlreadyExistsException("Game already exists in the database.");

        // Validate that the contributor exists
        Contributor contributor = contributorDAO.findByUsername(addGameDTO.contributorUsername())
                .orElseThrow(() -> new Exceptions.ContributorNotFoundException("Contributor not found with username " + addGameDTO.contributorUsername()));

        // Map the DTO to a game entity
        Game game = mapper.addGameDtoToGame(addGameDTO);

        // Set contributor
        game.setContributor(contributor);

        Set<String> genres = parseGenres(addGameDTO.genres());
        for (String desc : genres) {
            try {
                GenreType genreType = GenreType.fromLabel(desc);
                Optional<Genre> genre = genreDAO.findByLabel(genreType);
                genre.ifPresent(game::addGenre);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid genre provided: " + desc);
            }
        }

        // Handle file upload
        String filename = null;
        if (addGameDTO.coverImage() != null && !addGameDTO.coverImage().isEmpty()) {
            try {
                // Generate unique filename while preserving extension
                String originalFilename = Objects.requireNonNull(addGameDTO.coverImage().getOriginalFilename());
                String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                filename = UUID.randomUUID() + extension;

                // Create a renamed version of the file
                MultipartFile renamedFile = new RenamedMultipartFile(addGameDTO.coverImage(), filename);

                // Store the file
                storageService.store(renamedFile);
            } catch (Exception e) {
                throw new Exceptions.StorageException("Failed to store cover image: " + e.getMessage(), e);
            }
        }

        if (filename != null) {
            game.setCoverImagePath(filename);
        }
        // Save the book along with any new authors
        gameDAO.create(game);

    }

    @Cacheable(
            value = "gamesCache",
            key = "#searchGameDTO.toString()"
    )
    @Override
    public Page<GameDTO> getAll(SearchGameDTO searchGameDTO) throws Exceptions.PageOutOfRangeException {
        if(searchGameDTO.page() < 0) throw new Exceptions.PageOutOfRangeException("Page number cannot be negative");

        else if(searchGameDTO.size() <= 0) throw new Exceptions.PageOutOfRangeException("Page size must be greater than 0");

        else if(searchGameDTO.size() > MAX_PAGE_SIZE) throw new Exceptions.PageOutOfRangeException("Page size cannot exceed " + MAX_PAGE_SIZE);

        Pageable pageable = PageRequest.of(searchGameDTO.page(), searchGameDTO.size());

        Page<Game> gamePage = gameDAO.findAllOrderByPostedAtDesc(searchGameDTO.title(), searchGameDTO.genres(), pageable);

        return gamePage.map(mapper.gameToGameDTO);
    }

    private Set<String> parseGenres(String genresString) {
        return Arrays.stream(genresString.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }
}
