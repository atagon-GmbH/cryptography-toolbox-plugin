package com.atagon.cryptography.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Message hashing utility for SHA256, SHA512, and MD5.
 * Project by atagon GmbH (www.atagon.com).
 */
public final class HashUtil {

    private HashUtil() {
    }

    public enum HashAlgorithm {
        SHA256("SHA-256", "SHA-256"),
        SHA512("SHA-512", "SHA-512"),
        MD5("MD5", "MD5");

        private final String algorithmName;
        private final String displayName;

        HashAlgorithm(String algorithmName, String displayName) {
            this.algorithmName = algorithmName;
            this.displayName = displayName;
        }

        public String getAlgorithmName() {
            return algorithmName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public static String sha256(String input) {
        return hash(input, HashAlgorithm.SHA256);
    }

    public static String sha512(String input) {
        return hash(input, HashAlgorithm.SHA512);
    }

    public static String md5(String input) {
        return hash(input, HashAlgorithm.MD5);
    }

    public static String hash(String input, HashAlgorithm algorithm) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm.getAlgorithmName());
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not available: " + algorithm.getAlgorithmName(), e);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuilder hexString = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
