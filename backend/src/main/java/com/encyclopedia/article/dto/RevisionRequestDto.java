package com.encyclopedia.article.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RevisionRequestDto(
        @NotNull @Pattern(regexp = "uk|en", message = "lang must be 'uk' or 'en'") String lang,
        @NotNull JsonNode content
) {
}
