package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Game;

import java.util.Optional;

public interface GameDAO {
    Optional<Game> findByTitle(String title);
    void create(Game game);
}
