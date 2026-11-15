package com.workintech.twitterapi.service.impl;

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

import com.workintech.twitterapi.service.CommentService;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(
            CommentRepository commentRepository,
            TweetRepository tweetRepository,
            UserRepository userRepository) {

        this.commentRepository = commentRepository;
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentResponse createComment(
            CommentRequest request,
            String username) {

        User user = findUserByUsername(username);
        Tweet tweet = findTweetById(request.tweetId());

        Comment comment = new Comment();
        comment.setContent(request.content());
        comment.setUser(user);
        comment.setTweet(tweet);

        Comment savedComment =
                commentRepository.saveAndFlush(comment);

        return mapToResponse(savedComment);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(
            Long id,
            CommentUpdateRequest request,
            String username) {

        Comment comment = findCommentById(id);

        if (!comment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException(
                    "You can only update your own comments"
            );
        }

        comment.setContent(request.content());

        Comment updatedComment =
                commentRepository.saveAndFlush(comment);

        return mapToResponse(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String username) {

        Comment comment = findCommentById(id);

        boolean isCommentOwner =
                comment.getUser().getUsername().equals(username);

        boolean isTweetOwner =
                comment.getTweet().getUser().getUsername()
                        .equals(username);

        if (!isCommentOwner && !isTweetOwner) {
            throw new AccessDeniedException(
                    "You are not allowed to delete this comment"
            );
        }

        commentRepository.delete(comment);
        commentRepository.flush();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> findByTweetId(Long tweetId) {

        findTweetById(tweetId);

        return commentRepository.findByTweetId(tweetId)
                .stream()
                .map(this::mapToResponse)
                .toList();
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

    private Comment findCommentById(Long id) {

        return commentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Comment not found"
                        )
                );
    }

    private CommentResponse mapToResponse(Comment comment) {

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getTweet().getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}
