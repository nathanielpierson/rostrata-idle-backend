package com.rostrata.idle.controller;

import com.rostrata.idle.tree.Tree;
import com.rostrata.idle.tree.TreeCreateRequest;
import com.rostrata.idle.tree.TreeService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

