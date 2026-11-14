package com.workintech.twitterapi.dto;

public record AuthResponse(

    String token,
    String tokenType,
    String username
) {
    
}
