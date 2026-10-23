package com.encyclopedia.genre;

import com.encyclopedia.article.Article;
import com.encyclopedia.article.ArticleRepository;
import com.encyclopedia.article.ArticleRevision;
import com.encyclopedia.article.ArticleRevisionRepository;
import com.encyclopedia.common.ApiException;
import com.encyclopedia.common.JsonSupport;
import com.encyclopedia.genre.dto.GenreDto;
import com.encyclopedia.genre.dto.GenreRequestDto;
import com.encyclopedia.genre.dto.GenreSummaryDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final ArticleRepository articleRepository;
    private final ArticleRevisionRepository revisionRepository;
    private final GenreMapper mapper;
    private final JsonSupport json;

    public GenreServiceImpl(GenreRepository genreRepository,
                            ArticleRepository articleRepository,
                            ArticleRevisionRepository revisionRepository,
                            GenreMapper mapper,
                            JsonSupport json) {
        this.genreRepository = genreRepository;
        this.articleRepository = articleRepository;
        this.revisionRepository = revisionRepository;
        this.mapper = mapper;
        this.json = json;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GenreSummaryDto> listGenres() {
        return genreRepository.findAll().stream()
                .sorted(Comparator.comparing(Genre::getNameUk))
                .map(mapper::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GenreDto getBySlug(String slug) {
        Genre genre = genreRepository.findBySlug(slug)
                .orElseThrow(() -> ApiException.notFound("Genre not found: " + slug));
        Article article = articleRepository.findByGenreId(genre.getId()).orElse(null);
        return mapper.toDto(genre, article);
    }

    @Override
    @Transactional
    public GenreDto create(GenreRequestDto request) {
        if (genreRepository.existsBySlug(request.slug())) {
            throw ApiException.conflict("A genre with this slug already exists");
        }
        Genre genre = new Genre(
                request.slug(),
                request.nameUk(),
                request.nameEn(),
                request.infobox() == null ? "[]" : json.write(request.infobox()));
        genre = genreRepository.save(genre);

        String content = request.content() == null ? "{}" : json.write(request.content());
        Article article = new Article(genre.getId(), content);
        article = articleRepository.save(article);

        return mapper.toDto(genre, article);
    }

    @Override
    @Transactional
    public GenreDto update(UUID id, GenreRequestDto request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Genre not found: " + id));
        genre.setSlug(request.slug());
        genre.setNameUk(request.nameUk());
        genre.setNameEn(request.nameEn());
        if (request.infobox() != null) {
            genre.setInfobox(json.write(request.infobox()));
        }
        genre = genreRepository.save(genre);

        Article article = articleRepository.findByGenreId(genre.getId()).orElse(null);
        if (request.content() != null) {
            if (article == null) {
                article = new Article(genre.getId(), json.write(request.content()));
            } else {
                article.setCurrentContent(json.write(request.content()));
                article.setUpdatedAt(Instant.now());
            }
            article = articleRepository.save(article);
        }
        return mapper.toDto(genre, article);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Genre not found: " + id));
        articleRepository.findByGenreId(genre.getId()).ifPresent(article -> {
            List<ArticleRevision> revisions = revisionRepository.findByArticleIdOrderByCreatedAtDesc(article.getId());
            revisionRepository.deleteAll(revisions);
            articleRepository.delete(article);
        });
        genreRepository.delete(genre);
    }
}
