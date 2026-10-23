package com.encyclopedia.genre;

import com.encyclopedia.genre.dto.GenreDto;
import com.encyclopedia.genre.dto.GenreRequestDto;
import com.encyclopedia.genre.dto.GenreSummaryDto;

import java.util.List;
import java.util.UUID;

public interface GenreService {
    List<GenreSummaryDto> listGenres();
    GenreDto getBySlug(String slug);
    GenreDto create(GenreRequestDto request);
    GenreDto update(UUID id, GenreRequestDto request);
    void delete(UUID id);
}
