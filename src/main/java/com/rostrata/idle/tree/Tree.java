package com.rostrata.idle.tree;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "trees")
public class Tree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private Integer secondsToChop;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false)
    private Integer levelRequirement;

    protected Tree() {
    }

    public Tree(String name, Integer secondsToChop, String imageUrl, Integer levelRequirement) {
        this.name = name;
        this.secondsToChop = secondsToChop;
        this.imageUrl = imageUrl;
        this.levelRequirement = levelRequirement;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSecondsToChop() {
        return secondsToChop;
    }

    public void setSecondsToChop(Integer secondsToChop) {
        this.secondsToChop = secondsToChop;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getLevelRequirement() {
        return levelRequirement;
    }

    public void setLevelRequirement(Integer levelRequirement) {
        this.levelRequirement = levelRequirement;
    }
}

