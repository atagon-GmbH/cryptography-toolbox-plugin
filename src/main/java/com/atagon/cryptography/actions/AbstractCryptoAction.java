package com.atagon.cryptography.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base action for all cryptographic operations on selected editor text.
 * Project by atagon GmbH (www.atagon.com).
 */
public abstract class AbstractCryptoAction extends AnAction {

    public AbstractCryptoAction() {
    }

    public AbstractCryptoAction(@Nullable String text, @Nullable String description) {
        super(text, description, null);
    }

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

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        SelectionModel selectionModel = editor.getSelectionModel();
        String selectedText = selectionModel.getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            return;
        }

        try {
            String result = executeCryptoOperation(project, selectedText);
            if (result == null) {
                // Operation was cancelled by user
                return;
            }

            int start = selectionModel.getSelectionStart();
            int end = selectionModel.getSelectionEnd();
            Document document = editor.getDocument();

            WriteCommandAction.runWriteCommandAction(project, getActionTitle(), null, () -> {
                document.replaceString(start, end, result);
            });

        } catch (Exception ex) {
            Messages.showErrorDialog(
                    project,
                    "Cryptographic operation failed:\n" + ex.getMessage(),
                    getActionTitle() + " Error"
            );
        }
    }

    @NotNull
    protected abstract String getActionTitle();

    @Nullable
    protected abstract String executeCryptoOperation(@Nullable Project project, @NotNull String input) throws Exception;
}
