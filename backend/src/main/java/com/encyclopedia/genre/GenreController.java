package com.encyclopedia.genre;

import com.encyclopedia.genre.dto.GenreDto;
import com.encyclopedia.genre.dto.GenreRequestDto;
import com.encyclopedia.genre.dto.GenreSummaryDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreSummaryDto> list() {
        return genreService.listGenres();
    }

    @GetMapping("/{slug}")
    public GenreDto getBySlug(@PathVariable String slug) {
        return genreService.getBySlug(slug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreDto create(@Valid @RequestBody GenreRequestDto request) {
        return genreService.create(request);
    }

    @PutMapping("/{id}")
    public GenreDto update(@PathVariable UUID id, @Valid @RequestBody GenreRequestDto request) {
        return genreService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        genreService.delete(id);
    }
}
