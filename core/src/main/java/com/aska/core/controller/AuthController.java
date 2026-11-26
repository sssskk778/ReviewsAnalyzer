package com.aska.core.controller;

import com.aska.core.dto.UserResponse;
import com.aska.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(Principal principal){
        return UserResponse.from(userService.getByEmail(principal.getName()));
    }
}
