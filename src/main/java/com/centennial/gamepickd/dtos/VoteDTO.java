package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @description DTO for retrieving votes
 * @param username
 * @param inFavor
 */
public record VoteDTO(
        @NotBlank
        String username,
        @NotNull
        boolean inFavor
) {
}
