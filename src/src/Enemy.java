import java.awt.*;

/**
 * @author Daniel Chen
 */
public class Enemy {

    private double x;
    private double y;
    private int r;

    private double rad;
    private double speed;
    private double dx;
    private double dy;

    private int health;
    private int type;
    private int rank;

    private Color enemyColor;

    private boolean slow;
    private boolean ready;
    private boolean dead;

    private boolean hit;
    private long hitTimerNanoseconds;
    private final long hitTimerDelayMilliseconds = 50;

    public Enemy (int type, int rank) {
        this.type = type;
        this.rank = rank;
        slow = false;

        switch(type) {
            // Default enemy type
            case 1:
                enemyColor = Color.BLUE;
                speed = 4;

                switch(rank) {
                    case 1:
                        r = 5;
                        health = 1;
                        break;
                    case 2:
                        r = 10;
                        health = 2;
                        break;
                    case 3:
                        r = 15;
                        health = 3;
                        break;
                    case 4:
                        r = 20;
                        health = 4;
                }

                break;

            // Fast, weak
            case 2:
                enemyColor = Color.GREEN;
                speed = 6;

                switch(rank) {
                    case 1:
                        r = 5;
                        health = 2;
                        break;
                    case 2:
                        r = 10;
                        health = 3;
                        break;
                    case 3:
                        r = 15;
                        health = 4;
                        break;
                    case 4:
                        r = 20;
                        health = 5;
                }

                break;

            // Slow, strong
            case 3:
                enemyColor = Color.ORANGE;
                speed = 2;

                switch(rank) {
                    case 1:
                        r = 5;
                        health = 4;
                        break;
                    case 2:
                        r = 10;
                        health = 5;
                        break;
                    case 3:
                        r = 15;
                        health = 6;
                        break;
                    case 4:
                        r = 20;
                        health = 7;
                }

                break;
        }


        x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
        y = -r;

        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        ready = false;
        dead = false;

        hit = false;
        hitTimerNanoseconds = 0;
    }

    public void hit(int damage) {
        health -= damage;
        if (health <= 0) {
            dead = true;
        }

        hit = true;
        hitTimerNanoseconds = System.nanoTime();
    }

    /**
     * When enemies are killed explode into multiple smaller ranked enemies.
     */
    public void explode() {
        if (rank > 1) {
            int amount = 3;

            for (int i = 0; i < amount; i++) {
                Enemy e = new Enemy(getType(), getRank() - 1);

                e.x = this.x;
                e.y = this.y;
                double angle;
                angle = Math.random() * 360;

                e.rad = Math.toRadians(angle);
                GamePanel.enemies.add(e);
            }
        }
    }

    public void update() {
        moveEnemy();
        checkReady();
        enemyBoundaryCollision();
        checkHit();
    }

    private void moveEnemy() {
        if (slow) {
            x += dx * 0.3;
            y += dx * 0.3;
        } else {
            x += dx;
            y += dy;
        }
    }

    private void checkReady() {
        if (!ready) {
            if (x > r && x < GamePanel.WIDTH - r && y > r && y < GamePanel.HEIGHT - r) {
                ready = true;
            }
        }
    }

    /**
     * If enemy collides with boundary, enemy bounces off wall.
     */
    private void enemyBoundaryCollision() {
        if (x < r && dx < 0) dx = -dx;
        if (y < r && dy < 0) dy = -dy;
        if (x > GamePanel.WIDTH - r && dx > 0) dx = -dx;
        if (y > GamePanel.HEIGHT - r && dy > 0) dy = -dy;
    }

    private void checkHit() {
        if (hit) {
            long elapsedMilliseconds = (System.nanoTime() - hitTimerNanoseconds) / 1000000;
            if (elapsedMilliseconds > hitTimerDelayMilliseconds) {
                hit = false;
                hitTimerNanoseconds = 0;
            }
        }
    }

    public void draw(Graphics2D g) {
        if (hit) {
            g.setColor(Color.RED);
        } else {
            g.setColor(enemyColor);
        }

        g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }

    public boolean isDead() { return dead; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getR() { return r; }
    public int getType() { return type; }
    public int getRank() { return rank; }
    public void setSlow(boolean slow) { this.slow = slow; }
}
