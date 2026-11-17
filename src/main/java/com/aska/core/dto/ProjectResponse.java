package com.aska.core.dto;

import com.aska.core.model.Marketplace;
import com.aska.core.model.Project;

import java.time.LocalDateTime;

public record ProjectResponse(Long id, String marketplaceUrl,
                              Marketplace marketplace, String productName,
                              String imageUrl, double currentRating,
                              LocalDateTime createdAt, LocalDateTime updatedAt
) {
    public static ProjectResponse from(Project project){
        return new ProjectResponse(project.getProjectId(),project.getMarketplaceUrl(),
                project.getMarketplace(), project.getProductName(), project.getImageUrl(),
                project.getCurrentRating(), project.getCreatedAt(), project.getUpdatedAt());
    }
}
