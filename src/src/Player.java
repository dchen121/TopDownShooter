import java.awt.*;

/**
 * @author Daniel Chen
 */
public class Player {

    private final Color normalColor = Color.BLACK;
    private final Color recoveringColor = Color.YELLOW;
    private final int MAX_DAMAGE = 5;

    private int x;
    private int y;

    private int r;
    private int dx;
    private int dy;

    private int speed;
    private int damage;
    private int powerLevel;

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;

    private boolean firing;
    private boolean rapidFire;
    private long firingTimerNanoseconds;
    private long firingDelayMilliseconds;

    private int lives;
    private boolean recovering;
    private long recoveryTimerNanoseconds;
    private final long recoveryTimeMilliseconds = 2000;

    private int score;

    public Player() {
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT / 2;
        r = 10;

        dx = 0;
        dy = 0;

        speed = 4;
        damage = 1;
        powerLevel = 0;

        firing = false;
        rapidFire = false;
        firingTimerNanoseconds = System.nanoTime();
        firingDelayMilliseconds = 200;

        lives = 3;
        recovering = false;
        recoveryTimerNanoseconds = 0;


        score = 0;
    }

    public void update() {
        movePlayer();
        checkRecovering();
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

    private void checkRecovering() {
        if (recovering) {
            long elapsed = (System.nanoTime() - recoveryTimerNanoseconds) / 1000000;
            if (elapsed > recoveryTimeMilliseconds) {
                recovering = false;
                recoveryTimerNanoseconds = 0;
            }
        }
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
     * Fire bullets based on power level. Player can only fire once per firing delay time.
     */
    private void fire() {
        if (firing) {
            long tempFiringDelayMilliseconds = firingDelayMilliseconds;

            if (rapidFire) {
                tempFiringDelayMilliseconds = firingDelayMilliseconds / 2;
            }

            long elapsedMilliseconds = (System.nanoTime() - firingTimerNanoseconds) / 1000000;

            if (elapsedMilliseconds >= tempFiringDelayMilliseconds) {
                firingTimerNanoseconds = System.nanoTime();

                if (powerLevel < 2) {
                    GamePanel.bullets.add(new Bullet(270, x, y));
                }
                else if (powerLevel < 5) {
                    GamePanel.bullets.add(new Bullet(270, x + (r / 2), y));
                    GamePanel.bullets.add(new Bullet(270, x - (r / 2), y));
                }
                else if (powerLevel < 10){
                    GamePanel.bullets.add(new Bullet(275, x + (r / 3), y));
                    GamePanel.bullets.add(new Bullet(270, x, y));
                    GamePanel.bullets.add(new Bullet(265, x - (r / 3), y));
                }
                else {
                    GamePanel.bullets.add(new Bullet(280, x + (r / 2), y));
                    GamePanel.bullets.add(new Bullet(275, x + (r / 3), y));
                    GamePanel.bullets.add(new Bullet(270, x, y));
                    GamePanel.bullets.add(new Bullet(265, x - (r / 3), y));
                    GamePanel.bullets.add(new Bullet(260, x - (r / 2), y));
                }
            }
        }
    }

    public void draw(Graphics2D g) {
        if (recovering) {
            g.setColor(recoveringColor);
        } else {
            g.setColor(normalColor);
        }

        g.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

    public void hit() {
        loseLife();
        recovering = true;
        recoveryTimerNanoseconds = System.nanoTime();
    }

    public void addScore(int i) { score += i; }
    public void gainLife() { lives++; }
    public void loseLife() { lives--; }
    public void increasePower(int i) { powerLevel += i; }

    public void increaseDamage(int i) {
        damage += i;
        if (damage > MAX_DAMAGE) {
            damage = MAX_DAMAGE;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getR() { return r; }
    public Color getNormalColor() { return normalColor; }
    public int getDamage() { return damage; }
    public boolean isRecovering() { return recovering; }
    public boolean isDead() { return lives <= 0; }
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
    public void setRapidFire(boolean rapidFire) { this.rapidFire = rapidFire; }
}
