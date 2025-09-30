package com.centennial.gamepickd.repository;

import com.centennial.gamepickd.entities.Member;
import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.impl.MemberDAOJpaImpl;
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
class MemberDAOJpaImplTest {

    @Autowired
    private EntityManager entityManager;

    private MemberDAOJpaImpl memberDAO;

    @BeforeEach
    void setUp() {
        memberDAO = new MemberDAOJpaImpl(entityManager);
    }

    @Transactional
    @Test
    void create_ShouldPersistMember() {
        // Arrange
        User user = new User();
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("hashedPass");
        user.setEnabled(true);

        entityManager.persist(user);

        Member member = new Member.Builder()
                .firstName("John")
                .lastName("Doe")
                .user(user)
                .build();

        member.setCreatedAt(LocalDateTime.now());

        // Act
        memberDAO.create(member);

        // Flush & clear to force Hibernate to hit DB, not cache
        entityManager.flush();
        entityManager.clear();

        // Assert
        Member persisted = entityManager.find(Member.class, member.getId());
        assertThat(persisted).isNotNull();
        assertThat(persisted.getId()).isNotNull();
        assertThat(persisted.getFirstName()).isEqualTo("John");
        assertThat(persisted.getLastName()).isEqualTo("Doe");
        assertThat(persisted.getUser().getUsername()).isEqualTo("johndoe");
    }

}
