package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.dtos.AddReviewDTO;
import com.centennial.gamepickd.dtos.DeleteReviewDTO;
import com.centennial.gamepickd.dtos.ReviewDTO;
import com.centennial.gamepickd.dtos.VoteDTO;
import com.centennial.gamepickd.entities.Game;
import com.centennial.gamepickd.entities.Review;
import com.centennial.gamepickd.entities.Vote;
import com.centennial.gamepickd.repository.contracts.GameDAO;
import com.centennial.gamepickd.repository.contracts.ReviewDAO;
import com.centennial.gamepickd.repository.contracts.UserDAO;
import com.centennial.gamepickd.repository.contracts.VoteDAO;
import com.centennial.gamepickd.services.contracts.ReviewService;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import com.centennial.gamepickd.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final GameDAO gameDAO;
    private final ReviewDAO reviewDAO;
    private final VoteDAO voteDAO;
    private final Mapper mapper;
    private final SecurityUtils securityUtils;

    @Autowired
    public ReviewServiceImpl(
            @Qualifier("gameDAOJpaImpl") GameDAO gameDAO,
            @Qualifier("reviewDaoDynamoDbImpl") ReviewDAO reviewDAO,
            @Qualifier("voteDaoDynamoDbImpl") VoteDAO voteDAO,
            Mapper mapper,
            SecurityUtils securityUtils
    ) {
        this.gameDAO = gameDAO;
        this.reviewDAO = reviewDAO;
        this.voteDAO = voteDAO;
        this.mapper = mapper;
        this.securityUtils = securityUtils;
    }

    @Transactional
    @CacheEvict(value = "reviewsCache", allEntries = true)
    @Override
    public void add(AddReviewDTO addReviewDTO) throws Exceptions.GameNotFoundException {

        // Step 1: Identify current user
        String currentUsername = securityUtils.getCurrentUsername();

        // Validate that the game exists
        gameDAO.findById(addReviewDTO.gameId())
                .orElseThrow(() -> new Exceptions.GameNotFoundException("Game not found with id " + addReviewDTO.gameId()));

        reviewDAO.create(mapper.addReviewDtoToReview(addReviewDTO, currentUsername));
    }

    @Override
    @Cacheable(value = "reviewsCache")
    public Set<ReviewDTO> getAllReviewsByGameId(long gameId) throws Exceptions.GameNotFoundException {

        // Validate that the game exists
        gameDAO.findById(gameId)
                .orElseThrow(() -> new Exceptions.GameNotFoundException("Game not found with id " + gameId));

        // Fetch all reviews for this game
        Set<Review> reviews = reviewDAO.findAllByGameId(gameId);

        // Fetch all votes once
        Set<Vote> allVotes = voteDAO.findAll();

        // Group votes by reviewId
        Map<String, Set<VoteDTO>> votesByReview =
                allVotes.stream()
                        .collect(Collectors.groupingBy(
                                Vote::getReviewId,
                                Collectors.mapping(
                                        v -> new VoteDTO(v.getUsername(), v.isInFavor()),
                                        Collectors.toSet()
                                )
                        ));

        return reviews.stream()
                .map(review -> mapper.reviewToReviewDto(review, votesByReview.getOrDefault(review.getReviewId(), Set.of())))
                .collect(Collectors.toSet());
    }

    @Transactional
    @CacheEvict(value = "reviewsCache", allEntries = true)
    @Override
    public void deleteById(DeleteReviewDTO deleteReviewDTO) throws Exceptions.ReviewNotFoundException {
        // Step 1: Identify current user
        String currentUsername = securityUtils.getCurrentUsername();

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

        if (!review.getUsername().equalsIgnoreCase(currentUsername)) {
            throw new AccessDeniedException("You are not allowed to delete this review");
        }

        if (!review.getTimeStamp().equals(deleteReviewDTO.timeStamp())) {
            throw new Exceptions.ReviewNotFoundException(
                    "The timestamp did not match the review id. It might be incorrect."
            );
        }

    }

    @Override
    public Map<Long, Double> getAverageRatesForGames(Set<Long> gameIds) {
        return reviewDAO.calculateAverageRateForGames(gameIds);
    }

    @Transactional
    @CacheEvict(value = "reviewsCache", allEntries = true)
    @Override
    public void deleteAllByGameId(long gameId) {
        Set<Review> reviews = reviewDAO.findAllByGameId(gameId);
        reviewDAO.deleteAll(reviews);
    }
}
