package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.dtos.AddReviewDTO;
import com.centennial.gamepickd.dtos.DeleteReviewDTO;
import com.centennial.gamepickd.dtos.ReviewDTO;
import com.centennial.gamepickd.entities.Review;
import com.centennial.gamepickd.services.contracts.ReviewService;
import com.centennial.gamepickd.util.Exceptions;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

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

    @GetMapping("/reviews/{gameId}")
    public Set<ReviewDTO> getAllReviewsByGame(@PathVariable long gameId) throws Exceptions.GameNotFoundException {
        return reviewService.getAllReviewsByGameId(gameId);
    }

    @DeleteMapping("reviews")
    public void deleteReviewById(@RequestBody DeleteReviewDTO deleteReviewDTO) throws Exceptions.ReviewNotFoundException {
        reviewService.deleteById(deleteReviewDTO);
    }
}
