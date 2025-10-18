package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record AddGameDTO(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotBlank(message = "Genres are required")
        String genres,

        @NotBlank(message = "Publisher is required")
        String publisher,

        @NotBlank(message = "Platforms are required")
        String platforms,

        @NotBlank(message = "Contributor's username is required")
        String contributorUsername,

        @NotNull
        MultipartFile coverImage
) { }
