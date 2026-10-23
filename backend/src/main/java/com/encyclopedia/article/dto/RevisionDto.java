package com.encyclopedia.article.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record RevisionDto(
        UUID id,
        UUID articleId,
        UUID genreId,
        String genreName,
        String lang,
        JsonNode content,
        String status,
        String authorName,
        Instant createdAt,
        Instant reviewedAt,
        String reason
) {
}
