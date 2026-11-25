package com.aska.core.dto;

import java.util.Map;

public record ApiError(int status, String message, Map<String, String> errors) {

    public static ApiError of(int status, String message) {
        return new ApiError(status, message, Map.of());
    }
}
