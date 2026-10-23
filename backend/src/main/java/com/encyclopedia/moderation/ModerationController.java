package com.encyclopedia.moderation;

import com.encyclopedia.article.dto.RejectionDto;
import com.encyclopedia.article.dto.RevisionDto;
import com.encyclopedia.auth.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/moderation")
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @GetMapping("/revisions")
    public List<RevisionDto> getPendingRevisions() {
        return moderationService.getPendingRevisions();
    }

    @PostMapping("/revisions/{revisionId}/approve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void approve(@PathVariable UUID revisionId, @AuthenticationPrincipal User admin) {
        moderationService.approve(revisionId, admin);
    }

    @PostMapping("/revisions/{revisionId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable UUID revisionId,
                       @RequestBody(required = false) RejectionDto dto,
                       @AuthenticationPrincipal User admin) {
        moderationService.reject(revisionId, admin, dto == null ? null : dto.reason());
    }
}
