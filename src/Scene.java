import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences;

// ==========================================
// IMPORTS FOR MUSIC
// ==========================================
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

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

    // ==========================================
    // MUSIC
    // ==========================================
    private Clip music;

    // ==========================================
    // HIGH SCORE
    // ==========================================

    private Preferences prefs =
            Preferences.userNodeForPackage(Scene.class);

    private int highScore =
            prefs.getInt("highScore", 0);

    public static final int PLAYER_X = 150;
    public static final int PLAYER_Y = 150;

    // ==========================================
    // CHANGE DIRECTION
    // ==========================================

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    // ==========================================
    // CHANGE DIFFICULTY
    // ==========================================

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
        initGameElements();
    }

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public Scene(int x, int y, int width, int height,
                 CardLayout cardLayout, JPanel mainPanel) {

        this.width = width;
        this.height = height;

        this.setBounds(
                x,
                y,
                width,
                height
        );

        this.setLayout(new BorderLayout());

        JButton backButton =
                new JButton("Back to Menu");

        backButton.setFocusable(false);

        // ==========================================
        // BACK TO MENU
        // ==========================================

        backButton.addActionListener(e -> {

            // עוצרים את המשחק
            isRunning = false;

            // עוצרים את המוזיקה
            stopMusic();

            // חוזרים לתפריט
            cardLayout.show(mainPanel, "MENU");
        });

        JPanel topPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        topPanel.setOpaque(false);

        topPanel.add(backButton);

        this.add(
                topPanel,
                BorderLayout.NORTH
        );

        this.random = new Random();
        this.rocks = new ArrayList<>();

        initGameElements();

        this.setFocusable(true);

        this.addKeyListener(
                new MovementListener(
                        this.player,
                        this
                )
        );
    }

    // ==========================================
    // INITIALIZE GAME
    // ==========================================

    private void initGameElements() {

        this.score = 0;

        this.player =
                new Player(
                        PLAYER_X,
                        PLAYER_Y
                );

        generateFood();
        generateRocks();
    }

    // ==========================================
    // GENERATE FOOD
    // ==========================================

    private void generateFood() {

        while (true) {

            int fx =
                    random.nextInt(
                            width - 50
                    );

            int fy =
                    random.nextInt(
                            height - 50
                    );

            Rectangle foodRect =
                    new Rectangle(
                            fx,
                            fy,
                            20,
                            20
                    );

            boolean validPosition = true;

            // ==========================================
            // CHECK ROCKS
            // ==========================================

            for (Rectangle rock : rocks) {

                Rectangle rockSafeArea =
                        new Rectangle(
                                rock.x - 10,
                                rock.y - 10,
                                rock.width + 20,
                                rock.height + 20
                        );

                if (foodRect.intersects(rockSafeArea)) {

                    validPosition = false;
                    break;
                }
            }

            // ==========================================
            // CHECK PLAYER
            // ==========================================

            if (validPosition && player != null) {

                if (playerTouchesRock(
                        player,
                        foodRect
                )) {

                    validPosition = false;
                }
            }

            // ==========================================
            // VALID POSITION
            // ==========================================

            if (validPosition) {

                food = new Point(
                        fx,
                        fy
                );

                // צבע אקראי לאוכל
                foodColor = new Color(
                        random.nextInt(150) + 55,
                        random.nextInt(150) + 55,
                        random.nextInt(150) + 55
                );

                return;
            }
        }
    }

    // ==========================================
    // GENERATE ROCKS
    // ==========================================

    private void generateRocks() {

        int rockCount = 0;

        rocks.clear();

        // מספר הסלעים לפי רמת הקושי
        if (difficulty == 1) {
            rockCount = random.nextInt(5, 9);
        }

        if (difficulty == 2) {
            rockCount = random.nextInt(10, 14);
        }

        if (difficulty == 3) {
            rockCount = random.nextInt(14, 18);
        }

        Player tempPlayer =
                new Player(
                        PLAYER_X,
                        PLAYER_Y
                );

        // ==========================================
        // CREATE ROCKS
        // ==========================================

        for (int i = 0; i < rockCount; i++) {

            int rx =
                    random.nextInt(
                            width - 100
                    );

            int ry =
                    random.nextInt(
                            height - 100
                    );

            Rectangle newRock =
                    new Rectangle(
                            rx,
                            ry,
                            40,
                            40
                    );

            boolean overlaps = true;

            while (overlaps) {

                overlaps = false;

                // ==========================================
                // DON'T PLACE ROCK ON PLAYER
                // ==========================================

                if (playerTouchesRock(
                        tempPlayer,
                        newRock
                )) {

                    overlaps = true;
                }

                // ==========================================
                // DON'T PLACE ROCK ON ROCK
                // ==========================================

                if (!overlaps) {

                    for (Rectangle existingRock : rocks) {

                        if (newRock.intersects(existingRock)) {

                            overlaps = true;
                            break;
                        }
                    }
                }

                // ==========================================
                // GENERATE NEW POSITION
                // ==========================================

                if (overlaps) {

                    rx =
                            random.nextInt(
                                    width - 100
                            );

                    ry =
                            random.nextInt(
                                    height - 100
                            );

                    newRock =
                            new Rectangle(
                                    rx,
                                    ry,
                                    40,
                                    40
                            );
                }
            }

            rocks.add(newRock);
        }
    }

    // ==========================================
    // CHECK PLAYER PARTS (WITH DIFFICULTY HITBOX SHRINKING)
    // ==========================================

    private boolean playerTouchesRock(
            Player p,
            Rectangle targetRect) {

        // ==========================================
        // DIFFICULTY HITBOX ADJUSTMENT (SHRINKING)
        // ==========================================
        // רמה 1 (קלה): ההיטבוקס מוקטן כדי לתת יותר "סלחנות" לשחקן
        // רמה 2 (בינונית): הקטנה קטנה יותר
        // רמה 3 (קשה): ללא הקטנה (היטבוקס מלא ומדויק)
        int shrinkFactor = 0;
        if (difficulty == 1) {
            shrinkFactor = 2; // מקטין בשני הצדדים בשווה (פיקסל אחד מכל צד לשמירה על מרכוז)
        } else if (difficulty == 2) {
            shrinkFactor = 1;
        } else if (difficulty == 3) {
            shrinkFactor = 0;
        }

        // HEAD
        Rectangle headRect = p.getHeadRect();
        Rectangle currentHead = new Rectangle(
                headRect.x + (shrinkFactor / 2),
                headRect.y + (shrinkFactor / 2),
                Math.max(1, headRect.width - shrinkFactor),
                Math.max(1, headRect.height - shrinkFactor)
        );

        // BODY DIMENSIONS
        int shoulderWidth = Player.SIZE / 2;
        int bodyX = p.getX() - shoulderWidth;
        int bodyY = p.getY() + Player.SIZE;
        int bodyWidth = Player.SIZE + shoulderWidth + shoulderWidth;
        int bodyHeight = Player.SIZE + shoulderWidth + shoulderWidth;

        Rectangle bodyRect = new Rectangle(bodyX, bodyY, bodyWidth, bodyHeight);
        Rectangle currentBody = new Rectangle(
                bodyRect.x + (shrinkFactor / 2),
                bodyRect.y + (shrinkFactor / 2),
                Math.max(1, bodyRect.width - shrinkFactor),
                Math.max(1, bodyRect.height - shrinkFactor)
        );

        // LEFT ARM
        Rectangle leftArmRect = new Rectangle(
                bodyX - 8,
                bodyY + 4,
                8,
                bodyHeight - 8
        );
        Rectangle currentLeftArm = new Rectangle(
                leftArmRect.x + (shrinkFactor / 2),
                leftArmRect.y + (shrinkFactor / 2),
                Math.max(1, leftArmRect.width - shrinkFactor),
                Math.max(1, leftArmRect.height - shrinkFactor)
        );

        // RIGHT ARM
        Rectangle rightArmRect = new Rectangle(
                bodyX + bodyWidth,
                bodyY + 4,
                8,
                bodyHeight - 8
        );
        Rectangle currentRightArm = new Rectangle(
                rightArmRect.x + (shrinkFactor / 2),
                rightArmRect.y + (shrinkFactor / 2),
                Math.max(1, rightArmRect.width - shrinkFactor),
                Math.max(1, rightArmRect.height - shrinkFactor)
        );

        // LEFT LEG
        Rectangle leftLegRect = new Rectangle(
                bodyX + 3,
                bodyY + bodyHeight,
                7,
                15
        );
        Rectangle currentLeftLeg = new Rectangle(
                leftLegRect.x + (shrinkFactor / 2),
                leftLegRect.y + (shrinkFactor / 2),
                Math.max(1, leftLegRect.width - shrinkFactor),
                Math.max(1, leftLegRect.height - shrinkFactor)
        );

        // RIGHT LEG
        Rectangle rightLegRect = new Rectangle(
                bodyX + bodyWidth - 10,
                bodyY + bodyHeight,
                7,
                15
        );
        Rectangle currentRightLeg = new Rectangle(
                rightLegRect.x + (shrinkFactor / 2),
                rightLegRect.y + (shrinkFactor / 2),
                Math.max(1, rightLegRect.width - shrinkFactor),
                Math.max(1, rightLegRect.height - shrinkFactor)
        );

        // ==========================================
        // CHECK ALL PLAYER PARTS AGAINST TARGET
        // ==========================================

        return currentHead.intersects(targetRect)
                || currentBody.intersects(targetRect)
                || currentLeftArm.intersects(targetRect)
                || currentRightArm.intersects(targetRect)
                || currentLeftLeg.intersects(targetRect)
                || currentRightLeg.intersects(targetRect);
    }

    // ==========================================
    // PLAY MUSIC
    // ==========================================

    private void playMusic() {

        try {

            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(
                            getClass().getResource(
                                    "/Overworld_Run.wav"
                            )
                    );

            music = AudioSystem.getClip();
            music.open(audioInputStream);
            music.loop(Clip.LOOP_CONTINUOUSLY);
            music.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // STOP MUSIC
    // ==========================================

    private void stopMusic() {

        if (music != null) {
            music.stop();
            music.close();
            music = null;
        }
    }

    // ==========================================
    // START GAME
    // ==========================================

    public void startGame() {

        if (!isRunning) {
            isRunning = true;
            playMusic();
            mainGameLoop();
        }
    }

    // ==========================================
    // GET SLEEP TIME
    // ==========================================

    private int getSleepTime() {

        if (difficulty == 1) {
            return 10;
        }

        if (difficulty == 2) {
            return 6;
        }

        return 2;
    }

    // ==========================================
    // GET PLAYER SPEED
    // ==========================================

    private int getPlayerSpeed() {

        if (difficulty == 1) {
            return 1;
        }

        if (difficulty == 2) {
            return 1;
        }

        return 1;
    }

    // ==========================================
    // MAIN GAME LOOP
    // ==========================================

    private void mainGameLoop() {

        Thread gameThread =
                new Thread(() -> {

                    while (isRunning) {

                        try {

                            Thread.sleep(getSleepTime());

                            // ==========================================
                            // PLAYER MOVEMENT
                            // ==========================================

                            if (direction != null) {

                                if (direction == 0) {
                                    // RIGHT
                                    if (player.getX() < width - 25) {
                                        player.moveRight(getPlayerSpeed());
                                    } else {
                                        direction = 1;
                                    }

                                } else if (direction == 1) {
                                    // LEFT
                                    if (player.getX() >= 25) {
                                        player.moveLeft(getPlayerSpeed());
                                    } else {
                                        direction = 0;
                                    }

                                } else if (direction == 2) {
                                    // DOWN
                                    if (player.getY() < height - 55) {
                                        player.moveDown(getPlayerSpeed());
                                    } else {
                                        direction = 3;
                                    }

                                } else if (direction == 3) {
                                    // UP
                                    if (player.getY() >= 25) {
                                        player.moveUp(getPlayerSpeed());
                                    } else {
                                        direction = 2;
                                    }
                                }
                            }

                            // ==========================================
                            // FOOD COLLISION
                            // ==========================================

                            Rectangle foodRect =
                                    new Rectangle(
                                            food.x,
                                            food.y,
                                            20,
                                            20
                                    );

                            if (player.touchesFood(foodRect)) {
                                score++;
                                generateFood();
                            }

                            // ==========================================
                            // ROCK COLLISION
                            // ==========================================

                            for (Rectangle rock : rocks) {

                                Rectangle rockHitbox =
                                        new Rectangle(
                                                rock.x,
                                                rock.y,
                                                rock.width,
                                                rock.height
                                        );

                                boolean hitRock =
                                        playerTouchesRock(
                                                player,
                                                rockHitbox
                                        );

                                if (hitRock) {

                                    // ==========================================
                                    // SHOCK FACE (פרצוף מופתע בעת פגיעה)
                                    // ==========================================
                                    player.setHit(true);
                                    repaint(); // צביעה מיידית כדי להציג את הפרצוף המופתע

                                    // ==========================================
                                    // HIGH SCORE
                                    // ==========================================

                                    if (score > highScore) {

                                        highScore = score;

                                        prefs.putInt(
                                                "highScore",
                                                highScore
                                        );

                                        JOptionPane.showMessageDialog(
                                                this,
                                                "NEW HIGH SCORE! 🏆\n" +
                                                        "Your new record: "
                                                        + highScore
                                        );

                                    } else {

                                        JOptionPane.showMessageDialog(
                                                this,
                                                "YOU HIT A ROCK!!!\n" +
                                                        "Your score: "
                                                        + score +
                                                        "\nHigh Score: "
                                                        + highScore
                                        );
                                    }

                                    // ==========================================
                                    // RESET GAME
                                    // ==========================================

                                    score = 0;

                                    player =
                                            new Player(
                                                    PLAYER_X,
                                                    PLAYER_Y
                                            );

                                    direction = null;

                                    generateRocks();
                                    generateFood();

                                    break;
                                }
                            }

                            // ==========================================
                            // REPAINT
                            // ==========================================

                            repaint();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });

        gameThread.start();
    }

    // ==========================================
    // DRAW
    // ==========================================

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // ==========================================
        // BACKGROUND
        // ==========================================

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // ==========================================
        // ROCKS
        // ==========================================

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

            g.setColor(Color.BLACK);
        }

        // ==========================================
        // FOOD
        // ==========================================

        if (food != null &&
                foodColor != null) {

            g.setColor(foodColor);

            g.fillOval(
                    food.x,
                    food.y,
                    20,
                    20
            );
        }

        // ==========================================
        // PLAYER
        // ==========================================

        if (player != null) {
            player.draw(g);
        }

        // ==========================================
        // SCORE
        // ==========================================

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

        g.drawString(
                "High Score: " + highScore,
                20,
                60
        );
    }
}