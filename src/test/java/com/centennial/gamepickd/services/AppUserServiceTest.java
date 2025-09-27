package com.centennial.gamepickd.services;

import com.centennial.gamepickd.entities.AuthorityType;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import com.centennial.gamepickd.services.impl.AppUserService;
import com.centennial.gamepickd.util.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {
    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AppUserService appUserService;

    private User user;

    @BeforeEach
    void setup() {
        // Initialize a User instance for testing with the new AuthorityType structure
        AuthorityType authorityType = new AuthorityType(RoleType.MEMBER);

        user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        user.setEnabled(true);
        user.setAuthorities(Set.of(authorityType));
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        when(userDAO.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = appUserService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_MEMBER")));
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());

        verify(userDAO, times(1)).findByUsername("testUser");
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        when(userDAO.findByUsername("nonExistentUser")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> appUserService.loadUserByUsername("nonExistentUser")
        );
        assertEquals("User with username: nonExistentUser was not found", exception.getMessage());
        verify(userDAO, times(1)).findByUsername("nonExistentUser");
    }

    @Test
    void loadUserByUsername_UserDisabled_ReturnsUserDetailsWithDisabledFlag() {
        // Arrange
        user.setEnabled(false);
        when(userDAO.findByUsername("testUser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = appUserService.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        assertFalse(userDetails.isEnabled());
        verify(userDAO, times(1)).findByUsername("testUser");
    }
}
