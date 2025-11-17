package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Vote;

import java.util.Optional;
import java.util.Set;

public interface VoteDAO {
    void create(Vote vote);
    void update(Vote vote);
    Optional<Vote> delete(Vote vote);
    Set<Vote> findAllByReview(String reviewId);
    Optional<Vote> findByReviewIdAndUsername(String reviewId, String username);
}
