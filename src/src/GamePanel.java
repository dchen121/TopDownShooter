import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Daniel Chen
 */
public class GamePanel extends JPanel implements Runnable {

    public static int WIDTH = 400;
    public static int HEIGHT = 400;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private int FPS = 30;
    private double averageFPS;

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus(); // Make sure keyboard inputs are detected
    }

    /**
     * Automatically called upon adding GamePanel to container in Game.java
     */
    @Override
    public void addNotify() {
        super.addNotify();

        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        running = true;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();

        // Used to maintain FPS
        long startTimeNanoseconds; // Start time of each loop
        long URDTimeMilliseconds; // Time it takes to update, render, and draw the game
        long waitTimeMilliseconds; // Time needed to wait to meet target time
        long targetTimeMilliseconds = 1000 / FPS; // Time for each loop to run to maintain FPS

        // Used to calculate average FPS
        long totalTime = 0;
        int frameCount = 0;
        int maxFrameCount = 30;

        // GAME LOOP
        while (running) {
            // Get current start time of a loop in nanoseconds
            startTimeNanoseconds = System.nanoTime();

            // Update, render, and draw
            gameUpdate();
            gameRender();
            gameDraw();

            /**
             * Control speed of the game loop
             */
            URDTimeMilliseconds = (System.nanoTime() - startTimeNanoseconds) / 1000000;
            waitTimeMilliseconds = targetTimeMilliseconds - URDTimeMilliseconds;

            try {
                Thread.sleep(waitTimeMilliseconds);
            }
            catch(Exception e) {
            }

            /**
             * Calculating the average FPS
             */
            totalTime += System.nanoTime() - startTimeNanoseconds;
            frameCount++;

            if (frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000.0);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    /**
     * Update game logic (player, enemy, and projectile position, and collision detection).
     */
    private void gameUpdate() {

    }

    /**
     * Render items that are currently active to an off-screen image before it
     * gets displayed to the actual screen (double buffering).
     *
     * Items rendered includes player, enemies, background, and projectiles.
     */
    private void gameRender() {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.BLACK);
        g.drawString("FPS: " + averageFPS, 10, 10);
    }

    /**
     * Draw rendered items to the game panel.
     */
    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }
}
