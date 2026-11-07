package com.workintech.twitterapi.service;

public interface LikeService {

    boolean hasUserLikedTweet(Long userId, Long tweetId);

    long countLikes(Long tweetId);
}
