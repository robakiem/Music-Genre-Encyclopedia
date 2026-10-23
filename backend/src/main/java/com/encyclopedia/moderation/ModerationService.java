package com.encyclopedia.moderation;

import com.encyclopedia.article.dto.RevisionDto;
import com.encyclopedia.auth.User;

import java.util.List;
import java.util.UUID;

public interface ModerationService {
    List<RevisionDto> getPendingRevisions();
    void approve(UUID revisionId, User admin);
    void reject(UUID revisionId, User admin, String reason);
}
