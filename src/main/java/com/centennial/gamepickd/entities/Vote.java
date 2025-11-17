package com.centennial.gamepickd.entities;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Vote {
    private String reviewId;  // PK
    private String username;  // SK (ensures 1 vote/user)
    private boolean inFavor;

    @DynamoDbPartitionKey
    public String getReviewId() { return reviewId; }

    @DynamoDbSortKey
    public String getUsername() { return username; }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isInFavor() { return inFavor; }
    public void setInFavor(boolean inFavor) { this.inFavor = inFavor; }
}
