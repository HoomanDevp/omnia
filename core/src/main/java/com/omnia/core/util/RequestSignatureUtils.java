package com.omnia.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RequestSignatureUtils {
    private RequestSignatureUtils() {}

    // Thread-local instance of MessageDigest for SHA-256 to avoid repeated creation
    private static final ThreadLocal<MessageDigest> SHA256_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not supported", e);
        }
    });

    public static String computeSignature(String method, String path, long timeout, String clientInfo, String body) {
        StringBuilder sb = new StringBuilder(512); // Pre-allocate buffer
        sb.append(method)
                .append('|')
                .append(path)
                .append('|')
                .append(timeout)
                .append('|')
                .append(clientInfo != null ? clientInfo : "")
                .append('|')
                .append(body != null ? body : "");

        byte[] input = sb.toString().getBytes(StandardCharsets.UTF_8);
        MessageDigest digest = SHA256_DIGEST.get();
        digest.reset(); // Clear previous state
        byte[] hash = digest.digest(input);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }

    public static String computeSignature(String method, String path, String clientInfo, String body) {
        StringBuilder sb = new StringBuilder(512); // Pre-allocate buffer
        sb.append(method)
                .append('|')
                .append(path)
                .append('|')
                .append(clientInfo != null ? clientInfo : "")
                .append('|')
                .append(body != null ? body : "");

        byte[] input = sb.toString().getBytes(StandardCharsets.UTF_8);
        MessageDigest digest = SHA256_DIGEST.get();
        digest.reset(); // Clear previous state
        byte[] hash = digest.digest(input);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}
