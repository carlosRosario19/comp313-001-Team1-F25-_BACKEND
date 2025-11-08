package com.centennial.gamepickd.services.contracts;

import com.centennial.gamepickd.dtos.AddReviewDTO;
import com.centennial.gamepickd.util.Exceptions;

public interface ReviewService {
    void add(AddReviewDTO addReviewDTO) throws Exceptions.UserNotFoundException, Exceptions.GameNotFoundException;
}
