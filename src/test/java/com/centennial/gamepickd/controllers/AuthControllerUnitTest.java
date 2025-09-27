package com.centennial.gamepickd.controllers;

import com.centennial.gamepickd.services.impl.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthControllerUnitTest {
    private TokenService tokenService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        tokenService = mock(TokenService.class);
        authController = new AuthController(tokenService);
    }

    //This will test if the AuthController is invoking the TokenService correctly.
    @Test
    void login_ShouldReturnToken_WhenAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        String expectedToken = "sampleToken";

        when(authentication.isAuthenticated()).thenReturn(true);
        when(tokenService.generateToken(authentication)).thenReturn(expectedToken);

        String actualToken = authController.login(authentication);

        assertEquals(expectedToken, actualToken);
        verify(tokenService).generateToken(authentication);
    }

    @Test
    void login_ShouldThrowUnauthorized_WhenAuthenticationIsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authController.login(null));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void login_ShouldThrowUnauthorized_WhenNotAuthenticated() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authController.login(authentication));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    void login_ShouldThrowUnauthorized_WhenAnonymousAuthentication() {
        Authentication anonymousAuth = new AnonymousAuthenticationToken(
                "key", "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authController.login(anonymousAuth));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
