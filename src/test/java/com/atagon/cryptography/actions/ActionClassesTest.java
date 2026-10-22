package com.atagon.cryptography.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Cryptography Toolbox Action classes and action metadata.
 * Project by atagon GmbH (www.atagon.com).
 */
public class ActionClassesTest {

    @Test
    public void testEncodeBase64Action() {
        EncodeBase64Action action = new EncodeBase64Action();
        assertEquals("Encode with Base64", action.getActionTitle());
        assertEquals(ActionUpdateThread.BGT, action.getActionUpdateThread());

        String input = "Hello atagon GmbH!";
        String output = action.executeCryptoOperation(null, input);
        assertNotNull(output);
        assertEquals("SGVsbG8gYXRhZ29uIEdtYkgh", output);
    }

    @Test
    public void testDecodeBase64Action() {
        DecodeBase64Action action = new DecodeBase64Action();
        assertEquals("Decode with Base64", action.getActionTitle());
        assertEquals(ActionUpdateThread.BGT, action.getActionUpdateThread());

        String base64Input = "SGVsbG8gYXRhZ29uIEdtYkgh";
        String output = action.executeCryptoOperation(null, base64Input);
        assertEquals("Hello atagon GmbH!", output);
    }

    @Test
    public void testEncodeBase32Action() {
        EncodeBase32Action action = new EncodeBase32Action();
        assertEquals("Encode with Base32", action.getActionTitle());
        assertEquals(ActionUpdateThread.BGT, action.getActionUpdateThread());

        String input = "foobar";
        String output = action.executeCryptoOperation(null, input);
        assertEquals("MZXW6YTBOI======", output);
    }

    @Test
    public void testDecodeBase32Action() {
        DecodeBase32Action action = new DecodeBase32Action();
        assertEquals("Decode with Base32", action.getActionTitle());
        assertEquals(ActionUpdateThread.BGT, action.getActionUpdateThread());

        String base32Input = "MZXW6YTBOI======";
        String output = action.executeCryptoOperation(null, base32Input);
        assertEquals("foobar", output);
    }

    @Test
    public void testHashSha256Action() {
        HashSha256Action action = new HashSha256Action();
        assertEquals("Hash with SHA-256", action.getActionTitle());
        assertEquals(ActionUpdateThread.BGT, action.getActionUpdateThread());

        String input = "atagon GmbH";
        String output = action.executeCryptoOperation(null, input);
        assertNotNull(output);
        assertEquals(64, output.length());
    }

    @Test
    public void testHashSha512Action() {
        HashSha512Action action = new HashSha512Action();
        assertEquals("Hash with SHA-512", action.getActionTitle());
        assertEquals(ActionUpdateThread.BGT, action.getActionUpdateThread());

        String input = "atagon GmbH";
        String output = action.executeCryptoOperation(null, input);
        assertNotNull(output);
        assertEquals(128, output.length());
    }

    @Test
    public void testHashMd5Action() {
        HashMd5Action action = new HashMd5Action();
        assertEquals("Hash with MD5", action.getActionTitle());
        assertEquals(ActionUpdateThread.BGT, action.getActionUpdateThread());

        String input = "atagon GmbH";
        String output = action.executeCryptoOperation(null, input);
        assertNotNull(output);
        assertEquals(32, output.length());
    }

    @Test
    public void testSymmetricAndAsymmetricActionTitles() {
        assertEquals("Encrypt with AES-256-GCM", new EncryptAesGcmAction().getActionTitle());
        assertEquals("Decrypt with AES-256-GCM", new DecryptAesGcmAction().getActionTitle());
        assertEquals("Encrypt with AES", new EncryptAesAction().getActionTitle());
        assertEquals("Decrypt with AES", new DecryptAesAction().getActionTitle());
        assertEquals("Encrypt with Blowfish", new EncryptBlowfishAction().getActionTitle());
        assertEquals("Decrypt with Blowfish", new DecryptBlowfishAction().getActionTitle());
        assertEquals("Encrypt with RSA-OAEP", new EncryptRsaOaepAction().getActionTitle());
        assertEquals("Decrypt with RSA-OAEP", new DecryptRsaOaepAction().getActionTitle());
        assertEquals("Encrypt with RSA", new EncryptRsaAction().getActionTitle());
        assertEquals("Decrypt with RSA", new DecryptRsaAction().getActionTitle());
    }

    @Test
    public void testActionGroupsUpdateThread() {
        assertEquals(ActionUpdateThread.BGT, new EncryptActionGroup().getActionUpdateThread());
        assertEquals(ActionUpdateThread.BGT, new DecryptActionGroup().getActionUpdateThread());
        assertEquals(ActionUpdateThread.BGT, new HashActionGroup().getActionUpdateThread());
        assertEquals(ActionUpdateThread.BGT, new CryptographyToolboxGroup().getActionUpdateThread());
    }
}
