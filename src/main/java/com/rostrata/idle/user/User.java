package com.rostrata.idle.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Long woodcuttingXp = 0L;

    @Column(nullable = false)
    private Long fishingXp = 0L;

    @Column(nullable = false)
    private Long miningXp = 0L;

    @Column(nullable = false)
    private Long huntingXp = 0L;

    protected User() {
    }

    public User(String email, String username, String passwordHash) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Long getWoodcuttingXp() {
        return woodcuttingXp;
    }

    public void setWoodcuttingXp(Long woodcuttingXp) {
        this.woodcuttingXp = woodcuttingXp;
    }

    public Long getFishingXp() {
        return fishingXp;
    }

    public void setFishingXp(Long fishingXp) {
        this.fishingXp = fishingXp;
    }

    public Long getMiningXp() {
        return miningXp;
    }

    public void setMiningXp(Long miningXp) {
        this.miningXp = miningXp;
    }

    public Long getHuntingXp() {
        return huntingXp;
    }

    public void setHuntingXp(Long huntingXp) {
        this.huntingXp = huntingXp;
    }
}

