package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.CommentRequest;
import com.workintech.twitterapi.dto.CommentResponse;
import com.workintech.twitterapi.dto.CommentUpdateRequest;

import java.util.List;

public interface CommentService {

    CommentResponse createComment(CommentRequest request, String username);

    CommentResponse updateComment(Long id, CommentUpdateRequest request, String username);

    void deleteComment(Long id, String username);

    List<CommentResponse> findByTweetId(Long tweetId);
}
