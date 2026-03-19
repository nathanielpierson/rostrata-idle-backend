package com.rostrata.idle.tree;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TreeRepository extends JpaRepository<Tree, Long> {
    Optional<Tree> findByName(String name);
}

