package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppUserService implements UserDetailsService {

    private final UserDAO userDAO;

    @Autowired
    public AppUserService(@Qualifier("userDAOJpaImpl") UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " was not found"));

        // Map authorities to the Spring Security format
        Set<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map(authorityType -> new SimpleGrantedAuthority(authorityType.getLabel().str()))
                .collect(Collectors.toSet());

        // Return UserDetails with roles
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Ensure this matches the password encoder
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getEnabled())
                .build();
    }
}
