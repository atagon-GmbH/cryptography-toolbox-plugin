package com.atagon.cryptography.crypto;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA Asymmetric cryptography implementation (PKCS#1 v1.5 and RSA-OAEP with SHA-256).
 * Project by atagon GmbH (www.atagon.com).
 */
public final class RsaCrypto {

    public static final String TRANSFORMATION_PKCS1 = "RSA/ECB/PKCS1Padding";
    public static final String TRANSFORMATION_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private RsaCrypto() {
    }

    public static class KeyPairPem {
        private final String publicKeyPem;
        private final String privateKeyPem;

        public KeyPairPem(String publicKeyPem, String privateKeyPem) {
            this.publicKeyPem = publicKeyPem;
            this.privateKeyPem = privateKeyPem;
        }

        public String getPublicKeyPem() {
            return publicKeyPem;
        }

        public String getPrivateKeyPem() {
            return privateKeyPem;
        }
    }

    public static KeyPairPem generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySize, new SecureRandom());
        KeyPair keyPair = generator.generateKeyPair();

        String pubPem = formatPublicKeyPem((RSAPublicKey) keyPair.getPublic());
        String privPem = formatPrivateKeyPem((RSAPrivateKey) keyPair.getPrivate());

        return new KeyPairPem(pubPem, privPem);
    }

    public static String formatPublicKeyPem(PublicKey publicKey) {
        String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(publicKey.getEncoded());
        return "-----BEGIN PUBLIC KEY-----\n" + base64 + "\n-----END PUBLIC KEY-----\n";
    }

    public static String formatPrivateKeyPem(PrivateKey privateKey) {
        String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(privateKey.getEncoded());
        return "-----BEGIN PRIVATE KEY-----\n" + base64 + "\n-----END PRIVATE KEY-----\n";
    }

    public static PublicKey parsePublicKey(String pemOrBase64) throws Exception {
        if (pemOrBase64 == null || pemOrBase64.trim().isEmpty()) {
            throw new IllegalArgumentException("Public key cannot be empty");
        }
        String clean = pemOrBase64
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replace("-----END RSA PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(clean);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    public static PrivateKey parsePrivateKey(String pemOrBase64) throws Exception {
        if (pemOrBase64 == null || pemOrBase64.trim().isEmpty()) {
            throw new IllegalArgumentException("Private key cannot be empty");
        }
        String clean = pemOrBase64
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(clean);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }

    // --- RSA PKCS#1 v1.5 ---

    public static String encrypt(String plainText, String publicKeyPem) throws Exception {
        PublicKey publicKey = parsePublicKey(publicKeyPem);
        return encrypt(plainText, publicKey);
    }

    public static String encrypt(String plainText, PublicKey publicKey) throws Exception {
        return encryptInternal(plainText, publicKey, false);
    }

    public static String decrypt(String cipherTextBase64, String privateKeyPem) throws Exception {
        PrivateKey privateKey = parsePrivateKey(privateKeyPem);
        return decrypt(cipherTextBase64, privateKey);
    }

    public static String decrypt(String cipherTextBase64, PrivateKey privateKey) throws Exception {
        return decryptInternal(cipherTextBase64, privateKey, false);
    }

    // --- RSA-OAEP (SHA-256) ---

    public static String encryptOaep(String plainText, String publicKeyPem) throws Exception {
        PublicKey publicKey = parsePublicKey(publicKeyPem);
        return encryptOaep(plainText, publicKey);
    }

    public static String encryptOaep(String plainText, PublicKey publicKey) throws Exception {
        return encryptInternal(plainText, publicKey, true);
    }

    public static String decryptOaep(String cipherTextBase64, String privateKeyPem) throws Exception {
        PrivateKey privateKey = parsePrivateKey(privateKeyPem);
        return decryptOaep(cipherTextBase64, privateKey);
    }

    public static String decryptOaep(String cipherTextBase64, PrivateKey privateKey) throws Exception {
        return decryptInternal(cipherTextBase64, privateKey, true);
    }

    // --- Internal Helpers for Chunked RSA Encryption / Decryption ---

    private static String encryptInternal(String plainText, PublicKey publicKey, boolean isOaep) throws Exception {
        if (plainText == null) {
            return null;
        }

        Cipher cipher;
        int paddingOverhead;

        if (isOaep) {
            cipher = Cipher.getInstance(TRANSFORMATION_OAEP);
            OAEPParameterSpec oaepSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepSpec);
            // OAEP with SHA-256 padding overhead: 2 * hLen + 2 = 2 * 32 + 2 = 66 bytes
            paddingOverhead = 66;
        } else {
            cipher = Cipher.getInstance(TRANSFORMATION_PKCS1);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            // PKCS#1 v1.5 padding overhead: 11 bytes
            paddingOverhead = 11;
        }

        byte[] plainBytes = plainText.getBytes(StandardCharsets.UTF_8);

        int keyBits = (publicKey instanceof RSAKey) ? ((RSAKey) publicKey).getModulus().bitLength() : 2048;
        int maxBlockSize = (keyBits / 8) - paddingOverhead;
        if (maxBlockSize <= 0) {
            throw new IllegalArgumentException("RSA key size is too small for the selected padding scheme");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        while (offset < plainBytes.length) {
            int length = Math.min(plainBytes.length - offset, maxBlockSize);
            byte[] encryptedChunk = cipher.doFinal(plainBytes, offset, length);
            out.write(encryptedChunk);
            offset += length;
        }

        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    private static String decryptInternal(String cipherTextBase64, PrivateKey privateKey, boolean isOaep) throws Exception {
        if (cipherTextBase64 == null) {
            return null;
        }

        String clean = cipherTextBase64.replaceAll("\\s+", "");
        byte[] cipherBytes = Base64.getDecoder().decode(clean);

        Cipher cipher;
        if (isOaep) {
            cipher = Cipher.getInstance(TRANSFORMATION_OAEP);
            OAEPParameterSpec oaepSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepSpec);
        } else {
            cipher = Cipher.getInstance(TRANSFORMATION_PKCS1);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
        }

        int keyBits = (privateKey instanceof RSAKey) ? ((RSAKey) privateKey).getModulus().bitLength() : 2048;
        int blockSize = keyBits / 8;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offset = 0;
        while (offset < cipherBytes.length) {
            int length = Math.min(cipherBytes.length - offset, blockSize);
            byte[] decryptedChunk = cipher.doFinal(cipherBytes, offset, length);
            out.write(decryptedChunk);
            offset += length;
        }

        return out.toString(StandardCharsets.UTF_8);
    }
}
