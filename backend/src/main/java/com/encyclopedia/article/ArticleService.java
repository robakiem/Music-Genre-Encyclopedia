package com.encyclopedia.article;

import com.encyclopedia.article.dto.ArticleDto;
import com.encyclopedia.article.dto.RevisionDto;
import com.encyclopedia.auth.User;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.UUID;

public interface ArticleService {
    ArticleDto getArticleByGenre(UUID genreId);
    ArticleDto getArticleById(UUID articleId);
    RevisionDto proposeRevision(UUID articleId, String lang, JsonNode content, User author);
    List<RevisionDto> getRevisionHistory(UUID articleId);
}
