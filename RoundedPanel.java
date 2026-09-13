import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * RoundedPanel
 * A JPanel painted as a rounded-corner "card" (white surface with a soft
 * border) floating on top of the app's gradient background - the classic
 * modern web-app card look.
 */
public class RoundedPanel extends JPanel {

    private final int arc;
    private final Color background;

    public RoundedPanel(int arc, Color background) {
        this.arc = arc;
        this.background = background;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(background);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2d.dispose();
        super.paintComponent(g);
    }
}
