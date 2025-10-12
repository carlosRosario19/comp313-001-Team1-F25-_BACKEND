package com.centennial.gamepickd.repository;

import com.centennial.gamepickd.entities.AuthorityType;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import com.centennial.gamepickd.repository.impl.UserDAOJpaImpl;
import com.centennial.gamepickd.util.enums.RoleType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test") // This activates application-test.properties
class UserDAOJpaImlTest {

    @Autowired
    private EntityManager entityManager;

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOJpaImpl(entityManager);
    }

    @Transactional
    @Test
    void testCreateUser() {
        // Arrange
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("testuser@example.com");
        user.setPassword("testPassword");
        user.setEnabled(true);

        Set<AuthorityType> authorities = new HashSet<>();
        AuthorityType authority = new AuthorityType(RoleType.MEMBER);
        entityManager.persist(authority); // persist it first!
        authorities.add(authority);
        user.setAuthorities(authorities);

        // Act
        userDAO.create(user);
        entityManager.flush();

        User persistedUser = entityManager
                .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", "testUser")
                .getSingleResult();

        // Assert
        assertNotNull(persistedUser, "User should be persisted in the database.");
        assertEquals("testUser", persistedUser.getUsername());
        assertEquals("testuser@example.com", persistedUser.getEmail());
        assertEquals("testPassword", persistedUser.getPassword());
        assertTrue(persistedUser.getEnabled());
        assertNotNull(persistedUser.getAuthorities());
        assertEquals(1, persistedUser.getAuthorities().size());
    }

    @Transactional
    @Test
    void testFindByUsername_UserExists() {
        // Arrange
        User user = new User();
        user.setUsername("existingUser");
        user.setEmail("existing@example.com");
        user.setPassword("password123");
        user.setEnabled(true);

        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear(); // Clear the cache to ensure we read from DB

        // Act
        Optional<User> result = userDAO.findByUsername("existingUser");

        // Assert
        assertTrue(result.isPresent(), "User should be found in the database.");
        assertEquals("existingUser", result.get().getUsername());
        assertEquals("existing@example.com", result.get().getEmail());
        assertEquals("password123", result.get().getPassword());
        assertTrue(result.get().getEnabled());
    }

    @Transactional
    @Test
    void testFindByUsername_UserDoesNotExist() {
        // Act
        Optional<User> result = userDAO.findByUsername("nonExistentUser");

        // Assert
        assertFalse(result.isPresent(), "User should not be found in the database.");
    }

    @Transactional
    @Test
    void testFindByUsernameOrEmail_FindsByUsernameOrEmail() {
        // Arrange
        User user1 = new User();
        user1.setUsername("john_doe");
        user1.setEmail("john@example.com");
        user1.setPassword("password123");
        user1.setEnabled(true);

        User user2 = new User();
        user2.setUsername("jane_doe");
        user2.setEmail("jane@example.com");
        user2.setPassword("password456");
        user2.setEnabled(true);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
        entityManager.clear();

        // Act
        // Case 1: Find by username
        var resultByUsername = userDAO.findByUsernameOrEmail("john_doe", "nonexistent@example.com");

        // Case 2: Find by email
        var resultByEmail = userDAO.findByUsernameOrEmail("nonexistentUser", "jane@example.com");

        // Case 3: Find by username OR email (both matching)
        var resultByBoth = userDAO.findByUsernameOrEmail("john_doe", "john@example.com");

        // Assert
        // Username match
        assertEquals(1, resultByUsername.size(), "Should find one user by username.");
        assertEquals("john_doe", resultByUsername.get(0).getUsername());

        // Email match
        assertEquals(1, resultByEmail.size(), "Should find one user by email.");
        assertEquals("jane@example.com", resultByEmail.get(0).getEmail());

        // Both username or email match same user
        assertEquals(1, resultByBoth.size(), "Should find the same user when both username and email match.");
        assertEquals("john_doe", resultByBoth.get(0).getUsername());
    }

    @Transactional
    @Test
    void testFindByUsernameOrEmail_NoMatchFound() {
        // Arrange
        User user = new User();
        user.setUsername("existing_user");
        user.setEmail("existing@example.com");
        user.setPassword("secret");
        user.setEnabled(true);

        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();

        // Act
        var result = userDAO.findByUsernameOrEmail("unknown_user", "unknown@example.com");

        // Assert
        assertTrue(result.isEmpty(), "Should return empty list when no match found.");
    }

}
