package com.centennial.gamepickd.repository;

import com.centennial.gamepickd.entities.Genre;
import com.centennial.gamepickd.repository.contracts.GenreDAO;
import com.centennial.gamepickd.repository.impl.GenreDAOJpaImpl;
import com.centennial.gamepickd.util.enums.GenreType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class GenreDAOJpaImplTest {

    @Autowired
    private EntityManager entityManager;

    private GenreDAO genreDAO;

    @BeforeEach
    void setUp() {
        genreDAO = new GenreDAOJpaImpl(entityManager);
    }

    @Test
    @Transactional
    void findByLabels_shouldReturnGenres_whenGenresExist() {
        // Arrange
        Genre genre1 = new Genre(GenreType.RPG);
        Genre genre2 = new Genre(GenreType.SHOOTER);
        entityManager.persist(genre1);
        entityManager.persist(genre2);
        entityManager.flush();

        // Act
        Set<Genre> result = genreDAO.findByLabels(Set.of(GenreType.RPG, GenreType.SHOOTER));

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(Genre::getLabel))
                .containsExactlyInAnyOrder(GenreType.RPG, GenreType.SHOOTER);
    }

    @Test
    @Transactional
    void findByLabels_shouldReturnEmptySet_whenGenresDoNotExist() {
        // Act
        Set<Genre> result = genreDAO.findByLabels(Set.of(GenreType.MMORPG));

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void findAll_shouldReturnAllGenres_whenMultipleExist() {
        // Arrange
        Genre genre1 = new Genre(GenreType.INDIE);
        Genre genre2 = new Genre(GenreType.SHOOTER);
        Genre genre3 = new Genre(GenreType.RPG);
        entityManager.persist(genre1);
        entityManager.persist(genre2);
        entityManager.persist(genre3);
        entityManager.flush();

        // Act
        Set<GenreType> result = genreDAO.findAll();

        // Assert
        assertThat(result)
                .hasSize(3)
                .containsExactlyInAnyOrder(GenreType.INDIE, GenreType.SHOOTER, GenreType.RPG);
    }

    @Test
    @Transactional
    void findAll_shouldReturnEmptySet_whenNoGenresExist() {
        // Act
        Set<GenreType> result = genreDAO.findAll();

        // Assert
        assertThat(result).isEmpty();
    }

}
