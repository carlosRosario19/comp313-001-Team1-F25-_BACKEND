package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @description DTO for adding a review
 * @param comment
 * @param rate
 * @param gameId
 */
public record AddReviewDTO (

        @NotBlank(message = "Comment is required")
        String comment,

        @NotNull
        @Min(value = 1, message = "Rate must be at least 1")
        @Max(value = 5, message = "Rate must be at most 5")
        int rate,

        @NotNull
        long gameId
) { }
