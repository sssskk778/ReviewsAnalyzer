package com.aska.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ProjectRequest(
        @NotBlank(message = "URL is required")
        @URL(message = "Please provide a valid URL")
        @Size(max = 1000, message = "URL must not exceed 1000 characters")
        String marketplaceUrl,

        @NotBlank(message = "Product name is required")
        @Size(min = 3, max = 255, message = "Product name must be between 3 and 255 characters")
        String productName,

        @Size(max = 1000, message = "Image URL must not exceed 1000 characters")
        String imageUrl
) {
}
