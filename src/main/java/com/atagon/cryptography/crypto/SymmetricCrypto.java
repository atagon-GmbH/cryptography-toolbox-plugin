package com.atagon.cryptography.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Symmetric cryptography implementation for AES (CBC & GCM) and Blowfish.
 * Project by atagon GmbH (www.atagon.com).
 */
public final class SymmetricCrypto {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int GCM_IV_LENGTH_BYTES = 12; // 96-bit IV for GCM
    private static final int GCM_TAG_LENGTH_BITS = 128; // 128-bit authentication tag

    private SymmetricCrypto() {
    }

    // --- AES-256-CBC ---

    public static String encryptAes(String plainText, String keyOrPassphrase) throws Exception {
        if (plainText == null) {
            return null;
        }
        if (keyOrPassphrase == null || keyOrPassphrase.isEmpty()) {
            throw new IllegalArgumentException("Encryption key/passphrase cannot be empty");
        }

        byte[] keyBytes = deriveKey(keyOrPassphrase, "AES", 32); // 256-bit AES
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = new byte[16]; // 128-bit block size for AES-CBC
        SECURE_RANDOM.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV + Encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decryptAes(String cipherTextBase64, String keyOrPassphrase) throws Exception {
        if (cipherTextBase64 == null) {
            return null;
        }
        if (keyOrPassphrase == null || keyOrPassphrase.isEmpty()) {
            throw new IllegalArgumentException("Decryption key/passphrase cannot be empty");
        }

        String cleaned = cipherTextBase64.replaceAll("\\s+", "");
        byte[] combined = Base64.getDecoder().decode(cleaned);

        if (combined.length < 16) {
            throw new IllegalArgumentException("Invalid AES cipher text: payload is shorter than IV block length");
        }

        byte[] iv = Arrays.copyOfRange(combined, 0, 16);
        byte[] cipherBytes = Arrays.copyOfRange(combined, 16, combined.length);

        byte[] keyBytes = deriveKey(keyOrPassphrase, "AES", 32);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decrypted = cipher.doFinal(cipherBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // --- AES-256-GCM ---

    public static String encryptAesGcm(String plainText, String keyOrPassphrase) throws Exception {
        if (plainText == null) {
            return null;
        }
        if (keyOrPassphrase == null || keyOrPassphrase.isEmpty()) {
            throw new IllegalArgumentException("Encryption key/passphrase cannot be empty");
        }

        byte[] keyBytes = deriveKey(keyOrPassphrase, "AES", 32); // 256-bit AES
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
        SECURE_RANDOM.nextBytes(iv);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV (12 bytes) + Encrypted data (which includes 16-byte authentication tag)
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decryptAesGcm(String cipherTextBase64, String keyOrPassphrase) throws Exception {
        if (cipherTextBase64 == null) {
            return null;
        }
        if (keyOrPassphrase == null || keyOrPassphrase.isEmpty()) {
            throw new IllegalArgumentException("Decryption key/passphrase cannot be empty");
        }

        String cleaned = cipherTextBase64.replaceAll("\\s+", "");
        byte[] combined = Base64.getDecoder().decode(cleaned);

        int minLength = GCM_IV_LENGTH_BYTES + (GCM_TAG_LENGTH_BITS / 8);
        if (combined.length < minLength) {
            throw new IllegalArgumentException("Invalid AES-GCM cipher text: payload is too short for IV and authentication tag");
        }

        byte[] iv = Arrays.copyOfRange(combined, 0, GCM_IV_LENGTH_BYTES);
        byte[] cipherBytes = Arrays.copyOfRange(combined, GCM_IV_LENGTH_BYTES, combined.length);

        byte[] keyBytes = deriveKey(keyOrPassphrase, "AES", 32);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec);

        byte[] decrypted = cipher.doFinal(cipherBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // --- Blowfish ---

    public static String encryptBlowfish(String plainText, String keyOrPassphrase) throws Exception {
        if (plainText == null) {
            return null;
        }
        if (keyOrPassphrase == null || keyOrPassphrase.isEmpty()) {
            throw new IllegalArgumentException("Encryption key/passphrase cannot be empty");
        }

        byte[] keyBytes = deriveKey(keyOrPassphrase, "Blowfish", 16); // 128-bit Blowfish key
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "Blowfish");

        byte[] iv = new byte[8]; // 64-bit block size for Blowfish
        SECURE_RANDOM.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Combine IV + Encrypted data
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decryptBlowfish(String cipherTextBase64, String keyOrPassphrase) throws Exception {
        if (cipherTextBase64 == null) {
            return null;
        }
        if (keyOrPassphrase == null || keyOrPassphrase.isEmpty()) {
            throw new IllegalArgumentException("Decryption key/passphrase cannot be empty");
        }

        String cleaned = cipherTextBase64.replaceAll("\\s+", "");
        byte[] combined = Base64.getDecoder().decode(cleaned);

        if (combined.length < 8) {
            throw new IllegalArgumentException("Invalid Blowfish cipher text: payload is shorter than IV block length");
        }

        byte[] iv = Arrays.copyOfRange(combined, 0, 8);
        byte[] cipherBytes = Arrays.copyOfRange(combined, 8, combined.length);

        byte[] keyBytes = deriveKey(keyOrPassphrase, "Blowfish", 16);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "Blowfish");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);

        byte[] decrypted = cipher.doFinal(cipherBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static byte[] deriveKey(String passphrase, String algorithm, int targetLengthBytes) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] hash = sha256.digest(passphrase.getBytes(StandardCharsets.UTF_8));
        return Arrays.copyOf(hash, targetLengthBytes);
    }
}
