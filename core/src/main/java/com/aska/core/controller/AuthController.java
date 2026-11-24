package com.aska.core.controller;

import com.aska.core.constants.AppConstants;
import com.aska.core.dto.LoginRequest;
import com.aska.core.dto.RegisterRequest;
import com.aska.core.dto.UserResponse;
import com.aska.core.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserResponse.from(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {

        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(authenticationRequest);

        String jwt = userService.generateJwtToken(authentication);

        return ResponseEntity.ok()
                .header(AppConstants.JWT_HEADER, jwt)
                .body(UserResponse.from(userService.getByEmail(authentication.getName())));
    }

    @GetMapping("/me")
    public UserResponse me(Principal principal){
        return UserResponse.from(userService.getByEmail(principal.getName()));
    }
}
