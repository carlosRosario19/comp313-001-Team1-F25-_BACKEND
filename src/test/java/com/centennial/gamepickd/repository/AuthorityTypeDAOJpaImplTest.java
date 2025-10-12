package com.centennial.gamepickd.repository;

import com.centennial.gamepickd.entities.AuthorityType;
import com.centennial.gamepickd.repository.contracts.AuthorityTypeDAO;
import com.centennial.gamepickd.repository.impl.AuthorityTypeDAOJpaImpl;
import com.centennial.gamepickd.util.enums.RoleType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class AuthorityTypeDAOJpaImplTest {

    @Autowired
    private EntityManager entityManager;

    private AuthorityTypeDAO authorityTypeDAO;

    @BeforeEach
    void setUp() {
        authorityTypeDAO = new AuthorityTypeDAOJpaImpl(entityManager);
    }

    @Transactional
    @Test
    void testFindByLabel_WhenAuthorityExists() {
        // Arrange
        AuthorityType authorityType = new AuthorityType(RoleType.ADMIN);
        entityManager.persist(authorityType);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<AuthorityType> result = authorityTypeDAO.findByLabel(RoleType.ADMIN);

        // Assert
        assertTrue(result.isPresent(), "AuthorityType should be found for given label.");
        assertEquals(RoleType.ADMIN, result.get().getLabel(), "Returned label should match the search value.");
    }

    @Transactional
    @Test
    void testFindByLabel_WhenAuthorityDoesNotExist() {
        // Arrange
        // (no AuthorityType persisted)

        // Act
        Optional<AuthorityType> result = authorityTypeDAO.findByLabel(RoleType.MEMBER);

        // Assert
        assertTrue(result.isEmpty(), "Should return empty Optional when no AuthorityType found for label.");
    }
}
