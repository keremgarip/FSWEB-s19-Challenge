package com.workintech.twitterapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record LikeRequest(

    @NotNull(message = "Tweet ID cannot be null")
    @Positive(message = "Tweet ID must be positive")
    Long tweetId
) {
    
}
