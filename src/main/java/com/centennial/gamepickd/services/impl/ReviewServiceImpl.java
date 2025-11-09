package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.dtos.AddReviewDTO;
import com.centennial.gamepickd.dtos.DeleteReviewDTO;
import com.centennial.gamepickd.dtos.ReviewDTO;
import com.centennial.gamepickd.entities.Review;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import com.centennial.gamepickd.repository.contracts.ReviewDAO;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import com.centennial.gamepickd.services.contracts.ReviewService;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final UserDAO userDAO;
    private final GameDAO gameDAO;
    private final ReviewDAO reviewDAO;
    private final Mapper mapper;

    @Autowired
    public ReviewServiceImpl(
            @Qualifier("userDAOJpaImpl") UserDAO userDAO,
            @Qualifier("gameDAOJpaImpl") GameDAO gameDAO,
            @Qualifier("reviewDaoDynamoDbImpl") ReviewDAO reviewDAO,
            Mapper mapper
    ) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.reviewDAO = reviewDAO;
        this.mapper = mapper;
    }

    @Transactional
    @CacheEvict(value = "reviewsCache", allEntries = true)
    @Override
    public void add(AddReviewDTO addReviewDTO) throws Exceptions.UserNotFoundException, Exceptions.GameNotFoundException {
        // Validate that the user exists
        userDAO.findByUsername(addReviewDTO.username())
                .orElseThrow(() -> new Exceptions.UserNotFoundException("User not found with username " + addReviewDTO.username()));

        // Validate that the game exists
        gameDAO.findById(addReviewDTO.gameId())
                .orElseThrow(() -> new Exceptions.GameNotFoundException("Game not found with id " + addReviewDTO.gameId()));

        reviewDAO.create(mapper.addReviewDtoToReview(addReviewDTO));
    }

    @Override
    @Cacheable(value = "reviewsCache")
    public Set<ReviewDTO> getAllReviewsByGameId(long gameId) throws Exceptions.GameNotFoundException {

        // Validate that the game exists
        gameDAO.findById(gameId)
                .orElseThrow(() -> new Exceptions.GameNotFoundException("Game not found with id " + gameId));

        return reviewDAO.findAllByGameId(gameId).stream()
                .map(mapper::reviewToReviewDto)
                .collect(Collectors.toSet());
    }

    @Transactional
    @CacheEvict(value = "reviewsCache", allEntries = true)
    @Override
    public Review deleteById(DeleteReviewDTO deleteReviewDTO) throws Exceptions.ReviewNotFoundException {
        Optional<Review> deleted = reviewDAO.deleteByIdAndTimeStamp(
                deleteReviewDTO.reviewId(),
                deleteReviewDTO.timeStamp()
        );

        if (deleted.isEmpty()) {
            throw new Exceptions.ReviewNotFoundException(
                    "The review with id " + deleteReviewDTO.reviewId() + " was not found."
            );
        }

        Review review = deleted.get();

        if (!review.getTimeStamp().equals(deleteReviewDTO.timeStamp())) {
            throw new Exceptions.ReviewNotFoundException(
                    "The timestamp did not match the review id. It might be incorrect."
            );
        }

        return review;
    }

    @Override
    public Map<Long, Double> getAverageRatesForGames(Set<Long> gameIds) {
        return reviewDAO.calculateAverageRateForGames(gameIds);
    }
}
