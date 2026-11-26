package com.aska.core.controller;

import com.aska.core.dto.ErrorResponse;
import com.aska.core.dto.UserResponse;
import com.aska.core.service.IUserService;
import com.aska.core.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "Пользователи", description = "Данные текущего пользователя")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final IUserService userService;

    @Operation(
            summary = "Текущий пользователь",
            description = "Возвращает данные пользователя, вошедшего в аккаунт"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные пользователя получены"),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется вход в аккаунт",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/me")
    public UserResponse me(Principal principal){
        return UserResponse.from(userService.getByEmail(principal.getName()));
    }
}