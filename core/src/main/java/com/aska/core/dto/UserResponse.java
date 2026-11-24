package com.aska.core.dto;

import com.aska.core.model.Tier;
import com.aska.core.model.User;

public record UserResponse(Long id, String email, String name, Tier tier) {
    public static UserResponse from(User user){
        return new UserResponse(user.getUserId(),user.getEmail(),user.getName(), user.getTier());
    }
}
