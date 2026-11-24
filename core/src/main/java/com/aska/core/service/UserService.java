package com.aska.core.service;

import com.aska.core.constants.AppConstants;
import com.aska.core.dto.RegisterRequest;
import com.aska.core.exception.ConflictException;
import com.aska.core.exception.UnauthorizedException;
import com.aska.core.model.Authority;
import com.aska.core.model.User;
import com.aska.core.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(RegisterRequest request) {
        String email = normalize(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Пользователь с таким email уже существует");
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

    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Пользователь не найден"));
    }

    public String generateJwtToken(Authentication authentication) {
        SecretKey secretKey = Keys.hmacShaKeyFor(AppConstants.JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .issuer("Aska")
                .subject("JWT Token")
                .claim("username", authentication.getName())
                .claim("authorities", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + AppConstants.JWT_EXPIRATION_MS))
                .signWith(secretKey)
                .compact();
    }

    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}


