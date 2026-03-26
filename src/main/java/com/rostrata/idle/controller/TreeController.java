package com.rostrata.idle.controller;

import com.rostrata.idle.tree.Tree;
import com.rostrata.idle.tree.TreeCreateRequest;
import com.rostrata.idle.tree.TreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.rostrata.idle.auth.CustomUserDetails;

import java.util.List;

@RestController
public class TreeController {
    private final TreeService treeService;

    public TreeController(TreeService treeService) {
        this.treeService = treeService;
    }

    @GetMapping("/trees")
    public ResponseEntity<List<Tree>> getTrees() {
        return ResponseEntity.ok(treeService.getAllTrees());
    }

    @PostMapping("/trees")
    public ResponseEntity<Tree> createTree(@RequestBody TreeCreateRequest request) {
        Tree created = treeService.createOrUpdate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/trees/seed-defaults")
    public ResponseEntity<List<Tree>> seedDefaults() {
        List<Tree> trees = treeService.seedDefaultTrees();
        return ResponseEntity.status(HttpStatus.CREATED).body(trees);
    }

    @PostMapping("/trees/{treeId}/chop")
    public ResponseEntity<TreeService.ChopResult> chopTree(
            @PathVariable Long treeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        TreeService.ChopResult result = treeService.chopTree(userDetails.getUser(), treeId);
        return ResponseEntity.ok(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

