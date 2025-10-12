package com.centennial.gamepickd.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContributorTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("test123");
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
    }

    @Test
    void testSettersAndGetters() {
        Contributor contributor = new Contributor();
        contributor.setId(200L);
        contributor.setUser(testUser);
        contributor.setFirstName("Alice");
        contributor.setLastName("Smith");

        assertEquals(200L, contributor.getId());
        assertEquals(testUser, contributor.getUser());
        assertEquals("Alice", contributor.getFirstName());
        assertEquals("Smith", contributor.getLastName());
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
        assertNotNull(contributor); // should create empty object
    }
}
