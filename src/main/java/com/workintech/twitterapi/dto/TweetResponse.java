package com.workintech.twitterapi.dto;

import java.time.LocalDateTime;

public record TweetResponse(
    Long id,
    String content,
    Long userId,
    String username,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

}
