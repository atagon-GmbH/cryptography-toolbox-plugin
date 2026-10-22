package com.atagon.cryptography.actions;

import com.atagon.cryptography.crypto.HashUtil;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Action to replace selected text with its SHA-256 hash.
 * Project by atagon GmbH (www.atagon.com).
 */
public class HashSha256Action extends AbstractCryptoAction {

    @Override
    protected @NotNull String getActionTitle() {
        return "Hash with SHA-256";
    }

    @Override
    protected @Nullable String executeCryptoOperation(@Nullable Project project, @NotNull String input) {
        return HashUtil.sha256(input);
    }
}
