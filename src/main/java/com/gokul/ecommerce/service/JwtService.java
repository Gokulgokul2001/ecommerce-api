package com.gokul.ecommerce.service;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "my-super-secret-key-for-ecommerce-api-2026";

    private static final long EXPIRATION_TIME =
            1000L * 60 * 60; // 1 hour

    // =========================
    // GENERATE TOKEN
    // =========================

    public String generateToken(String email, String role) {

        long issuedAt = System.currentTimeMillis();

        long expiration = issuedAt + EXPIRATION_TIME;

        String header =
                "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

        String payload =
                "{"
                        + "\"sub\":\"" + email + "\","
                        + "\"role\":\"" + role + "\","
                        + "\"iat\":" + issuedAt + ","
                        + "\"exp\":" + expiration
                        + "}";

        String encodedHeader =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                header.getBytes(StandardCharsets.UTF_8)
                        );

        String encodedPayload =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(
                                payload.getBytes(StandardCharsets.UTF_8)
                        );

        String data =
                encodedHeader + "." + encodedPayload;

        String signature = generateSignature(data);

        return data + "." + signature;
    }

    // =========================
    // VALIDATE TOKEN
    // =========================

    public boolean validateToken(String token) {

        try {

            String[] parts = token.split("\\.");

            if (parts.length != 3) {
                return false;
            }

            String data =
                    parts[0] + "." + parts[1];

            String expectedSignature =
                    generateSignature(data);

            if (!expectedSignature.equals(parts[2])) {
                return false;
            }

            String payloadJson =
                    new String(
                            Base64.getUrlDecoder().decode(parts[1]),
                            StandardCharsets.UTF_8
                    );

            long expiration =
                    extractExpiration(payloadJson);

            return System.currentTimeMillis() < expiration;

        } catch (Exception exception) {

            return false;
        }
    }

    // =========================
    // EXTRACT EMAIL
    // =========================

    public String extractEmail(String token) {

        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            throw new RuntimeException("Invalid JWT token");
        }

        String payloadJson =
                new String(
                        Base64.getUrlDecoder().decode(parts[1]),
                        StandardCharsets.UTF_8
                );

        return extractValue(payloadJson, "sub");
    }

    // =========================
    // EXTRACT ROLE
    // =========================

    public String extractRole(String token) {

        String[] parts = token.split("\\.");

        if (parts.length != 3) {
            throw new RuntimeException("Invalid JWT token");
        }

        String payloadJson =
                new String(
                        Base64.getUrlDecoder().decode(parts[1]),
                        StandardCharsets.UTF_8
                );

        return extractValue(payloadJson, "role");
    }

    // =========================
    // EXTRACT EXPIRATION
    // =========================

    private long extractExpiration(String payloadJson) {

        String expiration =
                extractValue(payloadJson, "exp");

        return Long.parseLong(expiration);
    }

    // =========================
    // EXTRACT JSON VALUE
    // =========================

    private String extractValue(
            String json,
            String key) {

        String searchKey =
                "\"" + key + "\":";

        int start =
                json.indexOf(searchKey);

        if (start == -1) {
            throw new RuntimeException(
                    "JWT field not found: " + key
            );
        }

        start += searchKey.length();

        while (start < json.length()
                && json.charAt(start) == ' ') {

            start++;
        }

        if (json.charAt(start) == '"') {

            start++;

            int end =
                    json.indexOf('"', start);

            return json.substring(start, end);
        }

        int end = start;

        while (end < json.length()
                && json.charAt(end) != ','
                && json.charAt(end) != '}') {

            end++;
        }

        return json.substring(start, end);
    }

    // =========================
    // GENERATE SIGNATURE
    // =========================

    private String generateSignature(String data) {

        try {

            SecretKeySpec secretKey =
                    new SecretKeySpec(
                            SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                            "HmacSHA256"
                    );

            Mac mac =
                    Mac.getInstance("HmacSHA256");

            mac.init(secretKey);

            byte[] signature =
                    mac.doFinal(
                            data.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(signature);

        } catch (Exception exception) {

            throw new RuntimeException(
                    "Failed to generate JWT",
                    exception
            );
        }
    }
}