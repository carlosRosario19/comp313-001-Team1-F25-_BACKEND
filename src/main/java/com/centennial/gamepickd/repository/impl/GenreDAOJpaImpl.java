package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Genre;
import com.centennial.gamepickd.repository.contracts.GenreDAO;
import com.centennial.gamepickd.util.enums.GenreType;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class GenreDAOJpaImpl implements GenreDAO {

    private final EntityManager entityManager;

    @Autowired
    public GenreDAOJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Set<Genre> findByLabels(Set<GenreType> labels) {
        String query = "SELECT g FROM Genre g WHERE g.label IN :labels";
        return new HashSet<>(
                entityManager.createQuery(query, Genre.class)
                        .setParameter("labels", labels)
                        .getResultList()
        );
    }

    @Override
    public Set<GenreType> findAll() {
        String query = "SELECT g.label FROM Genre g";
        return new HashSet<>(entityManager.createQuery(query, GenreType.class).getResultList());
    }
}
