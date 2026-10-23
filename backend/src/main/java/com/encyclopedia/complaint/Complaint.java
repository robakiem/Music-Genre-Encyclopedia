package com.encyclopedia.complaint;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The article being reported (the "report this article" target).
    @Column(name = "article_id", nullable = false)
    private UUID articleId;

    @Column(length = 8)
    private String lang;

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(columnDefinition = "text", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "resolved_by")
    private UUID resolvedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(columnDefinition = "text")
    private String decision;

    protected Complaint() {
    }

    public Complaint(UUID articleId, String lang, UUID authorId, String reason) {
        this.articleId = articleId;
        this.lang = lang;
        this.authorId = authorId;
        this.reason = reason;
        this.status = ComplaintStatus.OPEN;
        this.createdAt = Instant.now();
    }

    public void resolve(UUID adminId, String decision) {
        this.status = ComplaintStatus.RESOLVED;
        this.resolvedBy = adminId;
        this.resolvedAt = Instant.now();
        this.decision = decision;
    }

    public UUID getId() {
        return id;
    }

    public UUID getArticleId() {
        return articleId;
    }

    public String getLang() {
        return lang;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getReason() {
        return reason;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public UUID getResolvedBy() {
        return resolvedBy;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public String getDecision() {
        return decision;
    }
}
