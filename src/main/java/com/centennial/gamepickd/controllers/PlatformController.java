package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.services.contracts.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class PlatformController {

    private final PlatformService platformService;

    @Autowired
    public PlatformController(@Qualifier("platformServiceImpl") PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping("/platforms")
    public Set<String> getAllPlatforms() {
        return platformService.getAll();
    }
}
