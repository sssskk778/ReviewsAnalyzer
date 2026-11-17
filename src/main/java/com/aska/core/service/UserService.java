package com.aska.core.service;

import com.aska.core.dto.LoginRequest;
import com.aska.core.dto.RegisterRequest;
import com.aska.core.dto.UserResponse;
import com.aska.core.model.Authority;
import com.aska.core.model.User;
import com.aska.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public User register(RegisterRequest request) {
        String email = normalize(request.email());

        if (userRepository.existsByEmail(email)) {
           throw new ResponseStatusException(HttpStatus.CONFLICT, "");
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setName(request.name().trim());

        Authority authority = new Authority();
        authority.setName("ROLE_USER");
        authority.setUser(user);
        user.getAuthorities().add(authority);

        return userRepository.save(user);
    }

    public Authentication authenticate(LoginRequest request) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalize(request.email()),request.password()));
    }

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Пользователь не найден"));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}


