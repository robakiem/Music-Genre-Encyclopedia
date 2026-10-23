package com.encyclopedia.moderation;

import com.encyclopedia.article.*;
import com.encyclopedia.article.dto.RevisionDto;
import com.encyclopedia.auth.User;
import com.encyclopedia.auth.UserRepository;
import com.encyclopedia.common.ApiException;
import com.encyclopedia.common.JsonSupport;
import com.encyclopedia.genre.Genre;
import com.encyclopedia.genre.GenreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ModerationServiceImpl implements ModerationService {

    private final ArticleRevisionRepository revisionRepository;
    private final ArticleRepository articleRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;
    private final NotificationPort notificationPort;
    private final ArticleMapper mapper;
    private final JsonSupport json;

    public ModerationServiceImpl(ArticleRevisionRepository revisionRepository,
                                 ArticleRepository articleRepository,
                                 GenreRepository genreRepository,
                                 UserRepository userRepository,
                                 NotificationPort notificationPort,
                                 ArticleMapper mapper,
                                 JsonSupport json) {
        this.revisionRepository = revisionRepository;
        this.articleRepository = articleRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
        this.notificationPort = notificationPort;
        this.mapper = mapper;
        this.json = json;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RevisionDto> getPendingRevisions() {
        return revisionRepository.findByStatusOrderByCreatedAtAsc(RevisionStatus.PENDING).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void approve(UUID revisionId, User admin) {
        ArticleRevision revision = requirePending(revisionId);
        Article article = articleRepository.findById(revision.getArticleId())
                .orElseThrow(() -> ApiException.notFound("Article not found: " + revision.getArticleId()));

        // Apply the proposed content to the article's chosen language.
        ObjectNode content = asObject(article.getCurrentContent());
        content.set(revision.getLang(), json.read(revision.getContent()));
        article.setCurrentContent(json.write(content));
        article.setUpdatedAt(Instant.now());
        articleRepository.save(article);

        revision.markApproved(admin.getId());
        revisionRepository.save(revision);
        notificationPort.notifyAuthor(revision, "APPROVED");
    }

    @Override
    @Transactional
    public void reject(UUID revisionId, User admin, String reason) {
        ArticleRevision revision = requirePending(revisionId);
        revision.markRejected(admin.getId(), reason);
        revisionRepository.save(revision);
        notificationPort.notifyAuthor(revision, "REJECTED");
    }

    private ArticleRevision requirePending(UUID revisionId) {
        ArticleRevision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> ApiException.notFound("Revision not found: " + revisionId));
        if (revision.getStatus() != RevisionStatus.PENDING) {
            throw ApiException.badRequest("Revision has already been reviewed");
        }
        return revision;
    }

    private ObjectNode asObject(String storedJson) {
        JsonNode node = json.read(storedJson);
        if (node instanceof ObjectNode objectNode) {
            return objectNode;
        }
        return json.mapper().createObjectNode();
    }

    private RevisionDto toDto(ArticleRevision rev) {
        String authorName = userRepository.findById(rev.getAuthorId())
                .map(User::getUsername).orElse("—");
        Genre genre = articleRepository.findById(rev.getArticleId())
                .flatMap(a -> genreRepository.findById(a.getGenreId()))
                .orElse(null);
        UUID genreId = genre == null ? null : genre.getId();
        String genreName = genre == null ? null : genre.getNameUk();
        return mapper.toDto(rev, authorName, genreId, genreName);
    }
}
