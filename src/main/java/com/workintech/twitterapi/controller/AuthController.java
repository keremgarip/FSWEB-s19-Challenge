package com.workintech.twitterapi.controller;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.workintech.twitterapi.dto.AuthResponse;
import com.workintech.twitterapi.dto.LoginRequest;
import com.workintech.twitterapi.dto.RegisterRequest;
import com.workintech.twitterapi.dto.UserResponse;
import com.workintech.twitterapi.service.AuthService;
import com.workintech.twitterapi.service.UserService;

import jakarta.validation.Valid;

@RestController 
public class AuthController {
    
    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse response = userService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }
}
