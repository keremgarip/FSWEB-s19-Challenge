package com.workintech.twitterapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest (
    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 280, message="Comment cannot exceed 280 characters")
    String content
){
    
}
