import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

/**
 * UIFactory
 * Small factory methods that build consistently styled Swing components
 * (buttons, text fields, labels) so every screen in the app shares the
 * same look without repeating styling code everywhere.
 */
public class UIFactory {

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.FONT_BUTTON);
        button.setBackground(UITheme.ACCENT_DARK);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 22, 10, 22));
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.FONT_BUTTON);
        button.setBackground(UITheme.FIELD_BG);
        button.setForeground(UITheme.TEXT_DARK);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new CompoundBorder(
                new LineBorder(UITheme.FIELD_BORDER, 1, true),
                new EmptyBorder(9, 18, 9, 18)));
        return button;
    }

    public static JButton linkButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UITheme.FONT_LABEL);
        button.setForeground(UITheme.ACCENT_DARK);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JTextField textField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    public static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    private static void styleField(JTextField field) {
        field.setFont(UITheme.FONT_LABEL);
        field.setBackground(UITheme.FIELD_BG);
        field.setForeground(UITheme.TEXT_DARK);
        field.setCaretColor(UITheme.TEXT_DARK);
        field.setBorder(new CompoundBorder(
                new LineBorder(UITheme.FIELD_BORDER, 1, true),
                new EmptyBorder(8, 10, 8, 10)));
        field.setPreferredSize(new Dimension(260, 36));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    }

    public static JLabel heading(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_HEADING);
        label.setForeground(UITheme.TEXT_DARK);
        return label;
    }

    public static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UITheme.FONT_LABEL);
        label.setForeground(UITheme.TEXT_MUTED);
        return label;
    }

    public static JLabel statusLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(UITheme.FONT_LABEL);
        return label;
    }
}
