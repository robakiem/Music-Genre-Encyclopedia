package com.encyclopedia.article;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "genre_id", nullable = false, unique = true)
    private UUID genreId;

    // JSON object of bilingual content: { "uk": {summary, sections, references}, "en": {...} }
    @Column(name = "current_content", columnDefinition = "text")
    private String currentContent;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Article() {
    }

    public Article(UUID genreId, String currentContent) {
        this.genreId = genreId;
        this.currentContent = currentContent;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getGenreId() {
        return genreId;
    }

    public String getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(String currentContent) {
        this.currentContent = currentContent;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
