package com.workintech.twitterapi.service.impl;

import com.workintech.twitterapi.dto.TweetRequest;
import com.workintech.twitterapi.dto.TweetResponse;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.ResourceNotFoundException;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import com.workintech.twitterapi.service.TweetService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TweetServiceImpl implements TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetServiceImpl(
            TweetRepository tweetRepository,
            UserRepository userRepository) {

        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TweetResponse createTweet(
            TweetRequest request,
            String username) {

        User user = findUserByUsername(username);

        Tweet tweet = new Tweet();
        tweet.setContent(request.content());
        tweet.setUser(user);

        Tweet savedTweet = tweetRepository.saveAndFlush(tweet);

        return mapToResponse(savedTweet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TweetResponse> findByUserId(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }

        return tweetRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TweetResponse findById(Long id) {

        Tweet tweet = findTweetById(id);

        return mapToResponse(tweet);
    }

    @Override
    @Transactional
    public TweetResponse updateTweet(
            Long id,
            TweetRequest request,
            String username) {

        Tweet tweet = findTweetById(id);

        validateOwnership(tweet, username);

        tweet.setContent(request.content());

        Tweet updatedTweet = tweetRepository.saveAndFlush(tweet);

        return mapToResponse(updatedTweet);
    }

    @Override
    @Transactional
    public void deleteTweet(Long id, String username) {

        Tweet tweet = findTweetById(id);

        validateOwnership(tweet, username);

        tweetRepository.delete(tweet);
        tweetRepository.flush();
    }

    private Tweet findTweetById(Long id) {

        return tweetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tweet not found"
                        )
                );
    }

    private User findUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private void validateOwnership(
            Tweet tweet,
            String username) {

        if (!tweet.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException(
                    "You can only modify your own tweets"
            );
        }
    }

    private TweetResponse mapToResponse(Tweet tweet) {

        return new TweetResponse(
                tweet.getId(),
                tweet.getContent(),
                tweet.getUser().getId(),
                tweet.getUser().getUsername(),
                tweet.getCreatedAt(),
                tweet.getUpdatedAt()
        );
    }
}
