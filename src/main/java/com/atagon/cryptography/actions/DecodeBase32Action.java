package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.Base32;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to decode selected Base32 text.
 * Project by atagon GmbH (www.atagon.com).
 */
public class DecodeBase32Action extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Decode with Base32";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) {
        return Base32.decodeToString(input);
    }
}
