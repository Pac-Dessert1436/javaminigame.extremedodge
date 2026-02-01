import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public interface CircleObject {
    double getX();
    double getY();
    double getRadius();

    // Player class
    public class Player implements CircleObject {
        double x, y;
        double radius;
        Color color;

        public Player(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = new Color(135, 206, 235, 180);
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getRadius() {
            return radius;
        }
    }

    // Enemy class
    public class Enemy implements CircleObject {
        double x, y;
        double radius;
        double speed;
        double vx, vy;
        Color color;

        public Enemy(GamePanel gamePanel, Player player, int difficulty) {
            Random random = new Random();
            int side = random.nextInt(4);

            switch (side) {
                case 0: // Top
                    x = random.nextDouble() * gamePanel.getWidth();
                    y = -20;
                    break;
                case 1: // Right
                    x = gamePanel.getWidth() + 20;
                    y = random.nextDouble() * gamePanel.getHeight();
                    break;
                case 2: // Bottom
                    x = random.nextDouble() * gamePanel.getWidth();
                    y = gamePanel.getHeight() + 20;
                    break;
                case 3: // Left
                    x = -20;
                    y = random.nextDouble() * gamePanel.getHeight();
                    break;
            }

            radius = 15 + random.nextDouble() * 20;
            speed = 1 + random.nextDouble() * 2 + difficulty * 0.3;

            double angle = Math.atan2(player.y - y, player.x - x);
            vx = Math.cos(angle) * speed;
            vy = Math.sin(angle) * speed;

            int hue = random.nextInt(60);
            color = new Color(Color.HSBtoRGB((float) hue / 360f, 1.0f, 0.5f));
        }

        public void update(Player player) {
            x += vx;
            y += vy;

            // Track player
            double angle = Math.atan2(player.y - y, player.x - x);
            vx = Math.cos(angle) * speed;
            vy = Math.sin(angle) * speed;
        }

        public void draw(Graphics2D g2d) {
            // Add glow effect
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Glow
            RadialGradientPaint glowGradient = new RadialGradientPaint(
                    (float) x, (float) y, (float) (radius * 1.2),
                    new float[] { 0.0f, 1.0f },
                    new Color[] { new Color(color.getRed(), color.getGreen(), color.getBlue(), 200),
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), 0) });
            g2d.setPaint(glowGradient);
            g2d.fill(new Ellipse2D.Double(
                    x - radius * 1.2,
                    y - radius * 1.2,
                    radius * 2.4,
                    radius * 2.4));

            // Main body
            g2d.setColor(color);
            g2d.fill(new Ellipse2D.Double(
                    x - radius,
                    y - radius,
                    radius * 2,
                    radius * 2));
        }

        @Override
        public double getX() {
            return x;
        }

        @Override
        public double getY() {
            return y;
        }

        @Override
        public double getRadius() {
            return radius;
        }
    }
}