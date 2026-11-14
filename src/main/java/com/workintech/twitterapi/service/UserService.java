package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.RegisterRequest;
import com.workintech.twitterapi.dto.UserResponse;
import com.workintech.twitterapi.entity.User;

import java.util.Optional;

public interface UserService {

    UserResponse register(RegisterRequest request);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
