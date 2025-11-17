package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Review;
import com.centennial.gamepickd.entities.Vote;
import com.centennial.gamepickd.repository.contracts.VoteDAO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class VoteDaoDynamoDbImpl implements VoteDAO {

    private static final String TABLE_NAME = "Votes";
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private DynamoDbTable<Vote> voteTable;
    private static final Logger logger = LoggerFactory.getLogger(ReviewDaoDynamoDbImpl.class);

    @Autowired
    public VoteDaoDynamoDbImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    @PostConstruct
    public void init() {
        voteTable = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(Vote.class));
    }

    @Override
    public void create(Vote vote) {
        try {
            voteTable.putItem(vote);
        } catch (Exception e) {
            logger.error("Error creating vote", e);
        }
    }

    @Override
    public void update(Vote vote) {
        try {
            voteTable.putItem(vote); // same as create (DynamoDB upsert)
        } catch (Exception e) {
            logger.error("Error updating vote", e);
        }
    }

    @Override
    public Optional<Vote> delete(Vote vote) {
        try {
            Key key = Key.builder()
                    .partitionValue(vote.getReviewId())
                    .sortValue(vote.getUsername())
                    .build();

            Vote deleted = voteTable.deleteItem(key);

            if (deleted != null) {
                logger.info("Deleted vote: reviewId={}, username={}", vote.getReviewId(), vote.getUsername());
                return Optional.of(deleted);
            } else {
                logger.warn("Vote not found for delete: reviewId={}, username={}", vote.getReviewId(), vote.getUsername());
                return Optional.empty();
            }

        } catch (Exception e) {
            logger.error("Error deleting vote", e);
            throw e;
        }
    }

    @Override
    public Set<Vote> findAllByReview(String reviewId) {
        Set<Vote> votes = new HashSet<>();

        try {
            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(reviewId)
                            .build()))
                    .build();

            voteTable.query(request)
                    .items()
                    .forEach(votes::add);

            logger.info("Fetched {} votes for reviewId={}", votes.size(), reviewId);

        } catch (Exception e) {
            logger.error("Error querying votes for reviewId={}", reviewId, e);
            throw e;
        }

        return votes;
    }

    @Override
    public Optional<Vote> findByReviewIdAndUsername(String reviewId, String username) {
        try {
            Key key = Key.builder()
                    .partitionValue(reviewId)
                    .sortValue(username)
                    .build();

            Vote vote = voteTable.getItem(key);
            return Optional.ofNullable(vote);

        } catch (Exception e) {
            logger.error("Error fetching vote for reviewId={} username={}", reviewId, username, e);
            throw e;
        }
    }

    @Override
    public Set<Vote> findAll() {
        Set<Vote> votes = new HashSet<>();

        try {
            voteTable.scan()
                    .items()
                    .forEach(votes::add);

            logger.info("Fetched {} total votes", votes.size());

        } catch (Exception e) {
            logger.error("Error scanning all votes", e);
            throw e;
        }

        return votes;
    }
}
