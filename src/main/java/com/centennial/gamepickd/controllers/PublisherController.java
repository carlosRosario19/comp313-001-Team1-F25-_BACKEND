package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.services.contracts.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class PublisherController {

    private final PublisherService publisherService;

    @Autowired
    public PublisherController(@Qualifier("publisherServiceImpl") PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @GetMapping("/publishers")
    public Set<String> getAllPublishers() {
        return publisherService.getAll();
    }
}
