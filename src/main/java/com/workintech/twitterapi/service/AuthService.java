package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.AuthResponse;
import com.workintech.twitterapi.dto.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}
