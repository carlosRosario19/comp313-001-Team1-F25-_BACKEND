package com.centennial.gamepickd.dtos;

import com.centennial.gamepickd.util.enums.GenreType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @description DTO for retrieving games
 * @param id
 * @param title
 * @param description
 * @param coverImagePath
 * @param genres
 */
public record GameDTO(
        @NotNull
        long id,
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotBlank
        String coverImagePath,
        @NotEmpty
        Set<GenreType> genres,
        @NotNull
        LocalDateTime postedAt
) { }
