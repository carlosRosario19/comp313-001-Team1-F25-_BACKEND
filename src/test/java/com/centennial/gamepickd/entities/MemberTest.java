package com.centennial.gamepickd.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

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
    void testBuilderBuildsCorrectMember() {
        Member member = new Member.Builder()
                .id(100L)
                .user(testUser)
                .firstName("John")
                .lastName("Doe")
                .build();

        assertEquals(100L, member.getId());
        assertEquals(testUser, member.getUser());
        assertEquals("John", member.getFirstName());
        assertEquals("Doe", member.getLastName());
    }

    @Test
    void testSettersAndGetters() {
        Member member = new Member();
        member.setId(200L);
        member.setUser(testUser);
        member.setFirstName("Alice");
        member.setLastName("Smith");

        assertEquals(200L, member.getId());
        assertEquals(testUser, member.getUser());
        assertEquals("Alice", member.getFirstName());
        assertEquals("Smith", member.getLastName());
    }

    @Test
    void testToStringContainsFields() {
        Member member = new Member.Builder()
                .id(300L)
                .user(testUser)
                .firstName("Bob")
                .lastName("Marley")
                .build();

        String toString = member.toString();

        assertTrue(toString.contains("id=300"));
        assertTrue(toString.contains("Bob"));
        assertTrue(toString.contains("Marley"));
        assertTrue(toString.contains("testuser")); // user’s username
    }

    @Test
    void testDefaultConstructor() {
        Member member = new Member();
        assertNotNull(member); // should create empty object
    }
}
