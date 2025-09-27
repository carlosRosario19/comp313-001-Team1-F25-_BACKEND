package com.centennial.gamepickd.services;

import com.centennial.gamepickd.services.impl.TokenService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

class TokenServiceTest {
    private JwtEncoder jwtEncoder;
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        jwtEncoder = mock(JwtEncoder.class);
        tokenService = new TokenService(jwtEncoder);
    }

    //This will ensure the token generation logic works as expected.
    @Test
    void generateToken_ShouldReturnToken() {
        // Mock Authentication
        Authentication authentication = mock(Authentication.class);

        // Mock GrantedAuthority
        GrantedAuthority authority = mock(GrantedAuthority.class);
        when(authority.getAuthority()).thenReturn("ROLE_MEMBER");

        // Return mocked authorities
        Collection<GrantedAuthority> grantedAuthorities = Lists.newArrayList(authority);
        doReturn(grantedAuthorities).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("testUser");

        // Capture JwtEncoderParameters
        ArgumentCaptor<JwtEncoderParameters> captor = ArgumentCaptor.forClass(JwtEncoderParameters.class);

        // Create a map of claims to be passed to the claims builder
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("authorities", "ROLE_MEMBER");

        // Simulate headers using a Map<String, Object>
        Map<String, Object> headersMap = new HashMap<>();
        headersMap.put("typ", "JWT");  // Example of a header
        headersMap.put("alg", "HS256"); // Example of a header

        // Build the JwtClaimsSet using the claimsConsumer
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject("testUser")
                .claims(claimsMap::putAll) // Passing a Consumer to modify the claims map
                .build();

        Jwt mockJwt = new Jwt(
                "mockToken", // token value
                Instant.now(), // issuedAt
                Instant.now().plusSeconds(3600), // expiresAt
                headersMap, // headers
                claims.getClaims() // claims
        );

        // Mock the encoder to return the mock token
        when(jwtEncoder.encode(captor.capture())).thenReturn(mockJwt);

        // Invoke the method
        String token = tokenService.generateToken(authentication);

        // Assertions
        assertNotNull(token);
        assertNotNull(captor.getValue());

        JwtClaimsSet capturedClaims = captor.getValue().getClaims();
        JwtEncoderParameters capturedParameters = captor.getValue();
        assertNotNull(capturedParameters);
        assertNotNull(capturedClaims);
        assertNotNull(capturedClaims.getClaim("authorities"));
        assertNotNull(capturedClaims.getSubject());
    }
}
