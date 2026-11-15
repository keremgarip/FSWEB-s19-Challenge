package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByTweetId(Long tweetId);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.tweet.id = :tweetId")
    int deleteAllByTweetId(@Param("tweetId") Long tweetId);
}
