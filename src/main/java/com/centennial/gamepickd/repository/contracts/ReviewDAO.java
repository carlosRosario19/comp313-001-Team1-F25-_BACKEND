package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Review;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ReviewDAO {
    void create(Review review);
    Set<Review> findAllByGameId(long gameId);
    Optional<Review> deleteByIdAndTimeStamp(String reviewId, String timeStamp);
    Optional<Review> findById(String reviewId);
    Map<Long, Double> calculateAverageRateForGames(Set<Long> gameIds);
}
