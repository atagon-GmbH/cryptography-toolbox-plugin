package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.SymmetricCrypto;
import com.atagon.cryptography.ui.CryptoDialogs;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to encrypt selected text using AES.
 * Project by atagon GmbH (www.atagon.com).
 */
public class EncryptAesAction extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Encrypt with AES";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) throws Exception {
        String passphrase = CryptoDialogs.promptSymmetricKey(project, "AES", true);
        if (passphrase == null) {
            return null;
        }
        return SymmetricCrypto.encryptAes(input, passphrase);
    }
}
