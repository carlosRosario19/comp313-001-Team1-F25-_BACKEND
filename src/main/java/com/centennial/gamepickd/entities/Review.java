package com.centennial.gamepickd.entities;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Review {

    private String reviewId;
    private String timeStamp;
    private String comment;
    private int rate;
    private String username;
    private Long gameId;

    public Review(){}

    public Review(String reviewId, String timeStamp, String comment, int rate, String username, Long gameId) {
        this.reviewId = reviewId;
        this.timeStamp = timeStamp;
        this.comment = comment;
        this.rate = rate;
        this.username = username;
        this.gameId = gameId;
    }

    @DynamoDbPartitionKey
    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    @DynamoDbSortKey
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "gameId-timeStamp-index")
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
