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

    private boolean isLeft;
    private boolean isRight;
    private boolean isUp;
    private boolean isDown;

    private boolean isFiring;
    private long firingTimerNanoseconds;
    private long firingDelayMilliseconds;

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
        normalColor = Color.BLUE;
        hitColor = Color.RED;

        isFiring = false;
        firingTimerNanoseconds = System.nanoTime();
        firingDelayMilliseconds = 200;
    }

    public void update() {
        // Check movement and update direction of movement of player
        if (isLeft)   dx = -speed;
        if (isRight)  dx = speed;
        if (isUp)     dy = -speed;
        if (isDown)   dy = speed;

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

        // Player can only fire once per firing delay time
        if (isFiring) {
            long elapsedMilliseconds = (System.nanoTime() - firingTimerNanoseconds) / 1000000;
            if (elapsedMilliseconds >= firingDelayMilliseconds) {
                Bullet b = new Bullet(270, x, y); // Fire a bullet facing upwards
                GamePanel.bullets.add(b);
                firingTimerNanoseconds = System.nanoTime();
            }
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(normalColor);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

    public void setIsLeft(boolean isLeft) {
        this.isLeft = isLeft;
    }
    public void setIsRight(boolean isRight) {
        this.isRight = isRight;
    }
    public void setIsUp(boolean isUp) {
        this.isUp = isUp;
    }
    public void setIsDown(boolean isDown) {
        this.isDown = isDown;
    }
    public void setIsFiring(boolean isFiring) { this.isFiring = isFiring; }
}
