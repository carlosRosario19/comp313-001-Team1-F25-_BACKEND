package com.centennial.gamepickd.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContributorTest {

    private User testUser;
    private Game game1;
    private Game game2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("test123");

        game1 = new Game();
        game1.setId(10L);
        game1.setTitle("Game One");

        game2 = new Game();
        game2.setId(20L);
        game2.setTitle("Game Two");
    }

    @Test
    void testBuilderBuildsCorrectContributor() {
        Contributor contributor = new Contributor.Builder()
                .id(100L)
                .user(testUser)
                .firstName("John")
                .lastName("Doe")
                .build();

        assertEquals(100L, contributor.getId());
        assertEquals(testUser, contributor.getUser());
        assertEquals("John", contributor.getFirstName());
        assertEquals("Doe", contributor.getLastName());
        assertNull(contributor.getGames());
    }

    @Test
    void testBuilderWithGamesList() {
        List<Game> games = List.of(game1, game2);

        Contributor contributor = new Contributor.Builder()
                .id(101L)
                .user(testUser)
                .firstName("Jane")
                .lastName("Smith")
                .games(games)
                .build();

        assertEquals(101L, contributor.getId());
        assertEquals("Jane", contributor.getFirstName());
        assertNotNull(contributor.getGames());
        assertEquals(2, contributor.getGames().size());
        assertEquals("Game One", contributor.getGames().getFirst().getTitle());
    }

    @Test
    void testSettersAndGetters() {
        Contributor contributor = new Contributor();
        contributor.setId(200L);
        contributor.setUser(testUser);
        contributor.setFirstName("Alice");
        contributor.setLastName("Smith");

        List<Game> games = new ArrayList<>();
        games.add(game1);
        contributor.setGames(games);

        assertEquals(200L, contributor.getId());
        assertEquals(testUser, contributor.getUser());
        assertEquals("Alice", contributor.getFirstName());
        assertEquals("Smith", contributor.getLastName());
        assertNotNull(contributor.getGames());
        assertEquals(1, contributor.getGames().size());
    }

    @Test
    void testSetGamesWithNullList() {
        Contributor contributor = new Contributor();
        contributor.setGames(null);
        assertNull(contributor.getGames());
    }

    @Test
    void testToStringContainsFields() {
        Contributor contributor = new Contributor.Builder()
                .id(300L)
                .user(testUser)
                .firstName("Bob")
                .lastName("Marley")
                .build();

        String toString = contributor.toString();

        assertTrue(toString.contains("id=300"));
        assertTrue(toString.contains("Bob"));
        assertTrue(toString.contains("Marley"));
        assertTrue(toString.contains("testuser")); // user’s username
    }

    @Test
    void testDefaultConstructor() {
        Contributor contributor = new Contributor();
        assertNotNull(contributor);
    }
}
