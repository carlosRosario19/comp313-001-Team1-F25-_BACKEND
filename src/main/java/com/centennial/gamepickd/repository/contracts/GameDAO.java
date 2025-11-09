package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.util.enums.GenreType;
import com.centennial.gamepickd.util.enums.PlatformType;
import com.centennial.gamepickd.util.enums.PublisherType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface GameDAO {
    Optional<Game> findByTitle(String title);
    Optional<Game> findById(long gameId);
    void create(Game game);
    Page<Game> findAllOrderByPostedAtDesc(String title,
                                          Set<GenreType> genres,
                                          PublisherType publisher,
                                          Set<PlatformType> platforms,
                                          Pageable pageable);
    void update(Game game);
}
