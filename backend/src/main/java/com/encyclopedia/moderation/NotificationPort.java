package com.encyclopedia.moderation;

import com.encyclopedia.article.ArticleRevision;

// Outbound port: notify a revision's author of the moderation decision.
// Swap the adapter for email/push later without touching the service.
public interface NotificationPort {
    void notifyAuthor(ArticleRevision revision, String decision);
}
