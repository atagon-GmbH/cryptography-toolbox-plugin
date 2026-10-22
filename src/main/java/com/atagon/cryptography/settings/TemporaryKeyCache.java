package com.atagon.cryptography.settings;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory temporary cache for encryption/decryption symmetric keys and private keys.
 * Keys saved with the "remember" checkmark expire automatically after 4 hours.
 * Project by atagon GmbH (www.atagon.com).
 */
public final class TemporaryKeyCache {

    public static final long EXPIRATION_DURATION_MS = 4L * 60L * 60L * 1000L; // 4 Hours

    private static final TemporaryKeyCache INSTANCE = new TemporaryKeyCache();

    private final Map<String, CacheEntry> symmetricKeyCache = new ConcurrentHashMap<>();
    private volatile CacheEntry privateKeyEntry = null;

    private TemporaryKeyCache() {
    }

    public static TemporaryKeyCache getInstance() {
        return INSTANCE;
    }

    private static class CacheEntry {
        private final String value;
        private final long expiryTimestamp;

        public CacheEntry(String value, long durationMs) {
            this.value = value;
            this.expiryTimestamp = System.currentTimeMillis() + durationMs;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expiryTimestamp;
        }

        public String getValue() {
            return value;
        }

        public long getRemainingMillis() {
            return Math.max(0, expiryTimestamp - System.currentTimeMillis());
        }
    }

    // --- Symmetric Passphrases / Keys ---

    public void putSymmetricKey(String algorithm, String key) {
        if (algorithm == null || key == null || key.isEmpty()) {
            return;
        }
        symmetricKeyCache.put(algorithm.toUpperCase(), new CacheEntry(key, EXPIRATION_DURATION_MS));
    }

    public String getSymmetricKey(String algorithm) {
        if (algorithm == null) {
            return null;
        }
        String key = algorithm.toUpperCase();
        CacheEntry entry = symmetricKeyCache.get(key);
        if (entry != null) {
            if (entry.isExpired()) {
                symmetricKeyCache.remove(key);
                return null;
            }
            return entry.getValue();
        }
        return null;
    }

    public boolean hasSymmetricKey(String algorithm) {
        return getSymmetricKey(algorithm) != null;
    }

    public void removeSymmetricKey(String algorithm) {
        if (algorithm != null) {
            symmetricKeyCache.remove(algorithm.toUpperCase());
        }
    }

    public long getSymmetricKeyRemainingMillis(String algorithm) {
        if (algorithm == null) return 0;
        CacheEntry entry = symmetricKeyCache.get(algorithm.toUpperCase());
        if (entry != null && !entry.isExpired()) {
            return entry.getRemainingMillis();
        }
        return 0;
    }

    // --- Private Keys ---

    public void putPrivateKey(String privateKey) {
        if (privateKey == null || privateKey.trim().isEmpty()) {
            return;
        }
        this.privateKeyEntry = new CacheEntry(privateKey.trim(), EXPIRATION_DURATION_MS);
    }

    public String getPrivateKey() {
        CacheEntry entry = this.privateKeyEntry;
        if (entry != null) {
            if (entry.isExpired()) {
                this.privateKeyEntry = null;
                return null;
            }
            return entry.getValue();
        }
        return null;
    }

    public boolean hasPrivateKey() {
        return getPrivateKey() != null;
    }

    public void removePrivateKey() {
        this.privateKeyEntry = null;
    }

    public long getPrivateKeyRemainingMillis() {
        CacheEntry entry = this.privateKeyEntry;
        if (entry != null && !entry.isExpired()) {
            return entry.getRemainingMillis();
        }
        return 0;
    }

    public void clearAll() {
        symmetricKeyCache.clear();
        privateKeyEntry = null;
    }
}
