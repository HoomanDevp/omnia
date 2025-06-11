package com.omnia.core.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class RequestSignatureUtils {

    // Thread-local instance of MessageDigest for SHA-1 to avoid repeated creation
    private static final ThreadLocal<MessageDigest> SHA1_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-1 not supported", e);
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
        MessageDigest digest = SHA1_DIGEST.get();
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
        MessageDigest digest = SHA1_DIGEST.get();
        digest.reset(); // Clear previous state
        byte[] hash = digest.digest(input);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
    }
}
