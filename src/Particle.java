import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

public final class Particle {
    double x, y;
    double vx, vy;
    double radius;
    Color color;
    float alpha;
    float decay;

    public Particle(double x, double y, Color color) {
        Random random = new Random();
        this.x = x;
        this.y = y;
        this.vx = (random.nextDouble() - 0.5) * 8;
        this.vy = (random.nextDouble() - 0.5) * 8;
        this.radius = random.nextDouble() * 3 + 1;
        this.color = color;
        this.alpha = 1.0f;
        this.decay = 0.02f;
    }

    public void update() {
        x += vx;
        y += vy;
        alpha -= decay;
    }

    public void draw(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Set alpha
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (alpha * 255)));
        g2d.fill(new Ellipse2D.Double(
                x - radius,
                y - radius,
                radius * 2,
                radius * 2));
    }
}