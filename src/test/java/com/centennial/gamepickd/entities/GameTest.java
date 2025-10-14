package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.enums.GenreType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Contributor testContributor;
    private Genre testGenre1;
    private Genre testGenre2;

    @BeforeEach
    void setUp() {
        testContributor = new Contributor.Builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        testGenre1 = new Genre(GenreType.RPG);
        testGenre2 = new Genre(GenreType.MOBA);
    }

    @Test
    void testNoArgsConstructor() {
        Game game = new Game();

        assertNotNull(game);
        assertNull(game.getTitle());
        assertNull(game.getDescription());
        assertNull(game.getCoverImagePath());
        assertNull(game.getGenres());
        assertNull(game.getContributor());
    }

    @Test
    void testConstructorWithTitleAndDescription() {
        Game game = new Game("Skyrim", "Open-world RPG");

        assertEquals("Skyrim", game.getTitle());
        assertEquals("Open-world RPG", game.getDescription());
        assertNull(game.getCoverImagePath());
        assertNull(game.getGenres());
        assertNull(game.getContributor());
    }

    @Test
    void testFullConstructor() {
        List<Genre> genres = new ArrayList<>();
        genres.add(testGenre1);
        genres.add(testGenre2);

        Game game = new Game("LoL", "MOBA Game", "lol.jpg", genres, testContributor);

        assertEquals("LoL", game.getTitle());
        assertEquals("MOBA Game", game.getDescription());
        assertEquals("lol.jpg", game.getCoverImagePath());
        assertNotNull(game.getGenres());
        assertEquals(2, game.getGenres().size());
        assertEquals(testContributor, game.getContributor());
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

        game.setContributor(testContributor);

        assertEquals(100L, game.getId());
        assertEquals("Fortnite", game.getTitle());
        assertEquals("Battle Royale", game.getDescription());
        assertEquals("fortnite.png", game.getCoverImagePath());
        assertNotNull(game.getGenres());
        assertEquals(1, game.getGenres().size());
        assertEquals(testContributor, game.getContributor());
    }

    @Test
    void testAddGenreInitializesList() {
        Game game = new Game();
        assertNull(game.getGenres());

        game.addGenre(testGenre1);

        assertNotNull(game.getGenres());
        assertEquals(1, game.getGenres().size());
        assertEquals(testGenre1, game.getGenres().getFirst());
    }

    @Test
    void testAddGenreAppendsToExistingList() {
        List<Genre> genres = new ArrayList<>();
        genres.add(testGenre1);

        Game game = new Game();
        game.setGenres(genres);

        game.addGenre(testGenre2);

        assertNotNull(game.getGenres());
        assertEquals(2, game.getGenres().size());
        assertTrue(game.getGenres().contains(testGenre1));
        assertTrue(game.getGenres().contains(testGenre2));
    }

    @Test
    void testToStringContainsAllFields() {
        List<Genre> genres = new ArrayList<>();
        genres.add(testGenre1);
        Game game = new Game("Skyrim", "RPG Game", "skyrim.jpg", genres, testContributor);

        String toString = game.toString();

        assertTrue(toString.contains("Skyrim"));
        assertTrue(toString.contains("RPG Game"));
        assertTrue(toString.contains("skyrim.jpg"));
        assertTrue(toString.contains("RPG"));
        assertTrue(toString.contains("John"));
        assertTrue(toString.contains("Doe"));
    }
}
