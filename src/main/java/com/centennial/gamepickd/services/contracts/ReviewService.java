package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.AddReviewDTO;
import com.centennial.gamepickd.dtos.ReviewDTO;
import com.centennial.gamepickd.util.Exceptions;

import java.util.Set;

public interface ReviewService {
    void add(AddReviewDTO addReviewDTO) throws Exceptions.UserNotFoundException, Exceptions.GameNotFoundException;
    Set<ReviewDTO> getAllReviewsByGameId(long gameId) throws Exceptions.GameNotFoundException;
}
