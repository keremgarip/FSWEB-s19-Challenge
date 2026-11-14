package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.TweetRequest;
import com.workintech.twitterapi.dto.TweetResponse;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.ResourceNotFoundException;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;
import com.workintech.twitterapi.service.impl.TweetServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TweetServiceImplTest {

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TweetServiceImpl tweetService;

    private User user;
    private Tweet tweet;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setContent("Original tweet");
        tweet.setUser(user);
    }

    @Test
    void createTweet_shouldSaveTweetSuccessfully() {

        TweetRequest request = new TweetRequest("My first tweet!");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(tweetRepository.saveAndFlush(any(Tweet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TweetResponse response =
                tweetService.createTweet(request, "testuser");

        assertEquals("My first tweet!", response.content());
        assertEquals("testuser", response.username());
        assertEquals(1L, response.userId());

        verify(tweetRepository).saveAndFlush(any(Tweet.class));
    }

    @Test
    void updateTweet_shouldUpdateWhenUserIsOwner() {

        TweetRequest request = new TweetRequest("Updated tweet");

        when(tweetRepository.findById(1L))
                .thenReturn(Optional.of(tweet));

        when(tweetRepository.saveAndFlush(any(Tweet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TweetResponse response =
                tweetService.updateTweet(1L, request, "testuser");

        assertEquals("Updated tweet", response.content());

        verify(tweetRepository).saveAndFlush(tweet);
    }

    @Test
    void updateTweet_shouldThrowExceptionWhenUserIsNotOwner() {

        TweetRequest request = new TweetRequest("Unauthorized update");

        when(tweetRepository.findById(1L))
                .thenReturn(Optional.of(tweet));

        assertThrows(
                AccessDeniedException.class,
                () -> tweetService.updateTweet(
                        1L,
                        request,
                        "anotheruser"
                )
        );

        verify(tweetRepository, never())
                .saveAndFlush(any(Tweet.class));
    }

    @Test
    void deleteTweet_shouldThrowExceptionWhenUserIsNotOwner() {

        when(tweetRepository.findById(1L))
                .thenReturn(Optional.of(tweet));

        assertThrows(
                AccessDeniedException.class,
                () -> tweetService.deleteTweet(1L, "anotheruser")
        );

        verify(tweetRepository, never())
                .delete(any(Tweet.class));
    }

    @Test
    void findById_shouldThrowExceptionWhenTweetDoesNotExist() {

        when(tweetRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tweetService.findById(99L)
        );
    }
}
