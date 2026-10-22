package com.atagon.cryptography.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistent configuration state for Cryptography Toolbox.
 * Project by atagon GmbH (www.atagon.com).
 */
@State(
        name = "com.atagon.cryptography.settings.CryptographySettingsState",
        storages = @Storage("CryptographyToolboxSettings.xml")
)
public class CryptographySettingsState implements PersistentStateComponent<CryptographySettingsState> {

    public List<SavedPublicKey> publicKeys = new ArrayList<>();

    public static CryptographySettingsState getInstance() {
        return ApplicationManager.getApplication().getService(CryptographySettingsState.class);
    }

    @Nullable
    @Override
    public CryptographySettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CryptographySettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public List<SavedPublicKey> getPublicKeys() {
        if (publicKeys == null) {
            publicKeys = new ArrayList<>();
        }
        return publicKeys;
    }

    public void setPublicKeys(List<SavedPublicKey> publicKeys) {
        this.publicKeys = publicKeys != null ? new ArrayList<>(publicKeys) : new ArrayList<>();
    }

    public void addPublicKey(SavedPublicKey key) {
        getPublicKeys().add(key);
    }

    public void removePublicKey(SavedPublicKey key) {
        getPublicKeys().remove(key);
    }
}
