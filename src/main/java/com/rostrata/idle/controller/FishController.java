package com.rostrata.idle.controller;

import com.rostrata.idle.auth.CustomUserDetails;
import com.rostrata.idle.fish.Fish;
import com.rostrata.idle.fish.FishCreateRequest;
import com.rostrata.idle.fish.FishService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FishController {

    private final FishService fishService;

    public FishController(FishService fishService) {
        this.fishService = fishService;
    }

    @GetMapping("/fish")
    public ResponseEntity<List<Fish>> getFish() {
        return ResponseEntity.ok(fishService.getAllFish());
    }

    @PostMapping("/fish")
    public ResponseEntity<Fish> createFish(@RequestBody FishCreateRequest request) {
        Fish created = fishService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/fish/seed-defaults")
    public ResponseEntity<List<Fish>> seedDefaults() {
        List<Fish> fish = fishService.seedDefaultFish();
        return ResponseEntity.status(HttpStatus.CREATED).body(fish);
    }

    @PostMapping("/fish/{fishId}/catch")
    public ResponseEntity<FishService.CatchResult> catchFish(
            @PathVariable Long fishId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        FishService.CatchResult result = fishService.catchFish(userDetails.getUser(), fishId);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
