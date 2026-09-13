import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * AuthManager
 * Handles user registration and login for the vault application.
 * Each user record stored in users.txt has the form:
 *      username:saltHex:passwordHashHex
 * The password is never stored in plain text - only a salted SHA-256
 * hash is persisted, so even if users.txt is leaked the raw passwords
 * cannot be recovered directly.
 */
public class AuthManager {

    private static final String USER_FILE = "users.txt";
    private static final int SALT_LENGTH = 16; // bytes

    /** Registers a new user. Returns false if the username already exists. */
    public boolean registerUser(String username, String password) {
        if (userExists(username)) {
            return false;
        }
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            String saltHex = HashUtil.bytesToHex(salt);
            String hashHex = HashUtil.hashPassword(password, salt);

            try (FileWriter fw = new FileWriter(USER_FILE, true)) {
                fw.write(username + ":" + saltHex + ":" + hashHex + System.lineSeparator());
            }
            ActivityLogger.log("New user registered: " + username);
            return true;
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }

    /** Verifies a username/password pair against the stored records. */
    public boolean loginUser(String username, String password) {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(":");
                if (parts.length != 3) {
                    continue;
                }
                String storedUsername = parts[0];
                String saltHex = parts[1];
                String storedHash = parts[2];

                if (storedUsername.equals(username)) {
                    byte[] salt = HashUtil.hexToBytes(saltHex);
                    String computedHash = HashUtil.hashPassword(password, salt);
                    boolean match = computedHash.equals(storedHash);
                    ActivityLogger.log("Login attempt for '" + username + "': "
                            + (match ? "SUCCESS" : "FAILED"));
                    return match;
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Error during login: " + e.getMessage());
            return false;
        }
        ActivityLogger.log("Login attempt for '" + username + "': FAILED (no such user)");
        return false;
    }

    /** Checks whether a username is already registered. */
    public boolean userExists(String username) {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user file: " + e.getMessage());
        }
        return false;
    }
}
