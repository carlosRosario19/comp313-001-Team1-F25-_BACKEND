package com.centennial.gamepickd.repository;

import com.centennial.gamepickd.entities.Contributor;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.impl.ContributorDAOJpaImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ContributorDAOJpaImplTest {

    @Autowired
    private EntityManager entityManager;

    private ContributorDAOJpaImpl contributorDAO;

    @BeforeEach
    void setup() {
        contributorDAO = new ContributorDAOJpaImpl(entityManager);
    }

    @Transactional
    @Test
    void create_ShouldPersistMember() {
        // Arrange
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("hashedPass");
        user.setEnabled(true);

        entityManager.persist(user);

        Contributor contributor = new Contributor.Builder()
                .firstName("John")
                .lastName("Doe")
                .user(user)
                .build();

        contributor.setCreatedAt(LocalDateTime.now());

        // Act
        contributorDAO.create(contributor);

        // Flush & clear to force Hibernate to hit DB, not cache
        entityManager.flush();
        entityManager.clear();

        // Assert
        Contributor persisted = entityManager.find(Contributor.class, contributor.getId());
        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getFirstName()).isEqualTo("John");
        assertThat(persisted.getLastName()).isEqualTo("Doe");
        assertThat(persisted.getUser().getUsername()).isEqualTo("johndoe");
    }

    @Transactional
    @Test
    void findByUsername_ShouldReturnContributor_WhenExists() {
        // Arrange
        User user = new User();
        user.setUsername("janedoe");
        user.setEmail("jane@example.com");
        user.setPassword("hashedPass");
        user.setEnabled(true);
        entityManager.persist(user);

        Contributor contributor = new Contributor.Builder()
                .firstName("Jane")
                .lastName("Doe")
                .user(user)
                .build();
        contributor.setCreatedAt(LocalDateTime.now());
        entityManager.persist(contributor);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<Contributor> result = contributorDAO.findByUsername("janedoe");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getFirstName()).isEqualTo("Jane");
        assertThat(result.get().getUser().getUsername()).isEqualTo("janedoe");
    }

    @Transactional
    @Test
    void findByUsername_ShouldReturnEmpty_WhenContributorDoesNotExist() {
        // Act
        Optional<Contributor> result = contributorDAO.findByUsername("ghost");

        // Assert
        assertThat(result).isEmpty();
    }
}
