package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.repository.contracts.GenreDAO;
import com.centennial.gamepickd.services.contracts.GenreService;
import com.centennial.gamepickd.util.enums.GenreType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDAO genreDAO;

    @Autowired
    public GenreServiceImpl(@Qualifier("genreDAOJpaImpl") GenreDAO genreDAO) {
        this.genreDAO = genreDAO;
    }


    @Override
    @Cacheable(value = "genresCache")
    public Set<String> getAll() {
        return genreDAO.findAll().stream()
                .map(GenreType::getVal)
                .collect(Collectors.toSet());
    }
}
