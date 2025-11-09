package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Review;
import com.centennial.gamepickd.repository.contracts.ReviewDAO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.*;

@Repository
public class ReviewDaoDynamoDbImpl implements ReviewDAO {

    private static final String TABLE_NAME = "Reviews";
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private DynamoDbTable<Review> reviewTable;
    private static final Logger logger = LoggerFactory.getLogger(ReviewDaoDynamoDbImpl.class);

    @Autowired
    public ReviewDaoDynamoDbImpl(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
    }

    @PostConstruct
    public void init() {
        reviewTable = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(Review.class));
    }

    @Override
    public void create(Review review) {
        try {
            PutItemEnhancedRequest<Review> request = PutItemEnhancedRequest.builder(Review.class)
                    .item(review)
                    .build();

            reviewTable.putItem(request);
        } catch (Exception e) {
            logger.error("Failed to save review to DynamoDB: {}", e.getMessage(), e);
        }
    }

    @Override
    public Set<Review> findAllByGameId(long gameId) {
        try {
            DynamoDbIndex<Review> gameIdIndex = reviewTable.index("gameId-timeStamp-index");

            // Perform the query — returns SdkIterable<Page<Review>>
            var results = gameIdIndex.query(r ->
                    r.queryConditional(
                            QueryConditional.keyEqualTo(
                                    Key.builder().partitionValue(gameId).build()
                            )
                    )
            );

            // Flatten manually
            Set<Review> reviews = new HashSet<>();
            results.forEach(page -> reviews.addAll(page.items()));

            return reviews;

        } catch (Exception e) {
            logger.error("Failed to fetch reviews by gameId {}: {}", gameId, e.getMessage(), e);
            return Set.of();
        }
    }

    @Override
    public Optional<Review> deleteByIdAndTimeStamp(String reviewId, String timeStamp) {
        try {
            Optional<Review> reviewOpt = findById(reviewId);

            if (reviewOpt.isEmpty()) {
                return Optional.empty();
            }

            Review review = reviewOpt.get();

            if (!review.getTimeStamp().equals(timeStamp)) {
                // The id exists but timestamp does not match
                return Optional.of(review); // return it so service can throw proper exception
            }

            // Delete the item
            Key key = Key.builder()
                    .partitionValue(reviewId)
                    .sortValue(timeStamp)
                    .build();

            reviewTable.deleteItem(r -> r.key(key));

            return Optional.of(review);

        } catch (Exception e) {
            logger.error("Failed to delete review with id {} and timestamp {}: {}", reviewId, timeStamp, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Review> findById(String reviewId) {
        try {
            // Query the table by reviewId (partition key) — might return multiple items if composite key allows
            var results = reviewTable.query(r ->
                    r.queryConditional(QueryConditional.keyEqualTo(Key.builder()
                            .partitionValue(reviewId)
                            .build()))
            );

            // Flatten results and return first match
            return results.stream()
                    .flatMap(page -> page.items().stream())
                    .findFirst();

        } catch (Exception e) {
            logger.error("Failed to fetch review by id {}: {}", reviewId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    @Override
    public Map<Long, Double> calculateAverageRateForGames(Set<Long> gameIds) {
        Map<Long, Double> averages = new HashMap<>();
        if (gameIds.isEmpty()) return averages;

        try {
            DynamoDbIndex<Review> gameIdIndex = reviewTable.index("gameId-timeStamp-index");

            Map<Long, Integer> sumMap = new HashMap<>();
            Map<Long, Integer> countMap = new HashMap<>();

            // For each gameId, query the GSI
            for (Long gameId : gameIds) {
                var results = gameIdIndex.query(r ->
                        r.queryConditional(QueryConditional.keyEqualTo(
                                Key.builder().partitionValue(gameId).build()
                        ))
                );

                for (var page : results) {
                    for (Review review : page.items()) {
                        sumMap.put(gameId, sumMap.getOrDefault(gameId, 0) + review.getRate());
                        countMap.put(gameId, countMap.getOrDefault(gameId, 0) + 1);
                    }
                }
            }

            // Compute averages
            for (Long gameId : gameIds) {
                int sum = sumMap.getOrDefault(gameId, 0);
                int count = countMap.getOrDefault(gameId, 0);
                averages.put(gameId, count == 0 ? 0.0 : ((double) sum / count));
            }

            return averages;

        } catch (Exception e) {
            logger.error("Failed to calculate average rates for gameIds {}: {}", gameIds, e.getMessage(), e);
            return averages;
        }
    }
}
