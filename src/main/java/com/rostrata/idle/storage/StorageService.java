package com.rostrata.idle.storage;

import com.rostrata.idle.user.User;
import com.rostrata.idle.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StorageService {

    private final StorageRepository storageRepository;
    private final UserRepository userRepository;

    public StorageService(StorageRepository storageRepository, UserRepository userRepository) {
        this.storageRepository = storageRepository;
        this.userRepository = userRepository;
    }

    public List<StorageItemResponse> getStorageForUser(User user) {
        List<StorageItem> items = storageRepository.findAllByUserId(user.getId());
        return items.stream()
                .map(i -> new StorageItemResponse(i.getItemKey(), i.getItemName(), i.getItemImageUrl(), i.getQuantity()))
                .toList();
    }

    @Transactional
    public StorageDelta addToStorage(User user, String itemKey, String itemName, String itemImageUrl, long quantityToAdd) {
        if (quantityToAdd <= 0) {
            throw new IllegalArgumentException("quantityToAdd must be > 0");
        }
        if (itemKey == null || itemKey.trim().isEmpty()) {
            throw new IllegalArgumentException("itemKey is required");
        }

        // Reload user defensively: some endpoints might pass a detached entity.
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getId()));

        String key = itemKey.trim();
        String name = itemName == null ? key : itemName.trim();
        String imageUrl = itemImageUrl == null ? "" : itemImageUrl.trim();

        StorageItem item = storageRepository.findByUserIdAndItemKey(managedUser.getId(), key)
                .orElseGet(() -> new StorageItem(managedUser.getId(), key, name, imageUrl, 0L));

        // If the server's item definition changed since last time, keep storage in sync.
        item.setItemName(name);
        item.setItemImageUrl(imageUrl);

        long before = item.getQuantity() == null ? 0L : item.getQuantity();
        long after = before + quantityToAdd;
        item.setQuantity(after);
        StorageItem saved = storageRepository.save(item);

        return new StorageDelta(
                saved.getItemKey(),
                saved.getItemName(),
                saved.getItemImageUrl(),
                quantityToAdd,
                saved.getQuantity()
        );
    }
}

