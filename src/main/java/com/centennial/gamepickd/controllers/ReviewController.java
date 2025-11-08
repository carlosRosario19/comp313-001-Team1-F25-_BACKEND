package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddReviewDTO;
import com.centennial.gamepickd.services.contracts.ReviewService;
import com.centennial.gamepickd.util.Exceptions;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(@Qualifier("reviewServiceImpl") ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/reviews")
    public void addReview(@Valid @RequestBody AddReviewDTO addReviewDTO) throws
            Exceptions.UserNotFoundException,
            Exceptions.GameNotFoundException
    {
        this.reviewService.add(addReviewDTO);
    }
}
