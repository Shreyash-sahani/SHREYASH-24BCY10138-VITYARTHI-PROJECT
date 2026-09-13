import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * RegisterPanel
 * Registration screen with a live password-strength meter that updates
 * as the user types, so they get feedback before submitting.
 */
public class RegisterPanel extends GradientPanel {

    private final Main app;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JPasswordField confirmField;
    private final JProgressBar strengthBar;
    private final JLabel strengthLabel;
    private final JLabel statusLabel;

    public RegisterPanel(Main app) {
        super(UITheme.BG_TOP, UITheme.BG_BOTTOM);
        this.app = app;
        setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(24, UITheme.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(36, 44, 32, 44));
        card.setPreferredSize(new Dimension(400, 560));
        card.setMaximumSize(new Dimension(400, 560));

        JLabel title = new JLabel("Create Account");
        title.setFont(UITheme.FONT_TITLE.deriveFont(24f));
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Your password never leaves this device unhashed.");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = UIFactory.textField();
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField = UIFactory.passwordField();
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmField = UIFactory.passwordField();
        confirmField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel usernameLabel = UIFactory.fieldLabel("USERNAME");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel passwordLabel = UIFactory.fieldLabel("PASSWORD");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel confirmLabel = UIFactory.fieldLabel("CONFIRM PASSWORD");
        confirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        strengthBar = new JProgressBar(0, 5);
        strengthBar.setValue(0);
        strengthBar.setStringPainted(false);
        strengthBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        strengthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        strengthBar.setForeground(UITheme.DANGER);
        strengthBar.setBackground(UITheme.FIELD_BG);

        strengthLabel = new JLabel(" ");
        strengthLabel.setFont(UITheme.FONT_LABEL);
        strengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateStrength();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateStrength();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateStrength();
            }
        });

        statusLabel = UIFactory.statusLabel();
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        var registerButton = UIFactory.primaryButton("Create Account");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> attemptRegister());
        confirmField.addActionListener(e -> attemptRegister());

        var loginLink = UIFactory.linkButton("Already have an account? Log in");
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLink.addActionListener(e -> app.showLogin());

        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(22));
        card.add(usernameLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(14));
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(6));
        card.add(strengthBar);
        card.add(Box.createVerticalStrut(4));
        card.add(strengthLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(confirmLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(confirmField);
        card.add(Box.createVerticalStrut(18));
        card.add(registerButton);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(loginLink);

        GridBagConstraints gbc = new GridBagConstraints();
        add(card, gbc);
    }

    private void updateStrength() {
        String password = new String(passwordField.getPassword());
        int score = PasswordStrengthChecker.score(password);
        strengthBar.setValue(score);
        String rating = PasswordStrengthChecker.rating(score);
        Color color;
        if (score <= 2) {
            color = UITheme.DANGER;
        } else if (score <= 4) {
            color = UITheme.WARNING;
        } else {
            color = UITheme.SUCCESS;
        }
        strengthBar.setForeground(color);
        strengthLabel.setForeground(color);
        strengthLabel.setText(password.isEmpty() ? " " : (rating + " (" + score + "/5)"));
    }

    private void attemptRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please fill in all fields.", UITheme.DANGER);
            return;
        }
        if (!password.equals(confirm)) {
            showStatus("Passwords do not match.", UITheme.DANGER);
            return;
        }

        boolean created = app.getAuthManager().registerUser(username, password);
        if (created) {
            showStatus("Account created! You can now log in.", UITheme.SUCCESS);
            reset();
            app.showLogin();
        } else {
            showStatus("Username already exists.", UITheme.DANGER);
        }
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    /** Clears the form fields. */
    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        confirmField.setText("");
        statusLabel.setText(" ");
        strengthBar.setValue(0);
        strengthLabel.setText(" ");
    }
}
