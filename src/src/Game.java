import javax.swing.*;

/**
 * @author Daniel Chen
 */
public class Game {

    public static void main(String[] args) {
        JFrame window = new JFrame("Top Down Shooter");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setContentPane(new GamePanel());
        window.pack();
        window.setVisible(true);
    }
}
