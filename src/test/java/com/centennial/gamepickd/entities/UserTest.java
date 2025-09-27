package com.centennial.gamepickd.entities;


import com.centennial.gamepickd.util.enums.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private final String testUsername = "testuser";
    private final String testEmail = "testuser@example.com";
    private final String testPassword = "securePassword123";
    private final boolean testEnabled = true;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername(testUsername);
        user.setEmail(testEmail);
        user.setPassword(testPassword);
        user.setEnabled(testEnabled);
    }

    @Test
    void testGettersAndSetters() {
        // Test initial values
        assertEquals(testUsername, user.getUsername());
        assertEquals(testEmail, user.getEmail());
        assertEquals(testPassword, user.getPassword());
        assertEquals(testEnabled, user.getEnabled());
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());

        // Test setters
        String newUsername = "newuser";
        user.setUsername(newUsername);
        assertEquals(newUsername, user.getUsername());

        String newEmail = "newuser@example.com";
        user.setEmail(newEmail);
        assertEquals(newEmail, user.getEmail());

        String newPassword = "newPassword456";
        user.setPassword(newPassword);
        assertEquals(newPassword, user.getPassword());

        user.setEnabled(false);
        assertFalse(user.getEnabled());

        Set<AuthorityType> authorities = new HashSet<>();
        authorities.add(new AuthorityType(RoleType.MEMBER));
        user.setAuthorities(authorities);

        assertNotNull(user.getAuthorities());
        assertEquals(1, user.getAuthorities().size());
    }

    @Test
    void testConstructors() {
        // Test no-arg constructor
        User emptyUser = new User();
        assertNull(emptyUser.getUsername());
        assertNull(emptyUser.getEmail());
        assertNull(emptyUser.getPassword());
        assertNull(emptyUser.getEnabled());
        assertNotNull(emptyUser.getAuthorities());
        assertTrue(emptyUser.getAuthorities().isEmpty());

        // Test parameterized constructor
        Set<AuthorityType> authorities = new HashSet<>();
        authorities.add(new AuthorityType(RoleType.ADMIN));

        User paramUser = new User(1L, "paramuser", "param@example.com", "paramPass", true, authorities);
        assertEquals(1L, paramUser.getId());
        assertEquals("paramuser", paramUser.getUsername());
        assertEquals("param@example.com", paramUser.getEmail());
        assertEquals("paramPass", paramUser.getPassword());
        assertTrue(paramUser.getEnabled());
        assertNotNull(paramUser.getAuthorities());
        assertEquals(1, paramUser.getAuthorities().size());
    }

    @Test
    void testAuthoritiesManagement() {
        // Initially should be empty set
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());

        // Test setting authorities
        Set<AuthorityType> authorities = new HashSet<>();
        AuthorityType authority1 = new AuthorityType(RoleType.MEMBER);
        AuthorityType authority2 = new AuthorityType(RoleType.ADMIN);
        authorities.add(authority1);
        authorities.add(authority2);

        user.setAuthorities(authorities);
        assertNotNull(user.getAuthorities());
        assertEquals(2, user.getAuthorities().size());

        // Test modifying the authorities set
        user.getAuthorities().remove(authority1);
        assertEquals(1, user.getAuthorities().size());
    }

    @Test
    void testNullValues() {
        user.setUsername(null);
        assertNull(user.getUsername());

        user.setEmail(null);
        assertNull(user.getEmail());

        user.setPassword(null);
        assertNull(user.getPassword());

        user.setEnabled(null);
        assertNull(user.getEnabled());

        user.setAuthorities(null);
        assertNull(user.getAuthorities());
    }

    @Test
    void testEmptyStrings() {
        user.setUsername("");
        assertEquals("", user.getUsername());

        user.setEmail("");
        assertEquals("", user.getEmail());

        user.setPassword(" ");
        assertEquals(" ", user.getPassword());

        // Test with empty authorities set
        user.setAuthorities(new HashSet<>());
        assertNotNull(user.getAuthorities());
        assertTrue(user.getAuthorities().isEmpty());
    }

    @Test
    void testToString() {
        // Test basic toString
        String toStringResult = user.toString();
        assertTrue(toStringResult.contains("username='" + testUsername + "'"));
        assertTrue(toStringResult.contains("email='" + testEmail + "'"));
        assertTrue(toStringResult.contains("password='" + testPassword + "'"));
        assertTrue(toStringResult.contains("enabled=" + testEnabled));
        assertTrue(toStringResult.contains("authorities=[]"));

        // Test with authorities
        Set<AuthorityType> authorities = new HashSet<>();
        authorities.add(new AuthorityType(RoleType.MEMBER));
        user.setAuthorities(authorities);

        toStringResult = user.toString();
        assertTrue(toStringResult.contains("authorities="));

        // Test with null fields
        user.setUsername(null);
        user.setEmail(null);
        user.setPassword(null);
        user.setAuthorities(null);
        toStringResult = user.toString();
        assertTrue(toStringResult.contains("username='null'"));
        assertTrue(toStringResult.contains("email='null'"));
        assertTrue(toStringResult.contains("password='null'"));
        assertTrue(toStringResult.contains("authorities=null"));
    }

    @Test
    void testEnabledStatus() {
        // Test valid enabled values
        user.setEnabled(true);
        assertTrue(user.getEnabled());

        user.setEnabled(false);
        assertFalse(user.getEnabled());

        user.setEnabled(null);
        assertNull(user.getEnabled());
    }

    @Test
    void testIdField() {
        // Test ID getter and setter
        user.setId(123L);
        assertEquals(123L, user.getId());
    }
}
