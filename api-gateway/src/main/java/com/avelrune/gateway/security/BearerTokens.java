package com.avelrune.gateway.security;

import org.springframework.http.HttpHeaders;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class BearerTokens {

    static final String ACCESS_TOKEN_HEADER = "X-Access-Token";

    private BearerTokens() {
    }

    static String extract(HttpHeaders headers) {
        List<String> tokens = candidates(headers);
        for (String token : tokens) {
            if (looksLikeJwt(token)) {
                return token;
            }
        }
        return tokens.isEmpty() ? null : tokens.get(0);
    }

    static List<String> candidates(HttpHeaders headers) {
        Set<String> unique = new LinkedHashSet<>();
        addNormalized(headers.get(ACCESS_TOKEN_HEADER), unique);
        addNormalized(headers.get(HttpHeaders.AUTHORIZATION), unique);
        return new ArrayList<>(unique);
    }

    private static void addNormalized(List<String> values, Set<String> unique) {
        if (values == null) {
            return;
        }
        for (String raw : values) {
            String token = normalize(raw);
            if (token.isEmpty() || token.startsWith("{{")) {
                continue;
            }
            unique.add(token);
        }
    }

    static String normalize(String raw) {
        if (raw == null || raw.isEmpty()) {
            return "";
        }
        String token = stripBom(raw).trim();
        token = stripMatchingQuotes(token);
        token = stripBearerPrefixes(token);
        token = token.replaceAll("\\s+", "");
        token = stripMatchingQuotes(token);
        token = stripBearerPrefixes(token);
        return token;
    }

    private static boolean looksLikeJwt(String token) {
        int firstDot = token.indexOf('.');
        int lastDot = token.lastIndexOf('.');
        return token.startsWith("eyJ") && firstDot > 0 && lastDot > firstDot;
    }

    private static String stripBom(String value) {
        if (!value.isEmpty() && value.charAt(0) == '\uFEFF') {
            return value.substring(1);
        }
        return value;
    }

    private static String stripMatchingQuotes(String token) {
        while (token.length() >= 2) {
            char first = token.charAt(0);
            char last = token.charAt(token.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                token = token.substring(1, token.length() - 1).trim();
                continue;
            }
            break;
        }
        return token;
    }

    private static String stripBearerPrefixes(String token) {
        while (token.length() >= 6 && token.regionMatches(true, 0, "Bearer", 0, 6)) {
            String rest = token.substring(6);
            if (rest.isEmpty()) {
                return "";
            }
            char next = rest.charAt(0);
            if (Character.isWhitespace(next) || next == ':' || next == '=') {
                token = rest.substring(1).trim();
                continue;
            }
            token = rest.trim();
        }
        return token;
    }
}
