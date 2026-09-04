package com.avelrune.gateway.docs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class PostmanCollectionConverter {

    private static final Set<String> METHODS = Set.of("get", "post", "put", "patch", "delete", "head", "options");
    private static final String LOGIN_BODY = """
            {
              "email": "admin@avelrune.com",
              "password": "AvelruneAdmin2026!"
            }""";
    private static final String REGISTER_BODY = """
            {
              "fullName": "Yeni Kullanici",
              "email": "yeni@avelrune.com",
              "password": "AvelruneUser2026!"
            }""";
    static final String PROPERTY_BODY = """
            {
              "title": "Kadikoy'de Postman ile acilan 2+1",
              "description": "Bu ilan Postman uzerinden yaratildi. Salon sokağa bakiyor ve mutfak ayri duruyor.",
              "price": 12500000,
              "listingType": "SALE",
              "propertyType": "APARTMENT",
              "city": "İstanbul",
              "district": "Kadıköy",
              "neighborhood": "Caferağa",
              "grossArea": 110,
              "netArea": 95,
              "roomCount": "2+1",
              "buildingAge": 10,
              "floor": 3,
              "totalFloors": 6,
              "heatingType": "COMBI",
              "furnished": false,
              "latitude": 40.9903,
              "longitude": 29.0255,
              "featured": false,
              "active": true,
              "features": ["Asansör", "Balkon"],
              "agentId": 1,
              "assignedUserId": 2,
              "imageUrls": ["/images/properties/p01-1.jpg"]
            }""";
    static final String INQUIRY_BODY = """
            {
              "fullName": "Mehmet Demir",
              "phone": "05321234567",
              "email": "mehmet.demir@example.com",
              "message": "Bu ilan icin yerinde gormek istiyorum. Hafta sonu uygun musunuz?",
              "propertyId": 1
            }""";

    private PostmanCollectionConverter() {
    }

    public static ObjectNode fromOpenApi(JsonNode spec, ObjectMapper mapper) {
        ObjectNode collection = mapper.createObjectNode();
        ObjectNode info = collection.putObject("info");
        JsonNode openApiInfo = spec.path("info");
        info.put("name", text(openApiInfo, "title", "API"));
        info.put("description", text(openApiInfo, "description", ""));
        info.put("schema", "https://schema.getpostman.com/json/collection/v2.1.0/collection.json");

        String baseUrl = "http://localhost";
        JsonNode servers = spec.path("servers");
        if (servers.isArray() && !servers.isEmpty()) {
            String url = servers.get(0).path("url").asText(baseUrl).trim();
            if (!url.isEmpty() && !"/".equals(url)) {
                baseUrl = url.replaceAll("/+$", "");
            }
        }

        ArrayNode variables = collection.putArray("variable");
        addVariable(variables, "baseUrl", baseUrl);

        ObjectNode auth = collection.putObject("auth");
        auth.put("type", "noauth");

        ArrayNode items = collection.putArray("item");
        JsonNode paths = spec.path("paths");
        if (paths.isObject()) {
            Iterator<String> pathNames = paths.fieldNames();
            while (pathNames.hasNext()) {
                String path = pathNames.next();
                JsonNode ops = paths.get(path);
                if (ops == null || !ops.isObject()) {
                    continue;
                }
                Iterator<String> methods = ops.fieldNames();
                while (methods.hasNext()) {
                    String method = methods.next();
                    if (!METHODS.contains(method.toLowerCase(Locale.ROOT))) {
                        continue;
                    }
                    items.add(toItem(mapper, path, method, ops.get(method)));
                }
            }
        }
        return collection;
    }

    private static ObjectNode toItem(ObjectMapper mapper, String path, String method, JsonNode operation) {
        ObjectNode item = mapper.createObjectNode();
        String name = text(operation, "operationId", method.toUpperCase(Locale.ROOT) + " " + path);
        item.put("name", name);

        ObjectNode request = item.putObject("request");
        request.put("method", method.toUpperCase(Locale.ROOT));
        if (isAuthSessionPath(path)) {
            request.putObject("auth").put("type", "noauth");
        }
        ArrayNode headers = request.putArray("header");
        if (!isAuthSessionPath(path)) {
            ObjectNode accessToken = headers.addObject();
            accessToken.put("key", "X-Access-Token");
            accessToken.put("value", "{{token}}");
            accessToken.put("type", "text");
        }
        List<ObjectNode> pathVariables = new ArrayList<>();
        List<ObjectNode> queryParams = new ArrayList<>();

        JsonNode parameters = operation.path("parameters");
        if (parameters.isArray()) {
            for (JsonNode parameter : parameters) {
                String in = parameter.path("in").asText();
                String key = parameter.path("name").asText();
                if (key.isEmpty() || key.startsWith("X-Authenticated-")) {
                    continue;
                }
                if ("header".equals(in)) {
                    if ("authorization".equalsIgnoreCase(key) || "x-access-token".equalsIgnoreCase(key)) {
                        continue;
                    }
                    ObjectNode header = headers.addObject();
                    header.put("key", key);
                    header.put("value", "");
                    header.put("type", "text");
                } else if ("path".equals(in)) {
                    ObjectNode variable = mapper.createObjectNode();
                    variable.put("key", key);
                    variable.put("value", "");
                    pathVariables.add(variable);
                } else if ("query".equals(in)) {
                    ObjectNode query = mapper.createObjectNode();
                    query.put("key", key);
                    query.put("value", parameter.path("example").asText(""));
                    query.put("disabled", true);
                    queryParams.add(query);
                }
            }
        }

        JsonNode requestBody = operation.path("requestBody").path("content");
        if (requestBody.isObject() && requestBody.has("application/json")) {
            ObjectNode contentType = headers.addObject();
            contentType.put("key", "Content-Type");
            contentType.put("value", "application/json");
            contentType.put("type", "text");
            ObjectNode body = request.putObject("body");
            body.put("mode", "raw");
            body.put("raw", exampleJson(path, method));
            ObjectNode options = body.putObject("options").putObject("raw");
            options.put("language", "json");
        }

        request.set("url", toUrl(mapper, path, pathVariables, queryParams));
        return item;
    }

    private static boolean isAuthSessionPath(String path) {
        return path.endsWith("/login") || path.endsWith("/register");
    }

    static String exampleJson(String path, String method) {
        String verb = method.toLowerCase(Locale.ROOT);
        if (path.endsWith("/login")) {
            return LOGIN_BODY;
        }
        if (path.endsWith("/register")) {
            return REGISTER_BODY;
        }
        if ("post".equals(verb) && "/api/inquiries".equals(path)) {
            return INQUIRY_BODY;
        }
        if (("post".equals(verb) || "put".equals(verb)) && isPropertyWritePath(path)) {
            return PROPERTY_BODY;
        }
        return "{\n  \n}";
    }

    private static boolean isPropertyWritePath(String path) {
        return "/api/properties".equals(path)
                || "/api/admin/properties".equals(path)
                || "/api/properties/{id}".equals(path)
                || "/api/admin/properties/{id}".equals(path);
    }

    private static ObjectNode toUrl(
            ObjectMapper mapper,
            String path,
            List<ObjectNode> pathVariables,
            List<ObjectNode> queryParams
    ) {
        ObjectNode url = mapper.createObjectNode();
        String postmanPath = path.replaceAll("\\{([^/]+)\\}", ":$1");
        url.put("raw", "{{baseUrl}}" + postmanPath);
        url.putArray("host").add("{{baseUrl}}");
        ArrayNode segments = url.putArray("path");
        for (String segment : postmanPath.split("/")) {
            if (!segment.isEmpty()) {
                segments.add(segment);
            }
        }
        if (!pathVariables.isEmpty()) {
            ArrayNode variable = url.putArray("variable");
            pathVariables.forEach(variable::add);
        }
        if (!queryParams.isEmpty()) {
            ArrayNode query = url.putArray("query");
            queryParams.forEach(query::add);
        }
        return url;
    }

    private static void addVariable(ArrayNode variables, String key, String value) {
        ObjectNode variable = variables.addObject();
        variable.put("key", key);
        variable.put("value", value);
    }

    private static String text(JsonNode node, String field, String fallback) {
        String value = node.path(field).asText("");
        return value.isEmpty() ? fallback : value;
    }
}
