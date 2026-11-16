package com.aska.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class AnalysisAspect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long aspectId;
    private String name;
    private String category;
    private int mentionCount;
    private int positiveMentions;
    private int negativeMentions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", referencedColumnName = "analysisId", nullable = false)
    private Analysis analysis;
}
