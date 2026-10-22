package com.atagon.cryptography.crypto;

import com.atagon.cryptography.settings.CryptographySettingsState;
import com.atagon.cryptography.settings.SavedPublicKey;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Settings state and data models.
 * Project by atagon GmbH (www.atagon.com).
 */
public class SettingsStateTest {

    @Test
    public void testSavedPublicKeyModel() {
        SavedPublicKey key1 = new SavedPublicKey("TestKey", "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA\n-----END PUBLIC KEY-----", "RSA", "Test Description");
        assertEquals("TestKey", key1.getName());
        assertEquals("RSA", key1.getAlgorithm());
        assertEquals("Test Description", key1.getDescription());
        assertTrue(key1.getKeyContent().contains("BEGIN PUBLIC KEY"));
        assertEquals("TestKey", key1.toString());

        // Equals and hashCode by name, content, algorithm
        SavedPublicKey key2 = new SavedPublicKey("TestKey", "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA\n-----END PUBLIC KEY-----", "RSA", "Another Description");
        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());

        SavedPublicKey differentName = new SavedPublicKey("OtherKey", key1.getKeyContent(), "RSA", "Desc");
        assertNotEquals(key1, differentName);

        SavedPublicKey differentContent = new SavedPublicKey("TestKey", "different-content", "RSA", "Desc");
        assertNotEquals(key1, differentContent);

        SavedPublicKey differentAlgo = new SavedPublicKey("TestKey", key1.getKeyContent(), "DSA", "Desc");
        assertNotEquals(key1, differentAlgo);

        assertNotEquals(key1, null);
        assertNotEquals(key1, "NotASavedPublicKey");
        assertEquals(key1, key1);

        // Default constructor & setters
        SavedPublicKey emptyKey = new SavedPublicKey();
        assertNull(emptyKey.getName());
        assertEquals("Unnamed Key", emptyKey.toString());

        emptyKey.setName("CustomName");
        emptyKey.setKeyContent("CustomContent");
        emptyKey.setAlgorithm("RSA-OAEP");
        emptyKey.setDescription("CustomDesc");

        assertEquals("CustomName", emptyKey.getName());
        assertEquals("CustomContent", emptyKey.getKeyContent());
        assertEquals("RSA-OAEP", emptyKey.getAlgorithm());
        assertEquals("CustomDesc", emptyKey.getDescription());
        assertEquals("CustomName", emptyKey.toString());

        // Constructor with null algorithm/description defaults
        SavedPublicKey defaultParamsKey = new SavedPublicKey("Key", "Content", null, null);
        assertEquals("RSA", defaultParamsKey.getAlgorithm());
        assertEquals("", defaultParamsKey.getDescription());
    }

    @Test
    public void testSettingsStateListManagement() {
        CryptographySettingsState state = new CryptographySettingsState();
        assertNotNull(state.getPublicKeys());
        assertEquals(0, state.getPublicKeys().size());

        SavedPublicKey key1 = new SavedPublicKey("API Key 1", "MIIBIjANBgk...", "RSA", "Prod Key");
        SavedPublicKey key2 = new SavedPublicKey("API Key 2", "MIIBIjANBgk...", "RSA", "Staging Key");

        state.addPublicKey(key1);
        assertEquals(1, state.getPublicKeys().size());
        assertTrue(state.getPublicKeys().contains(key1));

        state.addPublicKey(key2);
        assertEquals(2, state.getPublicKeys().size());

        state.removePublicKey(key1);
        assertEquals(1, state.getPublicKeys().size());
        assertFalse(state.getPublicKeys().contains(key1));
        assertTrue(state.getPublicKeys().contains(key2));

        // Setting public keys list
        List<SavedPublicKey> newList = new ArrayList<>();
        newList.add(key1);
        state.setPublicKeys(newList);
        assertEquals(1, state.getPublicKeys().size());
        assertTrue(state.getPublicKeys().contains(key1));

        // Setting null resets to empty list
        state.setPublicKeys(null);
        assertNotNull(state.getPublicKeys());
        assertEquals(0, state.getPublicKeys().size());

        // State roundtrip
        state.addPublicKey(key1);
        state.addPublicKey(key2);
        assertSame(state, state.getState());

        CryptographySettingsState stateCopy = new CryptographySettingsState();
        stateCopy.loadState(state);
        assertEquals(2, stateCopy.getPublicKeys().size());
        assertEquals("API Key 1", stateCopy.getPublicKeys().get(0).getName());
        assertEquals("API Key 2", stateCopy.getPublicKeys().get(1).getName());
    }
}
