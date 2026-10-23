package com.encyclopedia.moderation;

import com.encyclopedia.article.ArticleRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingNotificationAdapter implements NotificationPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingNotificationAdapter.class);

    @Override
    public void notifyAuthor(ArticleRevision revision, String decision) {
        log.info("Notify author {}: revision {} was {}",
                revision.getAuthorId(), revision.getId(), decision);
    }
}
