package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.RsaCrypto;
import com.atagon.cryptography.ui.CryptoDialogs;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to decrypt selected text using RSA with a private key.
 * Project by atagon GmbH (www.atagon.com).
 */
public class DecryptRsaAction extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Decrypt with RSA";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) throws Exception {
        String privateKeyPem = CryptoDialogs.promptRsaPrivateKey(project);
        if (privateKeyPem == null) {
            return null;
        }
        return RsaCrypto.decrypt(input, privateKeyPem);
    }
}
