package com.atagon.cryptography.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

/**
 * Drop-down Action Group for decryption algorithms.
 * Project by atagon GmbH (www.atagon.com).
 */
public class DecryptActionGroup extends DefaultActionGroup {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        boolean hasSelection = editor != null && editor.getSelectionModel().hasSelection();
        e.getPresentation().setEnabledAndVisible(editor != null && hasSelection);
    }
}
