package com.workintech.twitterapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TweetRequest (
    
    @NotBlank(message = "Tweet content cannot be empty")
    @Size(max = 280, message = "Tweet cannot exceed 280 characters")
    String content
) {

}
