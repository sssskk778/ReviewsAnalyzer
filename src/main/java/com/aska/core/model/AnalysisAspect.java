package com.aska.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class AnalysisAspect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aspectId;

    private String name;
    private String category;
    private int mentionCount;
    private int positiveMentions;
    private int negativeMentions;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "analysis_id", nullable = false)
    private Analysis analysis;
}
