package com.rostrata.idle.controller;

import com.rostrata.idle.auth.CustomUserDetails;
import com.rostrata.idle.storage.StorageItemResponse;
import com.rostrata.idle.storage.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StorageController {
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/storage")
    public ResponseEntity<List<StorageItemResponse>> getStorage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(storageService.getStorageForUser(userDetails.getUser()));
    }
}

