package com.encyclopedia.genre;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface GenreRepository extends JpaRepository<Genre, UUID> {
    Optional<Genre> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
