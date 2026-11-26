package com.aska.core.service.impl;

import com.aska.core.exception.UnauthorizedException;
import com.aska.core.model.User;
import com.aska.core.repository.UserRepository;
import com.aska.core.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public void ensureExists(String email, String name){
        String normalisedEmail = normalize(email);
        if(userRepository.existsByEmail(normalisedEmail)){
            return;
        }
        User user = new User();
        user.setEmail(normalisedEmail);
        user.setName(name);
        userRepository.save(user);
    }


    @Transactional(readOnly = true)
    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Пользователь не найден"));
    }


    private String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}


