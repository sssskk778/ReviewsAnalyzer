package com.aska.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Analysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status = AnalysisStatus.PENDING;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private int totalReviews;
    private double averageRating;
    private int positiveReviews;
    private int negativeReviews;
    private int neutralReviews;

    @Column(columnDefinition = "TEXT")
    private String summary;
    @Column(columnDefinition = "TEXT")
    private String recommendations;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisAspect> aspects = new ArrayList<>();

}
