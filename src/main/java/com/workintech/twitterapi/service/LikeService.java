package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.LikeRequest;
import com.workintech.twitterapi.dto.LikeResponse;

public interface LikeService {

    LikeResponse likeTweet(LikeRequest request, String username);

    void unlikeTweet(LikeRequest request, String username);

    boolean hasUserLikedTweet(Long userId, Long tweetId);

    long countLikes(Long tweetId);
}
