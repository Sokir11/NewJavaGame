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
    public static final int PLAYER_X=150;
    public static final int PLAYER_Y=150;

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        initGameElements();
    }
    //builder
    public Scene(int x, int y, int width, int height,
                 CardLayout cardLayout, JPanel mainPanel) {

        this.width = width;
        this.height = height;

        this.setBounds(x, y, width, height);

        // --- הוספת כפתור חזרה לתפריט כאן בדיוק ---
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
        // ----------------------------------------

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
    // GENERATES FOOD RANDOMLY
    private void generateFood() {

        int fx = random.nextInt(width - 50);
        int fy = random.nextInt(height - 50);

        food = new Point(fx, fy);

        foodColor = new Color(
                random.nextInt(150) + 55,
                random.nextInt(150) + 55,
                random.nextInt(150) + 55);
    }
    // ROCKS
    // GENERATES ROCKS BASED ON DIFFICULTY AND PREVENTS THEM FROM OVERLAPPING EACH OTHER OR THE PLAYER
    private void generateRocks() {
        int rockCount=0;
        rocks.clear();

        // RANDOMIZES THE AMOUNT OF ROCKS FOR EACH GAME DIFFICULTY MODE
        if(difficulty==1){ rockCount = random.nextInt(5,9);}
        if(difficulty==2){ rockCount = random.nextInt(10,14);}
        if(difficulty==3){ rockCount = random.nextInt(14,18);}

        // CREATES A RECTANGLE REPRESENTING THE INITIAL PLAYER COLLISION BOX
        Rectangle playerStartRect = new Rectangle(PLAYER_X - 15, PLAYER_Y, 40, 50);

        // LOOP TO CREATE EACH ROCK INDIVIDUALLY
        for (int i = 0; i < rockCount; i++) {

            // GENERATES RANDOM COORDINATES FOR THE ROCK
            int rx = random.nextInt(width - 100);
            int ry = random.nextInt(height - 100);
            Rectangle newRock = new Rectangle(rx, ry, 40, 40);

            boolean overlaps = true;

            // KEEPS GENERATING NEW COORDINATES WHILE THE ROCK OVERLAPS WITH THE PLAYER OR OTHER ROCKS
            while (overlaps) {
                overlaps = false;

                // CHECKS IF THE ROCK HITS THE PLAYER
                if (newRock.intersects(playerStartRect)) {
                    overlaps = true;
                } else {
                    // CHECKS IF THE ROCK HITS ANY ALREADY EXISTING ROCK
                    for (Rectangle existingRock : rocks) {
                        if (newRock.intersects(existingRock)) {
                            overlaps = true;
                            break;
                        }
                    }
                }

                // IF THERE IS AN OVERLAP, GENERATE NEW RANDOM COORDINATES
                if (overlaps) {
                    rx = random.nextInt(width - 100);
                    ry = random.nextInt(height - 100);
                    newRock = new Rectangle(rx, ry, 40, 40);
                }
            }

            // ADDS THE VALID SAFE ROCK TO THE ROCKS LIST
            rocks.add(newRock);
        }
    }
    //game start
    public void startGame() {
        if (!isRunning) {
            isRunning = true;
            mainGameLoop();
        }
    }
    //player movement
    private void mainGameLoop() {
        Thread gameThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(35);
                    if (direction != null) {
                        if (direction == 0) { // Right
                            if (player.getX() < width - 5) {
                                player.moveRight(difficulty + 3);
                            } else {
                                direction = 1;
                            }
                        } else if (direction == 1) { // Left
                            if (player.getX() >= 5) {
                                player.moveLeft(difficulty + 3);
                            } else {
                                direction = 0;
                            }
                        } else if (direction == 2) { // Down
                            if (player.getY() < height - 5) {
                                player.moveDown(difficulty + 3);
                            } else {
                                direction = 3;
                            }
                        } else if (direction == 3) { // Up
                            if (player.getY() >= 5) {
                                player.moveUp(difficulty + 3);
                            } else {
                                direction = 2;
                            }
                        }
                    }
                    Rectangle playerRect =
                            new Rectangle(
                                    player.getX() - 15,
                                    player.getY(),
                                    40,
                                    50
                            );

                    Rectangle foodRect =
                            new Rectangle(
                                    food.x,
                                    food.y,
                                    20,
                                    20
                            );

                    // Food collision
                    if (playerRect.intersects(foodRect)) {
                        score++;
                        generateFood();
                    }

                    // Rock collision
                    for (Rectangle rock : rocks) {
                        //Losers toast
                        if (playerRect.intersects(rock)) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "YOU HIT A ROCK!!! \nYour score: " + score);

                            score = 0;
                            player = new Player(150, 150);
                            direction = null;

                            // ג'نرור מחדש של האבנים והאוכל עם הפסילה
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
        // Background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // Rocks
        g.setColor(Color.BLACK);

        for (Rectangle rock : rocks) {

            g.fillRect(
                    rock.x,
                    rock.y,
                    rock.width,
                    rock.height
            );

            g.setColor(Color.BLACK);

            g.drawRect(
                    rock.x,
                    rock.y,
                    rock.width,
                    rock.height
            );

            g.setColor(Color.DARK_GRAY);
        }

        // Food
        if (food != null && foodColor != null) {

            g.setColor(foodColor);

            g.fillOval(
                    food.x,
                    food.y,
                    20,
                    20
            );
        }

        // Player
        if (player != null) {

            player.draw(g);
        }

        // Score
        g.setColor(Color.BLACK);

        g.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        g.drawString(
                "Score: " + score,
                20,
                30
        );

    }
}