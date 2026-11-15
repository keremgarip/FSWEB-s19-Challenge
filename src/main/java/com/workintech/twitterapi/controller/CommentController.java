package com.workintech.twitterapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.workintech.twitterapi.dto.CommentRequest;
import com.workintech.twitterapi.dto.CommentResponse;
import com.workintech.twitterapi.dto.CommentUpdateRequest;
import com.workintech.twitterapi.service.CommentService;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("/comment")
public class CommentController {
    
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping 
    public ResponseEntity<CommentResponse> createComment(@Valid @RequestBody CommentRequest request, Authentication authentication) {
        CommentResponse response = commentService.createComment(request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long id, @Valid @RequestBody CommentUpdateRequest request, Authentication authentication) {
        return ResponseEntity.ok(commentService.updateComment(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        commentService.deleteComment(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/findByTweetId")
    public ResponseEntity<List<CommentResponse>> findByTweetId(@RequestParam Long tweetId) {
        return ResponseEntity.ok(commentService.findByTweetId(tweetId));
    }
}
