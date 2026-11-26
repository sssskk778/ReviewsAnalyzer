package com.aska.core.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        response.setHeader("reviews-analyzer-denied-reason", "Authorization failed");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");

        String jsonResponse = String.format(
                "{\"path\": \"%s\", \"status\": %d, \"message\": \"%s\", \"timestamp\": \"%s\", \"errors\": {}}",
                request.getRequestURI(), HttpStatus.FORBIDDEN.value(), "Доступ запрещен", LocalDateTime.now());

        response.getWriter().write(jsonResponse);
    }
}
