import javax.swing.*;
import java.awt.*;

/**
 * @author Daniel Chen
 */
public class GamePanel extends JPanel {

    public static int WIDTH = 400;
    public static int HEIGHT = 400;

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }
}
