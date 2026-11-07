package com.workintech.twitterapi.service;

import com.workintech.twitterapi.entity.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> findByTweetId(Long tweetId);
}
