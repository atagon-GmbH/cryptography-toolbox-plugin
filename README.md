# Cryptography Toolbox for JetBrains IDEs

[![JetBrains Marketplace](https://img.shields.io/badge/JetBrains%20Marketplace-Plugin%20%2335389-087CFA?logo=jetbrains&logoColor=white)](https://plugins.jetbrains.com/plugin/35389-cryptography-toolbox)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Compatible IDEs](https://img.shields.io/badge/JetBrains%20IDEs-2024.2+-000000.svg?logo=intellijidea&logoColor=white)](https://plugins.jetbrains.com/plugin/35389-cryptography-toolbox)

A fast, developer-friendly cryptographic toolbox plugin for JetBrains IDEs (IntelliJ IDEA, WebStorm, PyCharm, PhpStorm, GoLand, CLion, Rider, Android Studio, and more).

Perform encryption, decryption, hashing, and encoding on highlighted text directly inside your code editor with in-place text replacement.

Available on the [JetBrains Marketplace: Cryptography Toolbox](https://plugins.jetbrains.com/plugin/35389-cryptography-toolbox).


---

## Key Features

- **In-Place Editor Actions:** Highlight any text and encrypt, decrypt, hash, or encode it directly via the context menu or main menu.
- **Symmetric Encryption & Decryption:**
  - **AES-256-GCM:** Authenticated symmetric encryption with random 96-bit IV and 128-bit authentication tag for integrity verification.
  - **AES-256 (CBC):** AES-CBC with PKCS5 padding, random IV, and key derivation.
  - **Blowfish:** 128-bit Blowfish-CBC encryption with PKCS5 padding and random IV.
- **Asymmetric Encryption & Decryption (RSA):**
  - **RSA-OAEP:** Modern, secure RSA encryption using OAEP with SHA-256 and MGF1, including multi-block chunking for arbitrary text lengths.
  - **RSA (PKCS#1 v1.5):** Traditional RSA encryption with support for multi-block chunking.
  - Supports PEM and Base64-encoded RSA public/private keys (PKCS#8).
- **Encoding & Decoding:**
  - **Base64:** RFC 4648 standard Base64 encoding and decoding.
  - **Base32:** RFC 4648 standard Base32 encoding and decoding.
- **Cryptographic Hashing:**
  - **SHA-256** and **SHA-512** (Hexadecimal digest)
  - **MD5** (Hexadecimal digest)
- **Key Management & In-Memory Caching:**
  - **Public Key Storage:** Save and label RSA public keys in IDE Settings for instant reuse during encryption prompts.
  - **4-Hour Passphrase & Private Key Cache:** Option to remember passphrases or private keys in memory for 4 hours to avoid repetitive input.
  - **RSA Key Pair Generator:** Generate fresh 2048-bit RSA key pairs directly within IDE Settings.

---

## Installation

### Option 1: Install from JetBrains Marketplace (Recommended)

1. Open your JetBrains IDE.
2. Navigate to **Settings** / **Preferences** (`⌘,` on macOS, `Ctrl+Alt+S` on Windows/Linux) &rarr; **Plugins**.
3. Select the **Marketplace** tab.
4. Search for **Cryptography Toolbox** or visit the [JetBrains Marketplace Plugin Page](https://plugins.jetbrains.com/plugin/35389-cryptography-toolbox).
5. Click **Install** and restart the IDE if prompted.

### Option 2: Install from ZIP Archive

1. Build the distribution package using Gradle:
   ```bash
   ./gradlew buildPlugin
   ```
2. The packaged plugin ZIP is created in:
   ```
   build/distributions/cryptography-toolbox-1.1.0.zip
   ```
3. In your JetBrains IDE:
   - Go to **Settings / Preferences** &rarr; **Plugins**.
   - Click the gear icon (**⚙️**) at the top right and select **Install Plugin from Disk...**.
   - Select the generated `.zip` file.
   - Click **Apply** / **OK**.

### Option 3: Run Development Sandbox

To test the plugin in a sandboxed IDE instance:
```bash
./gradlew runIde
```

---

## Usage Guide

### 1. Encrypting Selected Text
1. Highlight the plaintext in your editor.
2. Right-click to open the context menu, or navigate to **Tools** / **Edit** &rarr; **Cryptography Toolbox** &rarr; **Encrypt Selected Text**.
3. Choose your desired algorithm:
   - **AES-256-GCM / AES (CBC) / Blowfish:** Enter your passphrase. Optionally select **Remember for 4 hours** to cache it temporarily in memory.
   - **RSA-OAEP / RSA (PKCS#1):** Select a saved public key or paste a PEM/Base64 public key. Optionally check **Save this Public Key to Settings for reuse**.
   - **Base64 / Base32:** Replaces selected text immediately with the encoded string.
4. The highlighted text is replaced in-place with the resulting ciphertext.

### 2. Decrypting Selected Text
1. Highlight the ciphertext or encoded text.
2. Right-click and choose **Decrypt Selected Text** (or use the **Tools** / **Edit** menu).
3. Select the corresponding algorithm:
   - **AES-256-GCM / AES (CBC) / Blowfish:** Enter the passphrase (pre-filled if remembered).
   - **RSA-OAEP / RSA (PKCS#1):** Provide the private key in PEM or PKCS#8 Base64 format. Optionally select **Remember for 4 hours**.
   - **Base64 / Base32:** Decodes the selected text back to plaintext.
4. The ciphertext is replaced in-place with the decrypted plaintext.

### 3. Hashing Selected Text
1. Highlight any text in your editor.
2. Right-click and select **Hash Selected Text**.
3. Choose **SHA-256**, **SHA-512**, or **MD5**.
4. The text is replaced with the computed hexadecimal hash.

### 4. Key Management & RSA Key Generation
1. Open IDE Settings:
   - **macOS:** `Preferences / Settings` (`⌘,`) &rarr; **Tools** &rarr; **Cryptography Toolbox**
   - **Windows / Linux:** `File` &rarr; `Settings` (`Ctrl+Alt+S`) &rarr; **Tools** &rarr; **Cryptography Toolbox**
2. **Manage Public Keys:** Add, edit, or delete saved RSA public keys with custom labels.
3. **Generate Key Pairs:** Click **Generate New RSA Key Pair (2048-bit)** to generate an RSA public/private key pair. Save the public key to your toolbox and store the private key securely.

---

## Building and Testing

Prerequisites: JDK 21+

- **Run Unit Tests:**
  ```bash
  ./gradlew test
  ```
- **Build Plugin Archive:**
  ```bash
  ./gradlew buildPlugin
  ```

---

## Compatibility & Requirements

- **Java Version:** Java 21 or higher
- **Supported IDEs:** JetBrains IDEs version 2024.2 or newer (IntelliJ IDEA, WebStorm, PyCharm, PhpStorm, GoLand, CLion, Rider, DataGrip, Android Studio).

---

## License & Attribution

This project is licensed under the [MIT License](LICENSE).

Developed and maintained by **[atagon GmbH](https://www.atagon.com)**.

For questions, issues, or contributions, please visit the [JetBrains Marketplace page](https://plugins.jetbrains.com/plugin/35389-cryptography-toolbox).
