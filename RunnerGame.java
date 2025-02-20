import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class RunnerGame extends JPanel implements ActionListener, KeyListener, MouseListener {
    private int playerY = 250;
    private int velocityY = 0;
    private final int gravity = 1;
    private int jumpCount = 0;

    private ArrayList<Rectangle> obstacles;
    private Rectangle healthPickup;
    private Random random;
    private Timer timer;
    private int score = 0;
    private int lives = 3;
    private int highScore = 0;
    private int previousHighScore = 0;
    private boolean gameOver = false;

    private Image background, bird, tree, heart;

    public RunnerGame() {
        this.setPreferredSize(new Dimension(800, 400));
        this.setFocusable(true);
        this.addKeyListener(this);
        this.addMouseListener(this);

        obstacles = new ArrayList<>();
        random = new Random();

        // Load images
        background = new ImageIcon(getClass().getResource("/images/background.jpg")).getImage();
        bird = new ImageIcon(getClass().getResource("/images/bird.png")).getImage();
        tree = new ImageIcon(getClass().getResource("/images/tree.png")).getImage();
        heart = new ImageIcon(getClass().getResource("/images/heart.png")).getImage();

        startGame();
    }

    public void startGame() {
        if (score > highScore) {
            // previousHighScore = highScore; // Store the previous high score
            highScore = score; // Update high score
        }

        score = 0;
        lives = 3;
        playerY = 250;
        velocityY = 0;
        jumpCount = 0;
        obstacles.clear();
        healthPickup = null;
        gameOver = false;
        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            score++;

            // Apply gravity
            velocityY += gravity;
            playerY += velocityY;

            if (playerY >= 250) {
                playerY = 250;
                velocityY = 0;
                jumpCount = 0;
            }

            // Move obstacles
            for (int i = 0; i < obstacles.size(); i++) {
                Rectangle obs = obstacles.get(i);
                obs.x -= 5;
                if (obs.x + obs.width < 0) {
                    obstacles.remove(i);
                    i--;
                }
            }

            // Add new obstacles randomly
            if (random.nextInt(100) < 2) {
                obstacles.add(new Rectangle(800, 270, 60, 70)); // Tree
            }

            // Spawn health pickup (heart)
            if (healthPickup == null && random.nextInt(500) < 2) {
                healthPickup = new Rectangle(800, 230, 30, 30);
            }

            // Move health pickup
            if (healthPickup != null) {
                healthPickup.x -= 5;
                if (healthPickup.x + healthPickup.width < 0) {
                    healthPickup = null;
                }
            }

            // Check for collisions
            for (int i = 0; i < obstacles.size(); i++) {
                Rectangle obs = obstacles.get(i);
                if (obs.intersects(new Rectangle(50, playerY, 50, 50))) {
                    obstacles.remove(i);
                    i--;
                    lives--;
                    if (lives <= 0) {
                        gameOver = true;
                        timer.stop();
                    }
                    break;
                }
            }

            // Check for health pickup
            if (healthPickup != null && healthPickup.intersects(new Rectangle(50, playerY, 50, 50))) {
                lives = Math.min(lives + 1, 3); // Max 3 lives
                healthPickup = null;
            }
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.drawImage(background, 0, 0, 800, 400, null);

        // Draw player as a bird
        g.drawImage(bird, 50, playerY, 40, 40, null);

        // Draw obstacles as trees
        for (Rectangle obs : obstacles) {
            g.drawImage(tree, obs.x, obs.y, obs.width, obs.height, null);
        }

        // Draw health pickup (heart)
        if (healthPickup != null) {
            g.drawImage(heart, healthPickup.x, healthPickup.y, healthPickup.width, healthPickup.height, null);
        }

        // Draw score & high score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 650, 30);
        g.drawString("High Score: " + highScore, 650, 50);
        // g.drawString("Previous High Score: " + previousHighScore, 650, 70);

        // Draw lives
        g.setColor(Color.RED);
        g.drawString("Lives: " + lives, 50, 30);

        // Show game over screen
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, 800, 400);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 25));
            g.drawString("Game Over!", 320, 150);
            g.drawString("Score: " + score, 320, 190);
            if (score >= highScore) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 25));
                g.drawString("New High Score: " + score, 280, 230);
                g.drawString("You Did Well!!", 320, 270);
            } else {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 23));
                g.drawString("Last High Score: " + highScore, 280, 230);
                g.setFont(new Font("Arial", Font.BOLD, 20));
                g.setColor(Color.WHITE);
                g.drawString("Better luck next time!", 300, 270);
            }

            drawRestartButton(g);
        }
    }

    private void drawRestartButton(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(300, 320, 200, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Restart", 370, 350);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver && e.getX() >= 300 && e.getX() <= 500 && e.getY() >= 320 && e.getY() <= 370) {
            startGame();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && jumpCount < 2) {
            velocityY = -15;
            jumpCount++;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pookie Bird");
        RunnerGame game = new RunnerGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
