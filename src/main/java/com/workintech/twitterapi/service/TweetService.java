package com.workintech.twitterapi.service;

import com.workintech.twitterapi.entity.Tweet;

import java.util.List;
import java.util.Optional;

public interface TweetService {

    List<Tweet> findByUserId(Long userId);

    Optional<Tweet> findById(Long id);
}
