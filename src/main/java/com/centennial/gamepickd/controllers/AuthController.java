package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.services.impl.TokenService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final TokenService tokenService;

    public AuthController(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public String login(Authentication authentication){
        return tokenService.generateToken(authentication);
    }
}
