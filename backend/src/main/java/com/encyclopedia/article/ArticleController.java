package com.encyclopedia.article;

import com.encyclopedia.article.dto.ArticleDto;
import com.encyclopedia.article.dto.RevisionDto;
import com.encyclopedia.article.dto.RevisionRequestDto;
import com.encyclopedia.auth.User;
import com.encyclopedia.common.ApiException;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{articleId}")
    public ArticleDto getArticle(@PathVariable UUID articleId) {
        return articleService.getArticleById(articleId);
    }

    @GetMapping("/{articleId}/revisions")
    public List<RevisionDto> getRevisionHistory(@PathVariable UUID articleId) {
        return articleService.getRevisionHistory(articleId);
    }

    @PostMapping("/{articleId}/revisions")
    public RevisionDto proposeRevision(@PathVariable UUID articleId,
                                       @Valid @RequestBody RevisionRequestDto dto,
                                       @AuthenticationPrincipal User author) {
        if (author == null) {
            throw ApiException.unauthorized("You must log in to edit articles");
        }
        return articleService.proposeRevision(articleId, dto.lang(), dto.content(), author);
    }
}
