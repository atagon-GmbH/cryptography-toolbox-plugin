package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.SymmetricCrypto;
import com.atagon.cryptography.ui.CryptoDialogs;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to decrypt selected text using AES.
 * Project by atagon GmbH (www.atagon.com).
 */
public class DecryptAesAction extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Decrypt with AES";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) throws Exception {
        String passphrase = CryptoDialogs.promptSymmetricKey(project, "AES", false);
        if (passphrase == null) {
            return null;
        }
        return SymmetricCrypto.decryptAes(input, passphrase);
    }
}
