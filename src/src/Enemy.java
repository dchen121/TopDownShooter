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

    private boolean ready;
    private boolean dead;

    public Enemy (int type, int rank) {
        this.type = type;
        this.rank = rank;

        if (type == 1) {
            enemyColor = Color.RED;
            if (rank == 1) {
                speed = 2;
                r = 5;
                health = 1;
            }
        }

        x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
        y = -r;

        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        ready = false;
        dead = false;
    }

    public void hit() {
        health--;
        if (health <= 0) {
            dead = true;
        }
    }

    public void update() {
        moveEnemy();

        if (!ready) {
            if (x > r && x < GamePanel.WIDTH - r && y > r && y < GamePanel.HEIGHT - r) {
                ready = true;
            }
        }

        enemyBoundaryCollision();
    }

    private void moveEnemy() {
        x += dx;
        y += dy;
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

    public void draw(Graphics2D g) {
        g.setColor(enemyColor);
        g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }

    public boolean isDead() { return dead; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getR() { return r; }
    public int getType() { return type; }
    public int getRank() { return rank; }
}
