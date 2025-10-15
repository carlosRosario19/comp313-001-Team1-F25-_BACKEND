package com.centennial.gamepickd.dtos;

import com.centennial.gamepickd.util.enums.GenreType;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

/**
 * DTO for searching books using pagination and optional filters.
 *
 * @param page            Page number (0-based)
 * @param size            Page size
 * @param title           (Optional) Book title filter
 * @param genres          (Optional) Genres filter
 */
public record SearchGameDTO(
        @NotNull
        int page,
        @NotNull
        int size,
        String title,
        Set<GenreType> genres
) {
    @Override
    public String toString() {
        return "SearchGameDTO{" +
                "page=" + page +
                ", size=" + size +
                ", title='" + title + '\'' +
                ", genres=" + genres +
                '}';
    }
}
