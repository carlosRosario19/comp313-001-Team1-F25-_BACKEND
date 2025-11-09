package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.AddReviewDTO;
import com.centennial.gamepickd.dtos.DeleteReviewDTO;
import com.centennial.gamepickd.dtos.ReviewDTO;
import com.centennial.gamepickd.util.Exceptions;

import java.util.Map;
import java.util.Set;

public interface ReviewService {
    void add(AddReviewDTO addReviewDTO) throws Exceptions.GameNotFoundException;
    Set<ReviewDTO> getAllReviewsByGameId(long gameId) throws Exceptions.GameNotFoundException;
    void deleteById(DeleteReviewDTO deleteReviewDTO) throws Exceptions.ReviewNotFoundException;
    Map<Long, Double> getAverageRatesForGames(Set<Long> gameIds);
    void deleteAllByGameId(long gameId);
}
