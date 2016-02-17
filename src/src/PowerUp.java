import java.awt.*;

/**
 * @author Daniel Chen
 */
public class PowerUp {

    private double x;
    private double y;
    private int r;
    private Color powerUpColor;

    /**
     * 1 = +1 life
     * 2 = +1 power
     * 3 = +1 damage
     */
    private int type;

    public PowerUp(int type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
        r = 3;

        switch(type) {
            case 1:
                powerUpColor = Color.PINK;
                break;
            case 2:
                powerUpColor = Color.YELLOW;
                break;
            case 3:
                powerUpColor = Color.CYAN;
                break;
        }
    }

    /**
     * Update power up and return true if power up collides with boundary of game, false otherwise.
     */
    public boolean update() {
        y += 2;

        if (y > GamePanel.HEIGHT + r) {
            return true;
        }

        return false;
    }

    public void draw(Graphics2D g) {
        g.setColor(powerUpColor);
        g.fillRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getR() { return r; }
    public int getType() { return type; }
}
