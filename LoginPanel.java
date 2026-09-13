import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * LoginPanel
 * The app's landing screen: a gradient background with a centered white
 * "card" containing the username/password login form.
 */
public class LoginPanel extends GradientPanel {

    private final Main app;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JLabel statusLabel;

    public LoginPanel(Main app) {
        super(UITheme.BG_TOP, UITheme.BG_BOTTOM);
        this.app = app;
        setLayout(new GridBagLayout());

        RoundedPanel card = new RoundedPanel(24, UITheme.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 44, 36, 44));
        card.setPreferredSize(new Dimension(380, 420));
        card.setMaximumSize(new Dimension(380, 420));

        JLabel lockIcon = new JLabel("\uD83D\uDD12");
        lockIcon.setFont(new Font("SansSerif", Font.PLAIN, 36));
        lockIcon.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("SecureVault");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_DARK);
        title.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Encrypted File & Password Vault");
        subtitle.setFont(UITheme.FONT_SUBTITLE);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        usernameField = UIFactory.textField();
        usernameField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        passwordField = UIFactory.passwordField();
        passwordField.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        JLabel usernameLabel = UIFactory.fieldLabel("USERNAME");
        usernameLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        JLabel passwordLabel = UIFactory.fieldLabel("PASSWORD");
        passwordLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        statusLabel = UIFactory.statusLabel();
        statusLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel loginButtonWrap = new JPanel();
        loginButtonWrap.setOpaque(false);
        loginButtonWrap.setLayout(new BoxLayout(loginButtonWrap, BoxLayout.X_AXIS));
        loginButtonWrap.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        var loginButton = UIFactory.primaryButton("Login");
        loginButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());

        var registerLink = UIFactory.linkButton("New here? Create an account");
        registerLink.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        registerLink.addActionListener(e -> app.showRegister());

        card.add(lockIcon);
        card.add(Box.createVerticalStrut(6));
        card.add(title);
        card.add(subtitle);
        card.add(Box.createVerticalStrut(26));
        card.add(usernameLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(usernameField);
        card.add(Box.createVerticalStrut(16));
        card.add(passwordLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(18));
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));
        card.add(statusLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(registerLink);

        GridBagConstraints gbc = new GridBagConstraints();
        add(card, gbc);
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password.", UITheme.DANGER);
            return;
        }

        boolean ok = app.getAuthManager().loginUser(username, password);
        if (ok) {
            showStatus("Login successful!", UITheme.SUCCESS);
            passwordField.setText("");
            app.showDashboard(username);
        } else {
            showStatus("Invalid username or password.", UITheme.DANGER);
        }
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    /** Clears the form fields (called when the panel is shown after a logout). */
    public void reset() {
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
    }
}
