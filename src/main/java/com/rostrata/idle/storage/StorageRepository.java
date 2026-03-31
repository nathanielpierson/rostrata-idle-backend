package com.rostrata.idle.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StorageRepository extends JpaRepository<StorageItem, Long> {
    List<StorageItem> findAllByUserId(Long userId);

    Optional<StorageItem> findByUserIdAndItemKey(Long userId, String itemKey);
}

