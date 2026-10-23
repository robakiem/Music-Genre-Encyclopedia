package com.encyclopedia.article;

import com.encyclopedia.article.dto.ArticleDto;
import com.encyclopedia.article.dto.RevisionDto;
import com.encyclopedia.auth.User;
import com.encyclopedia.auth.UserRepository;
import com.encyclopedia.common.ApiException;
import com.encyclopedia.common.JsonSupport;
import com.encyclopedia.genre.Genre;
import com.encyclopedia.genre.GenreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleRevisionRepository revisionRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final ArticleMapper mapper;
    private final JsonSupport json;

    public ArticleServiceImpl(ArticleRepository articleRepository,
                              ArticleRevisionRepository revisionRepository,
                              GenreRepository genreRepository,
                              UserRepository userRepository,
                              ArticleMapper mapper,
                              JsonSupport json) {
        this.articleRepository = articleRepository;
        this.revisionRepository = revisionRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.json = json;
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto getArticleByGenre(UUID genreId) {
        Article article = articleRepository.findByGenreId(genreId)
                .orElseThrow(() -> ApiException.notFound("Article not found for genre " + genreId));
        return mapper.toDto(article);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDto getArticleById(UUID articleId) {
        return mapper.toDto(requireArticle(articleId));
    }

    @Override
    @Transactional
    public RevisionDto proposeRevision(UUID articleId, String lang, JsonNode content, User author) {
        Article article = requireArticle(articleId);
        ArticleRevision revision = new ArticleRevision(
                article.getId(), author.getId(), lang, json.write(content));
        revision = revisionRepository.save(revision);
        return toRevisionDto(revision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevisionDto> getRevisionHistory(UUID articleId) {
        requireArticle(articleId);
        return revisionRepository.findByArticleIdOrderByCreatedAtDesc(articleId).stream()
                .map(this::toRevisionDto)
                .toList();
    }

    private Article requireArticle(UUID articleId) {
        return articleRepository.findById(articleId)
                .orElseThrow(() -> ApiException.notFound("Article not found: " + articleId));
    }

    private RevisionDto toRevisionDto(ArticleRevision rev) {
        String authorName = userRepository.findById(rev.getAuthorId())
                .map(User::getUsername).orElse("—");
        Genre genre = articleRepository.findById(rev.getArticleId())
                .flatMap(a -> genreRepository.findById(a.getGenreId()))
                .orElse(null);
        UUID genreId = genre == null ? null : genre.getId();
        String genreName = genre == null ? null : genre.getNameUk();
        return mapper.toDto(rev, authorName, genreId, genreName);
    }
}
