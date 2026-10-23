package com.encyclopedia.article.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record ArticleDto(
        UUID id,
        UUID genreId,
        Instant updatedAt,
        JsonNode content
) {
}
