package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Contributor testContributor;
    private Genre testGenre1;
    private Genre testGenre2;
    private Platform testPlatform1;
    private Platform testPlatform2;
    private Publisher testPublisher;

    @BeforeEach
    void setUp() {
        testContributor = new Contributor.Builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        testGenre1 = new Genre(GenreType.RPG);
        testGenre2 = new Genre(GenreType.MOBA);

        testPlatform1 = new Platform();
        testPlatform1.setName(PlatformType.STEAM);

        testPlatform2 = new Platform();
        testPlatform2.setName(PlatformType.PLAYSTATION_5);

        testPublisher = new Publisher();
        testPublisher.setName(PublisherType.ROCKSTAR_GAMES);
    }

    @Test
    void testNoArgsConstructor() {
        Game game = new Game();

        assertNotNull(game);
        assertNull(game.getTitle());
        assertNull(game.getDescription());
        assertNull(game.getCoverImagePath());
        assertNull(game.getGenres());
        assertNull(game.getPlatforms());
        assertNull(game.getContributor());
        assertNull(game.getPublisher());
    }

    @Test
    void testConstructorWithTitleAndDescription() {
        Game game = new Game("Skyrim", "Open-world RPG");

        assertEquals("Skyrim", game.getTitle());
        assertEquals("Open-world RPG", game.getDescription());
        assertNull(game.getCoverImagePath());
        assertNull(game.getGenres());
        assertNull(game.getPlatforms());
        assertNull(game.getContributor());
        assertNull(game.getPublisher());
    }

    @Test
    void testFullConstructor() {
        List<Genre> genres = new ArrayList<>();
        genres.add(testGenre1);
        genres.add(testGenre2);

        List<Platform> platforms = new ArrayList<>();
        platforms.add(testPlatform1);
        platforms.add(testPlatform2);

        Game game = new Game(
                "LoL",
                "MOBA Game",
                "lol.jpg",
                genres,
                platforms,
                testContributor,
                testPublisher
        );

        assertEquals("LoL", game.getTitle());
        assertEquals("MOBA Game", game.getDescription());
        assertEquals("lol.jpg", game.getCoverImagePath());

        assertNotNull(game.getGenres());
        assertEquals(2, game.getGenres().size());
        assertTrue(game.getGenres().contains(testGenre1));
        assertTrue(game.getGenres().contains(testGenre2));

        assertNotNull(game.getPlatforms());
        assertEquals(2, game.getPlatforms().size());
        assertTrue(game.getPlatforms().contains(testPlatform1));
        assertTrue(game.getPlatforms().contains(testPlatform2));

        assertEquals(testContributor, game.getContributor());
        assertEquals(testPublisher, game.getPublisher());
    }

    @Test
    void testSettersAndGetters() {
        Game game = new Game();

        game.setId(100L);
        game.setTitle("Fortnite");
        game.setDescription("Battle Royale");
        game.setCoverImagePath("fortnite.png");

        List<Genre> genres = new ArrayList<>();
        genres.add(testGenre1);
        game.setGenres(genres);

        List<Platform> platforms = new ArrayList<>();
        platforms.add(testPlatform1);
        game.setPlatforms(platforms);

        game.setContributor(testContributor);
        game.setPublisher(testPublisher);

        assertEquals(100L, game.getId());
        assertEquals("Fortnite", game.getTitle());
        assertEquals("Battle Royale", game.getDescription());
        assertEquals("fortnite.png", game.getCoverImagePath());

        assertNotNull(game.getGenres());
        assertEquals(1, game.getGenres().size());
        assertEquals(testGenre1, game.getGenres().get(0));

        assertNotNull(game.getPlatforms());
        assertEquals(1, game.getPlatforms().size());
        assertEquals(testPlatform1, game.getPlatforms().get(0));

        assertEquals(testContributor, game.getContributor());
        assertEquals(testPublisher, game.getPublisher());
    }

    @Test
    void testAddGenresInitializesList() {
        Game game = new Game();
        assertNull(game.getGenres());

        game.addGenres(Set.of(testGenre1));

        assertNotNull(game.getGenres());
        assertEquals(1, game.getGenres().size());
        assertTrue(game.getGenres().contains(testGenre1));
    }

    @Test
    void testAddPlatformsInitializesList() {
        Game game = new Game();
        assertNull(game.getPlatforms());

        game.addPlatforms(Set.of(testPlatform1));

        assertNotNull(game.getPlatforms());
        assertEquals(1, game.getPlatforms().size());
        assertTrue(game.getPlatforms().contains(testPlatform1));
    }

    @Test
    void testAddGenresAppendsToExistingList() {
        List<Genre> genres = new ArrayList<>();
        genres.add(testGenre1);

        Game game = new Game();
        game.setGenres(genres);

        game.addGenres(Set.of(testGenre2));

        assertNotNull(game.getGenres());
        assertEquals(2, game.getGenres().size());
        assertTrue(game.getGenres().contains(testGenre1));
        assertTrue(game.getGenres().contains(testGenre2));
    }

    @Test
    void testAddPlatformsAppendsToExistingList() {
        List<Platform> platforms = new ArrayList<>();
        platforms.add(testPlatform1);

        Game game = new Game();
        game.setPlatforms(platforms);

        game.addPlatforms(Set.of(testPlatform2));

        assertNotNull(game.getPlatforms());
        assertEquals(2, game.getPlatforms().size());
        assertTrue(game.getPlatforms().contains(testPlatform1));
        assertTrue(game.getPlatforms().contains(testPlatform2));
    }

    @Test
    void testToStringContainsAllFields() {
        List<Genre> genres = new ArrayList<>();
        genres.add(testGenre1);

        List<Platform> platforms = new ArrayList<>();
        platforms.add(testPlatform1);

        Game game = new Game(
                "Skyrim",
                "RPG Game",
                "skyrim.jpg",
                genres,
                platforms,
                testContributor,
                testPublisher
        );

        String toString = game.toString();

        assertTrue(toString.contains("Skyrim"));
        assertTrue(toString.contains("RPG Game"));
        assertTrue(toString.contains("skyrim.jpg"));
        assertTrue(toString.contains("RPG"));
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
    }
}
