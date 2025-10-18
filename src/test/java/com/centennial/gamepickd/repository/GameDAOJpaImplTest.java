package com.centennial.gamepickd.repository;

import com.centennial.gamepickd.entities.*;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import com.centennial.gamepickd.repository.impl.GameDAOJpaImpl;
import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
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
    void create_shouldPersistGame_withPublisherAndPlatforms() {
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
                .user(user)
                .build();
        contributor.setCreatedAt(LocalDateTime.now());
        entityManager.persist(contributor);

        // 3️⃣ Create Publisher
        var publisher = new com.centennial.gamepickd.entities.Publisher();
        publisher.setName(PublisherType.CD_PROJEKT);
        entityManager.persist(publisher);

        // 4️⃣ Create Platforms
        var platform1 = new com.centennial.gamepickd.entities.Platform();
        platform1.setName(PlatformType.PC_WINDOWS);
        entityManager.persist(platform1);

        var platform2 = new com.centennial.gamepickd.entities.Platform();
        platform2.setName(PlatformType.PLAYSTATION_5);
        entityManager.persist(platform2);

        Set<com.centennial.gamepickd.entities.Platform> platforms = Set.of(platform1, platform2);

        // 5️⃣ Create Game
        Game game = new Game("The Witcher 3", "Open-world RPG");
        game.setContributor(contributor);
        game.setPublisher(publisher);
        game.addPlatforms(platforms);
        game.setCreatedAt(LocalDateTime.now());

        gameDAO.create(game);
        entityManager.flush();

        // 6️⃣ Assert
        Game persistedGame = entityManager.find(Game.class, game.getId());
        assertThat(persistedGame).isNotNull();
        assertThat(persistedGame.getTitle()).isEqualTo("The Witcher 3");
        assertThat(persistedGame.getContributor()).isNotNull();
        assertThat(persistedGame.getContributor().getFirstName()).isEqualTo("John");
        assertThat(persistedGame.getPublisher()).isNotNull();
    }

    @Test
    @Transactional
    void findByTitle_shouldReturnGame_withPublisherAndPlatforms_whenGameExists() {
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

        // 3️⃣ Create Publisher
        var publisher = new com.centennial.gamepickd.entities.Publisher();
        publisher.setName(PublisherType.UBISOFT);
        entityManager.persist(publisher);

        // 4️⃣ Create Platforms
        var platform1 = new com.centennial.gamepickd.entities.Platform();
        platform1.setName(PlatformType.XBOX_SERIES_X);
        entityManager.persist(platform1);

        Set<com.centennial.gamepickd.entities.Platform> platforms = Set.of(platform1);

        // 5️⃣ Create Game
        Game game = new Game("Cyberpunk 2077", "Futuristic RPG");
        game.setContributor(contributor);
        game.setPublisher(publisher);
        game.addPlatforms(platforms);
        game.setCreatedAt(LocalDateTime.now());

        gameDAO.create(game);
        entityManager.flush();

        // 6️⃣ Act
        var result = gameDAO.findByTitle("Cyberpunk 2077");

        // 7️⃣ Assert
        assertThat(result).isPresent();
        Game foundGame = result.get();
        assertThat(foundGame.getTitle()).isEqualTo("Cyberpunk 2077");
        assertThat(foundGame.getContributor()).isNotNull();
        assertThat(foundGame.getContributor().getFirstName()).isEqualTo("Jane");
        assertThat(foundGame.getPublisher()).isNotNull();
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
    void findAllOrderByPostedAtDesc_shouldReturnPagedGamesWithAllFilters() {
        // --- 1️⃣ Setup contributor and genres ---
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

        // --- 2️⃣ Setup publishers ---
        var epic = new Publisher();
        epic.setName(PublisherType.EPIC_GAMES);
        entityManager.persist(epic);

        var nintendo = new Publisher();
        nintendo.setName(PublisherType.NINTENDO); // ✅ use NINTENDO instead
        entityManager.persist(nintendo);

        // --- 3️⃣ Setup platforms ---
        var windows = new Platform();
        windows.setName(PlatformType.PC_WINDOWS);
        entityManager.persist(windows);

        var playstation = new Platform();
        playstation.setName(PlatformType.PLAYSTATION_5);
        entityManager.persist(playstation);

        var xbox = new Platform();
        xbox.setName(PlatformType.XBOX_SERIES_X);
        entityManager.persist(xbox);

        Set<Platform> pcAndConsole = Set.of(windows, playstation);

        // --- 4️⃣ Create sample games ---
        Game elderScrolls = new Game("Elder Scrolls V", "Open-world RPG");
        elderScrolls.setContributor(contributor);
        elderScrolls.setPublisher(nintendo);
        elderScrolls.addPlatforms(pcAndConsole);
        elderScrolls.setCreatedAt(LocalDateTime.now().minusDays(2));
        elderScrolls.addGenres(Set.of(rpgGenre));
        entityManager.persist(elderScrolls);

        Game darkSouls = new Game("Dark Souls", "Challenging RPG");
        darkSouls.setContributor(contributor);
        darkSouls.setPublisher(epic);
        darkSouls.addPlatforms(Set.of(windows));
        darkSouls.setCreatedAt(LocalDateTime.now().minusDays(1));
        darkSouls.addGenres(Set.of(rpgGenre));
        entityManager.persist(darkSouls);

        Game doom = new Game("Doom Eternal", "Fast-paced Shooter");
        doom.setContributor(contributor);
        doom.setPublisher(epic);
        doom.addPlatforms(Set.of(playstation, xbox));
        doom.setCreatedAt(LocalDateTime.now());
        doom.addGenres(Set.of(shooterGenre));
        entityManager.persist(doom);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 5);

        // --- ✅ 1. Pagination & ordering ---
        Page<Game> allGames = gameDAO.findAllOrderByPostedAtDesc(null, null, null, null, pageable);
        assertThat(allGames.getContent().getFirst().getTitle()).isEqualTo("Doom Eternal");
        assertThat(allGames.getContent().get(1).getTitle()).isEqualTo("Dark Souls");

        // --- ✅ 2. Filter by title ---
        Page<Game> titleFiltered = gameDAO.findAllOrderByPostedAtDesc("Dark", null, null, null, pageable);
        assertThat(titleFiltered.getContent().getFirst().getTitle()).isEqualTo("Dark Souls");

        // --- ✅ 3. Filter by genre ---
        Set<GenreType> rpgSet = Set.of(GenreType.RPG);
        Page<Game> rpgGames = gameDAO.findAllOrderByPostedAtDesc(null, rpgSet, null, null, pageable);
        assertThat(rpgGames.getContent().getFirst().getTitle()).isEqualTo("Dark Souls");

        // --- ✅ 4. Filter by publisher ---
        Page<Game> nintendoGames = gameDAO.findAllOrderByPostedAtDesc(
                null, null, PublisherType.NINTENDO, null, pageable
        );
        assertThat(nintendoGames.getContent().getFirst().getTitle())
                .isEqualTo("Elder Scrolls V");

        // --- ✅ 5. Filter by platform ---
        Set<PlatformType> playstationOnly = Set.of(PlatformType.PLAYSTATION_5);
        Page<Game> playstationGames = gameDAO.findAllOrderByPostedAtDesc(null, null, null, playstationOnly, pageable);

        // --- ✅ 6. Combined filters ---
        Page<Game> combinedFilter = gameDAO.findAllOrderByPostedAtDesc(
                "Doom",
                Set.of(GenreType.SHOOTER),
                PublisherType.EPIC_GAMES,
                Set.of(PlatformType.PLAYSTATION_5),
                pageable
        );

        assertThat(combinedFilter.getContent().getFirst().getTitle()).isEqualTo("Doom Eternal");
    }


}
