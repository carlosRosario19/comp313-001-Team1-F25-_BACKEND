package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Review;

import java.util.Set;

public interface ReviewDAO {
    void create(Review review);
    Set<Review> findAllByGameId(long gameId);
}
