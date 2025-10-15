package com.centennial.gamepickd.repository;

import com.centennial.gamepickd.entities.Contributor;
import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.entities.Genre;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import com.centennial.gamepickd.repository.impl.GameDAOJpaImpl;
import com.centennial.gamepickd.util.enums.GenreType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Test
    @Transactional
    void findAllOrderByPostedAtDesc_shouldReturnPagedGamesWithTitleAndGenreFilters() {
        // --- Setup contributor and genres ---
        User user = new User();
        user.setUsername("player");
        user.setEmail("player@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        entityManager.persist(user);

        Contributor contributor = new Contributor.Builder()
                .firstName("Player")
                .lastName("One")
                .user(user)
                .build();
        contributor.setCreatedAt(LocalDateTime.now());
        entityManager.persist(contributor);

        Genre rpgGenre = new Genre(GenreType.RPG);
        Genre shooterGenre = new Genre(GenreType.SHOOTER);
        entityManager.persist(rpgGenre);
        entityManager.persist(shooterGenre);

        // --- Create games with different createdAt and genres ---
        Game game1 = new Game("Elder Scrolls V", "Open-world RPG");
        game1.setContributor(contributor);
        game1.setCreatedAt(LocalDateTime.now().minusDays(2));
        game1.addGenre(rpgGenre);
        entityManager.persist(game1);

        Game game2 = new Game("Dark Souls", "Challenging RPG");
        game2.setContributor(contributor);
        game2.setCreatedAt(LocalDateTime.now().minusDays(1));
        game2.addGenre(rpgGenre);
        entityManager.persist(game2);

        Game game3 = new Game("Doom Eternal", "Fast-paced Shooter");
        game3.setContributor(contributor);
        game3.setCreatedAt(LocalDateTime.now());
        game3.addGenre(shooterGenre);
        entityManager.persist(game3);

        entityManager.flush();

        // --- 1️⃣ Test: pagination & ordering ---
        Pageable pageable = PageRequest.of(0, 5);
        Page<Game> page1 = gameDAO.findAllOrderByPostedAtDesc(null, null, pageable);

        assertThat(page1.getTotalElements()).isEqualTo(3);
        assertThat(page1.getContent().size()).isEqualTo(3);
        assertThat(page1.getContent().get(0).getTitle()).isEqualTo("Doom Eternal"); // newest first
        assertThat(page1.getContent().get(1).getTitle()).isEqualTo("Dark Souls");

        // --- 2️⃣ Test: filter by title ---
        Page<Game> titleFiltered = gameDAO.findAllOrderByPostedAtDesc("Dark", null, pageable);
        assertThat(titleFiltered.getTotalElements()).isEqualTo(1);
        assertThat(titleFiltered.getContent().getFirst().getTitle()).isEqualTo("Dark Souls");

        // --- 3️⃣ Test: filter by single genre ---
        Set<GenreType> rpgSet = new HashSet<>(List.of(GenreType.RPG));
        Page<Game> rpgGames = gameDAO.findAllOrderByPostedAtDesc(null, rpgSet, pageable);
        assertThat(rpgGames.getTotalElements()).isEqualTo(2);
        assertThat(rpgGames.getContent().get(0).getTitle()).isEqualTo("Dark Souls");
        assertThat(rpgGames.getContent().get(1).getTitle()).isEqualTo("Elder Scrolls V");

        // --- 4️⃣ Test: filter by multiple genres ---
        Set<GenreType> multipleGenres = new HashSet<>(Arrays.asList(GenreType.RPG, GenreType.SHOOTER));
        Page<Game> multiGenreGames = gameDAO.findAllOrderByPostedAtDesc(null, multipleGenres, pageable);
        assertThat(multiGenreGames.getTotalElements()).isEqualTo(3);
        assertThat(multiGenreGames.getContent().get(0).getTitle()).isEqualTo("Doom Eternal");
        assertThat(multiGenreGames.getContent().get(1).getTitle()).isEqualTo("Dark Souls");
        assertThat(multiGenreGames.getContent().get(2).getTitle()).isEqualTo("Elder Scrolls V");

    }


}
