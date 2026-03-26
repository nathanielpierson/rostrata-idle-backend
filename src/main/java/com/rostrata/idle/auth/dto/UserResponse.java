package com.rostrata.idle.auth.dto;

public class UserResponse {

    private Long id;
    private String email;
    private String username;
    private Long woodcuttingXp;
    private Long fishingXp;
    private Long miningXp;
    private Long huntingXp;

    public UserResponse(
            Long id,
            String email,
            String username,
            Long woodcuttingXp,
            Long fishingXp,
            Long miningXp,
            Long huntingXp
    ) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.woodcuttingXp = woodcuttingXp;
        this.fishingXp = fishingXp;
        this.miningXp = miningXp;
        this.huntingXp = huntingXp;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public Long getWoodcuttingXp() {
        return woodcuttingXp;
    }

    public Long getFishingXp() {
        return fishingXp;
    }

    public Long getMiningXp() {
        return miningXp;
    }

    public Long getHuntingXp() {
        return huntingXp;
    }
}

