package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.services.impl.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public String login(Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return tokenService.generateToken(authentication);
    }
}
