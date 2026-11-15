package com.workintech.twitterapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.workintech.twitterapi.dto.LikeRequest;
import com.workintech.twitterapi.dto.LikeResponse;
import com.workintech.twitterapi.service.LikeService;

import jakarta.validation.Valid;

@RestController 
public class LikeController {
    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/like")
    public ResponseEntity<LikeResponse> likeTweet(@Valid @RequestBody LikeRequest request, Authentication authentication) {
        LikeResponse response = likeService.likeTweet(request, authentication.getName());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/dislike")
    public ResponseEntity<Void> dislikeTweet(@Valid @RequestBody LikeRequest request, Authentication authentication) {
        likeService.unlikeTweet(request, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
