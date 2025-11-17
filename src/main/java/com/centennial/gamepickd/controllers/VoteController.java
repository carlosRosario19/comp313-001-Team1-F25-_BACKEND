package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddVoteDTO;
import com.centennial.gamepickd.dtos.DeleteVoteDTO;
import com.centennial.gamepickd.services.contracts.VoteService;
import com.centennial.gamepickd.util.Exceptions;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(@Qualifier("voteServiceImpl") VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/votes")
    public void add(@Valid @RequestBody AddVoteDTO addVoteDTO) throws Exceptions.ReviewNotFoundException {
        this.voteService.add(addVoteDTO);
    }

    @DeleteMapping("/votes/{reviewId}")
    public void delete(@Valid @PathVariable String reviewId) throws Exceptions.VoteNotFoundException {
        this.voteService.delete(new DeleteVoteDTO(reviewId));
    }
}
