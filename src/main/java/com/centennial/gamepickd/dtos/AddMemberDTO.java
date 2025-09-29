package com.centennial.gamepickd.dtos;

import jakarta.validation.constraints.*;
/**
 * @description DTO for adding a member
 * @param firstName
 * @param lastName
 * @param email
 * @param username
 * @param password
 */
public record AddMemberDTO(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotNull
        @Email
        String email,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 5, message = "Password must be at least 5 characters long")
        String password
) { }

