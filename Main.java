import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;

/**
 * Main
 * Entry point for the SecureVault desktop application. Builds a single
 * JFrame that swaps between three full-screen "pages" (Login, Register,
 * Dashboard) using a CardLayout, similar to routes on a web app.
 */
public class Main {

    private static final String CARD_LOGIN = "login";
    private static final String CARD_REGISTER = "register";
    private static final String CARD_DASHBOARD = "dashboard";

    private final AuthManager authManager = new AuthManager();
    private String currentUser = null;

    private CardLayout cardLayout;
    private Container cardContainer;
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private DashboardPanel dashboardPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().start());
    }

    private void start() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // If the system look and feel isn't available, Swing's default is used instead.
        }

        JFrame frame = new JFrame("SecureVault - Java Security Toolkit");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(820, 640));
        frame.setSize(900, 680);
        frame.setLocationRelativeTo(null);

        JPanel cards = new JPanel();
        cardLayout = new CardLayout();
        cards.setLayout(cardLayout);
        cardContainer = cards;

        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        dashboardPanel = new DashboardPanel(this);

        cards.add(loginPanel, CARD_LOGIN);
        cards.add(registerPanel, CARD_REGISTER);
        cards.add(dashboardPanel, CARD_DASHBOARD);

        frame.setContentPane(cards);
        frame.setVisible(true);

        cardLayout.show(cardContainer, CARD_LOGIN);
    }

    public AuthManager getAuthManager() {
        return authManager;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void showLogin() {
        loginPanel.reset();
        cardLayout.show(cardContainer, CARD_LOGIN);
    }

    public void showRegister() {
        registerPanel.reset();
        cardLayout.show(cardContainer, CARD_REGISTER);
    }

    public void showDashboard(String username) {
        this.currentUser = username;
        dashboardPanel.setUsername(username);
        cardLayout.show(cardContainer, CARD_DASHBOARD);
    }

    public void logout() {
        this.currentUser = null;
        showLogin();
    }
}
