package com.atagon.cryptography.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64 encoding and decoding utilities.
 * Project by atagon GmbH (www.atagon.com).
 */
public final class Base64Util {

    private Base64Util() {
    }

    public static String encode(String plainText) {
        if (plainText == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(plainText.getBytes(StandardCharsets.UTF_8));
    }

    public static String encode(byte[] data) {
        if (data == null) {
            return null;
        }
        return Base64.getEncoder().encodeToString(data);
    }

    public static String decodeToString(String base64Text) throws IllegalArgumentException {
        if (base64Text == null) {
            return null;
        }
        byte[] decoded = decode(base64Text);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public static byte[] decode(String base64Text) throws IllegalArgumentException {
        if (base64Text == null) {
            return null;
        }
        // Normalize whitespace / line breaks
        String cleaned = base64Text.replaceAll("\\s+", "");
        return Base64.getDecoder().decode(cleaned);
    }
}
