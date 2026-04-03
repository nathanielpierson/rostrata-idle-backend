package com.rostrata.idle.fish;

/**
 * Fish creation/update request.
 *
 * minTimeToFish and maxTimeToFish accept strings like "10s", "1m", or "1m20s", or plain seconds ("7"),
 * and are converted to total seconds server-side. The catch duration is a random integer in
 * [{@code min}, {@code max}] inclusive (chosen on the client per attempt).
 */
public record FishCreateRequest(
        String name,
        String imageUrl,
        Integer levelRequirement,
        String minTimeToFish,
        String maxTimeToFish,
        Integer xpGiven
) {
}
