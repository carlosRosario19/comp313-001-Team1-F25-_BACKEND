package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddGameDTO;
import com.centennial.gamepickd.services.contracts.GameService;
import com.centennial.gamepickd.util.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            @RequestParam("contributorUsername") String contributorUsername,
            @RequestParam("coverImage") MultipartFile coverImage
    ) throws Exceptions.GameAlreadyExistsException, Exceptions.StorageException, Exceptions.ContributorNotFoundException {
        AddGameDTO addGameDTO = new AddGameDTO(title, description, genres, contributorUsername, coverImage);
        gameService.add(addGameDTO);
    }
}
