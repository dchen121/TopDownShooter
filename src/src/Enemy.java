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

        switch(type) {
            // Default enemy type
            case 1:
                enemyColor = Color.RED;

                switch(rank) {
                    case 1:
                        speed = 2;
                        r = 5;
                        health = 1;
                        break;
                    case 2:
                        speed = 2;
                        r = 10;
                        health = 2;
                        break;
                    case 3:
                        speed = 1.5;
                        r = 20;
                        health = 3;
                        break;
                    case 4:
                        speed = 1.5;
                        r = 30;
                        health = 4;
                }

                break;

            // Fast, weak
            case 2:
                enemyColor = Color.RED;

                switch(rank) {
                    case 1:
                        speed = 3;
                        r = 5;
                        health = 2;
                        break;
                    case 2:
                        speed = 3;
                        r = 10;
                        health = 3;
                        break;
                    case 3:
                        speed = 4;
                        r = 15;
                        health = 4;
                        break;
                    case 4:
                        speed = 5;
                        r = 20;
                        health = 5;
                }

                break;

            // Slow, strong
            case 3:
                enemyColor = Color.GREEN;

                switch(rank) {
                    case 1:
                        speed = 1.5;
                        r = 5;
                        health = 5;
                        break;
                    case 2:
                        speed = 1.5;
                        r = 10;
                        health = 6;
                        break;
                    case 3:
                        speed = 1.25;
                        r = 15;
                        health = 7;
                        break;
                    case 4:
                        speed = 1;
                        r = 20;
                        health = 8;
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
    }

    public void hit() {
        health--;
        if (health <= 0) {
            dead = true;
        }
    }

    /**
     * When enemies are killed explode into multiple smaller ranked enemies.
     */
    public void explode() {
        if (rank > 1) {
            int amount = 3;

            for (int i = 0; i < amount; i++) {
                Enemy e = new Enemy(getType(), getRank() - 1);

                // Set position of new enemies where previous enemy was killed with random direction
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
