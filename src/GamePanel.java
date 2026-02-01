import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.awt.geom.*;

public final class GamePanel extends JPanel implements MouseMotionListener, MouseListener {
    private GameMain parent;
    public boolean gameRunning = true;
    private int score = 0;
    private long startTime;
    private int difficulty = 1;
    private int enemySpawnTimer = 0;
    private int enemySpawnInterval = 120;

    // Constants
    private final int PLAYER_RADIUS = 20;
    private final int SCREEN_WIDTH = 800;
    private final int SCREEN_HEIGHT = 600;

    // Player
    private CircleObject.Player player;

    // Enemy and particle lists
    private ArrayList<CircleObject.Enemy> enemies;
    private ArrayList<Particle> particles;

    // Mouse position
    private double targetX, targetY;
    private boolean isMouseDown;

    // Timer for game loop
    private Timer gameTimer;

    public GamePanel(GameMain parent) {
        this.parent = parent;
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(new Color(26, 26, 46));

        // Initialize game elements
        enemies = new ArrayList<>();
        particles = new ArrayList<>();
        targetX = SCREEN_WIDTH / 2;
        targetY = SCREEN_HEIGHT / 2;
        isMouseDown = false;

        // Initialize player with minimum radius to avoid zero-radius error
        player = new CircleObject.Player(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2, PLAYER_RADIUS);

        // Add mouse listeners
        addMouseMotionListener(this);
        addMouseListener(this);

        // Start game timer
        startTime = System.currentTimeMillis();
        gameTimer = new Timer(16, e -> gameLoop());
        gameTimer.start();
    }

    private void gameLoop() {
        if (gameRunning) {
            update();
            repaint();
        }
    }

    private void update() {
        // Update player position
        double dx = targetX - player.x;
        double dy = targetY - player.y;
        player.x += dx * 0.1;
        player.y += dy * 0.1;

        // Limit player to canvas
        player.x = Math.max(player.radius, Math.min(SCREEN_WIDTH - player.radius, player.x));
        player.y = Math.max(player.radius, Math.min(SCREEN_HEIGHT - player.radius, player.y));

        // Spawn enemies
        enemySpawnTimer++;
        if (enemySpawnTimer >= enemySpawnInterval) {
            enemies.add(new CircleObject.Enemy(this, player, difficulty));
            enemySpawnTimer = 0;
            enemySpawnInterval = Math.max(30, 120 - difficulty * 10);
        }

        // Update enemies
        for (int i = enemies.size() - 1; i >= 0; i--) {
            CircleObject.Enemy enemy = enemies.get(i);
            enemy.update(player);

            // Check collision with player
            if (checkCollision(player, enemy)) {
                gameRunning = false;
                long survivalTime = (System.currentTimeMillis() - startTime) / 1000;
                createExplosion(player.x, player.y, new Color(135, 206, 235, 180));
                repaint(); // Ensure final explosion is drawn
                gameTimer.stop(); // Stop game loop
                parent.showGameOver(score, survivalTime);
            }

            // Remove enemies that go off screen
            if (enemy.x < -50 || enemy.x > SCREEN_WIDTH + 50 ||
                    enemy.y < -50 || enemy.y > SCREEN_HEIGHT + 50) {
                enemies.remove(i);
                score += 10;
            }
        }

        // Check enemy-enemy collisions
        for (int i = enemies.size() - 1; i >= 0; i--) {
            for (int j = i - 1; j >= 0; j--) {
                try {
                    CircleObject.Enemy enemy1 = enemies.get(i);
                    CircleObject.Enemy enemy2 = enemies.get(j);

                    if (checkCollision(enemy1, enemy2)) {
                        createExplosion(enemy1.x, enemy1.y, enemy1.color);
                        createExplosion(enemy2.x, enemy2.y, enemy2.color);

                        enemies.remove(i);
                        enemies.remove(j);
                        score += 20;
                        break;
                    }
                } catch (Exception e) {
                    break;
                }
            }
        }

        // Update particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle particle = particles.get(i);
            particle.update();
            if (particle.alpha <= 0) {
                particles.remove(i);
            }
        }

        // Update score and difficulty
        score++;
        difficulty = Math.floorDiv(score, 500) + 1;

        parent.updateScore(score);
        parent.updateDifficulty(difficulty);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw background gradient
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(26, 26, 46),
                getWidth(), getHeight(), new Color(15, 52, 96));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw particles
        for (Particle particle : particles) {
            particle.draw(g2d);
        }

        // Draw enemies
        for (CircleObject.Enemy enemy : enemies) {
            enemy.draw(g2d);
        }

        // Draw player
        drawPlayer(g2d);
    }

    private void drawPlayer(Graphics2D g2d) {
        // Main body
        g2d.setColor(player.color);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Add glow effect
        RadialGradientPaint glowGradient = new RadialGradientPaint(
                (float) player.x, (float) player.y, (float) (player.radius * 1.5),
                new float[] { 0.0f, 1.0f },
                new Color[] { new Color(135, 206, 235, 180), new Color(135, 206, 235, 0) });
        g2d.setPaint(glowGradient);
        g2d.fill(new Ellipse2D.Double(
                player.x - player.radius * 1.5,
                player.y - player.radius * 1.5,
                player.radius * 3,
                player.radius * 3));

        // Draw main body
        g2d.setColor(player.color);
        g2d.fill(new Ellipse2D.Double(
                player.x - player.radius,
                player.y - player.radius,
                player.radius * 2,
                player.radius * 2));

        // Highlight effect
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fill(new Ellipse2D.Double(
                player.x - player.radius * 0.7,
                player.y - player.radius * 0.7,
                player.radius * 0.8,
                player.radius * 0.8));

        // Eyes
        g2d.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int eyeWidth = 3;
        int eyeHeight = (int) (player.radius * 0.6);
        int eyeY = (int) (player.y - eyeHeight / 2);

        // Left eye
        g2d.fillRect(
                (int) (player.x - player.radius * 0.5),
                eyeY,
                eyeWidth,
                eyeHeight);

        // Right eye
        g2d.fillRect(
                (int) (player.x + player.radius * 0.3),
                eyeY,
                eyeWidth,
                eyeHeight);
    }

    private boolean checkCollision(CircleObject obj1, CircleObject obj2) {
        double dx = obj1.getX() - obj2.getX();
        double dy = obj1.getY() - obj2.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < obj1.getRadius() + obj2.getRadius();
    }

    private void createExplosion(double x, double y, Color color) {
        for (int i = 0; i < 20; i++) {
            particles.add(new Particle(x, y, color));
        }
    }

    public void restartGame() {
        gameRunning = true;
        score = 0;
        difficulty = 1;
        enemies.clear();
        particles.clear();
        enemySpawnTimer = 0;
        enemySpawnInterval = 120;
        startTime = System.currentTimeMillis();

        // Reset player with minimum radius
        player = new CircleObject.Player(getWidth() / 2, getHeight() / 2, PLAYER_RADIUS);
        targetX = player.x;
        targetY = player.y;

        // Restart game timer
        gameTimer.start();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (isMouseDown) {
            targetX = e.getX();
            targetY = e.getY();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        targetX = e.getX();
        targetY = e.getY();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        isMouseDown = true;
        targetX = e.getX();
        targetY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isMouseDown = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
        isMouseDown = false;
    }
}