package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Platform;
import com.centennial.gamepickd.repository.contracts.PlatformDAO;
import com.centennial.gamepickd.util.enums.PlatformType;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public class PlatformDAOJpaImpl implements PlatformDAO {

    private final EntityManager entityManager;

    @Autowired
    public PlatformDAOJpaImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Set<Platform> findByLabels(Set<PlatformType> labels) {
        String query = "SELECT p FROM Platform p WHERE p.name IN :labels";
        return new HashSet<>(
                entityManager.createQuery(query, Platform.class)
                        .setParameter("labels", labels)
                        .getResultList()
        );
    }
}
