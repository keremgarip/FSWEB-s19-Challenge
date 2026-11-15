package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.RetweetRequest;
import com.workintech.twitterapi.dto.RetweetResponse;

public interface RetweetService {

    RetweetResponse createRetweet(RetweetRequest request, String username);

    void deleteRetweet(Long id, String username);

    boolean hasUserRetweeted(Long userId, Long tweetId);

    long countRetweets(Long tweetId);
}
