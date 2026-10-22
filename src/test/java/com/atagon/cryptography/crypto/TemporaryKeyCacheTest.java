package com.atagon.cryptography.crypto;

import com.atagon.cryptography.settings.TemporaryKeyCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for 4-hour expiring temporary key cache.
 * Project by atagon GmbH (www.atagon.com).
 */
public class TemporaryKeyCacheTest {

    private TemporaryKeyCache cache;

    @BeforeEach
    public void setUp() {
        cache = TemporaryKeyCache.getInstance();
        cache.clearAll();
    }

    @Test
    public void testSymmetricKeyCaching() {
        assertNull(cache.getSymmetricKey("AES"));
        assertFalse(cache.hasSymmetricKey("AES"));

        cache.putSymmetricKey("AES", "test-passphrase-123");
        assertTrue(cache.hasSymmetricKey("AES"));
        assertEquals("test-passphrase-123", cache.getSymmetricKey("AES"));
        assertEquals("test-passphrase-123", cache.getSymmetricKey("aes")); // case insensitive

        assertTrue(cache.getSymmetricKeyRemainingMillis("AES") > 0);
        assertTrue(cache.getSymmetricKeyRemainingMillis("AES") <= TemporaryKeyCache.EXPIRATION_DURATION_MS);

        cache.removeSymmetricKey("AES");
        assertNull(cache.getSymmetricKey("AES"));
        assertFalse(cache.hasSymmetricKey("AES"));
    }

    @Test
    public void testSymmetricKeyNullAndEdgeCases() {
        // Null and empty algorithm/key handling
        cache.putSymmetricKey(null, "some-key");
        assertNull(cache.getSymmetricKey(null));
        assertFalse(cache.hasSymmetricKey(null));
        assertEquals(0, cache.getSymmetricKeyRemainingMillis(null));

        cache.putSymmetricKey("AES", null);
        assertNull(cache.getSymmetricKey("AES"));

        cache.putSymmetricKey("AES", "");
        assertNull(cache.getSymmetricKey("AES"));

        // Overwrite existing key
        cache.putSymmetricKey("AES", "key-version-1");
        assertEquals("key-version-1", cache.getSymmetricKey("AES"));
        cache.putSymmetricKey("AES", "key-version-2");
        assertEquals("key-version-2", cache.getSymmetricKey("AES"));

        cache.removeSymmetricKey(null); // should not throw
    }

    @Test
    public void testPrivateKeyCaching() {
        assertNull(cache.getPrivateKey());
        assertFalse(cache.hasPrivateKey());

        String mockPrivKey = "-----BEGIN PRIVATE KEY-----\nMIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQg...\n-----END PRIVATE KEY-----";
        cache.putPrivateKey(mockPrivKey);

        assertTrue(cache.hasPrivateKey());
        assertEquals(mockPrivKey, cache.getPrivateKey());

        assertTrue(cache.getPrivateKeyRemainingMillis() > 0);
        assertTrue(cache.getPrivateKeyRemainingMillis() <= TemporaryKeyCache.EXPIRATION_DURATION_MS);

        cache.removePrivateKey();
        assertNull(cache.getPrivateKey());
        assertFalse(cache.hasPrivateKey());
    }

    @Test
    public void testPrivateKeyNullAndEdgeCases() {
        cache.putPrivateKey(null);
        assertNull(cache.getPrivateKey());
        assertFalse(cache.hasPrivateKey());
        assertEquals(0, cache.getPrivateKeyRemainingMillis());

        cache.putPrivateKey("   ");
        assertNull(cache.getPrivateKey());
        assertFalse(cache.hasPrivateKey());

        // Trim behavior
        String rawKey = "  \n  -----BEGIN PRIVATE KEY-----\nkeydata\n-----END PRIVATE KEY-----  \n  ";
        cache.putPrivateKey(rawKey);
        assertEquals(rawKey.trim(), cache.getPrivateKey());

        // Overwrite
        cache.putPrivateKey("-----BEGIN PRIVATE KEY-----\nnewkey\n-----END PRIVATE KEY-----");
        assertEquals("-----BEGIN PRIVATE KEY-----\nnewkey\n-----END PRIVATE KEY-----", cache.getPrivateKey());
    }

    @Test
    public void testMultipleSymmetricAlgorithms() {
        cache.putSymmetricKey("AES", "aes-pass");
        cache.putSymmetricKey("AES-256-GCM", "gcm-pass");
        cache.putSymmetricKey("BLOWFISH", "blowfish-pass");

        assertEquals("aes-pass", cache.getSymmetricKey("AES"));
        assertEquals("gcm-pass", cache.getSymmetricKey("AES-256-GCM"));
        assertEquals("blowfish-pass", cache.getSymmetricKey("Blowfish"));

        cache.clearAll();
        assertNull(cache.getSymmetricKey("AES"));
        assertNull(cache.getSymmetricKey("AES-256-GCM"));
        assertNull(cache.getSymmetricKey("BLOWFISH"));
        assertNull(cache.getPrivateKey());
    }
}
