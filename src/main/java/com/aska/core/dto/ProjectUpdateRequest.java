package com.aska.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectUpdateRequest(
        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
        String productName,

        @Size(max = 1000, message = "Image URL must not exceed 1000 characters")
        String imageUrl
) {
}
