# Cryptography Toolbox for JetBrains IDEs

A lightweight, powerful cryptographic toolbox plugin for JetBrains IDEs (WebStorm, IntelliJ IDEA, PyCharm, PhpStorm, GoLand, CLion, and more).

Developed by [atagon GmbH](https://www.atagon.com).

---

## Features

- **Text Selection Cryptography:** Quickly encrypt, decrypt, or hash any highlighted text directly inside your code editor and replace it in place.
- **Supported Encryption Algorithms:**
  - **AES-256-GCM** (Authenticated encryption with 128-bit GCM tag and random 96-bit IV)
  - **AES (CBC)** (256-bit AES-CBC with PKCS5 padding and random IV, PBKDF derivation)
  - **Blowfish** (128-bit Blowfish-CBC with PKCS5 padding and random IV)
  - **RSA-OAEP** (Modern RSA-OAEP with SHA-256 and MGF1, with multi-block chunking support)
  - **RSA (PKCS#1)** (PKCS#1 v1.5 with support for multi-block chunking of longer text)
  - **Base64** (Standard RFC 4648 encoding)
  - **Base32** (RFC 4648 Base32 encoding)
- **Supported Decryption Algorithms:**
  - **AES-256-GCM** (Authenticated decryption with integrity check)
  - **AES (CBC)**
  - **Blowfish**
  - **RSA-OAEP** (Requires private key input via prompt)
  - **RSA (PKCS#1)** (Requires private key input via prompt)
  - **Base64**
  - **Base32**
- **Supported Hashing Algorithms:**
  - **SHA-256**
  - **SHA-512**
  - **MD5**
- **Public Key Management & Reusability:** Save, manage, and label RSA public keys in IDE Settings or check the "Save this Public Key to Settings for reuse" box directly when encrypting text.
- **4-Hour Key Remembering Timer:** Check the "Remember for 4 hours" box during passphrase or private key prompts to cache keys in memory with an automatic 4-hour expiration timer.
- **Built-in RSA Key Pair Generator:** Generate 2048-bit RSA key pairs directly from the Settings dialog.

---

## How to Use

### 1. Encrypting Selected Text
1. Highlight any text in your editor.
2. Right-click to open the context menu (or use the **Tools** / **Edit** &rarr; **Cryptography Toolbox** menu).
3. Select **Encrypt Selected Text** and choose your algorithm:
   - **AES-256-GCM / AES / Blowfish:** Enter a passphrase when prompted. Optionally check **Remember passphrase for 4 hours** to avoid re-entering it for subsequent operations.
   - **RSA-OAEP / RSA:** Select a previously saved public key or paste a PEM/Base64 public key directly. Optionally check **Save this Public Key to Settings for reuse** to keep it available in Settings and future prompts.
   - **Base64 / Base32:** Instantly encodes the selected text.
4. The selected text is replaced in-place with the encrypted output.

### 2. Decrypting Selected Text
1. Highlight encrypted text (Base64-encoded ciphertext).
2. Right-click and navigate to **Decrypt Selected Text**.
3. Select the decryption algorithm:
   - **AES-256-GCM / AES / Blowfish:** Enter the decryption passphrase (pre-filled if remembered).
   - **RSA-OAEP / RSA:** Paste your private key (PKCS#8 or PEM format). Optionally check **Remember Private Key for 4 hours** to store it in memory temporarily.
   - **Base64 / Base32:** Instantly decodes the selected text.
4. The ciphertext is replaced with the decrypted plaintext.

### 3. Hashing Selected Text
1. Highlight any text in your editor.
2. Right-click and choose **Hash Selected Text**.
3. Select **SHA-256**, **SHA-512**, or **MD5**.
4. The selected text is replaced with the computed hexadecimal hash.

### 4. Managing Public Keys & Generating Keys
1. Open IDE Settings/Preferences:
   - **macOS:** `Preferences / Settings` (`⌘,`) &rarr; **Tools** &rarr; **Cryptography Toolbox**
   - **Windows / Linux:** `File` &rarr; `Settings` (`Ctrl+Alt+S`) &rarr; **Tools** &rarr; **Cryptography Toolbox**
2. **Add / Remove Public Keys:** Store named RSA public keys to quickly select them during encryption.
3. **Generate Key Pair:** Click **Generate New RSA Key Pair (2048-bit)** to generate both a public key and private key. Save the public key to your toolbox and store the private key in a secure location.

---

## Installation

### Option 1: Install from ZIP Archive

1. Build the distribution package using Gradle:
   ```bash
   ./gradlew buildPlugin
   ```
2. The packaged plugin archive will be generated at:
   ```
   build/distributions/cryptography-toolbox-1.1.0.zip
   ```
3. In your JetBrains IDE:
   - Go to **Settings / Preferences** &rarr; **Plugins**.
   - Click the gear icon (**⚙️**) at the top right and select **Install Plugin from Disk...**.
   - Choose `build/distributions/cryptography-toolbox-1.1.0.zip`.
   - Click **Apply** / **OK** and restart the IDE if prompted.

### Option 2: Run in a Sandboxed IDE Instance

To launch a development IDE instance with the plugin pre-installed:
```bash
./gradlew runIde
```

---

## Building and Testing

- **Run Tests:**
  ```bash
  ./gradlew test
  ```
- **Build Plugin:**
  ```bash
  ./gradlew build
  ```

---

## Requirements

- **Java Runtime:** Java 17 or higher
- **Compatible IDEs:** IntelliJ IDEA, WebStorm, PyCharm, PhpStorm, GoLand, CLion, Rider, Android Studio (version 2024.1 or newer).

---

## License & Attribution

This project is licensed under the [MIT License](LICENSE).

Developed and maintained by **[atagon GmbH](https://www.atagon.com)**.
