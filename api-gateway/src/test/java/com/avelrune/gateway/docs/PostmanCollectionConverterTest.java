package com.avelrune.gateway.docs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostmanCollectionConverterTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void collectionUsesOnlyTokenAndFilledBodies() throws Exception {
        JsonNode spec = mapper.readTree("""
                {
                  "openapi": "3.0.0",
                  "info": { "title": "Avelrune Auth API" },
                  "servers": [{ "url": "http://localhost" }],
                  "paths": {
                    "/api/auth/login": {
                      "post": {
                        "operationId": "login",
                        "requestBody": { "content": { "application/json": {} } }
                      }
                    },
                    "/api/properties": {
                      "get": {
                        "operationId": "search",
                        "parameters": [
                          { "name": "city", "in": "query" },
                          { "name": "Authorization", "in": "header" },
                          { "name": "X-Authenticated-User-Id", "in": "header" }
                        ]
                      },
                      "post": {
                        "operationId": "create",
                        "requestBody": { "content": { "application/json": {} } }
                      }
                    },
                    "/api/inquiries": {
                      "post": {
                        "operationId": "createInquiry",
                        "requestBody": { "content": { "application/json": {} } }
                      }
                    }
                  }
                }
                """);

        ObjectNode collection = PostmanCollectionConverter.fromOpenApi(spec, mapper);
        String json = mapper.writeValueAsString(collection);

        assertFalse(json.contains("bearerToken"));
        assertFalse(json.contains("X-Authenticated"));
        assertFalse(collection.has("event"));
        assertEquals("noauth", collection.path("auth").path("type").asText());

        JsonNode variables = collection.path("variable");
        assertEquals("baseUrl", variables.get(0).path("key").asText());
        assertEquals(1, variables.size());

        JsonNode login = findItem(collection, "login");
        assertEquals("noauth", login.path("request").path("auth").path("type").asText());
        assertTrue(login.path("request").path("body").path("raw").asText().contains("admin@avelrune.com"));

        JsonNode search = findItem(collection, "search");
        assertEquals("city", search.path("request").path("url").path("query").get(0).path("key").asText());
        assertEquals("X-Access-Token", search.path("request").path("header").get(0).path("key").asText());
        assertEquals("{{token}}", search.path("request").path("header").get(0).path("value").asText());

        JsonNode create = findItem(collection, "create");
        assertTrue(create.path("request").path("body").path("raw").asText().contains("listingType"));

        JsonNode inquiry = findItem(collection, "createInquiry");
        assertTrue(inquiry.path("request").path("body").path("raw").asText().contains("propertyId"));
    }

    private static JsonNode findItem(ObjectNode collection, String name) {
        for (JsonNode item : collection.path("item")) {
            if (name.equals(item.path("name").asText())) {
                return item;
            }
        }
        throw new AssertionError("missing item " + name);
    }
}
