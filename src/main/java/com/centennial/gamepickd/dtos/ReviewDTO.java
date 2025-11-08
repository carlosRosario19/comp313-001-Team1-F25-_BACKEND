package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @description DTO for retrieving reviews
 * @param reviewId
 * @param timeStamp
 * @param comment
 * @param rate
 * @param username
 * @param gameId
 */
public record ReviewDTO(
        @NotBlank
        String reviewId,
        @NotBlank
        String timeStamp,
        @NotBlank
        String comment,
        @NotNull
        int rate,
        @NotBlank
        String username,
        @NotNull
        long gameId
) {
}
