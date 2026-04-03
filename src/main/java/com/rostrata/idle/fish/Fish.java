package com.rostrata.idle.fish;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fish")
public class Fish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "level_required", nullable = false)
    private Integer levelRequirement;

    @Column(name = "min_time_to_fish", nullable = false)
    private Integer minSecondsToFish;

    @Column(name = "max_time_to_fish", nullable = false)
    private Integer maxSecondsToFish;

    @Column(name = "xp_given", nullable = false)
    private Integer xpGiven;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    protected Fish() {
    }

    public Fish(
            String name,
            Integer levelRequirement,
            Integer minSecondsToFish,
            Integer maxSecondsToFish,
            Integer xpGiven,
            String imageUrl
    ) {
        this.name = name;
        this.levelRequirement = levelRequirement;
        this.minSecondsToFish = minSecondsToFish;
        this.maxSecondsToFish = maxSecondsToFish;
        this.xpGiven = xpGiven;
        this.imageUrl = imageUrl;
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

    public Integer getLevelRequirement() {
        return levelRequirement;
    }

    public void setLevelRequirement(Integer levelRequirement) {
        this.levelRequirement = levelRequirement;
    }

    public Integer getMinSecondsToFish() {
        return minSecondsToFish;
    }

    public void setMinSecondsToFish(Integer minSecondsToFish) {
        this.minSecondsToFish = minSecondsToFish;
    }

    public Integer getMaxSecondsToFish() {
        return maxSecondsToFish;
    }

    public void setMaxSecondsToFish(Integer maxSecondsToFish) {
        this.maxSecondsToFish = maxSecondsToFish;
    }

    public Integer getXpGiven() {
        return xpGiven;
    }

    public void setXpGiven(Integer xpGiven) {
        this.xpGiven = xpGiven;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
