package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddContributorDTO;
import com.centennial.gamepickd.services.contracts.ContributorService;
import com.centennial.gamepickd.util.Exceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ContributorController {

    private final ContributorService contributorService;

    @Autowired
    public ContributorController(@Qualifier("contributorServiceImpl") ContributorService contributorService) {
        this.contributorService = contributorService;
    }

    @PostMapping("contributors")
    public void addMember(@RequestBody AddContributorDTO addContributorDTO) throws
            Exceptions.UsernameAlreadyExistsException,
            Exceptions.EmailAlreadyExistsException
    {
        contributorService.add(addContributorDTO);
    }
}
