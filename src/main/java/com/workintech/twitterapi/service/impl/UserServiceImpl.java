package com.workintech.twitterapi.service.impl;

import com.workintech.twitterapi.dto.RegisterRequest;
import com.workintech.twitterapi.dto.UserResponse;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.BusinessException;
import com.workintech.twitterapi.repository.UserRepository;
import com.workintech.twitterapi.service.UserService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        String username = request.username().trim();
        String email = request.email().trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(
                    "Username is already taken"
            );
        }

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(
                    "Email is already registered"
            );
        }

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);

        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.saveAndFlush(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
