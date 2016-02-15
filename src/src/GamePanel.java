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
    public static ArrayList<Enemy> enemies;

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
        init();

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

    private void init() {
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();

        // Spawn one round of enemies
        for (int i = 0; i < 5; i++) {
            Enemy e = new Enemy(1, 1);
            enemies.add(e);
        }
    }

    /**
     * Update game logic (player, enemy, and projectile position, and collision detection).
     */
    private void gameUpdate() {
        updatePlayer();
        updateBullets();
        updateEnemies();
        bulletEnemyCollision();
        removeDeadEnemies();
        playerEnemyCollision();
    }

    /**
     * Check bullet-enemy collision. If collision, mark enemy as hit and remove bullet.
     */
    private void bulletEnemyCollision() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            double bulletX = b.getX();
            double bulletY = b.getY();
            double bulletR = b.getR();

            for (int j = 0; j < enemies.size(); j++) {
                Enemy e = enemies.get(j);
                double enemyX = e.getX();
                double enemyY = e.getY();
                double enemyR = e.getR();

                double dx = bulletX - enemyX;
                double dy = bulletY - enemyY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < bulletR + enemyR) {
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    /**
     * Remove all dead enemies
     */
    private void removeDeadEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (e.isDead()) {
                player.addScore(e.getType() + e.getRank());
                enemies.remove(i);
                i--;
            }
        }
    }

    /**
     * Check player-enemy collision. If collision, mark player as hit and recovering. Player is invincible while recovering.
     */
    private void playerEnemyCollision() {
        if (!player.isRecovering()) {
            int playerX = player.getX();
            int playerY = player.getY();
            int playerR = player.getR();

            for (int i = 0; i < enemies.size(); i++) {
                Enemy e = enemies.get(i);
                double enemyX = e.getX();
                double enemyY = e.getY();
                double enemyR = e.getR();

                double dx = playerX - enemyX;
                double dy = playerY - enemyY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < playerR + enemyR) {
                    player.hit();
                }
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

        renderPlayer();
        renderBullets();
        renderEnemies();

        // draw player score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);
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

        if (keyCode == KeyEvent.VK_LEFT) player.setLeft(true);
        if (keyCode == KeyEvent.VK_RIGHT) player.setRight(true);
        if (keyCode == KeyEvent.VK_UP) player.setUp(true);
        if (keyCode == KeyEvent.VK_DOWN) player.setDown(true);
        if (keyCode == KeyEvent.VK_SPACE) player.setFiring(true);
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

        if (keyCode == KeyEvent.VK_LEFT) player.setLeft(false);
        if (keyCode == KeyEvent.VK_RIGHT) player.setRight(false);
        if (keyCode == KeyEvent.VK_UP) player.setUp(false);
        if (keyCode == KeyEvent.VK_DOWN) player.setDown(false);
        if (keyCode == KeyEvent.VK_SPACE) player.setFiring(false);
    }


    private void updatePlayer() {
        player.update();
    }

    private void updateEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
        }
    }

    /**
     * Update bullets and remove those that exceed game boundary
     */
    private void updateBullets() {
        // If bullets collide with boundary of game, remove them from the list of bullets
        for (int i = 0; i < bullets.size(); i++) {
            boolean collisionWithBoundary = bullets.get(i).update();
            if (collisionWithBoundary) {
                bullets.remove(i);
                i--;
            }
        }
    }

    private void renderPlayer() {
        player.draw(g);
    }

    private void renderBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }
    }

    private void renderEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }
    }
}
