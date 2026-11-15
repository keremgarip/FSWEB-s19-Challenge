package com.workintech.twitterapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.workintech.twitterapi.dto.RetweetRequest;
import com.workintech.twitterapi.dto.RetweetResponse;
import com.workintech.twitterapi.service.RetweetService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/retweet") 
public class RetweetController {
    private final RetweetService retweetService;

    public RetweetController(RetweetService retweetService) {
        this.retweetService = retweetService;
    }

    @PostMapping
    public ResponseEntity<RetweetResponse> createRetweet(@Valid @RequestBody RetweetRequest request, Authentication authentication) {
        RetweetResponse response = retweetService.createRetweet(request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRetweet(@PathVariable Long id, Authentication authentication) {
        retweetService.deleteRetweet(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
