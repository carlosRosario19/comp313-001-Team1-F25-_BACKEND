package com.centennial.gamepickd.dtos;

import com.centennial.gamepickd.util.enums.RoleType;

import java.util.Set;

/**
 * @description DTO for retrieving reviews
 * @param id
 * @param username
 * @param email
 * @param authorities
 */
public record UserDTO(
        long id,
        String username,
        String email,
        Set<RoleType> authorities
) { }
