package com.encyclopedia.config;

import com.encyclopedia.article.Article;
import com.encyclopedia.article.ArticleRepository;
import com.encyclopedia.auth.User;
import com.encyclopedia.auth.UserRepository;
import com.encyclopedia.auth.UserRole;
import com.encyclopedia.genre.Genre;
import com.encyclopedia.genre.GenreRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final ArticleRepository articleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public DataInitializer(UserRepository userRepository,
                           GenreRepository genreRepository,
                           ArticleRepository articleRepository,
                           PasswordEncoder passwordEncoder,
                           ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.genreRepository = genreRepository;
        this.articleRepository = articleRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        seedUsers();
        seedGenres();
    }

    private void seedUsers() {
        if (userRepository.count() > 0) {
            return;
        }
        userRepository.save(new User("admin", "admin@encyclopedia.local",
                passwordEncoder.encode("admin"), UserRole.ADMIN));
        userRepository.save(new User("demo", "demo@encyclopedia.local",
                passwordEncoder.encode("demo"), UserRole.USER));
        log.info("Seeded default users: admin/admin (ADMIN), demo/demo (USER)");
    }

    private void seedGenres() throws Exception {
        if (genreRepository.count() > 0) {
            return;
        }
        ClassPathResource resource = new ClassPathResource("seed/genres.json");
        try (InputStream in = resource.getInputStream()) {
            JsonNode root = objectMapper.readTree(in);
            for (JsonNode node : root) {
                String infobox = objectMapper.writeValueAsString(node.get("infobox"));
                String content = objectMapper.writeValueAsString(node.get("content"));
                Genre genre = new Genre(
                        node.get("slug").asText(),
                        node.get("nameUk").asText(),
                        node.get("nameEn").asText(),
                        infobox);
                genre = genreRepository.save(genre);
                articleRepository.save(new Article(genre.getId(), content));
            }
            log.info("Seeded {} genres with articles", root.size());
        }
    }
}
