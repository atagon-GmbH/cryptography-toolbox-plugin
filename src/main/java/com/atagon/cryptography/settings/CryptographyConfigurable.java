package com.atagon.cryptography.settings;

import com.atagon.cryptography.crypto.RsaCrypto;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

/**
 * Settings / Preferences configurable UI for Cryptography Toolbox.
 * Project by atagon GmbH (www.atagon.com).
 */
public class CryptographyConfigurable implements Configurable {

    private JPanel mainPanel;
    private final List<SavedPublicKey> keyList = new ArrayList<>();
    private PublicKeyTableModel tableModel;
    private JBTable table;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Cryptography Toolbox";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mainPanel = new JPanel(new BorderLayout(0, 10));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        JBLabel titleLabel = new JBLabel("<html><b>Cryptography Toolbox</b> by <a href='https://www.atagon.com'>atagon GmbH</a> (www.atagon.com)</html>");
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                BrowserUtil.browse("https://www.atagon.com");
            }
        });
        JBLabel descLabel = new JBLabel("Manage saved Public Keys for RSA encryption and cryptographic workflows.");
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descLabel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        tableModel = new PublicKeyTableModel(keyList);
        table = new JBTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel tablePanel = ToolbarDecorator.createDecorator(table)
                .setAddAction(button -> addKeyDialog())
                .setEditAction(button -> editKeyDialog())
                .setRemoveAction(button -> removeKey())
                .addExtraAction(new com.intellij.openapi.actionSystem.AnAction("Generate RSA Key Pair", "Generate a new 2048-bit RSA key pair", null) {
                    @Override
                    public void actionPerformed(com.intellij.openapi.actionSystem.AnActionEvent e) {
                        generateKeyPairDialog();
                    }
                })
                .createPanel();

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JBLabel helpText = new JBLabel("Saved public keys will be accessible when performing RSA encryption across your workspace.");
        helpText.setEnabled(false);
        footerPanel.add(helpText);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        reset();
        return mainPanel;
    }

    private void addKeyDialog() {
        KeyEditDialog dialog = new KeyEditDialog(null);
        if (dialog.showAndGet()) {
            SavedPublicKey newKey = dialog.getKey();
            keyList.add(newKey);
            tableModel.fireTableDataChanged();
        }
    }

    private void editKeyDialog() {
        int selected = table.getSelectedRow();
        if (selected >= 0 && selected < keyList.size()) {
            SavedPublicKey existing = keyList.get(selected);
            KeyEditDialog dialog = new KeyEditDialog(existing);
            if (dialog.showAndGet()) {
                SavedPublicKey updated = dialog.getKey();
                existing.setName(updated.getName());
                existing.setAlgorithm(updated.getAlgorithm());
                existing.setKeyContent(updated.getKeyContent());
                existing.setDescription(updated.getDescription());
                tableModel.fireTableRowsUpdated(selected, selected);
            }
        }
    }

    private void removeKey() {
        int selected = table.getSelectedRow();
        if (selected >= 0 && selected < keyList.size()) {
            keyList.remove(selected);
            tableModel.fireTableDataChanged();
        }
    }

    private void generateKeyPairDialog() {
        try {
            RsaCrypto.KeyPairPem keyPair = RsaCrypto.generateKeyPair(2048);
            GenerateKeyDialog dialog = new GenerateKeyDialog(keyPair);
            if (dialog.showAndGet()) {
                SavedPublicKey newKey = new SavedPublicKey(dialog.getKeyName(), keyPair.getPublicKeyPem(), "RSA", "Generated RSA Key Pair");
                keyList.add(newKey);
                tableModel.fireTableDataChanged();
                Messages.showInfoMessage("Public key '" + dialog.getKeyName() + "' added to saved keys list.\nEnsure you have safely copied your Private Key!", "RSA Key Pair Generated");
            }
        } catch (Exception ex) {
            Messages.showErrorDialog("Failed to generate RSA Key Pair: " + ex.getMessage(), "Key Generation Error");
        }
    }

    @Override
    public boolean isModified() {
        CryptographySettingsState state = CryptographySettingsState.getInstance();
        if (state == null) return false;
        List<SavedPublicKey> stored = state.getPublicKeys();
        if (stored.size() != keyList.size()) return true;
        for (int i = 0; i < stored.size(); i++) {
            if (!stored.get(i).equals(keyList.get(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        CryptographySettingsState state = CryptographySettingsState.getInstance();
        if (state != null) {
            state.setPublicKeys(new ArrayList<>(keyList));
        }
    }

    @Override
    public void reset() {
        keyList.clear();
        CryptographySettingsState state = CryptographySettingsState.getInstance();
        if (state != null) {
            for (SavedPublicKey key : state.getPublicKeys()) {
                keyList.add(new SavedPublicKey(key.getName(), key.getKeyContent(), key.getAlgorithm(), key.getDescription()));
            }
        }
        if (tableModel != null) {
            tableModel.fireTableDataChanged();
        }
    }

    // --- Table Model ---

    private static class PublicKeyTableModel extends AbstractTableModel {
        private final String[] columns = {"Name", "Algorithm", "Description", "Key Preview"};
        private final List<SavedPublicKey> keys;

        public PublicKeyTableModel(List<SavedPublicKey> keys) {
            this.keys = keys;
        }

        @Override
        public int getRowCount() {
            return keys.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            SavedPublicKey key = keys.get(rowIndex);
            switch (columnIndex) {
                case 0: return key.getName();
                case 1: return key.getAlgorithm();
                case 2: return key.getDescription();
                case 3:
                    String content = key.getKeyContent() != null ? key.getKeyContent().replaceAll("\\s+", " ") : "";
                    return content.length() > 40 ? content.substring(0, 40) + "..." : content;
                default: return "";
            }
        }
    }

    // --- Dialogs ---

    private static class KeyEditDialog extends DialogWrapper {
        private final JBTextField nameField = new JBTextField();
        private final JBTextField algorithmField = new JBTextField("RSA");
        private final JBTextArea contentArea = new JBTextArea(8, 45);
        private final JBTextField descField = new JBTextField();

        public KeyEditDialog(@Nullable SavedPublicKey existing) {
            super(true);
            setTitle(existing == null ? "Add Public Key" : "Edit Public Key");
            algorithmField.setEditable(false);
            if (existing != null) {
                nameField.setText(existing.getName());
                algorithmField.setText(existing.getAlgorithm());
                contentArea.setText(existing.getKeyContent());
                descField.setText(existing.getDescription());
            }
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
            panel.add(new JBLabel("Key Name:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            panel.add(nameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
            panel.add(new JBLabel("Algorithm:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            panel.add(algorithmField, gbc);

            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
            panel.add(new JBLabel("Description:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1.0;
            panel.add(descField, gbc);

            gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHWEST;
            panel.add(new JBLabel("Public Key (PEM / Base64):"), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
            contentArea.setLineWrap(true);
            panel.add(new JBScrollPane(contentArea), gbc);

            return panel;
        }

        @Override
        protected void doOKAction() {
            String name = nameField.getText().trim();
            String content = contentArea.getText().trim();
            if (name.isEmpty()) {
                Messages.showErrorDialog("Key name cannot be empty.", "Validation Error");
                return;
            }
            if (content.isEmpty()) {
                Messages.showErrorDialog("Key content cannot be empty.", "Validation Error");
                return;
            }
            try {
                RsaCrypto.parsePublicKey(content);
            } catch (Exception ex) {
                Messages.showErrorDialog("Invalid RSA Public Key format: " + ex.getMessage(), "Key Validation Error");
                return;
            }
            super.doOKAction();
        }

        public SavedPublicKey getKey() {
            return new SavedPublicKey(
                    nameField.getText().trim(),
                    contentArea.getText().trim(),
                    algorithmField.getText().trim(),
                    descField.getText().trim()
            );
        }
    }

    private static class GenerateKeyDialog extends DialogWrapper {
        private final JBTextField nameField = new JBTextField("My RSA Key");
        private final JBTextArea privateKeyArea = new JBTextArea(8, 45);
        private final JBTextArea publicKeyArea = new JBTextArea(6, 45);

        public GenerateKeyDialog(RsaCrypto.KeyPairPem keyPair) {
            super(true);
            setTitle("Generate New RSA Key Pair");
            privateKeyArea.setText(keyPair.getPrivateKeyPem());
            privateKeyArea.setEditable(false);
            publicKeyArea.setText(keyPair.getPublicKeyPem());
            publicKeyArea.setEditable(false);
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout(5, 10));

            JPanel topPanel = new JPanel(new BorderLayout(5, 5));
            topPanel.add(new JBLabel("Key Pair Name:"), BorderLayout.WEST);
            topPanel.add(nameField, BorderLayout.CENTER);
            panel.add(topPanel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 10));

            // Public key section
            JPanel pubPanel = new JPanel(new BorderLayout(5, 5));
            pubPanel.add(new JBLabel("Generated Public Key (will be saved):"), BorderLayout.NORTH);
            pubPanel.add(new JBScrollPane(publicKeyArea), BorderLayout.CENTER);
            centerPanel.add(pubPanel);

            // Private key section
            JPanel privPanel = new JPanel(new BorderLayout(5, 5));
            JPanel privHeader = new JPanel(new BorderLayout());
            JBLabel warningLabel = new JBLabel("<html><b>Private Key</b> (Copy and store securely - it will NOT be saved):</html>");
            warningLabel.setForeground(Color.RED.darker());
            JButton copyBtn = new JButton("Copy Private Key");
            copyBtn.addActionListener(e -> {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(privateKeyArea.getText()), null);
                Messages.showInfoMessage("Private Key copied to clipboard!", "Copied");
            });
            privHeader.add(warningLabel, BorderLayout.WEST);
            privHeader.add(copyBtn, BorderLayout.EAST);
            privPanel.add(privHeader, BorderLayout.NORTH);
            privPanel.add(new JBScrollPane(privateKeyArea), BorderLayout.CENTER);
            centerPanel.add(privPanel);

            panel.add(centerPanel, BorderLayout.CENTER);
            return panel;
        }

        public String getKeyName() {
            return nameField.getText().trim();
        }
    }
}
