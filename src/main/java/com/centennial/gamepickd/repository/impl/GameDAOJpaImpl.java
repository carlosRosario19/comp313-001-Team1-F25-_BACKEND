package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    public Optional<Game> findById(long gameId) {
        String query = "SELECT g FROM Game g WHERE g.id = :gameId";
        try{
            Game game = entityManager.createQuery(query, Game.class)
                    .setParameter("gameId", gameId)
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

    @Override
    public Page<Game> findAllOrderByPostedAtDesc(String title, Set<GenreType> genres, PublisherType publisher, Set<PlatformType> platforms, Pageable pageable) {
        // Base JPQL strings
        String countJpql = "SELECT COUNT(DISTINCT g) FROM Game g LEFT JOIN g.genres gen LEFT JOIN g.platforms plat WHERE 1=1";
        String selectJpql = "SELECT DISTINCT g FROM Game g LEFT JOIN g.genres gen LEFT JOIN g.platforms plat WHERE 1=1";

        Map<String, Object> params = new HashMap<>();

        // Title filter
        if (title != null && !title.isBlank()) {
            countJpql += " AND UPPER(g.title) LIKE UPPER(:title)";
            selectJpql += " AND UPPER(g.title) LIKE UPPER(:title)";
            params.put("title", "%" + title + "%"); // contains anywhere
        }

        // Genre filter (multiple genres allowed)
        if (genres != null && !genres.isEmpty()) {
            countJpql += " AND gen.label IN :genres";
            selectJpql += " AND gen.label IN :genres";
            params.put("genres", genres);
        }

        // Publisher filter
        if (publisher != null) {
            countJpql += " AND g.publisher.name = :publisher";
            selectJpql += " AND g.publisher.name = :publisher";
            params.put("publisher", publisher);
        }

        // Platform filter
        if (platforms != null && !platforms.isEmpty()) {
            countJpql += " AND plat.name IN :platforms";
            selectJpql += " AND plat.name IN :platforms";
            params.put("platforms", platforms);
        }

        // Order by newest first (createdAt descending)
        selectJpql += " ORDER BY g.createdAt DESC";

        // Count query
        Query countQuery = entityManager.createQuery(countJpql);
        params.forEach(countQuery::setParameter);
        long total = (Long) countQuery.getSingleResult();

        // Data query
        TypedQuery<Game> query = entityManager.createQuery(selectJpql, Game.class);
        params.forEach(query::setParameter);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return new PageImpl<>(query.getResultList(), pageable, total);
    }
}
