package com.centennial.gamepickd.entities;

import org.jspecify.annotations.NullMarked;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@NullMarked
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

    @DynamoDbAttribute("comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @DynamoDbAttribute("rate")
    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @DynamoDbAttribute("username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDbAttribute("gameId")
    @DynamoDbSecondaryPartitionKey(indexNames = "gameId-timeStamp-index")
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
