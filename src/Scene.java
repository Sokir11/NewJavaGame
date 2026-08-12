import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences;

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

    // משתנים לשמירת השיא
    private Preferences prefs = Preferences.userNodeForPackage(Scene.class);
    private int highScore = prefs.getInt("highScore", 0);

    public static final int PLAYER_X = 150;
    public static final int PLAYER_Y = 150;

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        initGameElements();
    }

    public Scene(int x, int y, int width, int height,
                 CardLayout cardLayout, JPanel mainPanel) {

        this.width = width;
        this.height = height;
        this.setBounds(x, y, width, height);
        this.setLayout(new BorderLayout());

        JButton backButton = new JButton("Back to Menu");
        backButton.setFocusable(false);
        backButton.addActionListener(e -> {
            isRunning = false;
            cardLayout.show(mainPanel, "MENU");
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);
        topPanel.add(backButton);
        this.add(topPanel, BorderLayout.NORTH);

        this.random = new Random();
        this.rocks = new ArrayList<>();

        initGameElements();

        this.setFocusable(true);
        this.addKeyListener(new MovementListener(this.player, this));
    }

    private void initGameElements() {
        this.score = 0;
        this.player = new Player(PLAYER_X, PLAYER_Y);
        generateFood();
        generateRocks();
    }

    private void generateFood() {
        int fx = random.nextInt(width - 50);
        int fy = random.nextInt(height - 50);
        food = new Point(fx, fy);
        foodColor = new Color(
                random.nextInt(150) + 55,
                random.nextInt(150) + 55,
                random.nextInt(150) + 55);
    }

    private void generateRocks() {
        int rockCount = 0;
        rocks.clear();

        if (difficulty == 1) { rockCount = random.nextInt(5, 9); }
        if (difficulty == 2) { rockCount = random.nextInt(10, 14); }
        if (difficulty == 3) { rockCount = random.nextInt(14, 18); }

        Player tempPlayer = new Player(PLAYER_X, PLAYER_Y);
        Rectangle playerHeadStart = tempPlayer.getHeadRect();
        Rectangle playerBodyStart = tempPlayer.getBodyRect();

        for (int i = 0; i < rockCount; i++) {
            int rx = random.nextInt(width - 100);
            int ry = random.nextInt(height - 100);
            Rectangle newRock = new Rectangle(rx, ry, 40, 40);

            boolean overlaps = true;

            while (overlaps) {
                overlaps = false;

                if (newRock.intersects(playerHeadStart) || newRock.intersects(playerBodyStart)) {
                    overlaps = true;
                } else {
                    for (Rectangle existingRock : rocks) {
                        if (newRock.intersects(existingRock)) {
                            overlaps = true;
                            break;
                        }
                    }
                }

                if (overlaps) {
                    rx = random.nextInt(width - 100);
                    ry = random.nextInt(height - 100);
                    newRock = new Rectangle(rx, ry, 40, 40);
                }
            }

            rocks.add(newRock);
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

                    int speed = difficulty + 3;
                    if (direction != null) {
                        if (direction == 0) { // Right
                            if (player.getX() < width - 25) {
                                player.moveRight(speed);
                            } else {
                                direction = 1;
                            }
                        } else if (direction == 1) { // Left
                            if (player.getX() >= 25) {
                                player.moveLeft(speed);
                            } else {
                                direction = 0;
                            }
                        } else if (direction == 2) { // Down
                            if (player.getY() < height - 55) {
                                player.moveDown(speed);
                            } else {
                                direction = 3;
                            }
                        } else if (direction == 3) { // Up
                            if (player.getY() >= 25) {
                                player.moveUp(speed);
                            } else {
                                direction = 2;
                            }
                        }
                    }

                    // PLAYER HITBOX
                    Rectangle headRect = player.getHeadRect();
                    Rectangle bodyRect = player.getBodyRect();
                    Rectangle foodRect = new Rectangle(food.x, food.y, 20, 20);

                    // Food collision
                    if (headRect.intersects(foodRect) || bodyRect.intersects(foodRect)) {
                        score++;
                        generateFood();
                    }

                    // Rock collision
                    for (Rectangle rock : rocks) {
                        if (headRect.intersects(rock) || bodyRect.intersects(rock)) {

                            if (score > highScore) {
                                highScore = score;
                                prefs.putInt("highScore", highScore);
                                JOptionPane.showMessageDialog(
                                        this,
                                        "NEW HIGH SCORE! 🏆\nYour new record: " + highScore);
                            } else {
                                JOptionPane.showMessageDialog(
                                        this,
                                        "YOU HIT A ROCK!!! \nYour score: " + score + "\nHigh Score: " + highScore);
                            }

                            score = 0;
                            player = new Player(PLAYER_X, PLAYER_Y);
                            direction = null;

                            generateRocks();
                            generateFood();

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
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
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

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 30);
        g.drawString("High Score: " + highScore, 20, 60);
    }
}