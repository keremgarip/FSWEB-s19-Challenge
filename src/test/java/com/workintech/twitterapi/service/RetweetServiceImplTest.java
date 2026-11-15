
package com.workintech.twitterapi.service;

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

import com.workintech.twitterapi.service.impl.RetweetServiceImpl;

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
class RetweetServiceImplTest {

    @Mock
    private RetweetRepository retweetRepository;

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RetweetServiceImpl retweetService;

    private User user;
    private Tweet tweet;
    private Retweet retweet;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUser(user);
        tweet.setContent("Test tweet");

        retweet = new Retweet();
        retweet.setId(1L);
        retweet.setUser(user);
        retweet.setTweet(tweet);
    }

    @Test
    void createRetweet_shouldSaveSuccessfully() {

        RetweetRequest request = new RetweetRequest(1L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(tweetRepository.findById(1L))
                .thenReturn(Optional.of(tweet));

        when(retweetRepository.saveAndFlush(any(Retweet.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RetweetResponse response =
                retweetService.createRetweet(request, "testuser");

        assertEquals(1L, response.userId());
        assertEquals(1L, response.tweetId());
        assertEquals("testuser", response.username());

        verify(retweetRepository)
                .saveAndFlush(any(Retweet.class));
    }

    @Test
    void createRetweet_shouldThrowExceptionWhenAlreadyRetweeted() {

        RetweetRequest request = new RetweetRequest(1L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(tweetRepository.findById(1L))
                .thenReturn(Optional.of(tweet));

        when(retweetRepository.existsByUserIdAndTweetId(1L, 1L))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> retweetService.createRetweet(
                        request,
                        "testuser"
                )
        );

        verify(retweetRepository, never())
                .saveAndFlush(any(Retweet.class));
    }

    @Test
    void createRetweet_shouldThrowExceptionWhenTweetNotFound() {

        RetweetRequest request = new RetweetRequest(99L);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(tweetRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> retweetService.createRetweet(
                        request,
                        "testuser"
                )
        );

        verify(retweetRepository, never())
                .saveAndFlush(any(Retweet.class));
    }

    @Test
    void deleteRetweet_shouldAllowOwner() {

        when(retweetRepository.findById(1L))
                .thenReturn(Optional.of(retweet));

        retweetService.deleteRetweet(1L, "testuser");

        verify(retweetRepository).delete(retweet);
        verify(retweetRepository).flush();
    }

    @Test
    void deleteRetweet_shouldRejectUnauthorizedUser() {

        when(retweetRepository.findById(1L))
                .thenReturn(Optional.of(retweet));

        assertThrows(
                AccessDeniedException.class,
                () -> retweetService.deleteRetweet(
                        1L,
                        "anotheruser"
                )
        );

        verify(retweetRepository, never())
                .delete(any(Retweet.class));

        verify(retweetRepository, never())
                .flush();
    }

    @Test
    void deleteRetweet_shouldThrowExceptionWhenNotFound() {

        when(retweetRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> retweetService.deleteRetweet(
                        99L,
                        "testuser"
                )
        );

        verify(retweetRepository, never())
                .delete(any(Retweet.class));
    }

    @Test
    void hasUserRetweeted_shouldReturnCorrectStatus() {

        when(retweetRepository.existsByUserIdAndTweetId(1L, 1L))
                .thenReturn(true);

        boolean result =
                retweetService.hasUserRetweeted(1L, 1L);

        assertTrue(result);

        verify(retweetRepository)
                .existsByUserIdAndTweetId(1L, 1L);
    }

    @Test
    void countRetweets_shouldReturnTotal() {

        when(retweetRepository.countByTweetId(1L))
                .thenReturn(3L);

        long result = retweetService.countRetweets(1L);

        assertEquals(3L, result);

        verify(retweetRepository).countByTweetId(1L);
    }
}

