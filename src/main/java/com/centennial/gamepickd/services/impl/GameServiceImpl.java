package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.dtos.GameDTO;
import com.centennial.gamepickd.dtos.RenamedMultipartFile;
import com.centennial.gamepickd.dtos.SearchGameDTO;
import com.centennial.gamepickd.entities.*;
import com.centennial.gamepickd.repository.contracts.*;
import com.centennial.gamepickd.services.contracts.GameService;
import com.centennial.gamepickd.services.contracts.ReviewService;
import com.centennial.gamepickd.services.contracts.StorageService;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
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
    private final PublisherDAO publisherDAO;
    private final PlatformDAO platformDAO;
    private final StorageService storageService;
    private final ReviewService reviewService;
    private final Mapper mapper;
    private static final int MAX_PAGE_SIZE = 100;

    public GameServiceImpl(
            @Qualifier("gameDAOJpaImpl") GameDAO gameDAO,
            @Qualifier("genreDAOJpaImpl") GenreDAO genreDAO,
            @Qualifier("contributorDAOJpaImpl") ContributorDAO contributorDAO,
            @Qualifier("publisherDAOJpaImpl") PublisherDAO publisherDAO,
            @Qualifier("platformDAOJpaImpl") PlatformDAO platformDAO,
            @Qualifier("s3StorageService") StorageService storageService,
            @Qualifier("reviewServiceImpl") ReviewService reviewService,
            Mapper mapper
    ) {
        this.gameDAO = gameDAO;
        this.genreDAO = genreDAO;
        this.contributorDAO = contributorDAO;
        this.publisherDAO = publisherDAO;
        this.platformDAO = platformDAO;
        this.storageService = storageService;
        this.reviewService = reviewService;
        this.mapper = mapper;
    }

    @Transactional
    @CacheEvict(value = "gamesCache", allEntries = true)
    @Override
    public void add(AddGameDTO addGameDTO) throws
            Exceptions.GameAlreadyExistsException,
            Exceptions.StorageException,
            Exceptions.ContributorNotFoundException,
            Exceptions.PublisherNotFoundException,
            Exceptions.PlatformNotFoundException,
            Exceptions.GenreNotFoundException{

        // Check if the game exists
        Optional<Game> existingGame = gameDAO.findByTitle(addGameDTO.title());
        if(existingGame.isPresent())
            throw new Exceptions.GameAlreadyExistsException("Game already exists in the database.");

        // Validate that the contributor exists
        Contributor contributor = contributorDAO.findByUsername(addGameDTO.contributorUsername())
                .orElseThrow(() -> new Exceptions.ContributorNotFoundException("Contributor not found with username " + addGameDTO.contributorUsername()));

        // Query the Publisher, and if not found, throw exception
        Publisher publisher = publisherDAO.findByLabel(PublisherType.fromValue(addGameDTO.publisher()))
                .orElseThrow(() -> new Exceptions.PublisherNotFoundException("Publisher not found with name " + addGameDTO.publisher()));

        // Map the DTO to a game entity
        Game game = mapper.addGameDtoToGame(addGameDTO);

        // Query the Platforms, and if not found, throw exception PlatformNotFound
        Set<PlatformType> platformLabels;
        try {
            platformLabels = parsePlatforms(addGameDTO.platforms());
        } catch (IllegalArgumentException e) {
            throw new Exceptions.PlatformNotFoundException("One or more provided platforms are invalid: " + addGameDTO.platforms());
        }

        // Fetch platform entities from DB
        Set<Platform> platforms = platformDAO.findByLabels(platformLabels);

        // Add platforms to entity
        game.addPlatforms(platforms);
        // Set contributor
        game.setContributor(contributor);
        // Set publisher
        game.setPublisher(publisher);

        Set<GenreType> genreLabels;
        try{
            genreLabels = parseGenres(addGameDTO.genres());
        } catch (IllegalArgumentException e) {
            throw new Exceptions.GenreNotFoundException("One or more provided genres are invalid: " + addGameDTO.genres());
        }

        // Fetch genres entities from DB
        Set<Genre> genres = genreDAO.findByLabels(genreLabels);
        // Add genres to entity
        game.addGenres(genres);

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
        Set<GenreType> genreTypes = parseGenreTypes(searchGameDTO.genres());
        Set<PlatformType> platformTypes = parsePlatformTypes(searchGameDTO.platforms());
        PublisherType publisherType = parsePublisherType(searchGameDTO.publisher());

        Page<Game> gamePage = gameDAO.findAllOrderByPostedAtDesc(
                searchGameDTO.title(),
                genreTypes,
                publisherType,
                platformTypes,
                pageable
        );

        // Extract IDs of the games in this page
        Set<Long> gameIdsInPage = gamePage.stream()
                .map(Game::getId)
                .collect(Collectors.toSet());

        // Get averages for only these games
        Map<Long, Double> averages = reviewService.getAverageRatesForGames(gameIdsInPage);


        return gamePage.map(game -> mapper.gameToGameDto(game, averages));
    }

    private Set<GenreType> parseGenres(String genresString) {
        return Arrays.stream(genresString.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(GenreType::fromValue)
                .collect(Collectors.toSet());
    }

    private Set<PlatformType> parsePlatforms(String platformsString) {
        return Arrays.stream(platformsString.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(PlatformType::fromValue)
                .collect(Collectors.toSet());
    }

    private Set<GenreType> parseGenreTypes(Set<String> genreStrings) {
        if (genreStrings == null || genreStrings.isEmpty()) return Collections.emptySet();

        return genreStrings.stream()
                .map(this::safeParseGenre)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<PlatformType> parsePlatformTypes(Set<String> platformStrings) {
        if (platformStrings == null || platformStrings.isEmpty()) return Collections.emptySet();

        return platformStrings.stream()
                .map(this::safeParsePlatform)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private PublisherType parsePublisherType(String publisher) {
        if (publisher == null || publisher.isBlank()) return null;

        try {
            // Try as enum name
            return PublisherType.valueOf(publisher.toUpperCase());
        } catch (IllegalArgumentException e1) {
            try {
                // Try as display value (the "val" field)
                return PublisherType.fromValue(publisher);
            } catch (IllegalArgumentException e2) {
                throw new IllegalArgumentException("Unknown publisher: " + publisher);
            }
        }
    }

    private GenreType safeParseGenre(String value) {
        try {
            return GenreType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e1) {
            try {
                return GenreType.fromValue(value);
            } catch (IllegalArgumentException e2) {
                return null; // Ignore unknown genre
            }
        }
    }

    private PlatformType safeParsePlatform(String value) {
        try {
            return PlatformType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e1) {
            try {
                return PlatformType.fromValue(value);
            } catch (IllegalArgumentException e2) {
                return null; // Ignore unknown platform
            }
        }
    }
}
