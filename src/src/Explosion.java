import java.awt.*;

/**
 * @author Daniel Chen
 */
public class Explosion {
    private double x;
    private double y;

    private int r;
    private final int maxRadius = 40;

    public Explosion(double x, double y, int r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public boolean update() {
        r++;

        if (r > maxRadius) {
            return true;
        }

        return false;
    }

    public void draw(Graphics2D g) {
        int alpha = (int) (255 * Math.sin(3.14 * r / maxRadius));

        if (alpha > 255) {
            alpha = 255;
        }

        g.setColor(new Color(255, 255, 255, alpha));
        g.drawOval((int) (x - r), (int) y - r, 2 * r, 2 * r);
        g.setColor(new Color(255, 255, 255, alpha));
    }
}
