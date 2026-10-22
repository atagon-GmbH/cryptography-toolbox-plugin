package com.atagon.cryptography.settings;

import java.util.Objects;

/**
 * Model representing a saved public key.
 * Project by atagon GmbH (www.atagon.com).
 */
public class SavedPublicKey {

    private String name;
    private String keyContent;
    private String algorithm = "RSA";
    private String description = "";

    public SavedPublicKey() {
    }

    public SavedPublicKey(String name, String keyContent, String algorithm, String description) {
        this.name = name;
        this.keyContent = keyContent;
        this.algorithm = algorithm != null ? algorithm : "RSA";
        this.description = description != null ? description : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyContent() {
        return keyContent;
    }

    public void setKeyContent(String keyContent) {
        this.keyContent = keyContent;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SavedPublicKey that = (SavedPublicKey) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(keyContent, that.keyContent) &&
                Objects.equals(algorithm, that.algorithm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, keyContent, algorithm);
    }

    @Override
    public String toString() {
        return name != null ? name : "Unnamed Key";
    }
}
