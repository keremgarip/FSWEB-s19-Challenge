package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.TweetRequest;
import com.workintech.twitterapi.dto.TweetResponse;

import java.util.List;

public interface TweetService {

    TweetResponse createTweet(TweetRequest request, String username);

    List<TweetResponse> findByUserId(Long userId);

    TweetResponse findById(Long id);

    TweetResponse updateTweet(Long id, TweetRequest request, String username);

    void deleteTweet(Long id, String username);
}
