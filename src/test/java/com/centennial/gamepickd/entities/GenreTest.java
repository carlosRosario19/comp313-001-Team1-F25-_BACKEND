package com.centennial.gamepickd.entities;

import com.centennial.gamepickd.util.enums.GenreType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void testNoArgsConstructorCreatesEmptyGenre() {
        Genre genre = new Genre();

        assertNotNull(genre);
        assertNull(genre.getLabel());
        assertNotNull(genre.getGames());
        assertTrue(genre.getGames().isEmpty());
    }

    @Test
    void testConstructorWithLabel() {
        Genre genre = new Genre(GenreType.RPG);

        assertEquals(GenreType.RPG, genre.getLabel());
        assertNotNull(genre.getGames());
        assertTrue(genre.getGames().isEmpty());
    }

    @Test
    void testConstructorWithLabelAndGames() {
        Game mockGame1 = new Game();
        mockGame1.setTitle("Skyrim");

        Game mockGame2 = new Game();
        mockGame2.setTitle("Witcher 3");

        List<Game> games = new ArrayList<>();
        games.add(mockGame1);
        games.add(mockGame2);

        Genre genre = new Genre(GenreType.RPG, games);

        assertEquals(GenreType.RPG, genre.getLabel());
        assertEquals(2, genre.getGames().size());
        assertEquals("Skyrim", genre.getGames().getFirst().getTitle());
    }

    @Test
    void testSettersAndGetters() {
        Genre genre = new Genre();
        genre.setId(10L);
        genre.setLabel(GenreType.SHOOTER);

        List<Game> games = new ArrayList<>();
        Game game = new Game();
        game.setTitle("DOOM");
        games.add(game);

        genre.setGames(games);

        assertEquals(10L, genre.getId());
        assertEquals(GenreType.SHOOTER, genre.getLabel());
        assertEquals(1, genre.getGames().size());
        assertEquals("DOOM", genre.getGames().getFirst().getTitle());
    }

    @Test
    void testToStringContainsLabelAndGames() {
        Game game = new Game();
        game.setTitle("Fortnite");

        List<Game> games = new ArrayList<>();
        games.add(game);

        Genre genre = new Genre(GenreType.MOBA, games);
        genre.setId(5L);

        String result = genre.toString();

        assertTrue(result.contains("MOBA"));
    }

    @Test
    void testGenreTypeGetVal() {
        assertEquals("RPG", GenreType.RPG.getVal());
        assertEquals("MOBA", GenreType.MOBA.getVal());
    }

    @Test
    void testGenreTypeFromLabelValid() {
        assertEquals(GenreType.INDIE, GenreType.fromValue("indie"));
        assertEquals(GenreType.SURVIVAL, GenreType.fromValue("SURVIVAL"));
    }

    @Test
    void testGenreTypeFromLabelInvalidThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> GenreType.fromValue("DUMMY-GENRE"));

        assertTrue(exception.getMessage().contains("Unknown genre label"));
    }
}
