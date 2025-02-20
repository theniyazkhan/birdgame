import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class RunnerGame extends JPanel implements ActionListener, KeyListener {
    private int playerY = 250;
    private int velocityY = 0;
    private final int gravity = 1;
    private int jumpCount = 0;

    private ArrayList<Rectangle> obstacles;
    private Random random;
    private Timer timer;
    private int score = 0;
    private int lives = 3;

    private Image background;
    private Image bird;
    private Image tree;

    public RunnerGame() {
        this.setPreferredSize(new Dimension(800, 400));
        this.setFocusable(true);
        this.addKeyListener(this);

        obstacles = new ArrayList<>();
        random = new Random();

        // Load images
        background = new ImageIcon(getClass().getResource("/images/background.jpg")).getImage();
        bird = new ImageIcon(getClass().getResource("/images/bird.png")).getImage();
        tree = new ImageIcon(getClass().getResource("/images/tree.png")).getImage();

        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
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
            obstacles.add(new Rectangle(800, 270, 50, 70)); // Tree size
        }

        // Check for collisions
        for (int i = 0; i < obstacles.size(); i++) {
            Rectangle obs = obstacles.get(i);
            if (obs.intersects(new Rectangle(50, playerY, 50, 50))) {
                obstacles.remove(i);
                i--;
                lives--;
                if (lives <= 0) {
                    timer.stop();
                    JOptionPane.showMessageDialog(this, "Game Over!\nScore: " + score, "Game Over",
                            JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
                break;
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
        g.drawImage(bird, 40, playerY, 40, 40, null);

        // Draw obstacles as trees
        for (Rectangle obs : obstacles) {
            g.drawImage(tree, obs.x, obs.y, obs.width, obs.height, null);
        }

        // Draw score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 650, 30);

        // Draw lives
        g.setColor(Color.RED);
        g.drawString("Lives: " + lives, 50, 30);
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

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pookie Bird");
        RunnerGame game = new RunnerGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
