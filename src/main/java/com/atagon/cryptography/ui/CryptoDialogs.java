package com.atagon.cryptography.ui;

import com.atagon.cryptography.crypto.RsaCrypto;
import com.atagon.cryptography.settings.CryptographySettingsState;
import com.atagon.cryptography.settings.SavedPublicKey;
import com.atagon.cryptography.settings.TemporaryKeyCache;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPasswordField;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialogs for user key and passphrase prompts with temporary key caching and public key saving.
 * Project by atagon GmbH (www.atagon.com).
 */
public final class CryptoDialogs {

    private CryptoDialogs() {
    }

    @Nullable
    public static String promptSymmetricKey(Project project, String algorithm, boolean encrypt) {
        String action = encrypt ? "Encrypt" : "Decrypt";
        SymmetricKeyDialog dialog = new SymmetricKeyDialog(project, algorithm, action);
        if (dialog.showAndGet()) {
            return dialog.getKey();
        }
        return null;
    }

    @Nullable
    public static String promptRsaPublicKey(Project project) {
        return promptRsaPublicKey(project, "RSA Encryption - Public Key");
    }

    @Nullable
    public static String promptRsaPublicKey(Project project, String dialogTitle) {
        RsaPublicKeyDialog dialog = new RsaPublicKeyDialog(project, dialogTitle);
        if (dialog.showAndGet()) {
            return dialog.getPublicKeyContent();
        }
        return null;
    }

    @Nullable
    public static String promptRsaPrivateKey(Project project) {
        return promptRsaPrivateKey(project, "RSA Decryption - Private Key Prompt");
    }

    @Nullable
    public static String promptRsaPrivateKey(Project project, String dialogTitle) {
        RsaPrivateKeyDialog dialog = new RsaPrivateKeyDialog(project, dialogTitle);
        if (dialog.showAndGet()) {
            return dialog.getPrivateKeyContent();
        }
        return null;
    }

    // --- Symmetric Key Dialog ---

    private static class SymmetricKeyDialog extends DialogWrapper {
        private final JBPasswordField passwordField = new JBPasswordField();
        private final JCheckBox showPasswordCheckBox = new JCheckBox("Show passphrase");
        private final JCheckBox rememberKeyCheckBox = new JCheckBox("Remember passphrase for 4 hours (auto-expires)");
        private final JBLabel cachedNoticeLabel = new JBLabel();
        private final String algorithm;
        private final String action;

        public SymmetricKeyDialog(@Nullable Project project, String algorithm, String action) {
            super(project, true);
            this.algorithm = algorithm;
            this.action = action;
            setTitle(action + " with " + algorithm + " - atagon Cryptography");

            // Check if there is a cached key
            String cachedKey = TemporaryKeyCache.getInstance().getSymmetricKey(algorithm);
            if (cachedKey != null) {
                passwordField.setText(cachedKey);
                rememberKeyCheckBox.setSelected(true);
                long remainingMin = TemporaryKeyCache.getInstance().getSymmetricKeyRemainingMillis(algorithm) / (60 * 1000);
                cachedNoticeLabel.setText("<html><small style='color:green;'>✓ Loaded remembered passphrase (" + remainingMin + "m remaining)</small></html>");
            } else {
                rememberKeyCheckBox.setSelected(false);
            }

            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout(5, 10));
            panel.setPreferredSize(new Dimension(420, 130));

            JPanel topPanel = new JPanel(new BorderLayout(5, 2));
            JBLabel label = new JBLabel("Enter " + algorithm + " Secret Key / Passphrase to " + action.toLowerCase() + ":");
            topPanel.add(label, BorderLayout.NORTH);
            if (!cachedNoticeLabel.getText().isEmpty()) {
                topPanel.add(cachedNoticeLabel, BorderLayout.SOUTH);
            }
            panel.add(topPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
            passwordField.setEchoChar('•');
            centerPanel.add(passwordField, BorderLayout.CENTER);

            JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 2, 2));
            showPasswordCheckBox.addActionListener(e -> {
                if (showPasswordCheckBox.isSelected()) {
                    passwordField.setEchoChar((char) 0);
                } else {
                    passwordField.setEchoChar('•');
                }
            });
            optionsPanel.add(showPasswordCheckBox);
            optionsPanel.add(rememberKeyCheckBox);

            centerPanel.add(optionsPanel, BorderLayout.SOUTH);

            panel.add(centerPanel, BorderLayout.CENTER);
            return panel;
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return passwordField;
        }

        @Override
        protected void doOKAction() {
            String key = new String(passwordField.getPassword()).trim();
            if (key.isEmpty()) {
                Messages.showErrorDialog("Key/passphrase cannot be empty.", "Validation Error");
                return;
            }

            if (rememberKeyCheckBox.isSelected()) {
                TemporaryKeyCache.getInstance().putSymmetricKey(algorithm, key);
            } else {
                TemporaryKeyCache.getInstance().removeSymmetricKey(algorithm);
            }

            super.doOKAction();
        }

        public String getKey() {
            return new String(passwordField.getPassword());
        }
    }

    // --- RSA Public Key Dialog ---

    private static class RsaPublicKeyDialog extends DialogWrapper {
        private final Project project;
        private final JComboBox<Object> savedKeysComboBox = new JComboBox<>();
        private final JBTextArea publicKeyArea = new JBTextArea(9, 48);
        private final JCheckBox saveToSettingsCheckBox = new JCheckBox("Save this Public Key to Settings for reuse");
        private final JBTextField keyNameField = new JBTextField("Saved Public Key");
        private final JPanel keyNamePanel = new JPanel(new BorderLayout(5, 2));

        public RsaPublicKeyDialog(@Nullable Project project, String title) {
            super(project, true);
            this.project = project;
            setTitle(title != null ? title : "RSA Encryption - Public Key");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout(5, 8));

            JPanel topPanel = new JPanel(new BorderLayout(5, 5));
            topPanel.add(new JBLabel("Select saved Public Key:"), BorderLayout.WEST);

            savedKeysComboBox.addItem("-- Select or Enter Custom Key --");
            CryptographySettingsState state = CryptographySettingsState.getInstance();
            if (state != null) {
                List<SavedPublicKey> keys = state.getPublicKeys();
                for (SavedPublicKey key : keys) {
                    savedKeysComboBox.addItem(key);
                }
            }

            savedKeysComboBox.addActionListener(e -> {
                Object selected = savedKeysComboBox.getSelectedItem();
                if (selected instanceof SavedPublicKey) {
                    publicKeyArea.setText(((SavedPublicKey) selected).getKeyContent());
                    saveToSettingsCheckBox.setSelected(false);
                    keyNamePanel.setVisible(false);
                }
            });

            topPanel.add(savedKeysComboBox, BorderLayout.CENTER);

            JButton settingsBtn = new JButton("Manage Keys...");
            settingsBtn.addActionListener(e -> {
                ShowSettingsUtil.getInstance().showSettingsDialog(project, "Cryptography Toolbox");
                refreshComboBox();
            });
            topPanel.add(settingsBtn, BorderLayout.EAST);

            panel.add(topPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
            centerPanel.add(new JBLabel("Public Key Content (PEM or Base64):"), BorderLayout.NORTH);
            publicKeyArea.setLineWrap(true);
            centerPanel.add(new JBScrollPane(publicKeyArea), BorderLayout.CENTER);

            // Bottom panel with save checkmark and name
            JPanel bottomPanel = new JPanel(new BorderLayout(5, 4));
            bottomPanel.add(saveToSettingsCheckBox, BorderLayout.NORTH);

            keyNamePanel.add(new JBLabel("Key Label:"), BorderLayout.WEST);
            keyNamePanel.add(keyNameField, BorderLayout.CENTER);
            keyNamePanel.setVisible(false);
            bottomPanel.add(keyNamePanel, BorderLayout.SOUTH);

            saveToSettingsCheckBox.addActionListener(e -> {
                keyNamePanel.setVisible(saveToSettingsCheckBox.isSelected());
                pack();
            });

            centerPanel.add(bottomPanel, BorderLayout.SOUTH);

            panel.add(centerPanel, BorderLayout.CENTER);
            return panel;
        }

        private void refreshComboBox() {
            Object current = savedKeysComboBox.getSelectedItem();
            savedKeysComboBox.removeAllItems();
            savedKeysComboBox.addItem("-- Select or Enter Custom Key --");
            CryptographySettingsState state = CryptographySettingsState.getInstance();
            if (state != null) {
                for (SavedPublicKey key : state.getPublicKeys()) {
                    savedKeysComboBox.addItem(key);
                }
            }
            if (current != null) {
                savedKeysComboBox.setSelectedItem(current);
            }
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return savedKeysComboBox.getItemCount() > 1 ? savedKeysComboBox : publicKeyArea;
        }

        @Override
        protected void doOKAction() {
            String content = publicKeyArea.getText().trim();
            if (content.isEmpty()) {
                Messages.showErrorDialog("Public Key content cannot be empty.", "Validation Error");
                return;
            }

            try {
                RsaCrypto.parsePublicKey(content);
            } catch (Exception ex) {
                Messages.showErrorDialog("Invalid RSA Public Key: " + ex.getMessage(), "Key Validation Error");
                return;
            }

            if (saveToSettingsCheckBox.isSelected()) {
                String name = keyNameField.getText().trim();
                if (name.isEmpty()) {
                    name = "Saved Public Key";
                }
                CryptographySettingsState state = CryptographySettingsState.getInstance();
                if (state != null) {
                    SavedPublicKey newKey = new SavedPublicKey(name, content, "RSA", "Saved from editor prompt");
                    state.addPublicKey(newKey);
                }
            }

            super.doOKAction();
        }

        public String getPublicKeyContent() {
            return publicKeyArea.getText().trim();
        }
    }

    // --- RSA Private Key Dialog ---

    private static class RsaPrivateKeyDialog extends DialogWrapper {
        private final JBTextArea privateKeyArea = new JBTextArea(9, 48);
        private final JCheckBox rememberKeyCheckBox = new JCheckBox("Remember Private Key for 4 hours (auto-expires)");
        private final JBLabel cachedNoticeLabel = new JBLabel();

        public RsaPrivateKeyDialog(@Nullable Project project, String title) {
            super(project, true);
            setTitle(title != null ? title : "RSA Decryption - Private Key Prompt");

            // Check if there is a cached private key
            String cachedKey = TemporaryKeyCache.getInstance().getPrivateKey();
            if (cachedKey != null) {
                privateKeyArea.setText(cachedKey);
                rememberKeyCheckBox.setSelected(true);
                long remainingMin = TemporaryKeyCache.getInstance().getPrivateKeyRemainingMillis() / (60 * 1000);
                cachedNoticeLabel.setText("<html><small style='color:green;'>✓ Loaded remembered private key (" + remainingMin + "m remaining)</small></html>");
            } else {
                rememberKeyCheckBox.setSelected(false);
            }

            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout(5, 8));

            JPanel topPanel = new JPanel(new BorderLayout(5, 2));
            JBLabel hint = new JBLabel("<html>Please provide your <b>RSA Private Key</b> (PEM or PKCS#8 format) to decrypt the selected text:</html>");
            topPanel.add(hint, BorderLayout.NORTH);
            if (!cachedNoticeLabel.getText().isEmpty()) {
                topPanel.add(cachedNoticeLabel, BorderLayout.SOUTH);
            }
            panel.add(topPanel, BorderLayout.NORTH);

            privateKeyArea.setLineWrap(true);
            panel.add(new JBScrollPane(privateKeyArea), BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout(5, 3));
            bottomPanel.add(rememberKeyCheckBox, BorderLayout.NORTH);

            JBLabel note = new JBLabel("<html><small>Note: Private keys are processed in memory and never written to permanent disk storage.</small></html>");
            note.setForeground(Color.GRAY);
            bottomPanel.add(note, BorderLayout.SOUTH);

            panel.add(bottomPanel, BorderLayout.SOUTH);

            return panel;
        }

        @Nullable
        @Override
        public JComponent getPreferredFocusedComponent() {
            return privateKeyArea;
        }

        @Override
        protected void doOKAction() {
            String content = privateKeyArea.getText().trim();
            if (content.isEmpty()) {
                Messages.showErrorDialog("Private Key content cannot be empty.", "Validation Error");
                return;
            }

            try {
                RsaCrypto.parsePrivateKey(content);
            } catch (Exception ex) {
                Messages.showErrorDialog("Invalid RSA Private Key: " + ex.getMessage(), "Key Validation Error");
                return;
            }

            if (rememberKeyCheckBox.isSelected()) {
                TemporaryKeyCache.getInstance().putPrivateKey(content);
            } else {
                TemporaryKeyCache.getInstance().removePrivateKey();
            }

            super.doOKAction();
        }

        public String getPrivateKeyContent() {
            return privateKeyArea.getText().trim();
        }
    }
}
