package com.aska.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;


public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        response.setHeader("reviews-analyzer-error-reason", "Authentication failed");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = String.format(
                "{\"path\": \"%s\", \"status\": %d, \"message\": \"%s\", \"timestamp\": \"%s\", \"errors\": {}}",
                request.getRequestURI(), HttpStatus.UNAUTHORIZED.value(), "Требуется вход в аккаунт", LocalDateTime.now());
        response.getWriter().write(jsonResponse);
    }
}
