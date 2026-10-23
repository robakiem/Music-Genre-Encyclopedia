package com.encyclopedia.complaint.dto;

import java.time.Instant;
import java.util.UUID;

public record ComplaintDto(
        UUID id,
        UUID articleId,
        UUID genreId,
        String genreName,
        String lang,
        String reason,
        String status,
        String authorName,
        Instant createdAt,
        Instant resolvedAt,
        String decision
) {
}
