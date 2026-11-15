package com.workintech.twitterapi.service.impl;

import com.workintech.twitterapi.dto.LikeRequest;
import com.workintech.twitterapi.dto.LikeResponse;

import com.workintech.twitterapi.entity.Like;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;

import com.workintech.twitterapi.exception.BusinessException;
import com.workintech.twitterapi.exception.ResourceNotFoundException;

import com.workintech.twitterapi.repository.LikeRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;

import com.workintech.twitterapi.service.LikeService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public LikeServiceImpl(
            LikeRepository likeRepository,
            TweetRepository tweetRepository,
            UserRepository userRepository) {

        this.likeRepository = likeRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public LikeResponse likeTweet(
            LikeRequest request,
            String username) {

        User user = findUserByUsername(username);
        Tweet tweet = findTweetById(request.tweetId());

        if (likeRepository.existsByUserIdAndTweetId(
                user.getId(),
                tweet.getId())) {

            throw new BusinessException(
                    "You have already liked this tweet"
            );
        }

        Like like = new Like();
        like.setUser(user);
        like.setTweet(tweet);

        Like savedLike = likeRepository.saveAndFlush(like);

        return mapToResponse(savedLike);
    }

    @Override
    @Transactional
    public void unlikeTweet(
            LikeRequest request,
            String username) {

        User user = findUserByUsername(username);

        Like like = likeRepository.findByUserIdAndTweetId(
                user.getId(),
                request.tweetId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Like not found"
                )
        );

        likeRepository.delete(like);
        likeRepository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserLikedTweet(
            Long userId,
            Long tweetId) {

        return likeRepository.existsByUserIdAndTweetId(
                userId,
                tweetId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countLikes(Long tweetId) {

        return likeRepository.countByTweetId(tweetId);
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

    private LikeResponse mapToResponse(Like like) {

        return new LikeResponse(
                like.getId(),
                like.getTweet().getId(),
                like.getUser().getId(),
                like.getUser().getUsername(),
                like.getCreatedAt()
        );
    }
}
