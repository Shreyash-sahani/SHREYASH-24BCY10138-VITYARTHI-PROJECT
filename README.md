# SHREYASH-24BCY10138-VITYARTHI-PROJECT
# SecureVault — Secure File Encryption & Password Management System (GUI Edition)

A desktop Java Swing application (cyber-security domain) with a modern,
web-app-style interface: gradient background, card-based login/register
screens, and a tabbed dashboard for file encryption, decryption, password
strength checking, and file-integrity verification. Built entirely with
the standard Java library — **no external dependencies**.

## Features

- **Login / Register screens** — gradient background with a centered
  white card, live password-strength meter while creating an account.
  Passwords are never stored in plain text — only a random salt + SHA-256
  hash is saved to `users.txt`.
- **Encrypt File tab** — AES-256-GCM authenticated encryption. The AES key
  is derived from the user's password with PBKDF2WithHmacSHA256 (65,536
  iterations). File pickers let you browse for the source file and choose
  where to save the encrypted output.
- **Decrypt File tab** — restores the original file; fails with a clear
  message (instead of corrupt output) if the password is wrong or the
  file was tampered with, thanks to GCM's authentication tag.
- **Password Strength tab** — live 0–5 strength meter and suggestions as
  you type.
- **Integrity Check tab** — compute a SHA-256 checksum of any file.
- **Activity Log tab** — read-only view of `activity_log.txt`, refreshable
  from the UI, showing every login, encryption, and decryption with a
  timestamp.

## Requirements

- JDK 17 or later (Swing + AWT are part of the standard library — nothing
  extra to install)

## How to Compile & Run

```bash
# From the folder containing all the .java files:
javac *.java
java Main
```

A window titled **"SecureVault - Java Security Toolkit"** opens showing
the login screen. Click **"New here? Create an account"** to register
first.

## Project Structure

| File                          | Purpose                                                     |
|--------------------------------|---------------------------------------------------------------|
| `Main.java`                    | App entry point; CardLayout navigation between screens        |
| `LoginPanel.java`               | Login screen UI                                               |
| `RegisterPanel.java`            | Registration screen UI with live strength meter               |
| `DashboardPanel.java`           | Main tabbed screen (encrypt/decrypt/strength/integrity/log)   |
| `AuthManager.java`              | Registration / login logic, salted password hashing           |
| `CryptoUtil.java`               | AES-256-GCM encryption/decryption, PBKDF2 key derivation      |
| `HashUtil.java`                 | SHA-256 helpers (hashing + file checksums)                    |
| `PasswordStrengthChecker.java`  | Password strength scoring (0–5) and suggestions               |
| `ActivityLogger.java`           | Timestamped audit logging                                     |
| `UITheme.java`                  | Shared colors and fonts                                       |
| `UIFactory.java`                | Factory methods for consistently styled buttons/fields/labels |
| `GradientPanel.java`            | Reusable gradient-background panel                             |
| `RoundedPanel.java`             | Reusable rounded-corner "card" panel                            |

## Encrypted File Format

```
[ 16-byte random salt ][ 12-byte random IV ][ AES-GCM ciphertext + 16-byte auth tag ]
```

Everything needed to decrypt (except the password) is embedded in the
file itself, so no separate key file has to be managed.

## Notes

- `users.txt` and `activity_log.txt` are created in the working directory
  the first time they're needed.
- The look and feel defaults to your OS's native Swing theme
  (`UIManager.getSystemLookAndFeelClassName()`), so buttons/menus match
  Windows, macOS, or Linux automatically; the gradient background and
  card styling are custom-painted on top of that.
