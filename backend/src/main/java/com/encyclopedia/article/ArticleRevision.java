package com.encyclopedia.article;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "article_revisions")
public class ArticleRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "article_id", nullable = false)
    private UUID articleId;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    // Language this revision targets: "uk" or "en".
    @Column(nullable = false, length = 8)
    private String lang;

    // JSON object of the proposed content: { summary, sections, references }
    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RevisionStatus status = RevisionStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "reviewed_by")
    private UUID reviewedBy;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @Column(columnDefinition = "text")
    private String reason;

    protected ArticleRevision() {
    }

    public ArticleRevision(UUID articleId, UUID authorId, String lang, String content) {
        this.articleId = articleId;
        this.authorId = authorId;
        this.lang = lang;
        this.content = content;
        this.status = RevisionStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void markApproved(UUID adminId) {
        this.status = RevisionStatus.APPROVED;
        this.reviewedBy = adminId;
        this.reviewedAt = Instant.now();
    }

    public void markRejected(UUID adminId, String reason) {
        this.status = RevisionStatus.REJECTED;
        this.reviewedBy = adminId;
        this.reviewedAt = Instant.now();
        this.reason = reason;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArticleId() {
        return articleId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getLang() {
        return lang;
    }

    public String getContent() {
        return content;
    }

    public RevisionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getReviewedBy() {
        return reviewedBy;
    }

    public Instant getReviewedAt() {
        return reviewedAt;
    }

    public String getReason() {
        return reason;
    }
}
