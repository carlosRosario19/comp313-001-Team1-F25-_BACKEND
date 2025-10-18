package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Publisher;
import com.centennial.gamepickd.repository.contracts.PublisherDAO;
import com.centennial.gamepickd.util.enums.PublisherType;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PublisherDAOJpaImpl implements PublisherDAO {

    private final EntityManager entityManager;

    @Autowired
    public PublisherDAOJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Publisher> findByLabel(PublisherType label) {
        String query = "SELECT p FROM Publisher p WHERE p.name = :label";
        try{
            Publisher publisher = entityManager.createQuery(query, Publisher.class)
                    .setParameter("label", label)
                    .getSingleResult();

            return Optional.ofNullable(publisher);
        } catch (Exception e){
            return Optional.empty();
        }
    }
}
