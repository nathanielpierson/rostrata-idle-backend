package com.rostrata.idle.storage;

/**
 * Delta returned along with a chop/loot action so the frontend can update storage instantly.
 */
public record StorageDelta(
        String itemKey,
        String itemName,
        String itemImageUrl,
        Long quantityAdded,
        Long newQuantity
) {
}

