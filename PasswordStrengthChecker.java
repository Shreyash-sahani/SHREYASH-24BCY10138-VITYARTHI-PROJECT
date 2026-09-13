/**
 * PasswordStrengthChecker
 * Scores a password on 5 criteria (length, uppercase, lowercase, digit,
 * special character). Used both by the GUI strength meter (score/rating)
 * and anywhere a plain text summary is needed (checkStrength).
 */
public class PasswordStrengthChecker {

    /** Returns a score from 0 to 5 based on the number of criteria met. */
    public static int score(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{};:,.<>?/].*")) score++;
        return score;
    }

    /** Converts a 0-5 score into a human readable rating. */
    public static String rating(int score) {
        switch (score) {
            case 5:
                return "STRONG";
            case 3:
            case 4:
                return "MODERATE";
            default:
                return "WEAK";
        }
    }

    /** Returns bullet-point suggestions for any criteria not yet met. */
    public static String suggestions(String password) {
        StringBuilder feedback = new StringBuilder();
        if (password.length() < 8) {
            feedback.append("- Use at least 8 characters\n");
        }
        if (!password.matches(".*[A-Z].*")) {
            feedback.append("- Add an uppercase letter\n");
        }
        if (!password.matches(".*[a-z].*")) {
            feedback.append("- Add a lowercase letter\n");
        }
        if (!password.matches(".*[0-9].*")) {
            feedback.append("- Add a digit\n");
        }
        if (!password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{};:,.<>?/].*")) {
            feedback.append("- Add a special character (e.g. !@#$%)\n");
        }
        return feedback.toString();
    }

    /** Convenience method combining score, rating, and suggestions into one block of text. */
    public static String checkStrength(String password) {
        int s = score(password);
        StringBuilder result = new StringBuilder();
        result.append("Strength: ").append(rating(s)).append(" (").append(s).append("/5)\n");
        String tips = suggestions(password);
        if (!tips.isEmpty()) {
            result.append("Suggestions:\n").append(tips);
        }
        return result.toString();
    }
}
