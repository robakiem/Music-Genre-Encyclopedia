package com.encyclopedia.genre.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;

public record GenreRequestDto(
        @NotBlank String slug,
        @NotBlank String nameUk,
        @NotBlank String nameEn,
        JsonNode infobox,
        JsonNode content
) {
}
