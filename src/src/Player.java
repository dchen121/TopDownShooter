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
    private Color normalColor;
    private Color hitColor;

    public Player() {
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT / 2;
        r = 7;

        dx = 0;
        dy = 0;
        speed = 7;

        lives = 3;
        normalColor = Color.WHITE;
        hitColor = Color.RED;
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
        g.setColor(normalColor);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);

        g.setStroke(new BasicStroke(3));
        g.setColor(normalColor.darker());
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
    }


    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

}
