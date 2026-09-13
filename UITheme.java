import java.awt.Color;
import java.awt.Font;

/**
 * UITheme
 * Central place for all colors and fonts used across the GUI so the
 * app has one consistent, modern "web app" look and feel.
 */
public class UITheme {

    // Background gradient (dark slate, top -> bottom)
    public static final Color BG_TOP = new Color(15, 23, 42);
    public static final Color BG_BOTTOM = new Color(30, 41, 59);

    // Accent (sky blue)
    public static final Color ACCENT = new Color(56, 189, 248);
    public static final Color ACCENT_DARK = new Color(2, 132, 199);

    // Card / panel surfaces
    public static final Color CARD_BG = new Color(255, 255, 255);
    public static final Color FIELD_BG = new Color(248, 250, 252);
    public static final Color FIELD_BORDER = new Color(203, 213, 225);

    // Text
    public static final Color TEXT_DARK = new Color(15, 23, 42);
    public static final Color TEXT_LIGHT = new Color(241, 245, 249);
    public static final Color TEXT_MUTED = new Color(100, 116, 139);

    // Status colors
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color WARNING = new Color(202, 138, 4);

    // Fonts
    public static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 30);
    public static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_LABEL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BUTTON = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);
}
