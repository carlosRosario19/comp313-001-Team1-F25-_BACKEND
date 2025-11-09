package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.User;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class UserDAOJpaImpl implements UserDAO {

    private final EntityManager entityManager;

    @Autowired
    public UserDAOJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void create(User user){
        entityManager.persist(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String query = "SELECT u FROM User u WHERE u.username = :username";
        try{
            User user = entityManager.createQuery(query, User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            return Optional.ofNullable(user);
        } catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public List<User> findByUsernameOrEmail(String username, String email) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username OR u.email = :email";
        return entityManager.createQuery(jpql, User.class)
                .setParameter("username", username)
                .setParameter("email", email)
                .getResultList();
    }

    @Override
    public Set<User> findAll() {
        String jpql = "SELECT u FROM User u";
        List<User> users = entityManager.createQuery(jpql, User.class)
                .getResultList();

        // Convert List → Set to ensure uniqueness
        return Set.copyOf(users);
    }
}
