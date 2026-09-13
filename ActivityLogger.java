import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ActivityLogger
 * Appends timestamped activity entries (login attempts, encryption /
 * decryption operations, errors) to activity_log.txt so every security
 * relevant action on the vault is auditable.
 */
public class ActivityLogger {

    private static final String LOG_FILE = "activity_log.txt";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = "[" + timestamp + "] " + message;
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(line + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Warning: could not write to activity log (" + e.getMessage() + ")");
        }
    }
}
