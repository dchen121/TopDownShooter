import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * @author Daniel Chen
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static int WIDTH = 500;
    public static int HEIGHT = 600;

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private int FPS = 30;
    private double averageFPS;

    public static Player player;
    public static ArrayList<Bullet> bullets;

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

        addKeyListener(this);
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

        player = new Player();
        bullets = new ArrayList<Bullet>();

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

            // Update, Render, and Draw game
            gameUpdate();
            gameRender();
            gameDraw();

            // Control speed of the game loop
            URDTimeMilliseconds = (System.nanoTime() - startTimeNanoseconds) / 1000000;
            waitTimeMilliseconds = targetTimeMilliseconds - URDTimeMilliseconds;

            try {
                Thread.sleep(waitTimeMilliseconds);
            }
            catch(Exception e) {
            }

            // Calculate the average FPS
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
        player.update();

        // If bullets collide with boundary of game, remove them from the list of bullets
        for (int i = 0; i < bullets.size(); i++) {
            boolean collisionWithBoundary = bullets.get(i).update();
            if (collisionWithBoundary) {
                bullets.remove(i);
                i--;
            }
        }
    }

    /**
     * Render items that are currently active to an off-screen image before it
     * gets displayed to the actual screen (double buffering).
     *
     * Items rendered includes player, enemies, background, and projectiles.
     */
    private void gameRender() {
        // Set background color
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // Display average FPS
        g.setColor(Color.BLACK);
        g.drawString("FPS: " + (int) averageFPS, 10, 10);

        player.draw(g);

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }
    }

    /**
     * Draw rendered items to the game panel.
     */
    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) player.setIsLeft(true);
        if (keyCode == KeyEvent.VK_RIGHT) player.setIsRight(true);
        if (keyCode == KeyEvent.VK_UP) player.setIsUp(true);
        if (keyCode == KeyEvent.VK_DOWN) player.setIsDown(true);
        if (keyCode == KeyEvent.VK_SPACE) player.setIsFiring(true);
    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) player.setIsLeft(false);
        if (keyCode == KeyEvent.VK_RIGHT) player.setIsRight(false);
        if (keyCode == KeyEvent.VK_UP) player.setIsUp(false);
        if (keyCode == KeyEvent.VK_DOWN) player.setIsDown(false);
        if (keyCode == KeyEvent.VK_SPACE) player.setIsFiring(false);
    }
}
