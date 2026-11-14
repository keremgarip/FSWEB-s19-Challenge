package com.workintech.twitterapi.config;

import com.workintech.twitterapi.security.CustomUserDetailsService;
import com.workintech.twitterapi.security.JwtAuthenticationFilter;
import com.workintech.twitterapi.security.JwtService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.DispatcherType;

@Configuration
public class SecurityConfig {

        @Bean
        public AuthenticationManager authenticationManager(
                        CustomUserDetailsService userDetailsService,
                        PasswordEncoder passwordEncoder) {

                DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

                provider.setPasswordEncoder(passwordEncoder);

                return new ProviderManager(provider);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        JwtService jwtService,
                        CustomUserDetailsService userDetailsService) {

                JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);

                http
                                .csrf(csrf -> csrf.disable())

                                .sessionManagement(session -> session.sessionCreationPolicy(
                                                SessionCreationPolicy.STATELESS))

                                .authorizeHttpRequests(auth -> auth

                                                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                                                .requestMatchers(
                                                                "/register",
                                                                "/login")
                                                .permitAll()

                                                .anyRequest().authenticated())

                                .exceptionHandling(exceptions -> exceptions
                                                .authenticationEntryPoint(
                                                                (request, response, authException) -> response
                                                                                .sendError(
                                                                                                HttpServletResponse.SC_UNAUTHORIZED,
                                                                                                "Unauthorized"))
                                                .accessDeniedHandler(
                                                                (request, response, accessDeniedException) -> response
                                                                                .sendError(
                                                                                                HttpServletResponse.SC_FORBIDDEN,
                                                                                                "Forbidden")))

                                .httpBasic(basic -> basic.disable())

                                .formLogin(form -> form.disable())

                                .addFilterBefore(
                                                jwtFilter,
                                                UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
