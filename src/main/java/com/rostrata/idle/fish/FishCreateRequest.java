package com.rostrata.idle.fish;

/**
 * Fish creation/update request.
 *
 * timeToFish is accepted as strings like "10s", "1m", or "1m20s" and is converted to total seconds server-side.
 */
public record FishCreateRequest(
        String name,
        String imageUrl,
        Integer levelRequirement,
        String timeToFish,
        Integer xpGiven
) {
}
