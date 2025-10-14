package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Contributor;
import com.centennial.gamepickd.repository.contracts.ContributorDAO;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ContributorDAOJpaImpl implements ContributorDAO {

    private final EntityManager entityManager;

    @Autowired
    public ContributorDAOJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void create(Contributor contributor) {
        entityManager.persist(contributor);
    }

    @Override
    public Optional<Contributor> findByUsername(String username) {
        String query = "SELECT c FROM Contributor c WHERE c.user.username = :username";

        try{
            Contributor contributor = entityManager.createQuery(query, Contributor.class)
                    .setParameter("username", username)
                    .getSingleResult();

            return Optional.ofNullable(contributor);
        } catch (Exception e){
            return Optional.empty();
        }
    }
}
