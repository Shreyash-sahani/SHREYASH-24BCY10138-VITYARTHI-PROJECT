import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;

/**
 * GradientPanel
 * A JPanel that paints a smooth top-to-bottom color gradient as its
 * background, used to give the app a "web page hero section" look
 * instead of a flat gray Swing panel.
 */
public class GradientPanel extends JPanel {

    private final Color colorTop;
    private final Color colorBottom;

    public GradientPanel(Color colorTop, Color colorBottom) {
        this.colorTop = colorTop;
        this.colorBottom = colorBottom;
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        GradientPaint gp = new GradientPaint(0, 0, colorTop, 0, h, colorBottom);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, w, h);
        g2d.dispose();
        super.paintComponent(g);
    }
}
