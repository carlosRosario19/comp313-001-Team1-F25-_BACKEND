package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Genre;
import com.centennial.gamepickd.util.enums.GenreType;

import java.util.Optional;
import java.util.Set;

public interface GenreDAO {
    Optional<Genre> findByLabel(GenreType genreType);
    Set<GenreType> findAll();
}
