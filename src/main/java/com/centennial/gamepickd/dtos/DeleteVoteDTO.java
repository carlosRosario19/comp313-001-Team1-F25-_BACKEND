package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.NotBlank;

/**
 * @description DTO for adding a review
 * @param reviewId
 */
public record DeleteVoteDTO(
        @NotBlank(message = "Review ID is required")
        String reviewId
) {
}
