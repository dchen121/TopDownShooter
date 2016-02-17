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

    public static final int WIDTH = 500;
    public static final int HEIGHT = 600;
    public static final String FONT_STYLE = "Tahoma";

    private Thread thread;
    private boolean running;

    private BufferedImage image;
    private Graphics2D g;

    private final int FPS = 60;
    private double averageFPS;

    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<PowerUp> powerUps;
    public static ArrayList<Text> texts;

    private long waveStartTimerNanoseconds;
    private long waveStartTimerElapsedMilliseconds;
    private int waveNumber;
    private boolean waveStart;
    private final int waveDelayMilliseconds = 3000;

    private long slowMoTimerNanoseconds;
    private long slowMoTimerElapsedMilliseconds;
    private long slowMoDurationMilliseconds = 5000;

    private long rapidFireTimerNanoseconds;
    private long rapidFireElapsedMilliseconds;
    private long rapidFireDurationMilliseconds = 2500;


    /**
     * Constructor
     */
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

        gameOver();
    }

    /**
     * Initialize fields.
     */
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
        powerUps = new ArrayList<PowerUp>();
        texts = new ArrayList<Text>();

        waveStartTimerNanoseconds = 0;
        waveStartTimerElapsedMilliseconds = 0;
        waveStart = true;
        waveNumber = 0;
    }

    /**
     * Draw game over screen with final score.
     */
    private void gameOver() {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font(FONT_STYLE, Font.PLAIN, 16));

        String s = "G A M E   O V E R";
        int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        g.drawString(s, (WIDTH - length) / 2, HEIGHT / 2);

        s = "Final Score: " + player.getScore();
        length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
        int height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
        g.drawString(s, (WIDTH - length) / 2, (HEIGHT / 2) + height + 10);

        gameDraw();
    }

    /**
     * Update game logic (player, enemy, and projectile position, and collision detection).
     */
    private void gameUpdate() {
        spawnWave();

        player.update();
        updateBullets();
        updateEnemies();
        updatePowerUps();
        updateTexts();

        checkBulletEnemyCollision();
        removeDeadEnemies();

        checkPlayerEnemyCollision();
        checkPlayerPowerUpCollision();
    }

    /**
     * Render items that are currently active to an off-screen image before it
     * gets displayed to the actual screen (double buffering).
     *
     * Items rendered includes player, enemies, background, and projectiles.
     */
    private void gameRender() {
        drawBackground();
        displayAverageFPS();
        displayPlayerLives();
        displayPlayerScore();

        if (waveStartTimerNanoseconds != 0) {
            displayWaveNumber();
        }

        player.draw(g);
        renderBullets();
        renderEnemies();
        renderPowerUps();
        renderTexts();

        if (slowMoTimerNanoseconds != 0) {
            displaySlowMo();
        }

        if (rapidFireTimerNanoseconds != 0) {
            displayRapidFire();
        }
    }

    private void displayRapidFire() {
        g.setColor(Color.WHITE);
        g.drawRect(20, 80, 100, 8);
        g.fillRect(20, 80, (int) (100 - (100 * rapidFireElapsedMilliseconds / rapidFireDurationMilliseconds)), 8);
    }

    private void displaySlowMo() {
        g.setColor(Color.WHITE);
        g.drawRect(20, 60, 100, 8);
        g.fillRect(20, 60, (int) (100 - (100 * slowMoTimerElapsedMilliseconds / slowMoDurationMilliseconds)), 8);
    }

    private void drawBackground() {
        if (slowMoTimerElapsedMilliseconds != 0) {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }
        else if (rapidFireElapsedMilliseconds != 0) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }
        else {
            g.setColor(Color.GRAY);
            g.fillRect(0, 0, WIDTH, HEIGHT);
        }
    }

    private void displayAverageFPS() {
        g.setColor(Color.BLACK);
        g.setFont(new Font(FONT_STYLE, Font.PLAIN, 11));
        g.drawString("FPS: " + (int) averageFPS, 5, 10);
    }

    private void displayPlayerLives() {
        for (int i = 0; i < player.getLives(); i++) {
            g.setColor(player.getNormalColor());
            g.fillOval(20 + (20 * i), 20, 15, 15);
        }
    }

    private void displayPlayerScore() {
        g.setColor(Color.BLACK);
        g.setFont(new Font(FONT_STYLE, Font.PLAIN, 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100, 30);
    }

    private void displayWaveNumber() {
            g.setFont(new Font(FONT_STYLE, Font.PLAIN, 18));
            String s = "-   W A V E   " + waveNumber + "   -";
            int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();

            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerElapsedMilliseconds / waveDelayMilliseconds));
            if (alpha > 255) {
                alpha = 255;
            }

            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
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
     * Spawn next wave.
     */
    private void spawnWave() {
        if (waveStartTimerNanoseconds == 0 && enemies.size() == 0) {
            waveNumber++;
            waveStart = false;
            waveStartTimerNanoseconds = System.nanoTime();
        } else {
            waveStartTimerElapsedMilliseconds = (System.nanoTime() - waveStartTimerNanoseconds) / 1000000;

            if (waveStartTimerElapsedMilliseconds > waveDelayMilliseconds) {
                waveStart = true;
                waveStartTimerNanoseconds = 0;
                waveStartTimerElapsedMilliseconds = 0;
            }
        }

        if (waveStart && enemies.size() == 0) {
            createNewEnemies();
        }
    }

    /**
     * Create enemies for current wave.
     */
    private void createNewEnemies() {
        enemies.clear();

        for (int i = 0; i < waveNumber; i++) {
            enemies.add(new Enemy(1, 1));
        }

        for (int i = 0; i < waveNumber / 3; i++) {
            enemies.add(new Enemy(2, 1));
        }

        for (int j = 0; j < waveNumber / 5; j++) {
            enemies.add(new Enemy(3, 1));
        }

        for (int k = 0; k < waveNumber / 7; k++) {
            enemies.add(new Enemy(1, 3));
        }

        for (int i = 0; i < waveNumber / 10; i++) {
            enemies.add(new Enemy(2, 3));
        }

        for (int i = 0; i < waveNumber / 15; i++) {
            enemies.add(new Enemy(3, 3));
        }
    }

    /**
     * Check bullet-enemy collision. If collision, mark enemy as hit and remove bullet.
     */
    private void checkBulletEnemyCollision() {
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
                    e.hit(player.getDamage());
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
                double random = Math.random();
                if (random < 0.001) powerUps.add(new PowerUp(1, e.getX(), e.getY()));
                else if (random < 0.05) powerUps.add(new PowerUp(2, e.getX(), e.getY()));
                else if (random < 0.1) powerUps.add(new PowerUp(3, e.getX(), e.getY()));
                else if (random < 0.15) powerUps.add(new PowerUp(4, e.getX(), e.getY()));
                else if (random < 0.2) powerUps.add(new PowerUp(5, e.getX(), e.getY()));

                player.addScore(e.getType() + e.getRank());
                enemies.remove(i);
                i--;

                e.explode();
            }
        }
    }

    /**
     * Check player-enemy collision. If collision, mark player as hit and recovering. Player is invincible while recovering.
     */
    private void checkPlayerEnemyCollision() {
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

                if (!player.isRecovering() && distance < playerR + enemyR) {
                    player.hit();

                    if (player.isDead()) {
                        running = false;
                    }
                }
            }
        }
    }

    /**
     * Check player-power up collision. If collision, add appropriate power up to player.
     */
    private void checkPlayerPowerUpCollision() {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerR = player.getR();

        for (int i = 0; i < powerUps.size(); i++) {
            PowerUp powerUp = powerUps.get(i);

            double powerUpX = powerUp.getX();
            double powerUpy = powerUp.getY();
            double powerUpR = powerUp.getR();

            double dx = playerX - powerUpX;
            double dy = playerY - powerUpy;
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < playerR + powerUpR) {
                int type = powerUp.getType();

                switch(type) {
                    case 1:
                        player.gainLife();
                        break;
                    case 2:
                        player.increasePower(1);
                        break;
                    case 3:
                        player.increaseDamage(1);
                        break;
                    case 4:
                        slowMoTimerNanoseconds = System.nanoTime();
                        for (int j = 0; j < enemies.size(); j++) {
                            enemies.get(j).setSlow(true);
                        }
                        break;
                    case 5:
                        rapidFireTimerNanoseconds = System.nanoTime();
                        player.setRapidFire(true);
                }

                texts.add(new Text(player.getX(), player.getY(), powerUp.getName()));

                powerUps.remove(i);
                i--;
            }
        }

        if (slowMoTimerNanoseconds != 0 ) {
            slowMoTimerElapsedMilliseconds = (System.nanoTime() - slowMoTimerNanoseconds) / 1000000;

            if (slowMoTimerElapsedMilliseconds > slowMoDurationMilliseconds) {
                slowMoTimerNanoseconds = 0;
                for (int j = 0; j < enemies.size(); j++) {
                    enemies.get(j).setSlow(false);
                }
            }
        }

        if (rapidFireTimerNanoseconds != 0) {
            rapidFireElapsedMilliseconds = (System.nanoTime() - rapidFireTimerNanoseconds) / 1000000;

            if (rapidFireElapsedMilliseconds > rapidFireDurationMilliseconds) {
                rapidFireTimerNanoseconds = 0;
                player.setRapidFire(false);
            }
        }
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

    private void updateEnemies() {
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
        }
    }

    /**
     * Update bullets and remove those that exceed game boundary
     */
    private void updateBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            boolean collisionWithBoundary = bullets.get(i).update();
            if (collisionWithBoundary) {
                bullets.remove(i);
                i--;
            }
        }
    }

    /**
     * Update power ups and remove those that have been collected or exceed game boundary
     */
    private void updatePowerUps() {
        for (int i = 0; i < powerUps.size(); i++) {
            boolean remove = powerUps.get(i).update();
            if (remove) {
                powerUps.remove(i);
                i--;
            }
        }
    }

    private void updateTexts() {
        for (int i = 0; i < texts.size(); i++) {
            boolean remove = texts.get(i).update();
            if (remove) {
                texts.remove(i);
                i--;
            }
        }
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

    private void renderPowerUps() {
        for (int i = 0; i < powerUps.size(); i++) {
            powerUps.get(i).draw(g);
        }
    }

    private void renderTexts() {
        for (int i = 0; i < texts.size(); i++) {
            texts.get(i).draw(g);
        }
    }
}
