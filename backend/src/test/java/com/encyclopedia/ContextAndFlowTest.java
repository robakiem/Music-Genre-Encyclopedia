package com.encyclopedia;

import com.encyclopedia.article.*;
import com.encyclopedia.article.dto.RevisionDto;
import com.encyclopedia.auth.User;
import com.encyclopedia.auth.UserRepository;
import com.encyclopedia.auth.UserRole;
import com.encyclopedia.genre.GenreRepository;
import com.encyclopedia.genre.dto.GenreDto;
import com.encyclopedia.genre.GenreService;
import com.encyclopedia.moderation.ModerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ContextAndFlowTest {

    @Autowired GenreRepository genreRepository;
    @Autowired UserRepository userRepository;
    @Autowired GenreService genreService;
    @Autowired ArticleService articleService;
    @Autowired ModerationService moderationService;
    @Autowired ArticleRepository articleRepository;
    @Autowired ObjectMapper objectMapper;

    @Test
    void contextLoadsAndSeedsData() {
        assertThat(genreRepository.count()).isEqualTo(6);
        assertThat(userRepository.findByUsername("admin")).isPresent();
        assertThat(userRepository.findByUsername("admin").get().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void proposeAndApproveRevisionUpdatesArticle() throws Exception {
        GenreDto jazz = genreService.getBySlug("jazz");
        User author = userRepository.findByUsername("demo").orElseThrow();
        User admin = userRepository.findByUsername("admin").orElseThrow();

        var content = objectMapper.readTree(
                "{\"summary\":\"EDITED SUMMARY\",\"sections\":[],\"references\":[]}");

        RevisionDto rev = articleService.proposeRevision(
                jazz.article().id(), "uk", content, author);
        assertThat(rev.status()).isEqualTo("PENDING");

        assertThat(moderationService.getPendingRevisions()).hasSize(1);

        moderationService.approve(rev.id(), admin);

        Article updated = articleRepository.findByGenreId(jazz.id()).orElseThrow();
        assertThat(updated.getCurrentContent()).contains("EDITED SUMMARY");
        assertThat(moderationService.getPendingRevisions()).isEmpty();
    }
}
