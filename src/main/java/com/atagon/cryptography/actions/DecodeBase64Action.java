package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.Base64Util;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to decode selected Base64 text.
 * Project by atagon GmbH (www.atagon.com).
 */
public class DecodeBase64Action extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Decode with Base64";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) {
        return Base64Util.decodeToString(input);
    }
}
