package com.encyclopedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApplicationApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void publicBrowsingAndMissingResourcesWork() throws Exception {
        mockMvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(6)));

        mockMvc.perform(get("/api/genres/jazz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is("jazz")))
                .andExpect(jsonPath("$.article.id").exists());

        mockMvc.perform(get("/api/genres/does-not-exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void registrationLoginAndCurrentUserWork() throws Exception {
        String username = "user" + UUID.randomUUID().toString().substring(0, 8);
        String registration = """
                {"username":"%s","email":"%s@example.com","password":"secret"}
                """.formatted(username, username);

        String token = mockMvc.perform(post("/api/auth/register")
                        .contentType(APPLICATION_JSON).content(registration))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.role", is("USER")))
                .andReturn().getResponse().getContentAsString();
        token = objectMapper.readTree(token).get("token").asText();

        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(username)))
                .andExpect(jsonPath("$.token").doesNotExist());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"demo\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.token").isString());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anonymousAndUserAccessAreRestrictedByRole() throws Exception {
        String articleId = articleId();
        String userToken = login("demo", "demo");

        mockMvc.perform(post("/api/genres").contentType(APPLICATION_JSON).content(genreJson("new-genre")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/moderation/revisions"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/genres").header("Authorization", bearer(userToken))
                        .contentType(APPLICATION_JSON).content(genreJson("new-genre")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/moderation/revisions").header("Authorization", bearer(userToken)))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/articles/" + articleId + "/revisions")
                        .header("Authorization", bearer(userToken))
                        .contentType(APPLICATION_JSON)
                        .content("{\"lang\":\"uk\",\"content\":{\"summary\":\"updated\"}}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    void adminCanCreateUpdateAndDeleteGenres() throws Exception {
        String token = login("admin", "admin");
        String slug = "test-" + UUID.randomUUID().toString().substring(0, 8);

        String created = mockMvc.perform(post("/api/genres")
                        .header("Authorization", bearer(token))
                        .contentType(APPLICATION_JSON).content(genreJson(slug)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug", is(slug)))
                .andReturn().getResponse().getContentAsString();
        String id = objectMapper.readTree(created).get("id").asText();

        mockMvc.perform(put("/api/genres/" + id).header("Authorization", bearer(token))
                        .contentType(APPLICATION_JSON).content(genreJson(slug + "-updated")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug", is(slug + "-updated")));

        mockMvc.perform(delete("/api/genres/" + id).header("Authorization", bearer(token)))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/genres/" + slug + "-updated"))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminCanApproveAndRejectRevisions() throws Exception {
        String articleId = articleId();
        String userToken = login("demo", "demo");
        String adminToken = login("admin", "admin");

        String approved = propose(articleId, userToken, "approved");
        mockMvc.perform(get("/api/moderation/revisions").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(post("/api/moderation/revisions/" + approved + "/approve")
                        .header("Authorization", bearer(adminToken)))
                .andExpect(status().isNoContent());

        String rejected = propose(articleId, userToken, "rejected");
        mockMvc.perform(post("/api/moderation/revisions/" + rejected + "/reject")
                        .header("Authorization", bearer(adminToken))
                        .contentType(APPLICATION_JSON).content("{\"reason\":\"Insufficient sources\"}"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/articles/" + articleId + "/revisions"))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status").isString());
        mockMvc.perform(get("/api/moderation/revisions").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void usersCanFileComplaintsAndAdminsCanResolveThem() throws Exception {
        String articleId = articleId();
        String userToken = login("demo", "demo");
        String adminToken = login("admin", "admin");

        String complaint = mockMvc.perform(post("/api/complaints")
                        .header("Authorization", bearer(userToken)).contentType(APPLICATION_JSON)
                        .content("{\"articleId\":\"" + articleId + "\",\"lang\":\"uk\",\"reason\":\"Incorrect source\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andReturn().getResponse().getContentAsString();
        String complaintId = objectMapper.readTree(complaint).get("id").asText();

        mockMvc.perform(get("/api/complaints").header("Authorization", bearer(userToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/complaints?status=OPEN").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
        mockMvc.perform(post("/api/complaints/" + complaintId + "/resolve")
                        .header("Authorization", bearer(adminToken)).contentType(APPLICATION_JSON)
                        .content("{\"decision\":\"Fixed\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void validationAndInvalidRevisionLanguageReturnBadRequest() throws Exception {
        String userToken = login("demo", "demo");
        String articleId = articleId();

        mockMvc.perform(post("/api/auth/register").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"x\",\"email\":\"bad\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status", is(400)));
        mockMvc.perform(post("/api/articles/" + articleId + "/revisions")
                        .header("Authorization", bearer(userToken)).contentType(APPLICATION_JSON)
                        .content("{\"lang\":\"fr\",\"content\":{}}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/complaints").header("Authorization", bearer(userToken))
                        .contentType(APPLICATION_JSON).content("{\"articleId\":\"" + articleId + "\",\"reason\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    private String articleId() throws Exception {
        String response = mockMvc.perform(get("/api/genres/jazz"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("article").get("id").asText();
    }

    private String login(String username, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login").contentType(APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    private String propose(String articleId, String token, String summary) throws Exception {
        String response = mockMvc.perform(post("/api/articles/" + articleId + "/revisions")
                        .header("Authorization", bearer(token)).contentType(APPLICATION_JSON)
                        .content("{\"lang\":\"uk\",\"content\":{\"summary\":\"" + summary + "\"}}"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }

    private String genreJson(String slug) {
        return "{\"slug\":\"" + slug + "\",\"nameUk\":\"Test\",\"nameEn\":\"Test\","
                + "\"content\":{\"uk\":{\"summary\":\"Test\",\"sections\":[],\"references\":[]}}}";
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}