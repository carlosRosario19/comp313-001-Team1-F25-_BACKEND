package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.services.contracts.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(@Qualifier("genreServiceImpl") GenreService genreService) {
        this.genreService = genreService;
    }


    @GetMapping("/genres")
    public Set<String> getAllGenres() {
        return genreService.getAll();
    }

}
