package com.encyclopedia.article;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArticleRevisionRepository extends JpaRepository<ArticleRevision, UUID> {
    List<ArticleRevision> findByArticleIdOrderByCreatedAtDesc(UUID articleId);
    List<ArticleRevision> findByStatusOrderByCreatedAtAsc(RevisionStatus status);
}
