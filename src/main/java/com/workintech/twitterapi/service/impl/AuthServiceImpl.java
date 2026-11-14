package com.workintech.twitterapi.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.workintech.twitterapi.dto.AuthResponse;
import com.workintech.twitterapi.dto.LoginRequest;
import com.workintech.twitterapi.security.JwtService;
import com.workintech.twitterapi.service.AuthService;

@Service 
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        
        Authentication authentication =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
                );
        
        String username = authentication.getName();

        String token = jwtService.generateToken(username);

        return new AuthResponse(token, "Bearer", username);
    }
    
}
