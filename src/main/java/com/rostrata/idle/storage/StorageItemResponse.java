package com.rostrata.idle.storage;

/**
 * JSON returned from GET /storage.
 * Kept as a DTO (record) so it doesn't depend on any JPA entity shape.
 */
public record StorageItemResponse(
        String itemKey,
        String itemName,
        String itemImageUrl,
        Long quantity
) {
}

