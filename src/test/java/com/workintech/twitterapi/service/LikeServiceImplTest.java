
package com.workintech.twitterapi.service;

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

import com.workintech.twitterapi.service.impl.LikeServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LikeServiceImpl likeService;

    private User user;
    private Tweet tweet;
    private Like like;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUser(user);
        tweet.setContent("Test tweet");

        like = new Like();
        like.setId(1L);
        like.setUser(user);
        like.setTweet(tweet);
    }

    @Test
    void likeTweet_shouldSaveSuccessfully() {

        LikeRequest request = new LikeRequest(1L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(tweetRepository.findById(1L))
                .thenReturn(Optional.of(tweet));

        when(likeRepository.existsByUserIdAndTweetId(1L, 1L))
                .thenReturn(false);

        when(likeRepository.saveAndFlush(any(Like.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LikeResponse response =
                likeService.likeTweet(request, "testuser");

        assertEquals(1L, response.userId());
        assertEquals(1L, response.tweetId());
        assertEquals("testuser", response.username());

        verify(likeRepository).saveAndFlush(any(Like.class));
    }

    @Test
    void likeTweet_shouldThrowExceptionWhenAlreadyLiked() {

        LikeRequest request = new LikeRequest(1L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(tweetRepository.findById(1L))
                .thenReturn(Optional.of(tweet));

        when(likeRepository.existsByUserIdAndTweetId(1L, 1L))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> likeService.likeTweet(request, "testuser")
        );

        verify(likeRepository, never())
                .saveAndFlush(any(Like.class));
    }

    @Test
    void likeTweet_shouldThrowExceptionWhenTweetNotFound() {

        LikeRequest request = new LikeRequest(99L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(tweetRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> likeService.likeTweet(request, "testuser")
        );

        verify(likeRepository, never())
                .saveAndFlush(any(Like.class));
    }

    @Test
    void unlikeTweet_shouldDeleteSuccessfully() {

        LikeRequest request = new LikeRequest(1L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(likeRepository.findByUserIdAndTweetId(1L, 1L))
                .thenReturn(Optional.of(like));

        likeService.unlikeTweet(request, "testuser");

        verify(likeRepository).delete(like);
        verify(likeRepository).flush();
    }

    @Test
    void unlikeTweet_shouldThrowExceptionWhenLikeNotFound() {

        LikeRequest request = new LikeRequest(99L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(likeRepository.findByUserIdAndTweetId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> likeService.unlikeTweet(request, "testuser")
        );

        verify(likeRepository, never())
                .delete(any(Like.class));
    }

    @Test
    void hasUserLikedTweet_shouldReturnCorrectStatus() {

        when(likeRepository.existsByUserIdAndTweetId(1L, 1L))
                .thenReturn(true);

        boolean result = likeService.hasUserLikedTweet(1L, 1L);

        assertTrue(result);

        verify(likeRepository)
                .existsByUserIdAndTweetId(1L, 1L);
    }

    @Test
    void countLikes_shouldReturnTotalLikes() {

        when(likeRepository.countByTweetId(1L))
                .thenReturn(5L);

        long result = likeService.countLikes(1L);

        assertEquals(5L, result);

        verify(likeRepository).countByTweetId(1L);
    }
}
