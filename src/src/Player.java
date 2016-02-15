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

    private boolean firing;
    private long firingTimerNanoseconds;
    private long firingDelayMilliseconds;

    private int lives;
    private boolean recovering;
    private long recoveryTimerNanoseconds;

    private int score;

    private Color normalColor;
    private Color recoveringColor;

    public Player() {
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT / 2;
        r = 7;

        dx = 0;
        dy = 0;
        speed = 7;

        lives = 3;
        recovering = false;
        recoveryTimerNanoseconds = 0;

        normalColor = Color.BLUE;
        recoveringColor = Color.YELLOW;

        firing = false;
        firingTimerNanoseconds = System.nanoTime();
        firingDelayMilliseconds = 200;

        score = 0;
    }

    public void update() {
        movePlayer();
        playerBoundaryCollision();
        fire();
    }

    /**
     * Update direction and move player.
     */
    private void movePlayer() {
        if (left)   dx = -speed;
        if (right)  dx = speed;
        if (up)     dy = -speed;
        if (down)   dy = speed;

        x += dx;
        y += dy;

        dx = 0;
        dy = 0;
    }

    /**
     * Check player-boundary collision. Prevent player from going beyond game boundary.
     */
    private void playerBoundaryCollision() {
        if (x < r) x = r;
        if (y < r) y = r;
        if (x > GamePanel.WIDTH - r) x = GamePanel.WIDTH - r;
        if (y > GamePanel.HEIGHT - r) y = GamePanel.HEIGHT - r;
    }

    /**
     * Fire bullet. Player can only fire once per firing delay time.
     */
    private void fire() {
        // Player can only fire once per firing delay time
        if (firing) {
            long elapsedMilliseconds = (System.nanoTime() - firingTimerNanoseconds) / 1000000;
            if (elapsedMilliseconds >= firingDelayMilliseconds) {
                Bullet b = new Bullet(270, x, y); // Fire a bullet facing upwards
                GamePanel.bullets.add(b);
                firingTimerNanoseconds = System.nanoTime();
            }
        }

        long elapsed = (System.nanoTime() - recoveryTimerNanoseconds) / 1000000;
        if (elapsed > 2000) {
            recovering = false;
            recoveryTimerNanoseconds = 0;
        }
    }

    public void draw(Graphics2D g) {
        if (recovering) {
            g.setColor(recoveringColor);
            g.fillOval(x - r, y - r, 2 * r, 2 * r);
        } else {
            g.setColor(normalColor);
            g.fillOval(x - r, y - r, 2 * r, 2 * r);
        }
    }

    public void hit() {
        lives--;
        recovering = true;
        recoveryTimerNanoseconds = System.nanoTime();
    }

    public void addScore(int i) {
        score += i;
    }

    public boolean isDead() {
        return lives <= 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getR() { return r; }
    public boolean isRecovering() { return recovering; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public void setLeft(boolean b) {
        this.left = b;
    }
    public void setRight(boolean b) {
        this.right = b;
    }
    public void setUp(boolean b) {
        this.up = b;
    }
    public void setDown(boolean b) { this.down = b; }


    public void setFiring(boolean b) { this.firing = b; }
}
