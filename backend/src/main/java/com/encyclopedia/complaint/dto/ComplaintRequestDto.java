package com.encyclopedia.complaint.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ComplaintRequestDto(
        @NotNull UUID articleId,
        String lang,
        @NotBlank String reason
) {
}
