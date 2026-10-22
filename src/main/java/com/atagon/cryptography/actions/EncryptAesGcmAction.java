package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.SymmetricCrypto;
import com.atagon.cryptography.ui.CryptoDialogs;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to encrypt selected text using AES-256-GCM authenticated encryption.
 * Project by atagon GmbH (www.atagon.com).
 */
public class EncryptAesGcmAction extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Encrypt with AES-256-GCM";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) throws Exception {
        String passphrase = CryptoDialogs.promptSymmetricKey(project, "AES-256-GCM", true);
        if (passphrase == null) {
            return null;
        }
        return SymmetricCrypto.encryptAesGcm(input, passphrase);
    }
}
