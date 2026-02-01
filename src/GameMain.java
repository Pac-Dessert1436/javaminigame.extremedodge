import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameMain extends JFrame {
    private GamePanel gamePanel;
    private JLabel scoreLabel;
    private JLabel difficultyLabel;
    private JButton restartButton;

    public GameMain() {
        setTitle("Extreme Dodge - Java Mini Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Add key listener for 'R' to restart
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    gamePanel.restartGame();
                }
            }
        });
        setFocusable(true);
        requestFocusInWindow();

        // Set up the main layout
        setLayout(new BorderLayout());

        // Game info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(new Color(26, 26, 46));
        infoPanel.setOpaque(true);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        scoreLabel.setPreferredSize(new Dimension(200, 30));
        scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

        difficultyLabel = new JLabel("Difficulty: 1");
        difficultyLabel.setForeground(new Color(135, 206, 235));
        difficultyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        difficultyLabel.setPreferredSize(new Dimension(200, 20));
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        infoPanel.add(scoreLabel);
        infoPanel.add(difficultyLabel);

        add(infoPanel, BorderLayout.NORTH);

        // Create a layered panel to hold both game and overlay
        JPanel layeredPanel = new JPanel();
        layeredPanel.setLayout(null);

        // Game panel
        gamePanel = new GamePanel(this);
        gamePanel.setBounds(0, 0, 800, 600);
        layeredPanel.add(gamePanel);

        restartButton = new JButton("Restart Game");
        restartButton.setBackground(new Color(135, 206, 235, 180));
        restartButton.setForeground(Color.WHITE);
        restartButton.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        restartButton.setBorderPainted(false);
        restartButton.setFocusPainted(false);
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartButton.setPreferredSize(new Dimension(200, 40));
        restartButton.addActionListener(e -> gamePanel.restartGame());

        // Add layered panel to center
        layeredPanel.setPreferredSize(new Dimension(800, 600));
        add(layeredPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    public void updateScore(int score) {
        scoreLabel.setText("Score: " + score);
    }

    public void updateDifficulty(int difficulty) {
        if (gamePanel.gameRunning) {
            difficultyLabel.setText("Difficulty: " + difficulty);
        } else {
            difficultyLabel.setText("Press 'R' to restart.");
        }
    }

    public void showGameOver(int score, long survivalTime) {
        // Determine rank
        String rank;
        if (score < 500) {
            rank = "Newbie Warrior 😊";
        } else if (score < 1000) {
            rank = "Dodge Apprentice 💪";
        } else if (score < 2000) {
            rank = "Movement Master 😎";
        } else if (score < 5000) {
            rank = "Extreme Survivor 🔥";
        } else {
            rank = "Legendary Dodger 👑";
        }

        JOptionPane.showMessageDialog(this,
                String.format("Score: %d\nSurvival Time: %d seconds\nRank: %s",
                        score, survivalTime, rank),
                "GAME OVER!", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMain game = new GameMain();
            game.setVisible(true);
        });
    }
}