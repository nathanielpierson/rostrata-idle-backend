package com.rostrata.idle.controller;

import com.rostrata.idle.tree.Tree;
import com.rostrata.idle.tree.TreeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TreeController {

    private final TreeRepository treeRepository;

    public TreeController(TreeRepository treeRepository) {
        this.treeRepository = treeRepository;
    }

    @GetMapping("/trees")
    public ResponseEntity<List<Tree>> getTrees() {
        List<Tree> trees = treeRepository.findAll();
        return ResponseEntity.ok(trees);
    }
}

