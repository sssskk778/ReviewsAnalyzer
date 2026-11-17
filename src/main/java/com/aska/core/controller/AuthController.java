package com.aska.core.controller;

import com.aska.core.dto.LoginRequest;
import com.aska.core.dto.RegisterRequest;
import com.aska.core.dto.UserResponse;
import com.aska.core.model.User;
import com.aska.core.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserResponse.from(userService.register(request));
    }

    @PostMapping("/login")
    public UserResponse login(@Valid @RequestBody LoginRequest request,
                              HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication authentication = userService.authenticate(request);

        if(httpRequest.getSession(false) != null){
            httpRequest.changeSessionId();
        }

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context,httpRequest,httpResponse);

        return UserResponse.from(userService.getByEmail(authentication.getName()));
    }

    @GetMapping("/me")
    public UserResponse me(Principal principal){
        return UserResponse.from(userService.getByEmail(principal.getName()));
    }
}
