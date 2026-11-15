package com.workintech.twitterapi.service.impl;

import com.workintech.twitterapi.dto.RetweetRequest;
import com.workintech.twitterapi.dto.RetweetResponse;

import com.workintech.twitterapi.entity.Retweet;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;

import com.workintech.twitterapi.exception.BusinessException;
import com.workintech.twitterapi.exception.ResourceNotFoundException;

import com.workintech.twitterapi.repository.RetweetRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;

import com.workintech.twitterapi.service.RetweetService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RetweetServiceImpl implements RetweetService {

    private final RetweetRepository retweetRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public RetweetServiceImpl(
            RetweetRepository retweetRepository,
            TweetRepository tweetRepository,
            UserRepository userRepository) {

        this.retweetRepository = retweetRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public RetweetResponse createRetweet(
            RetweetRequest request,
            String username) {

        User user = findUserByUsername(username);
        Tweet tweet = findTweetById(request.tweetId());

        if (retweetRepository.existsByUserIdAndTweetId(
                user.getId(),
                tweet.getId())) {

            throw new BusinessException(
                    "You have already retweeted this tweet"
            );
        }

        Retweet retweet = new Retweet();
        retweet.setUser(user);
        retweet.setTweet(tweet);

        Retweet savedRetweet =
                retweetRepository.saveAndFlush(retweet);

        return mapToResponse(savedRetweet);
    }

    @Override
    @Transactional
    public void deleteRetweet(Long id, String username) {

        Retweet retweet = retweetRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Retweet not found"
                        )
                );

        if (!retweet.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException(
                    "You can only delete your own retweets"
            );
        }

        retweetRepository.delete(retweet);
        retweetRepository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserRetweeted(
            Long userId,
            Long tweetId) {

        return retweetRepository.existsByUserIdAndTweetId(
                userId,
                tweetId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countRetweets(Long tweetId) {

        return retweetRepository.countByTweetId(tweetId);
    }

    private User findUserByUsername(String username) {

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        )
                );
    }

    private Tweet findTweetById(Long tweetId) {

        return tweetRepository.findById(tweetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Tweet not found"
                        )
                );
    }

    private RetweetResponse mapToResponse(Retweet retweet) {

        return new RetweetResponse(
                retweet.getId(),
                retweet.getTweet().getId(),
                retweet.getUser().getId(),
                retweet.getUser().getUsername(),
                retweet.getCreatedAt()
        );
    }
}
