package com.encyclopedia.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JsonSupport {

    private final ObjectMapper objectMapper;

    public JsonSupport(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** Parse a stored TEXT column into a JsonNode (null/blank -> NullNode). */
    public JsonNode read(String json) {
        if (json == null || json.isBlank()) {
            return objectMapper.nullNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Stored content is not valid JSON");
        }
    }

    /** Serialize any value (JsonNode or POJO) to a compact JSON string for storage. */
    public String write(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw ApiException.badRequest("Content could not be serialized to JSON");
        }
    }

    public ObjectMapper mapper() {
        return objectMapper;
    }
}
