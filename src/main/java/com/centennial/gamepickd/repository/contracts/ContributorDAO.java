package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Contributor;

import java.util.Optional;

public interface ContributorDAO {
    void create(Contributor contributor);
    Optional<Contributor> findByUsername(String username);
}
