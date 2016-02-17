import java.awt.*;

/**
 * @author Daniel Chen
 */
public class PowerUp {

    private double x;
    private double y;
    private int r;
    private Color color;
    private String name;

    /**
     * 1 = +1 life
     * 2 = +1 power
     * 3 = +1 damage
     * 4 = 5 second slow-mo
     * 5 = 5 second rapid-fire
     */
    private int type;

    public PowerUp(int type, double x, double y) {
        this.type = type;
        this.x = x;
        this.y = y;
        r = 3;

        switch(type) {
            case 1:
                color = Color.PINK;
                name = "+1 Life";
                break;
            case 2:
                color = Color.YELLOW;
                name = "+1 Power";
                break;
            case 3:
                color = Color.CYAN;
                name = "+1 Damage";
                break;
            case 4:
                color = Color.WHITE;
                name = "Slow-Mo";
                break;
            case 5:
                color = Color.GREEN;
                name = "Rapid Fire";
                break;
        }
    }

    /**
     * Update power up and return true if power up collides with boundary of game, false otherwise.
     */
    public boolean update() {
        y += 1;

        if (y > GamePanel.HEIGHT + r) {
            return true;
        }

        return false;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getR() { return r; }
    public int getType() { return type; }
    public String getName() { return name; }
}
