package com.atagon.cryptography.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * RFC 4648 compliant Base32 encoder and decoder.
 * Project by atagon GmbH (www.atagon.com).
 */
public final class Base32 {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
    private static final char[] ENCODE_TABLE = ALPHABET.toCharArray();
    private static final int[] DECODE_TABLE = new int[128];

    static {
        Arrays.fill(DECODE_TABLE, -1);
        for (int i = 0; i < ENCODE_TABLE.length; i++) {
            DECODE_TABLE[ENCODE_TABLE[i]] = i;
            // Also map lowercase
            if (ENCODE_TABLE[i] >= 'A' && ENCODE_TABLE[i] <= 'Z') {
                DECODE_TABLE[Character.toLowerCase(ENCODE_TABLE[i])] = i;
            }
        }
    }

    private Base32() {
    }

    public static String encode(String plainText) {
        if (plainText == null) {
            return null;
        }
        return encode(plainText.getBytes(StandardCharsets.UTF_8));
    }

    public static String encode(byte[] data) {
        if (data == null) {
            return null;
        }
        if (data.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder((data.length * 8 + 4) / 5);
        int buffer = 0;
        int bitsLeft = 0;

        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                int index = (buffer >> (bitsLeft - 5)) & 0x1F;
                bitsLeft -= 5;
                result.append(ENCODE_TABLE[index]);
            }
        }

        if (bitsLeft > 0) {
            int index = (buffer << (5 - bitsLeft)) & 0x1F;
            result.append(ENCODE_TABLE[index]);
        }

        // Add padding to multiple of 8
        while (result.length() % 8 != 0) {
            result.append('=');
        }

        return result.toString();
    }

    public static String decodeToString(String base32Text) throws IllegalArgumentException {
        if (base32Text == null) {
            return null;
        }
        byte[] decoded = decode(base32Text);
        return new String(decoded, StandardCharsets.UTF_8);
    }

    public static byte[] decode(String base32Text) throws IllegalArgumentException {
        if (base32Text == null) {
            return null;
        }
        String cleaned = base32Text.trim().replace("=", "");
        if (cleaned.isEmpty()) {
            return new byte[0];
        }

        int buffer = 0;
        int bitsLeft = 0;
        byte[] result = new byte[cleaned.length() * 5 / 8];
        int count = 0;

        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            if (c >= DECODE_TABLE.length || DECODE_TABLE[c] < 0) {
                throw new IllegalArgumentException("Invalid Base32 character: '" + c + "' at position " + i);
            }
            int val = DECODE_TABLE[c];
            buffer = (buffer << 5) | val;
            bitsLeft += 5;

            if (bitsLeft >= 8) {
                result[count++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xFF);
                bitsLeft -= 8;
            }
        }

        return Arrays.copyOf(result, count);
    }
}
