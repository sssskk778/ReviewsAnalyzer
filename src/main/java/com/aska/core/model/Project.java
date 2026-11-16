package com.aska.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
@Entity
public class Project extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long projectId;

    @URL(message = "Please provide a valid URL")
    private String marketplaceUrl;

    private enum marketplace {
        WILDBERRIES,
        OZON
    }
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String productName;

    private String imageUrl;
    private int currentRating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",referencedColumnName = "userId", nullable = true)
    private User user;
}
