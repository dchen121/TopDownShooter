import java.awt.*;

/**
 * @author Daniel Chen
 */
public class Bullet {

    private double x;
    private double y;
    private int r;

    private double rad;
    private double speed;
    private double dx;
    private double dy;

    private Color bulletColor;

    public Bullet(double angle, int x, int y) {
        this.x = x;
        this.y = y;
        r = 2;

        rad = Math.toRadians(angle);
        speed = 15;
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        bulletColor = Color.BLACK;
    }

    public boolean update() {
        x += dx;
        y += dy;

        // Check collision with boundary of game
        if (x < -r || x > GamePanel.WIDTH + r || y < -r || y > GamePanel.HEIGHT + r) {
            return true;
        }

        return false;
    }

    public void draw(Graphics2D g) {
        g.setColor(bulletColor);
        g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getR() { return r; }
}
