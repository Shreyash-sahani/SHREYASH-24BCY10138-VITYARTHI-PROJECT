import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * CryptoUtil
 * Core encryption engine of SecureVault.
 *
 * Design:
 *  - The user's master password is never used directly as the AES key.
 *    Instead PBKDF2WithHmacSHA256 (65,536 iterations) stretches the
 *    password + a random 16-byte salt into a 256-bit AES key. This
 *    defends against brute-force / rainbow-table attacks on weak
 *    passwords.
 *  - AES/GCM/NoPadding is an authenticated encryption mode: besides
 *    confidentiality it also detects tampering. If the file is modified
 *    or the wrong password is supplied, decryption fails loudly instead
 *    of silently returning garbage.
 *  - Every encrypted file is self-contained. The output layout is:
 *        [ 16-byte salt ][ 12-byte IV ][ ciphertext + 16-byte auth tag ]
 *    so no separate key file needs to be distributed.
 */
public class CryptoUtil {

    private static final int SALT_LENGTH = 16;      // bytes
    private static final int IV_LENGTH = 12;         // bytes (GCM standard)
    private static final int GCM_TAG_LENGTH = 128;    // bits
    private static final int PBKDF2_ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;        // bits (AES-256)

    /** Derives a 256-bit AES key from a password and salt using PBKDF2. */
    private static SecretKey deriveKey(char[] password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        spec.clearPassword();
        return new SecretKeySpec(keyBytes, "AES");
    }

    /** Reads an entire file into memory as a byte array. */
    private static byte[] readAllBytes(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(path)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            int read;
            while ((read = fis.read(chunk)) != -1) {
                buffer.write(chunk, 0, read);
            }
            return buffer.toByteArray();
        }
    }

    /**
     * Encrypts inputPath with AES-256-GCM using a key derived from password,
     * and writes [salt][iv][ciphertext] to outputPath.
     */
    public static void encryptFile(String inputPath, String outputPath, String password)
            throws IOException, GeneralSecurityException {

        byte[] plainText = readAllBytes(inputPath);

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKey key = deriveKey(password.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        byte[] cipherText = cipher.doFinal(plainText);

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(salt);
            fos.write(iv);
            fos.write(cipherText);
        }
    }

    /**
     * Decrypts a file previously produced by encryptFile(). Throws
     * GeneralSecurityException (specifically an authentication-tag failure)
     * if the password is wrong or the file has been tampered with.
     */
    public static void decryptFile(String inputPath, String outputPath, String password)
            throws IOException, GeneralSecurityException {

        byte[] fileBytes = readAllBytes(inputPath);
        if (fileBytes.length < SALT_LENGTH + IV_LENGTH) {
            throw new IOException("File is too short to be a valid SecureVault archive.");
        }

        byte[] salt = Arrays.copyOfRange(fileBytes, 0, SALT_LENGTH);
        byte[] iv = Arrays.copyOfRange(fileBytes, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
        byte[] cipherText = Arrays.copyOfRange(fileBytes, SALT_LENGTH + IV_LENGTH, fileBytes.length);

        SecretKey key = deriveKey(password.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

        byte[] plainText = cipher.doFinal(cipherText); // throws AEADBadTagException on wrong password / tampering

        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(plainText);
        }
    }
}
