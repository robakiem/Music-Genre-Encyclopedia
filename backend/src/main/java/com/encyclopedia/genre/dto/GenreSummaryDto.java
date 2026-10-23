package com.encyclopedia.genre.dto;

import java.util.UUID;

public record GenreSummaryDto(
        UUID id,
        String slug,
        NameDto name
) {
}
