package com.centennial.gamepickd.security;

import com.centennial.gamepickd.services.impl.AppUserService;
import com.centennial.gamepickd.util.enums.RoleType;
import com.centennial.gamepickd.util.enums.Routes;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final RsaKeyProperties rsaKeys;
    private final AppUserService appUserService;

    @Autowired
    public SecurityConfig(RsaKeyProperties rsaKeys, AppUserService appUserService) {
        this.rsaKeys = rsaKeys;
        this.appUserService = appUserService;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return appUserService;
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(appUserService);
        provider.setPasswordEncoder(this.passwordEncoder());
        return provider;
    }


    @Bean
    @Order(1)
    @SuppressWarnings("java:S4502") // Safe: CSRF disabled for stateless endpoints
    public SecurityFilterChain basicFilterChain(HttpSecurity http) throws Exception{

        return http
                .securityMatcher(Routes.LOGIN.val())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    @Order(2)
    @SuppressWarnings("java:S4502") // Safe: CSRF disabled for stateless endpoints
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception{

        return http
                .securityMatcher("/**")
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, Routes.HEALTH_CHECK.val()).permitAll()
                        .requestMatchers(HttpMethod.POST, Routes.MEMBERS.val()).permitAll()
                        .requestMatchers(HttpMethod.POST, Routes.CONTRIBUTORS.val()).hasRole(RoleType.ADMIN.val())
                        .requestMatchers(HttpMethod.POST, Routes.GAMES.val()).hasRole(RoleType.CONTRIBUTOR.val())
                        .requestMatchers(HttpMethod.GET, Routes.GAMES.val()).permitAll()
                        .requestMatchers(HttpMethod.PUT, Routes.GAMES.val()).hasRole(RoleType.CONTRIBUTOR.val())
                        .requestMatchers(HttpMethod.GET, Routes.IMAGES.val()).permitAll()
                        .requestMatchers(HttpMethod.GET, Routes.GENRES.val()).permitAll()
                        .requestMatchers(HttpMethod.GET, Routes.PLATFORMS.val()).permitAll()
                        .requestMatchers(HttpMethod.GET, Routes.PUBLISHERS.val()).permitAll()
                        .requestMatchers(HttpMethod.POST, Routes.REVIEWS.val()).hasRole(RoleType.MEMBER.val())
                        .requestMatchers(HttpMethod.GET, Routes.REVIEWS.val()).hasRole(RoleType.MEMBER.val())
                        .requestMatchers(HttpMethod.DELETE, Routes.REVIEWS.val()).hasRole(RoleType.CONTRIBUTOR.val())
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    @Bean
    public JwtEncoder jwtEncoder(){
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Angular app
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // React app
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
