package com.rostrata.idle.tree;

/**
 * Tree creation/update request.
 *
 * timeToChop is accepted as strings like "10s", "1m", or "1m20s" and is converted to total seconds server-side.
 */
public record TreeCreateRequest(
        String name,
        String imageUrl,
        Integer levelRequirement,
        String timeToChop
) {
}

