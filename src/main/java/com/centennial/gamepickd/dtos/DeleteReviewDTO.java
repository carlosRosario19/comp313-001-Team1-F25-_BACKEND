package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * @description DTO for adding a review
 * @param reviewId
 * @param timeStamp
 */
public record DeleteReviewDTO(
        @NotBlank(message = "Review ID is required")
        String reviewId,
        @NotBlank(message = "Time Stamp is required")
        String timeStamp
) { }
