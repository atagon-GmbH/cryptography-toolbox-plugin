package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.SymmetricCrypto;
import com.atagon.cryptography.ui.CryptoDialogs;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to decrypt selected text using Blowfish.
 * Project by atagon GmbH (www.atagon.com).
 */
public class DecryptBlowfishAction extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Decrypt with Blowfish";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) throws Exception {
        String passphrase = CryptoDialogs.promptSymmetricKey(project, "Blowfish", false);
        if (passphrase == null) {
            return null;
        }
        return SymmetricCrypto.decryptBlowfish(input, passphrase);
    }
}
