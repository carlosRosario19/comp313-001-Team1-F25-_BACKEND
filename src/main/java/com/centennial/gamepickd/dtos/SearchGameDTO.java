package com.centennial.gamepickd.dtos;

import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * DTO for searching books using pagination and optional filters.
 *
 * @param page       The page number (0-based index). Must be >= 0.
 * @param size       The number of items per page. Must be > 0 and <= maximum allowed page size.
 * @param title      (Optional) Partial or full title of the game to search for. Case-insensitive.
 * @param genres     (Optional) A set of game genres to filter by.
 * @param publisher  (Optional) The publisher to filter by.
 * @param platforms  (Optional) A set of gaming platforms to filter by.
 */
public record SearchGameDTO(
        @NotNull
        int page,
        @NotNull
        int size,
        String title,
        Set<String> genres,
        String publisher,
        Set<String> platforms
) { }
