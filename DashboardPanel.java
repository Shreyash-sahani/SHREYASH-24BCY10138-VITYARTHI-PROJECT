import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * DashboardPanel
 * The main application screen shown after a successful login. Presents
 * every SecureVault feature (encrypt, decrypt, password strength,
 * integrity check, activity log) as tabs inside a white card floating
 * on the app's gradient background.
 */
public class DashboardPanel extends GradientPanel {

    private final Main app;
    private final JLabel welcomeLabel;
    private final JTextArea logArea;

    public DashboardPanel(Main app) {
        super(UITheme.BG_TOP, UITheme.BG_BOTTOM);
        this.app = app;
        setLayout(new BorderLayout());

        // ---- Top bar ----
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(20, 28, 12, 28));

        JLabel appTitle = new JLabel("\uD83D\uDD12  SecureVault");
        appTitle.setFont(UITheme.FONT_HEADING);
        appTitle.setForeground(UITheme.TEXT_LIGHT);

        JPanel rightSide = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightSide.setOpaque(false);
        welcomeLabel = new JLabel("Welcome");
        welcomeLabel.setFont(UITheme.FONT_LABEL);
        welcomeLabel.setForeground(UITheme.TEXT_LIGHT);
        var logoutButton = UIFactory.secondaryButton("Logout");
        logoutButton.addActionListener(e -> app.logout());
        rightSide.add(welcomeLabel);
        rightSide.add(logoutButton);

        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(rightSide, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // ---- Card containing the tabs ----
        JPanel cardWrap = new JPanel(new BorderLayout());
        cardWrap.setOpaque(false);
        cardWrap.setBorder(new EmptyBorder(0, 28, 28, 28));

        RoundedPanel card = new RoundedPanel(20, UITheme.CARD_BG);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.FONT_LABEL);
        tabs.addTab("Encrypt File", buildEncryptTab());
        tabs.addTab("Decrypt File", buildDecryptTab());
        tabs.addTab("Password Strength", buildStrengthTab());
        tabs.addTab("Integrity Check", buildIntegrityTab());
        logArea = new JTextArea();
        tabs.addTab("Activity Log", buildLogTab());

        card.add(tabs, BorderLayout.CENTER);
        cardWrap.add(card, BorderLayout.CENTER);
        add(cardWrap, BorderLayout.CENTER);
    }

    /** Called by Main after a successful login. */
    public void setUsername(String username) {
        welcomeLabel.setText("Signed in as " + username);
        refreshLog();
    }

    // ---------------------------------------------------------------
    // Encrypt tab
    // ---------------------------------------------------------------
    private JPanel buildEncryptTab() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 30, 24, 30));

        JTextField inputField = UIFactory.textField();
        JTextField outputField = UIFactory.textField();
        JPasswordField passwordField = UIFactory.passwordField();
        JLabel statusLabel = UIFactory.statusLabel();
        JTextField checksumField = UIFactory.textField();
        checksumField.setEditable(false);
        checksumField.setFont(UITheme.FONT_MONO);

        var browseInput = UIFactory.secondaryButton("Browse...");
        browseInput.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                inputField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        var browseOutput = UIFactory.secondaryButton("Choose...");
        browseOutput.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("encrypted.enc"));
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                outputField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        var encryptButton = UIFactory.primaryButton("Encrypt File");
        encryptButton.addActionListener(e -> {
            String inputPath = inputField.getText().trim();
            String outputPath = outputField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (inputPath.isEmpty() || outputPath.isEmpty() || password.isEmpty()) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Please fill in the file, destination, and password.");
                return;
            }
            if (!new File(inputPath).exists()) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Input file not found.");
                return;
            }
            try {
                String checksum = HashUtil.fileChecksum(inputPath);
                CryptoUtil.encryptFile(inputPath, outputPath, password);
                checksumField.setText(checksum);
                statusLabel.setForeground(UITheme.SUCCESS);
                statusLabel.setText("Encrypted successfully -> " + outputPath);
                ActivityLogger.log("User '" + app.getCurrentUser() + "' encrypted file: "
                        + inputPath + " -> " + outputPath);
                refreshLog();
            } catch (IOException | GeneralSecurityException ex) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Encryption failed: " + ex.getMessage());
            }
        });

        panel.add(labeledRow("SOURCE FILE", inputField, browseInput));
        panel.add(Box.createVerticalStrut(16));
        panel.add(labeledRow("SAVE ENCRYPTED FILE AS", outputField, browseOutput));
        panel.add(Box.createVerticalStrut(16));
        panel.add(UIFactory.fieldLabel("ENCRYPTION PASSWORD"));
        panel.add(Box.createVerticalStrut(4));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));
        encryptButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(encryptButton);
        panel.add(Box.createVerticalStrut(14));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(UIFactory.fieldLabel("SHA-256 OF ORIGINAL FILE"));
        panel.add(Box.createVerticalStrut(4));
        checksumField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(checksumField);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ---------------------------------------------------------------
    // Decrypt tab
    // ---------------------------------------------------------------
    private JPanel buildDecryptTab() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 30, 24, 30));

        JTextField inputField = UIFactory.textField();
        JTextField outputField = UIFactory.textField();
        JPasswordField passwordField = UIFactory.passwordField();
        JLabel statusLabel = UIFactory.statusLabel();
        JTextField checksumField = UIFactory.textField();
        checksumField.setEditable(false);
        checksumField.setFont(UITheme.FONT_MONO);

        var browseInput = UIFactory.secondaryButton("Browse...");
        browseInput.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                inputField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        var browseOutput = UIFactory.secondaryButton("Choose...");
        browseOutput.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("decrypted_output"));
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                outputField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        var decryptButton = UIFactory.primaryButton("Decrypt File");
        decryptButton.addActionListener(e -> {
            String inputPath = inputField.getText().trim();
            String outputPath = outputField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (inputPath.isEmpty() || outputPath.isEmpty() || password.isEmpty()) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Please fill in the file, destination, and password.");
                return;
            }
            if (!new File(inputPath).exists()) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Encrypted file not found.");
                return;
            }
            try {
                CryptoUtil.decryptFile(inputPath, outputPath, password);
                String checksum = HashUtil.fileChecksum(outputPath);
                checksumField.setText(checksum);
                statusLabel.setForeground(UITheme.SUCCESS);
                statusLabel.setText("Decrypted successfully -> " + outputPath);
                ActivityLogger.log("User '" + app.getCurrentUser() + "' decrypted file: "
                        + inputPath + " -> " + outputPath);
                refreshLog();
            } catch (GeneralSecurityException ex) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Decryption failed: wrong password or file was tampered with.");
                ActivityLogger.log("Decryption FAILED (bad password/tampered) for user '"
                        + app.getCurrentUser() + "'");
            } catch (IOException ex) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Decryption failed: " + ex.getMessage());
            }
        });

        panel.add(labeledRow("ENCRYPTED FILE (.enc)", inputField, browseInput));
        panel.add(Box.createVerticalStrut(16));
        panel.add(labeledRow("SAVE DECRYPTED FILE AS", outputField, browseOutput));
        panel.add(Box.createVerticalStrut(16));
        panel.add(UIFactory.fieldLabel("DECRYPTION PASSWORD"));
        panel.add(Box.createVerticalStrut(4));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));
        decryptButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(decryptButton);
        panel.add(Box.createVerticalStrut(14));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(UIFactory.fieldLabel("SHA-256 OF DECRYPTED FILE"));
        panel.add(Box.createVerticalStrut(4));
        checksumField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(checksumField);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ---------------------------------------------------------------
    // Password strength tab
    // ---------------------------------------------------------------
    private JPanel buildStrengthTab() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 30, 24, 30));

        JTextField passwordField = UIFactory.textField();
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        JProgressBar strengthBar = new JProgressBar(0, 5);
        strengthBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        strengthBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        strengthBar.setForeground(UITheme.DANGER);
        strengthBar.setBackground(UITheme.FIELD_BG);

        JLabel ratingLabel = new JLabel(" ");
        ratingLabel.setFont(UITheme.FONT_LABEL);
        ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea suggestionsArea = new JTextArea(6, 30);
        suggestionsArea.setEditable(false);
        suggestionsArea.setFont(UITheme.FONT_LABEL);
        suggestionsArea.setLineWrap(true);
        suggestionsArea.setWrapStyleWord(true);
        suggestionsArea.setBackground(UITheme.FIELD_BG);
        JScrollPane suggestionsScroll = new JScrollPane(suggestionsArea);
        suggestionsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        suggestionsScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        passwordField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update() {
                String password = passwordField.getText();
                int score = PasswordStrengthChecker.score(password);
                strengthBar.setValue(score);
                String rating = PasswordStrengthChecker.rating(score);
                Color color = score <= 2 ? UITheme.DANGER : (score <= 4 ? UITheme.WARNING : UITheme.SUCCESS);
                strengthBar.setForeground(color);
                ratingLabel.setForeground(color);
                ratingLabel.setText(password.isEmpty() ? " " : (rating + " (" + score + "/5)"));
                String tips = PasswordStrengthChecker.suggestions(password);
                suggestionsArea.setText(tips.isEmpty() && !password.isEmpty()
                        ? "Great! This password meets every criterion." : tips);
            }
        });

        panel.add(UIFactory.fieldLabel("TYPE A PASSWORD TO TEST"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(strengthBar);
        panel.add(Box.createVerticalStrut(4));
        panel.add(ratingLabel);
        panel.add(Box.createVerticalStrut(16));
        panel.add(UIFactory.fieldLabel("SUGGESTIONS"));
        panel.add(Box.createVerticalStrut(4));
        panel.add(suggestionsScroll);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ---------------------------------------------------------------
    // Integrity check tab
    // ---------------------------------------------------------------
    private JPanel buildIntegrityTab() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(24, 30, 24, 30));

        JTextField pathField = UIFactory.textField();
        JLabel statusLabel = UIFactory.statusLabel();
        JTextField resultField = UIFactory.textField();
        resultField.setEditable(false);
        resultField.setFont(UITheme.FONT_MONO);

        var browseButton = UIFactory.secondaryButton("Browse...");
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                pathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        var computeButton = UIFactory.primaryButton("Compute SHA-256");
        computeButton.addActionListener(e -> {
            String path = pathField.getText().trim();
            if (path.isEmpty() || !new File(path).exists()) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Please choose a file that exists.");
                resultField.setText("");
                return;
            }
            try {
                String checksum = HashUtil.fileChecksum(path);
                resultField.setText(checksum);
                statusLabel.setForeground(UITheme.SUCCESS);
                statusLabel.setText("Checksum computed.");
            } catch (IOException | NoSuchAlgorithmException ex) {
                statusLabel.setForeground(UITheme.DANGER);
                statusLabel.setText("Could not compute checksum: " + ex.getMessage());
            }
        });

        panel.add(labeledRow("FILE TO VERIFY", pathField, browseButton));
        panel.add(Box.createVerticalStrut(18));
        computeButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(computeButton);
        panel.add(Box.createVerticalStrut(14));
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(UIFactory.fieldLabel("SHA-256 CHECKSUM"));
        panel.add(Box.createVerticalStrut(4));
        resultField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(resultField);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    // ---------------------------------------------------------------
    // Activity log tab
    // ---------------------------------------------------------------
    private JPanel buildLogTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(24, 30, 24, 30));

        logArea.setEditable(false);
        logArea.setFont(UITheme.FONT_MONO);
        logArea.setBackground(UITheme.FIELD_BG);
        JScrollPane scroll = new JScrollPane(logArea);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 12));
        bottomRow.setOpaque(false);
        var refreshButton = UIFactory.secondaryButton("Refresh");
        refreshButton.addActionListener(e -> refreshLog());
        bottomRow.add(refreshButton);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomRow, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshLog() {
        File logFile = new File("activity_log.txt");
        if (!logFile.exists()) {
            logArea.setText("No activity yet.");
            return;
        }
        try {
            String content = Files.readString(logFile.toPath());
            logArea.setText(content.isEmpty() ? "No activity yet." : content);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        } catch (IOException e) {
            logArea.setText("Could not read log: " + e.getMessage());
        }
    }

    /** Builds a "LABEL" over "[text field][button]" row used by several tabs. */
    private JPanel labeledRow(String labelText, JTextField field, javax.swing.JButton button) {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = UIFactory.fieldLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fieldRow = new JPanel(new BorderLayout(8, 0));
        fieldRow.setOpaque(false);
        fieldRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fieldRow.add(field, BorderLayout.CENTER);
        fieldRow.add(button, BorderLayout.EAST);

        wrap.add(label);
        wrap.add(Box.createVerticalStrut(4));
        wrap.add(fieldRow);
        return wrap;
    }
}
