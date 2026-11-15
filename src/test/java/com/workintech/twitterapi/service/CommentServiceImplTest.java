
package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.CommentRequest;
import com.workintech.twitterapi.dto.CommentUpdateRequest;
import com.workintech.twitterapi.dto.CommentResponse;

import com.workintech.twitterapi.entity.Comment;
import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;

import com.workintech.twitterapi.exception.ResourceNotFoundException;

import com.workintech.twitterapi.repository.CommentRepository;
import com.workintech.twitterapi.repository.TweetRepository;
import com.workintech.twitterapi.repository.UserRepository;

import com.workintech.twitterapi.service.impl.CommentServiceImpl;

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
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TweetRepository tweetRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User tweetOwner;
    private User commentOwner;
    private Tweet tweet;
    private Comment comment;

    @BeforeEach
    void setUp() {

        tweetOwner = new User();
        tweetOwner.setId(1L);
        tweetOwner.setUsername("testuser");

        commentOwner = new User();
        commentOwner.setId(2L);
        commentOwner.setUsername("testuser2");

        tweet = new Tweet();
        tweet.setId(1L);
        tweet.setUser(tweetOwner);
        tweet.setContent("Test tweet");

        comment = new Comment();
        comment.setId(1L);
        comment.setUser(commentOwner);
        comment.setTweet(tweet);
        comment.setContent("Test comment");
    }

    @Test
    void createComment_shouldSaveSuccessfully() {

        CommentRequest request =
                new CommentRequest(1L, "New comment");

        when(userRepository.findByUsername("testuser2"))
                .thenReturn(Optional.of(commentOwner));

        when(tweetRepository.findById(1L))
                .thenReturn(Optional.of(tweet));

        when(commentRepository.saveAndFlush(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponse response =
                commentService.createComment(request, "testuser2");

        assertEquals("New comment", response.content());
        assertEquals(1L, response.tweetId());
        assertEquals(2L, response.userId());
        assertEquals("testuser2", response.username());

        verify(commentRepository)
                .saveAndFlush(any(Comment.class));
    }

    @Test
    void updateComment_shouldUpdateWhenUserIsOwner() {

        CommentUpdateRequest request =
                new CommentUpdateRequest("Updated comment");

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        when(commentRepository.saveAndFlush(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentResponse response =
                commentService.updateComment(
                        1L,
                        request,
                        "testuser2"
                );

        assertEquals("Updated comment", response.content());

        verify(commentRepository)
                .saveAndFlush(comment);
    }

    @Test
    void updateComment_shouldThrowExceptionWhenNotOwner() {

        CommentUpdateRequest request =
                new CommentUpdateRequest("Unauthorized update");

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        assertThrows(
                AccessDeniedException.class,
                () -> commentService.updateComment(
                        1L,
                        request,
                        "testuser"
                )
        );

        verify(commentRepository, never())
                .saveAndFlush(any(Comment.class));
    }

    @Test
    void deleteComment_shouldAllowCommentOwner() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, "testuser2");

        verify(commentRepository).delete(comment);
        verify(commentRepository).flush();
    }

    @Test
    void deleteComment_shouldAllowTweetOwner() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, "testuser");

        verify(commentRepository).delete(comment);
        verify(commentRepository).flush();
    }

    @Test
    void deleteComment_shouldRejectUnauthorizedUser() {

        when(commentRepository.findById(1L))
                .thenReturn(Optional.of(comment));

        assertThrows(
                AccessDeniedException.class,
                () -> commentService.deleteComment(
                        1L,
                        "anotheruser"
                )
        );

        verify(commentRepository, never())
                .delete(any(Comment.class));
    }

    @Test
    void createComment_shouldThrowExceptionWhenTweetNotFound() {

        CommentRequest request =
                new CommentRequest(99L, "New comment");

        when(userRepository.findByUsername("testuser2"))
                .thenReturn(Optional.of(commentOwner));

        when(tweetRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.createComment(
                        request,
                        "testuser2"
                )
        );

        verify(commentRepository, never())
                .saveAndFlush(any(Comment.class));
    }
}
