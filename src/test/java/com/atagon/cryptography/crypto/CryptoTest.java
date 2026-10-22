package com.atagon.cryptography.crypto;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cryptography Toolbox algorithms.
 * Project by atagon GmbH (www.atagon.com).
 */
public class CryptoTest {

    // ==========================================
    // Base64 Tests
    // ==========================================

    @Test
    public void testBase64RoundTrip() {
        String original = "Hello atagon GmbH! 🔐 Cryptographic Toolbox";
        String encoded = Base64Util.encode(original);
        assertNotNull(encoded);
        String decoded = Base64Util.decodeToString(encoded);
        assertEquals(original, decoded);
    }

    @Test
    public void testBase64EdgeCases() {
        assertNull(Base64Util.encode((String) null));
        assertNull(Base64Util.encode((byte[]) null));
        assertNull(Base64Util.decodeToString(null));
        assertNull(Base64Util.decode(null));

        assertEquals("", Base64Util.encode(""));
        assertEquals("", Base64Util.encode(new byte[0]));
        assertEquals("", Base64Util.decodeToString(""));
        assertArrayEquals(new byte[0], Base64Util.decode(""));

        // Binary data round trip
        byte[] binaryData = new byte[]{0x00, (byte) 0xFF, 0x42, 0x7F, (byte) 0x80};
        String encodedBinary = Base64Util.encode(binaryData);
        assertArrayEquals(binaryData, Base64Util.decode(encodedBinary));

        // Whitespace & newlines handling
        String withWhitespace = "  SGVsbG8g \n d29ybGQ= \r\n ";
        assertEquals("Hello world", Base64Util.decodeToString(withWhitespace));

        // Invalid base64
        assertThrows(IllegalArgumentException.class, () -> Base64Util.decode("Invalid!@#$%^"));
    }

    // ==========================================
    // Base32 Tests
    // ==========================================

    @Test
    public void testBase32RoundTrip() {
        String original = "Hello atagon GmbH! Base32 Test";
        String encoded = Base32.encode(original);
        assertNotNull(encoded);
        String decoded = Base32.decodeToString(encoded);
        assertEquals(original, decoded);

        // RFC 4648 standard test vectors
        assertEquals("", Base32.encode(""));
        assertEquals("MY======", Base32.encode("f"));
        assertEquals("MZXQ====", Base32.encode("fo"));
        assertEquals("MZXW6===", Base32.encode("foo"));
        assertEquals("MZXW6YQ=", Base32.encode("foob"));
        assertEquals("MZXW6YTB", Base32.encode("fooba"));
        assertEquals("MZXW6YTBOI======", Base32.encode("foobar"));

        assertEquals("", Base32.decodeToString(""));
        assertEquals("f", Base32.decodeToString("MY======"));
        assertEquals("fo", Base32.decodeToString("MZXQ===="));
        assertEquals("foo", Base32.decodeToString("MZXW6==="));
        assertEquals("foob", Base32.decodeToString("MZXW6YQ="));
        assertEquals("fooba", Base32.decodeToString("MZXW6YTB"));
        assertEquals("foobar", Base32.decodeToString("MZXW6YTBOI======"));

        // Case insensitivity
        assertEquals("foobar", Base32.decodeToString("mzxw6ytboi======"));
        assertEquals("foobar", Base32.decodeToString("MzxW6yTbOi======"));

        // Unpadded decoding
        assertEquals("foobar", Base32.decodeToString("MZXW6YTBOI"));
        assertEquals("f", Base32.decodeToString("MY"));
    }

    @Test
    public void testBase32EdgeCases() {
        assertNull(Base32.encode((String) null));
        assertNull(Base32.encode((byte[]) null));
        assertNull(Base32.decodeToString(null));
        assertNull(Base32.decode(null));

        assertEquals("", Base32.encode(new byte[0]));
        assertArrayEquals(new byte[0], Base32.decode(""));

        // Binary bytes round trip across various lengths (1 to 10 bytes)
        for (int len = 1; len <= 10; len++) {
            byte[] raw = new byte[len];
            for (int i = 0; i < len; i++) {
                raw[i] = (byte) (i * 37 + 13);
            }
            String encoded = Base32.encode(raw);
            byte[] decoded = Base32.decode(encoded);
            assertArrayEquals(raw, decoded, "Failed for byte length: " + len);
        }

        // Invalid Base32 characters (e.g. 8, 9, 1, 0, symbols)
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("MZXW8==="));
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("MZXW9==="));
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("MZXW!==="));
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("MZXW#==="));
    }

    // ==========================================
    // Hash Tests
    // ==========================================

    @Test
    public void testHashingAlgorithms() {
        String input = "atagon GmbH www.atagon.com";

        String sha256 = HashUtil.sha256(input);
        assertNotNull(sha256);
        assertEquals(64, sha256.length());

        String sha512 = HashUtil.sha512(input);
        assertNotNull(sha512);
        assertEquals(128, sha512.length());

        String md5 = HashUtil.md5(input);
        assertNotNull(md5);
        assertEquals(32, md5.length());

        // Verify deterministic output
        assertEquals(sha256, HashUtil.sha256(input));
        assertEquals(sha512, HashUtil.sha512(input));
        assertEquals(md5, HashUtil.md5(input));
    }

    @Test
    public void testHashStandardVectors() {
        // Empty string standard hashes
        assertEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", HashUtil.sha256(""));
        assertEquals("cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e", HashUtil.sha512(""));
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", HashUtil.md5(""));

        // "The quick brown fox jumps over the lazy dog" standard hashes
        String fox = "The quick brown fox jumps over the lazy dog";
        assertEquals("d7a8fbb307d7809469ca9abcb0082e4f8d5651e46d3cdb762d02d0bf37c9e592", HashUtil.sha256(fox));
        assertEquals("07e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb642e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6", HashUtil.sha512(fox));
        assertEquals("9e107d9d372bb6826bd81d3542a419d6", HashUtil.md5(fox));

        // Null handling
        assertNull(HashUtil.sha256(null));
        assertNull(HashUtil.sha512(null));
        assertNull(HashUtil.md5(null));
        assertNull(HashUtil.hash(null, HashUtil.HashAlgorithm.SHA256));

        // Bytes to hex edge cases
        assertEquals("", HashUtil.bytesToHex(null));
        assertEquals("", HashUtil.bytesToHex(new byte[0]));
        assertEquals("000f7f80ff", HashUtil.bytesToHex(new byte[]{0x00, 0x0F, 0x7F, (byte) 0x80, (byte) 0xFF}));

        // HashAlgorithm enum coverage
        assertEquals("SHA-256", HashUtil.HashAlgorithm.SHA256.getAlgorithmName());
        assertEquals("SHA-256", HashUtil.HashAlgorithm.SHA256.getDisplayName());
        assertEquals("SHA-512", HashUtil.HashAlgorithm.SHA512.getAlgorithmName());
        assertEquals("SHA-512", HashUtil.HashAlgorithm.SHA512.getDisplayName());
        assertEquals("MD5", HashUtil.HashAlgorithm.MD5.getAlgorithmName());
        assertEquals("MD5", HashUtil.HashAlgorithm.MD5.getDisplayName());
    }

    // ==========================================
    // Symmetric Crypto Tests (AES-CBC, AES-GCM, Blowfish)
    // ==========================================

    @Test
    public void testAesEncryptionDecryption() throws Exception {
        String plainText = "Secret message for WebStorm and IntelliJ IDEA by atagon GmbH";
        String passphrase = "super-secret-passphrase-123";

        String encrypted = SymmetricCrypto.encryptAes(plainText, passphrase);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);

        String decrypted = SymmetricCrypto.decryptAes(encrypted, passphrase);
        assertEquals(plainText, decrypted);

        // Verify wrong passphrase fails
        assertThrows(Exception.class, () -> SymmetricCrypto.decryptAes(encrypted, "wrong-passphrase"));
    }

    @Test
    public void testAesGcmEncryptionDecryption() throws Exception {
        String plainText = "Authenticated secret message with AES-256-GCM by atagon GmbH";
        String passphrase = "gcm-secure-passphrase-999";

        String encrypted = SymmetricCrypto.encryptAesGcm(plainText, passphrase);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);

        String decrypted = SymmetricCrypto.decryptAesGcm(encrypted, passphrase);
        assertEquals(plainText, decrypted);

        // Verify wrong passphrase fails authentication
        assertThrows(Exception.class, () -> SymmetricCrypto.decryptAesGcm(encrypted, "wrong-passphrase"));

        // Verify tampered ciphertext fails authentication (AEAD integrity check)
        byte[] rawBytes = java.util.Base64.getDecoder().decode(encrypted);
        rawBytes[rawBytes.length - 1] ^= 0xFF; // flip bits in authentication tag
        String tampered = java.util.Base64.getEncoder().encodeToString(rawBytes);
        assertThrows(Exception.class, () -> SymmetricCrypto.decryptAesGcm(tampered, passphrase));
    }

    @Test
    public void testBlowfishEncryptionDecryption() throws Exception {
        String plainText = "Confidential text for Blowfish cipher";
        String passphrase = "blowfish-key-atagon";

        String encrypted = SymmetricCrypto.encryptBlowfish(plainText, passphrase);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);

        String decrypted = SymmetricCrypto.decryptBlowfish(encrypted, passphrase);
        assertEquals(plainText, decrypted);

        // Verify wrong passphrase fails
        assertThrows(Exception.class, () -> SymmetricCrypto.decryptBlowfish(encrypted, "wrong-passphrase"));
    }

    @Test
    public void testSymmetricCryptoEdgeCases() throws Exception {
        String key = "test-secret-key-atagon";

        // Null and empty checks
        assertNull(SymmetricCrypto.encryptAes(null, key));
        assertNull(SymmetricCrypto.decryptAes(null, key));
        assertNull(SymmetricCrypto.encryptAesGcm(null, key));
        assertNull(SymmetricCrypto.decryptAesGcm(null, key));
        assertNull(SymmetricCrypto.encryptBlowfish(null, key));
        assertNull(SymmetricCrypto.decryptBlowfish(null, key));

        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.encryptAes("data", ""));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.encryptAes("data", null));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.decryptAes("data", ""));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.decryptAes("data", null));

        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.encryptAesGcm("data", ""));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.encryptAesGcm("data", null));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.decryptAesGcm("data", ""));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.decryptAesGcm("data", null));

        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.encryptBlowfish("data", ""));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.encryptBlowfish("data", null));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.decryptBlowfish("data", ""));
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.decryptBlowfish("data", null));

        // Empty string roundtrips
        assertEquals("", SymmetricCrypto.decryptAes(SymmetricCrypto.encryptAes("", key), key));
        assertEquals("", SymmetricCrypto.decryptAesGcm(SymmetricCrypto.encryptAesGcm("", key), key));
        assertEquals("", SymmetricCrypto.decryptBlowfish(SymmetricCrypto.encryptBlowfish("", key), key));

        // Unicode, German umlauts, emojis, Chinese characters
        String complexText = "🔐 ÄÖÜäöüß - 密钥测试 - atagon GmbH 🚀 Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        assertEquals(complexText, SymmetricCrypto.decryptAes(SymmetricCrypto.encryptAes(complexText, key), key));
        assertEquals(complexText, SymmetricCrypto.decryptAesGcm(SymmetricCrypto.encryptAesGcm(complexText, key), key));
        assertEquals(complexText, SymmetricCrypto.decryptBlowfish(SymmetricCrypto.encryptBlowfish(complexText, key), key));

        // Large payload (50KB)
        StringBuilder largeBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeBuilder.append("atagon GmbH cryptographic payload block ").append(i).append("\n");
        }
        String largeText = largeBuilder.toString();
        assertEquals(largeText, SymmetricCrypto.decryptAes(SymmetricCrypto.encryptAes(largeText, key), key));
        assertEquals(largeText, SymmetricCrypto.decryptAesGcm(SymmetricCrypto.encryptAesGcm(largeText, key), key));
        assertEquals(largeText, SymmetricCrypto.decryptBlowfish(SymmetricCrypto.encryptBlowfish(largeText, key), key));

        // Short / truncated ciphertexts
        assertThrows(Exception.class, () -> SymmetricCrypto.decryptAes("dG9vX3Nob3J0", key)); // too short for 16-byte IV
        assertThrows(IllegalArgumentException.class, () -> SymmetricCrypto.decryptAesGcm("dG9vX3Nob3J0", key)); // too short for GCM IV + Tag
        assertThrows(Exception.class, () -> SymmetricCrypto.decryptBlowfish("dG9v", key)); // too short for 8-byte IV
    }

    // ==========================================
    // Asymmetric Crypto Tests (RSA PKCS#1 & RSA-OAEP)
    // ==========================================

    @Test
    public void testRsaEncryptionDecryption() throws Exception {
        RsaCrypto.KeyPairPem keyPair = RsaCrypto.generateKeyPair(2048);
        assertNotNull(keyPair.getPublicKeyPem());
        assertNotNull(keyPair.getPrivateKeyPem());
        assertTrue(keyPair.getPublicKeyPem().contains("BEGIN PUBLIC KEY"));
        assertTrue(keyPair.getPrivateKeyPem().contains("BEGIN PRIVATE KEY"));

        // Short message test (PKCS#1 v1.5)
        String shortMessage = "Quick RSA Test";
        String encryptedShort = RsaCrypto.encrypt(shortMessage, keyPair.getPublicKeyPem());
        assertNotNull(encryptedShort);
        String decryptedShort = RsaCrypto.decrypt(encryptedShort, keyPair.getPrivateKeyPem());
        assertEquals(shortMessage, decryptedShort);

        // Long message test (tests chunking mechanism)
        StringBuilder longMsgBuilder = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            longMsgBuilder.append("atagon GmbH Cryptography Toolbox Line ").append(i).append("\n");
        }
        String longMessage = longMsgBuilder.toString();

        String encryptedLong = RsaCrypto.encrypt(longMessage, keyPair.getPublicKeyPem());
        assertNotNull(encryptedLong);
        String decryptedLong = RsaCrypto.decrypt(encryptedLong, keyPair.getPrivateKeyPem());
        assertEquals(longMessage, decryptedLong);
    }

    @Test
    public void testRsaOaepEncryptionDecryption() throws Exception {
        RsaCrypto.KeyPairPem keyPair = RsaCrypto.generateKeyPair(2048);
        assertNotNull(keyPair.getPublicKeyPem());
        assertNotNull(keyPair.getPrivateKeyPem());

        // Short message test (RSA-OAEP with SHA-256)
        String shortMessage = "Quick RSA-OAEP Test Message";
        String encryptedShort = RsaCrypto.encryptOaep(shortMessage, keyPair.getPublicKeyPem());
        assertNotNull(encryptedShort);
        String decryptedShort = RsaCrypto.decryptOaep(encryptedShort, keyPair.getPrivateKeyPem());
        assertEquals(shortMessage, decryptedShort);

        // Long message test (tests OAEP chunking mechanism)
        StringBuilder longMsgBuilder = new StringBuilder();
        for (int i = 0; i < 40; i++) {
            longMsgBuilder.append("atagon GmbH RSA-OAEP Chunking Line ").append(i).append("\n");
        }
        String longMessage = longMsgBuilder.toString();

        String encryptedLong = RsaCrypto.encryptOaep(longMessage, keyPair.getPublicKeyPem());
        assertNotNull(encryptedLong);
        String decryptedLong = RsaCrypto.decryptOaep(encryptedLong, keyPair.getPrivateKeyPem());
        assertEquals(longMessage, decryptedLong);
    }

    @Test
    public void testRsaEdgeCasesAndKeySizes() throws Exception {
        // Test key generation with 1024-bit key
        RsaCrypto.KeyPairPem keyPair1024 = RsaCrypto.generateKeyPair(1024);
        assertNotNull(keyPair1024.getPublicKeyPem());
        assertNotNull(keyPair1024.getPrivateKeyPem());

        String msg = "Testing 1024-bit RSA key pair by atagon GmbH 🔐";
        String enc1024 = RsaCrypto.encrypt(msg, keyPair1024.getPublicKeyPem());
        assertEquals(msg, RsaCrypto.decrypt(enc1024, keyPair1024.getPrivateKeyPem()));

        String encOaep1024 = RsaCrypto.encryptOaep(msg, keyPair1024.getPublicKeyPem());
        assertEquals(msg, RsaCrypto.decryptOaep(encOaep1024, keyPair1024.getPrivateKeyPem()));

        // Null checks
        assertNull(RsaCrypto.encrypt((String) null, keyPair1024.getPublicKeyPem()));
        assertNull(RsaCrypto.decrypt((String) null, keyPair1024.getPrivateKeyPem()));
        assertNull(RsaCrypto.encryptOaep((String) null, keyPair1024.getPublicKeyPem()));
        assertNull(RsaCrypto.decryptOaep((String) null, keyPair1024.getPrivateKeyPem()));

        // Invalid key PEM formats
        assertThrows(Exception.class, () -> RsaCrypto.parsePublicKey("not-a-valid-key"));
        assertThrows(Exception.class, () -> RsaCrypto.parsePrivateKey("not-a-valid-key"));
        assertThrows(Exception.class, () -> RsaCrypto.encrypt("test", "invalid-key-pem"));
        assertThrows(Exception.class, () -> RsaCrypto.decrypt("test", "invalid-key-pem"));

        // Cross-padding mismatch tests (decrypting OAEP with PKCS#1 or vice-versa fails)
        RsaCrypto.KeyPairPem keyPair2048 = RsaCrypto.generateKeyPair(2048);
        String oaepCiphertext = RsaCrypto.encryptOaep("Secret OAEP Text", keyPair2048.getPublicKeyPem());
        assertThrows(Exception.class, () -> RsaCrypto.decrypt(oaepCiphertext, keyPair2048.getPrivateKeyPem()));

        String pkcs1Ciphertext = RsaCrypto.encrypt("Secret PKCS1 Text", keyPair2048.getPublicKeyPem());
        assertThrows(Exception.class, () -> RsaCrypto.decryptOaep(pkcs1Ciphertext, keyPair2048.getPrivateKeyPem()));

        // Decrypt with wrong private key fails
        RsaCrypto.KeyPairPem anotherPair = RsaCrypto.generateKeyPair(2048);
        assertThrows(Exception.class, () -> RsaCrypto.decrypt(pkcs1Ciphertext, anotherPair.getPrivateKeyPem()));
        assertThrows(Exception.class, () -> RsaCrypto.decryptOaep(oaepCiphertext, anotherPair.getPrivateKeyPem()));

        // Empty string roundtrip
        assertEquals("", RsaCrypto.decrypt(RsaCrypto.encrypt("", keyPair2048.getPublicKeyPem()), keyPair2048.getPrivateKeyPem()));
        assertEquals("", RsaCrypto.decryptOaep(RsaCrypto.encryptOaep("", keyPair2048.getPublicKeyPem()), keyPair2048.getPrivateKeyPem()));
    }
}
