package com.centennial.gamepickd.repository;

import com.centennial.gamepickd.entities.Contributor;
import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import com.centennial.gamepickd.repository.impl.GameDAOJpaImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
class GameDAOJpaImplTest {

    @Autowired
    private EntityManager entityManager;

    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        gameDAO = new GameDAOJpaImpl(entityManager);
    }

    @Test
    @Transactional
    void create_shouldPersistGame() {
        // 1️⃣ Create User
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("johndoe@example.com");
        user.setPassword("secret123");
        user.setEnabled(true);
        entityManager.persist(user);

        // 2️⃣ Create Contributor
        Contributor contributor = new Contributor.Builder()
                .firstName("John")
                .lastName("Doe")
                .user(user) // mandatory
                .build();
        contributor.setCreatedAt(LocalDateTime.now());
        entityManager.persist(contributor);

        // 3️⃣ Create Game
        Game game = new Game("The Witcher 3", "Open-world RPG");
        game.setContributor(contributor);
        game.setCreatedAt(LocalDateTime.now());
        gameDAO.create(game);

        // 4️⃣ Flush to DB
        entityManager.flush();

        // 5️⃣ Assert
        Game persistedGame = entityManager.find(Game.class, game.getId());
        assertThat(persistedGame).isNotNull();
        assertThat(persistedGame.getTitle()).isEqualTo("The Witcher 3");
        assertThat(persistedGame.getContributor()).isNotNull();
        assertThat(persistedGame.getContributor().getFirstName()).isEqualTo("John");
    }

    @Test
    @Transactional
    void findByTitle_shouldReturnGame_whenGameExists() {
        // 1️⃣ Create User
        User user = new User();
        user.setUsername("janedoe");
        user.setEmail("janedoe@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        entityManager.persist(user);

        // 2️⃣ Create Contributor
        Contributor contributor = new Contributor.Builder()
                .firstName("Jane")
                .lastName("Doe")
                .user(user)
                .build();
        contributor.setCreatedAt(LocalDateTime.now());
        entityManager.persist(contributor);

        // 3️⃣ Create Game
        Game game = new Game("Cyberpunk 2077", "Futuristic RPG");
        game.setContributor(contributor);
        game.setCreatedAt(LocalDateTime.now());
        gameDAO.create(game);
        entityManager.flush();

        // 4️⃣ Act
        var result = gameDAO.findByTitle("Cyberpunk 2077");

        // 5️⃣ Assert
        assertThat(result).isPresent();
        Game foundGame = result.get();
        assertThat(foundGame.getTitle()).isEqualTo("Cyberpunk 2077");
        assertThat(foundGame.getContributor()).isNotNull();
        assertThat(foundGame.getContributor().getFirstName()).isEqualTo("Jane");
    }

    @Test
    @Transactional
    void findByTitle_shouldReturnEmpty_whenGameDoesNotExist() {
        // 1️⃣ Act
        var result = gameDAO.findByTitle("Nonexistent Game");

        // 2️⃣ Assert
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    void findByTitle_shouldReturnCorrectGame_whenMultipleGamesExist() {
        // 1️⃣ Create User
        User user = new User();
        user.setUsername("alex");
        user.setEmail("alex@example.com");
        user.setPassword("pass123");
        user.setEnabled(true);
        entityManager.persist(user);

        // 2️⃣ Contributor
        Contributor contributor = new Contributor.Builder()
                .firstName("Alex")
                .lastName("Smith")
                .user(user)
                .build();
        contributor.setCreatedAt(LocalDateTime.now());
        entityManager.persist(contributor);

        // 3️⃣ Multiple Games
        Game game1 = new Game("Assassin's Creed", "Action RPG");
        game1.setContributor(contributor);
        game1.setCreatedAt(LocalDateTime.now());
        gameDAO.create(game1);

        Game game2 = new Game("Assassin's Creed Valhalla", "Open-world RPG");
        game2.setContributor(contributor);
        game2.setCreatedAt(LocalDateTime.now());
        gameDAO.create(game2);

        entityManager.flush();

        // 4️⃣ Act
        var result = gameDAO.findByTitle("Assassin's Creed");

        // 5️⃣ Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Assassin's Creed");
    }



}
