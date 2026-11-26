package com.aska.core.model;

import com.aska.core.enums.Marketplace;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Project extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false, length = 1000)
    private String marketplaceUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Marketplace marketplace;

    @Column(nullable = false)
    private String productName;

    @Column(length = 1000)
    private String imageUrl;

    private double currentRating;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Analysis> analyses = new ArrayList<>();
}
