package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Genre;
import com.centennial.gamepickd.util.enums.GenreType;

import java.util.Optional;
import java.util.Set;

public interface GenreDAO {
    Set<Genre> findByLabels(Set<GenreType> labels);
    Set<GenreType> findAll();
}
