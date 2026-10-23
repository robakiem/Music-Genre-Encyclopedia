package com.encyclopedia.genre;

import com.encyclopedia.article.Article;
import com.encyclopedia.article.ArticleMapper;
import com.encyclopedia.common.JsonSupport;
import com.encyclopedia.genre.dto.GenreDto;
import com.encyclopedia.genre.dto.GenreSummaryDto;
import com.encyclopedia.genre.dto.NameDto;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    private final JsonSupport json;
    private final ArticleMapper articleMapper;

    public GenreMapper(JsonSupport json, ArticleMapper articleMapper) {
        this.json = json;
        this.articleMapper = articleMapper;
    }

    public GenreSummaryDto toSummary(Genre genre) {
        return new GenreSummaryDto(
                genre.getId(),
                genre.getSlug(),
                new NameDto(genre.getNameUk(), genre.getNameEn())
        );
    }

    public GenreDto toDto(Genre genre, Article article) {
        return new GenreDto(
                genre.getId(),
                genre.getSlug(),
                new NameDto(genre.getNameUk(), genre.getNameEn()),
                json.read(genre.getInfobox()),
                article == null ? null : articleMapper.toDto(article)
        );
    }
}
