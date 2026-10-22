package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.RsaCrypto;
import com.atagon.cryptography.ui.CryptoDialogs;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to encrypt selected text using RSA with a public key.
 * Project by atagon GmbH (www.atagon.com).
 */
public class EncryptRsaAction extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Encrypt with RSA";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) throws Exception {
        String publicKeyPem = CryptoDialogs.promptRsaPublicKey(project);
        if (publicKeyPem == null) {
            return null;
        }
        return RsaCrypto.encrypt(input, publicKeyPem);
    }
}
