package com.workintech.twitterapi.service;

public interface RetweetService {

    boolean hasUserRetweeted(Long userId, Long tweetId);

    long countRetweets(Long tweetId);
}
