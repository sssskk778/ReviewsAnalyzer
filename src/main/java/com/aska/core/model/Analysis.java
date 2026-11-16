package com.aska.core.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long analysisId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private int totalReviews;
    private int averageRating;
    private int positiveReviews;
    private int negativeReviews;
    private int neutralReviews;
    private String summary;
    private String recommendations;
    private String errorMessage;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "projectId", nullable = true)
    private Project project;
}
