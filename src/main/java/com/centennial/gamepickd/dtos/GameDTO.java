package com.centennial.gamepickd.dtos;

import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
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
 * @param rateAverage
 * @param coverImagePath
 * @param genres
 * @param publisher
 * @param platforms
 * @param postedAt
 */
public record GameDTO(
        @NotNull
        long id,
        @NotBlank
        String title,
        @NotBlank
        String description,
        @NotNull
        double rateAverage,
        @NotBlank
        String coverImagePath,
        @NotEmpty
        Set<GenreType> genres,
        @NotBlank
        PublisherType publisher,
        @NotEmpty
        Set<PlatformType> platforms,
        @NotNull
        LocalDateTime postedAt
) { }
