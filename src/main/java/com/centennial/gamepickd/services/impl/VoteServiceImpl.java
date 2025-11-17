package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.dtos.AddVoteDTO;
import com.centennial.gamepickd.dtos.DeleteVoteDTO;
import com.centennial.gamepickd.entities.Vote;
import com.centennial.gamepickd.repository.contracts.ReviewDAO;
import com.centennial.gamepickd.repository.contracts.VoteDAO;
import com.centennial.gamepickd.services.contracts.VoteService;
import com.centennial.gamepickd.util.Exceptions;
import com.centennial.gamepickd.util.Mapper;
import com.centennial.gamepickd.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteDAO voteDAO;
    private final ReviewDAO reviewDAO;
    private final SecurityUtils securityUtils;
    private final Mapper mapper;

    @Autowired
    public VoteServiceImpl(
            @Qualifier("voteDaoDynamoDbImpl") VoteDAO voteDAO,
            @Qualifier("reviewDaoDynamoDbImpl") ReviewDAO reviewDAO,
            SecurityUtils securityUtils,
            Mapper mapper
    ) {
        this.voteDAO = voteDAO;
        this.reviewDAO= reviewDAO;
        this.securityUtils = securityUtils;
        this.mapper = mapper;
    }

    @CacheEvict(value = "reviewsCache", allEntries = true)
    @Transactional
    @Override
    public void add(AddVoteDTO addVoteDTO) throws Exceptions.ReviewNotFoundException {
        //Check if the review exists
        reviewDAO.findById(addVoteDTO.reviewId())
                .orElseThrow(() -> new Exceptions.ReviewNotFoundException("Review not found with id " + addVoteDTO.reviewId()));
        // Identify current user
        String currentUsername = securityUtils.getCurrentUsername();

        // 3. Check if a vote already exists from this user
        var existingVoteOpt = voteDAO.findByReviewIdAndUsername(addVoteDTO.reviewId(), currentUsername);

        boolean newVoteInFavor = addVoteDTO.inFavor(); // From DTO

        if (existingVoteOpt.isEmpty()) {
            // 4. No existing vote → create new vote
            Vote newVote = new Vote();
            newVote.setReviewId(addVoteDTO.reviewId());
            newVote.setUsername(currentUsername);
            newVote.setInFavor(newVoteInFavor);

            voteDAO.create(newVote);
            return;
        }

        // 5. Vote exists → check if change is needed
        Vote existingVote = existingVoteOpt.get();

        if (existingVote.isInFavor() == newVoteInFavor) {
            // No-op → vote identical, do nothing
            return;
        }

        // 6. User voted something, but now wants to change: update
        existingVote.setInFavor(newVoteInFavor);
        voteDAO.update(existingVote);

    }

    @CacheEvict(value = "reviewsCache", allEntries = true)
    @Transactional
    @Override
    public void delete(DeleteVoteDTO deleteVoteDTO) throws Exceptions.VoteNotFoundException {
        // Identify current user
        String currentUsername = securityUtils.getCurrentUsername();

        //Check if the review exists
        Vote vote = voteDAO.findByReviewIdAndUsername(deleteVoteDTO.reviewId(), currentUsername)
                .orElseThrow(() -> new Exceptions.VoteNotFoundException(
                        "No vote found for review ID " + deleteVoteDTO.reviewId() +
                                " by user '" + currentUsername + "'."
                ));

        //Delete vote
        voteDAO.delete(vote);
    }
}
