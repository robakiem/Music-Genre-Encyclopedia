package com.encyclopedia.genre.dto;

import com.encyclopedia.article.dto.ArticleDto;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record GenreDto(
        UUID id,
        String slug,
        NameDto name,
        JsonNode infobox,
        ArticleDto article
) {
}
