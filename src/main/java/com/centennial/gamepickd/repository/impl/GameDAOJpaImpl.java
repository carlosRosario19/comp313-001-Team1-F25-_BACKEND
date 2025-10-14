package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class GameDAOJpaImpl implements GameDAO {

    private final EntityManager entityManager;

    @Autowired
    public GameDAOJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public Optional<Game> findByTitle(String title) {
        String query = "SELECT g FROM Game g WHERE g.title = :title";
        try{
            Game game = entityManager.createQuery(query, Game.class)
                    .setParameter("title", title)
                    .getSingleResult();

            return Optional.ofNullable(game);
        } catch (NoResultException e){
            return Optional.empty();
        }
    }

    @Override
    public void create(Game game) {
        entityManager.persist(game);
    }
}
