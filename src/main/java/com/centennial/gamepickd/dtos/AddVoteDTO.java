package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @description DTO for adding a review
 * @param reviewId
 * @param inFavor
 */
public record AddVoteDTO(
        @NotBlank(message = "Comment is required")
        String reviewId,
        @NotNull
        boolean inFavor
) {
}
