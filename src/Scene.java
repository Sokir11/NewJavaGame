import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Scene extends JPanel {
    private Player player;
    private int width;
    private int height;
    private Integer direction = null;
    private boolean isRunning = false;
    private int score = 0;
    private int difficulty = 1;
    private Point food;
    private Color foodColor;
    private ArrayList<Rectangle> rocks;
    private Random random;

    public void setDirection(Integer direction) {
        this.direction = direction;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        initGameElements();
    }
    public Scene(int x, int y, int width, int height, CardLayout cardLayout, JPanel mainPanel) {
        this.width = width;
        this.height = height;
        this.setBounds(x, y, width, height);
        this.random = new Random();
        this.player = new Player(100, 100);
        this.rocks = new ArrayList<>();
        this.setFocusable(true);
        this.addKeyListener(new MovementListener(this.player, this));
    }
    private void initGameElements() {
        this.score = 0;
        this.player = new Player(150, 150);
        generateFood();
        generateRocks();
    }
    private void generateFood() {
        int fx = random.nextInt(width - 50);
        int fy = random.nextInt(height - 50);
        food = new Point(fx, fy);
        foodColor = new Color(random.nextInt(200) + 55, random.nextInt(200) + 55, random.nextInt(200) + 55);
    }
    private void generateRocks() {
        rocks.clear();
        int rockCount = difficulty * 5;
        for (int i = 0; i < rockCount; i++) {
            int rx = random.nextInt(width - 100);
            int ry = random.nextInt(height - 100);
            rocks.add(new Rectangle(rx, ry, 40, 40));
        }
    }
    public void startGame() {
        if (!isRunning) {
            isRunning = true;
            mainGameLoop();
        }
    }
    private void mainGameLoop() {
        Thread gameThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(35);
                    if (direction != null) {
                        if (direction == 0) player.moveRight();
                        else if (direction == 1) player.moveLeft();
                        else if (direction == 2) player.moveDown();
                        else if (direction == 3) player.moveUp();
                    }
                    Rectangle playerRect = new Rectangle(player.getX() - 15, player.getY(), 40, 50);
                    Rectangle foodRect = new Rectangle(food.x, food.y, 20, 20);
                    if (playerRect.intersects(foodRect)) {
                        score++;
                        generateFood();
                    }
                    for (Rectangle rock : rocks) {
                        if (playerRect.intersects(rock)) {
                            JOptionPane.showMessageDialog(this, "התנגשת בסלע שחור! הניקוד שלך: " + score);
                            score = 0;
                            player = new Player(150, 150);
                            direction = null;
                            break;
                        }
                    }
                    repaint();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        gameThread.start();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.DARK_GRAY);
        for (Rectangle rock : rocks) {
            g.fillRect(rock.x, rock.y, rock.width, rock.height);
            g.setColor(Color.BLACK);
            g.drawRect(rock.x, rock.y, rock.width, rock.height);
            g.setColor(Color.DARK_GRAY);
        }
        if (food != null && foodColor != null) {
            g.setColor(foodColor);
            g.fillOval(food.x, food.y, 20, 20);
        }
        if (player != null) {
            player.draw(g);
        }
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
    }
}
        this.player.draw(graphics);
    }
}
