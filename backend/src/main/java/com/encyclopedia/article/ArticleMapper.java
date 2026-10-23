package com.encyclopedia.article;

import com.encyclopedia.article.dto.ArticleDto;
import com.encyclopedia.article.dto.RevisionDto;
import com.encyclopedia.common.JsonSupport;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ArticleMapper {

    private final JsonSupport json;

    public ArticleMapper(JsonSupport json) {
        this.json = json;
    }

    public ArticleDto toDto(Article article) {
        return new ArticleDto(
                article.getId(),
                article.getGenreId(),
                article.getUpdatedAt(),
                json.read(article.getCurrentContent())
        );
    }

    public RevisionDto toDto(ArticleRevision rev, String authorName, UUID genreId, String genreName) {
        return new RevisionDto(
                rev.getId(),
                rev.getArticleId(),
                genreId,
                genreName,
                rev.getLang(),
                json.read(rev.getContent()),
                rev.getStatus().name(),
                authorName,
                rev.getCreatedAt(),
                rev.getReviewedAt(),
                rev.getReason()
        );
    }
}
