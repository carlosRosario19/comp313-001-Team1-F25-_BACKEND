package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.dtos.GameDTO;
import com.centennial.gamepickd.dtos.SearchGameDTO;
import com.centennial.gamepickd.services.contracts.GameService;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.enums.GenreType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(@Qualifier("gameServiceImpl") GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping(value = "/games", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addGame(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("genres") String genres,
            @RequestParam("publisher") String publisher,
            @RequestParam("platforms") String platforms,
            @RequestParam("contributorUsername") String contributorUsername,
            @RequestParam("coverImage") MultipartFile coverImage
    ) throws
            Exceptions.GameAlreadyExistsException,
            Exceptions.StorageException,
            Exceptions.ContributorNotFoundException,
            Exceptions.PlatformNotFoundException,
            Exceptions.GenreNotFoundException,
            Exceptions.PublisherNotFoundException {

        AddGameDTO addGameDTO = new AddGameDTO(title, description, genres, publisher, platforms, contributorUsername, coverImage);
        gameService.add(addGameDTO);
    }

    @GetMapping("/games")
    public PagedModel<EntityModel<GameDTO>> getAllGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Set<GenreType> genres,
            PagedResourcesAssembler<GameDTO> assembler
    ) throws Exceptions.PageOutOfRangeException {
        SearchGameDTO searchGameDTO = new SearchGameDTO(page, size, title, genres);
        Page<GameDTO> gamesPage = gameService.getAll(searchGameDTO);
        return assembler.toModel(gamesPage, EntityModel::of);
    }
}
