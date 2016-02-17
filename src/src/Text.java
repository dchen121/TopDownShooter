import java.awt.*;

/**
 * @author Daniel Chen
 */
public class Text {

    private double x;
    private double y;
    private String s;

    private long startTimeNanoseconds;
    private long elapsedMilliseconds;
    private final long durationMilliseconds = 2500;

    public Text(double x, double y, String s) {
        this.x = x;
        this.y = y;
        this.s = s;
        elapsedMilliseconds = 0;
        startTimeNanoseconds = System.nanoTime();
    }

    public boolean update() {
        elapsedMilliseconds = (System.nanoTime() - startTimeNanoseconds) / 1000000;

        if (elapsedMilliseconds > durationMilliseconds) {
            return true;
        }

        return false;
    }

    public void draw (Graphics2D g) {
        g.setFont(new Font(GamePanel.FONT_STYLE, Font.PLAIN, 12));

        int alpha = (int) (255 * Math.sin(3.14 * elapsedMilliseconds / durationMilliseconds));
        if (alpha > 255) {
            alpha = 255;
        }

        g.setColor(new Color(255, 255, 255, alpha));
        g.drawString(s, (int) x, (int) y);
    }

}
