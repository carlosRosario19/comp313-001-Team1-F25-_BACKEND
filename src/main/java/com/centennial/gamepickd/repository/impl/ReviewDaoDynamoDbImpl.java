package com.centennial.gamepickd.repository.impl;

import com.centennial.gamepickd.entities.Review;
import com.centennial.gamepickd.repository.contracts.ReviewDAO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;

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
}
