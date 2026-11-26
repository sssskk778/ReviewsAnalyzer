package com.aska.core.dto;

import com.aska.core.enums.Tier;
import com.aska.core.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Данные текущего пользователя")
public record UserResponse(

        @Schema(description = "Идентификатор пользователя", example = "1")
        Long id,

        @Schema(description = "Email пользователя", example = "user@example.com")
        String email,

        @Schema(description = "Имя пользователя", example = "Анна")
        String name,

        @Schema(description = "Тариф пользователя")
        Tier tier
) {
    public static UserResponse from(User user){
        return new UserResponse(user.getUserId(), user.getEmail(), user.getName(), user.getTier());
    }
}
