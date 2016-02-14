import java.awt.*;

/**
 * @author Daniel Chen
 */
public class Player {

    private int x;
    private int y;
    private int r;

    private int dx;
    private int dy;
    private int speed;

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    private int lives;
    private Color color1;
    private Color color2;

    public Player() {
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT / 2;
        r = 5;

        dx = 0;
        dy = 0;
        speed = 5;

        lives = 3;
        color1 = Color.WHITE;
        color2 = Color.RED;
    }

    public void update() {
        // Check movement and update direction of movement of player
        if (left)   dx = -speed;
        if (right)  dx = speed;
        if (up)     dy = -speed;
        if (down)   dy = speed;

        // Move player according to direction
        x += dx;
        y += dy;

        // Check player collision with the boundaries of the game
        if (x < r) x = r;
        if (y < r) y = r;
        if (x > GamePanel.WIDTH - r) x = GamePanel.WIDTH - r;
        if (y > GamePanel.HEIGHT - r) y = GamePanel.HEIGHT - r;

        dx = 0;
        dy = 0;
    }

    public void draw(Graphics2D g) {
        g.setColor(color1);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);

        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
    }



}
