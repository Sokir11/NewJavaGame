import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.prefs.Preferences;

// ==========================================
// IMPORTS FOR MUSIC
// ==========================================

// מאפשר לקרוא קובץ WAV
import javax.sound.sampled.AudioInputStream;

// מאפשר לפתוח ולקרוא את קובץ הקול
import javax.sound.sampled.AudioSystem;

// מאפשר לנגן את המוזיקה ולעשות לה LOOP
import javax.sound.sampled.Clip;

public class Scene extends JPanel {

    private Player player;
    private int width;
    private int height;

    // כיוון התנועה של השחקן
    // 0 = RIGHT
    // 1 = LEFT
    // 2 = DOWN
    // 3 = UP
    private Integer direction = null;

    // האם המשחק כרגע רץ
    private boolean isRunning = false;

    // הניקוד הנוכחי
    private int score = 0;

    // רמת הקושי
    private int difficulty = 1;

    // מיקום האוכל
    private Point food;

    // צבע האוכל
    private Color foodColor;

    // רשימת הסלעים
    private ArrayList<Rectangle> rocks;

    // מחולל מספרים אקראיים
    private Random random;


    // ==========================================
    // MUSIC
    // ==========================================

    // כאן נשמור את נגן המוזיקה
    private Clip music;


    // ==========================================
    // HIGH SCORE
    // ==========================================

    // Preferences מאפשר לשמור את השיא
    // גם אחרי שסוגרים את המשחק
    private Preferences prefs =
            Preferences.userNodeForPackage(Scene.class);

    private int highScore =
            prefs.getInt("highScore", 0);


    // ==========================================
    // PLAYER START POSITION
    // ==========================================

    public static final int PLAYER_X = 150;
    public static final int PLAYER_Y = 150;


    // ==========================================
    // CHANGE DIRECTION
    // ==========================================

    // מקבל את כיוון התנועה מה-MovementListener
    public void setDirection(Integer direction) {
        this.direction = direction;
    }


    // ==========================================
    // CHANGE DIFFICULTY
    // ==========================================

    // משנה את רמת הקושי ומאתחל את המשחק
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


        // ==========================================
        // SET SCENE SIZE
        // ==========================================

        this.setBounds(
                x,
                y,
                width,
                height
        );

        this.setLayout(new BorderLayout());


        // ==========================================
        // BACK BUTTON
        // ==========================================

        JButton backButton =
                new JButton("Back to Menu");

        backButton.setFocusable(false);


        // כאשר לוחצים על Back to Menu
        backButton.addActionListener(e -> {

            // עוצרים את המשחק
            isRunning = false;

            // עוצרים את המוזיקה
            stopMusic();

            // חוזרים לתפריט
            cardLayout.show(mainPanel, "MENU");
        });


        // ==========================================
        // TOP PANEL
        // ==========================================

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


        // ==========================================
        // INITIALIZE GAME
        // ==========================================

        this.random = new Random();

        this.rocks =
                new ArrayList<>();

        initGameElements();


        // ==========================================
        // KEYBOARD
        // ==========================================

        this.setFocusable(true);

        this.addKeyListener(
                new MovementListener(
                        this.player,
                        this
                )
        );
    }


    // ==========================================
    // INITIALIZE GAME ELEMENTS
    // ==========================================

    private void initGameElements() {

        // מאפסים את הניקוד
        this.score = 0;

        // יוצרים שחקן חדש
        this.player =
                new Player(
                        PLAYER_X,
                        PLAYER_Y
                );

        // יוצרים אוכל
        generateFood();

        // יוצרים סלעים
        generateRocks();
    }


    // ==========================================
    // GENERATE FOOD
    // ==========================================

    private void generateFood() {

        // ממשיכים להגריל עד שמוצאים
        // מקום חוקי לאוכל
        while (true) {

            // מיקום X אקראי
            int fx =
                    random.nextInt(
                            width - 50
                    );

            // מיקום Y אקראי
            int fy =
                    random.nextInt(
                            height - 50
                    );


            // מלבן שמייצג את האוכל
            Rectangle foodRect =
                    new Rectangle(
                            fx,
                            fy,
                            20,
                            20
                    );

            // בהתחלה מניחים שהמיקום חוקי
            boolean validPosition = true;


            // ==========================================
            // CHECK ROCKS
            // ==========================================

            // בודקים שהאוכל לא קרוב מדי לסלע
            for (Rectangle rock : rocks) {

                // אזור בטוח סביב הסלע
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

            // בודקים שהאוכל לא נוצר
            // בתוך הרובוט
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

                // שומרים את מיקום האוכל
                food = new Point(
                        fx,
                        fy
                );


                // נותנים לאוכל צבע אקראי
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

        // מוחקים את הסלעים הקודמים
        rocks.clear();


        // ==========================================
        // ROCK COUNT BY DIFFICULTY
        // ==========================================

        // EASY
        if (difficulty == 1) {

            rockCount =
                    random.nextInt(5, 9);
        }


        // MEDIUM
        if (difficulty == 2) {

            rockCount =
                    random.nextInt(10, 14);
        }


        // HARD
        if (difficulty == 3) {

            rockCount =
                    random.nextInt(14, 18);
        }


        // שחקן זמני שמשמש כדי לוודא
        // שסלע לא ייווצר על נקודת ההתחלה
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


            // יצירת סלע בגודל 40x40
            Rectangle newRock =
                    new Rectangle(
                            rx,
                            ry,
                            40,
                            40
                    );


            boolean overlaps = true;


            // ממשיכים להגריל עד שהמיקום חוקי
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
                // DON'T PLACE ROCK ON OTHER ROCK
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


            // מוסיפים את הסלע לרשימה
            rocks.add(newRock);
        }
    }


    // ==========================================
    // CHECK PLAYER PARTS
    // WITH DIFFICULTY HITBOX SHRINKING
    // ==========================================

    private boolean playerTouchesRock(
            Player p,
            Rectangle targetRect) {


        // ==========================================
        // HITBOX SHRINKING
        // ==========================================

        // ככל שהמשחק קל יותר,
        // ההיטבוקס קטן יותר.
        //
        // רמה 1:
        // יותר מקום לטעויות
        //
        // רמה 2:
        // פחות מקום לטעויות
        //
        // רמה 3:
        // היטבוקס מלא

        int shrinkFactor = 0;


        if (difficulty == 1) {

            shrinkFactor = 2;

        } else if (difficulty == 2) {

            shrinkFactor = 1;

        } else if (difficulty == 3) {

            shrinkFactor = 0;
        }


        // ==========================================
        // HEAD
        // ==========================================

        Rectangle headRect =
                p.getHeadRect();


        // יוצרים היטבוקס חדש לראש
        // לפי רמת הקושי
        Rectangle currentHead =
                new Rectangle(
                        headRect.x +
                                (shrinkFactor / 2),

                        headRect.y +
                                (shrinkFactor / 2),

                        Math.max(
                                1,
                                headRect.width -
                                        shrinkFactor
                        ),

                        Math.max(
                                1,
                                headRect.height -
                                        shrinkFactor
                        )
                );


        // ==========================================
        // BODY DIMENSIONS
        // ==========================================

        int shoulderWidth =
                Player.SIZE / 2;

        int bodyX =
                p.getX() -
                        shoulderWidth;

        int bodyY =
                p.getY() +
                        Player.SIZE;

        int bodyWidth =
                Player.SIZE +
                        shoulderWidth +
                        shoulderWidth;

        int bodyHeight =
                Player.SIZE +
                        shoulderWidth +
                        shoulderWidth;


        // גוף רגיל
        Rectangle bodyRect =
                new Rectangle(
                        bodyX,
                        bodyY,
                        bodyWidth,
                        bodyHeight
                );


        // גוף עם היטבוקס מוקטן
        Rectangle currentBody =
                new Rectangle(
                        bodyRect.x +
                                (shrinkFactor / 2),

                        bodyRect.y +
                                (shrinkFactor / 2),

                        Math.max(
                                1,
                                bodyRect.width -
                                        shrinkFactor
                        ),

                        Math.max(
                                1,
                                bodyRect.height -
                                        shrinkFactor
                        )
                );


        // ==========================================
        // LEFT ARM
        // ==========================================

        Rectangle leftArmRect =
                new Rectangle(
                        bodyX - 8,
                        bodyY + 4,
                        8,
                        bodyHeight - 8
                );


        Rectangle currentLeftArm =
                new Rectangle(
                        leftArmRect.x +
                                (shrinkFactor / 2),

                        leftArmRect.y +
                                (shrinkFactor / 2),

                        Math.max(
                                1,
                                leftArmRect.width -
                                        shrinkFactor
                        ),

                        Math.max(
                                1,
                                leftArmRect.height -
                                        shrinkFactor
                        )
                );


        // ==========================================
        // RIGHT ARM
        // ==========================================

        Rectangle rightArmRect =
                new Rectangle(
                        bodyX + bodyWidth,
                        bodyY + 4,
                        8,
                        bodyHeight - 8
                );


        Rectangle currentRightArm =
                new Rectangle(
                        rightArmRect.x +
                                (shrinkFactor / 2),

                        rightArmRect.y +
                                (shrinkFactor / 2),

                        Math.max(
                                1,
                                rightArmRect.width -
                                        shrinkFactor
                        ),

                        Math.max(
                                1,
                                rightArmRect.height -
                                        shrinkFactor
                        )
                );


        // ==========================================
        // LEFT LEG
        // ==========================================

        Rectangle leftLegRect =
                new Rectangle(
                        bodyX + 3,
                        bodyY + bodyHeight,
                        7,
                        15
                );


        Rectangle currentLeftLeg =
                new Rectangle(
                        leftLegRect.x +
                                (shrinkFactor / 2),

                        leftLegRect.y +
                                (shrinkFactor / 2),

                        Math.max(
                                1,
                                leftLegRect.width -
                                        shrinkFactor
                        ),

                        Math.max(
                                1,
                                leftLegRect.height -
                                        shrinkFactor
                        )
                );


        // ==========================================
        // RIGHT LEG
        // ==========================================

        Rectangle rightLegRect =
                new Rectangle(
                        bodyX + bodyWidth - 10,
                        bodyY + bodyHeight,
                        7,
                        15
                );


        Rectangle currentRightLeg =
                new Rectangle(
                        rightLegRect.x +
                                (shrinkFactor / 2),

                        rightLegRect.y +
                                (shrinkFactor / 2),

                        Math.max(
                                1,
                                rightLegRect.width -
                                        shrinkFactor
                        ),

                        Math.max(
                                1,
                                rightLegRect.height -
                                        shrinkFactor
                        )
                );


        // ==========================================
        // CHECK ALL PARTS
        // ==========================================

        // אם אחד מחלקי הרובוט נוגע במטרה
        // מחזירים true
        return currentHead.intersects(targetRect)
                || currentBody.intersects(targetRect)
                || currentLeftArm.intersects(targetRect)
                || currentRightArm.intersects(targetRect)
                || currentLeftLeg.intersects(targetRect)
                || currentRightLeg.intersects(targetRect);
    }


    // ==========================================
    // PLAY BACKGROUND MUSIC
    // ==========================================

    private void playMusic() {

        try {

            // טוענים את קובץ המוזיקה.
            //
            // IMPORTANT:
            // הקובץ צריך להיות בתוך src
            //
            // D:\NewJavaGame\src\
            // extra-life_A3pTxwAe.wav

            AudioInputStream audioInputStream =
                    AudioSystem.getAudioInputStream(
                            getClass().getResource(
                                    "/Overworld_Run.wav"
                            )
                    );


            // יוצרים Clip חדש
            music =
                    AudioSystem.getClip();


            // טוענים את הקובץ ל-Clip
            music.open(
                    audioInputStream
            );


            // ==========================================
            // LOOP
            // ==========================================

            // המוזיקה תחזור על עצמה
            // כל פעם שהיא מסתיימת
            music.loop(
                    Clip.LOOP_CONTINUOUSLY
            );


            // מתחילים לנגן
            music.start();

        } catch (Exception e) {

            // אם הקובץ לא נמצא או שיש בעיה
            // בפתיחת המוזיקה,
            // השגיאה תופיע ב-Console
            e.printStackTrace();
        }
    }


    // ==========================================
    // STOP MUSIC
    // ==========================================

    private void stopMusic() {

        // בודקים שהמוזיקה באמת קיימת
        if (music != null) {

            // עוצרים את המוזיקה
            music.stop();

            // סוגרים את קובץ הקול
            music.close();

            // מאפסים את המשתנה
            music = null;
        }
    }


    // ==========================================
    // START GAME
    // ==========================================

    public void startGame() {

        // מונע התחלה של המשחק
        // ושל המוזיקה מספר פעמים
        if (!isRunning) {

            // המשחק התחיל
            isRunning = true;


            // מתחילים את מוזיקת הרקע
            playMusic();


            // מתחילים את לולאת המשחק
            mainGameLoop();
        }
    }


    // ==========================================
    // GET SLEEP TIME
    // ==========================================

    private int getSleepTime() {

        // EASY
        if (difficulty == 1) {
            return 10;
        }


        // MEDIUM
        if (difficulty == 2) {
            return 6;
        }


        // HARD
        return 2;
    }


    // ==========================================
    // GET PLAYER SPEED
    // ==========================================

    private int getPlayerSpeed() {

        // כרגע בכל הרמות הרובוט
        // זז פיקסל אחד בכל תנועה.
        //
        // ההבדל במהירות נוצר באמצעות
        // זמן ההמתנה ב-getSleepTime().

        return 1;
    }


    // ==========================================
    // MAIN GAME LOOP
    // ==========================================

    private void mainGameLoop() {

        // יוצרים Thread נפרד למשחק
        Thread gameThread =
                new Thread(() -> {

                    // המשחק ממשיך כל עוד isRunning הוא true
                    while (isRunning) {

                        try {

                            // מחכים לפי רמת הקושי
                            Thread.sleep(
                                    getSleepTime()
                            );


                            // ==========================================
                            // PLAYER MOVEMENT
                            // ==========================================

                            if (direction != null) {

                                // -------------------------
                                // RIGHT
                                // -------------------------

                                if (direction == 0) {

                                    if (player.getX() <
                                            width - 25) {

                                        player.moveRight(
                                                getPlayerSpeed()
                                        );

                                    } else {

                                        direction = 1;
                                    }


                                    // -------------------------
                                    // LEFT
                                    // -------------------------

                                } else if (direction == 1) {

                                    if (player.getX() >= 25) {

                                        player.moveLeft(
                                                getPlayerSpeed()
                                        );

                                    } else {

                                        direction = 0;
                                    }


                                    // -------------------------
                                    // DOWN
                                    // -------------------------

                                } else if (direction == 2) {

                                    if (player.getY() <
                                            height - 55) {

                                        player.moveDown(
                                                getPlayerSpeed()
                                        );

                                    } else {

                                        direction = 3;
                                    }


                                    // -------------------------
                                    // UP
                                    // -------------------------

                                } else if (direction == 3) {

                                    if (player.getY() >= 25) {

                                        player.moveUp(
                                                getPlayerSpeed()
                                        );

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


                            // touchesFood בודקת:
                            // ראש + גוף + ידיים + רגליים
                            if (player.touchesFood(
                                    foodRect
                            )) {

                                // מוסיפים נקודה
                                score++;

                                // יוצרים אוכל חדש
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


                                // אם הרובוט פגע בסלע
                                if (hitRock) {


                                    // ==========================================
                                    // SHOCK FACE
                                    // ==========================================

                                    // משנים את הפנים של הרובוט
                                    // לפרצוף מופתע
                                    player.setHit(true);

                                    // מציירים מיד את השינוי
                                    repaint();


                                    // ==========================================
                                    // HIGH SCORE
                                    // ==========================================

                                    if (score > highScore) {

                                        // שומרים את השיא החדש
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

                                    // מאפסים את הניקוד
                                    score = 0;


                                    // יוצרים שחקן חדש
                                    player =
                                            new Player(
                                                    PLAYER_X,
                                                    PLAYER_Y
                                            );


                                    // מפסיקים את התנועה
                                    direction = null;


                                    // מייצרים סלעים חדשים
                                    generateRocks();


                                    // מייצרים אוכל חדש
                                    generateFood();


                                    // יוצאים מהלולאה של הסלעים
                                    break;
                                }
                            }


                            // ==========================================
                            // REPAINT
                            // ==========================================

                            // מציירים מחדש את המשחק
                            repaint();


                        } catch (InterruptedException e) {

                            e.printStackTrace();
                        }
                    }
                });


        // מפעילים את Thread המשחק
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

        // רקע לבן
        g.setColor(Color.WHITE);

        g.fillRect(
                0,
                0,
                width,
                height
        );


        // ==========================================
        // ROCKS
        // ==========================================

        // צבע הסלעים
        g.setColor(Color.BLACK);


        for (Rectangle rock : rocks) {

            // מילוי הסלע
            g.fillRect(
                    rock.x,
                    rock.y,
                    rock.width,
                    rock.height
            );


            // מסגרת הסלע
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

        // מציירים את האוכל רק אם הוא קיים
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

            // מציירים את הרובוט
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


        // ניקוד נוכחי
        g.drawString(
                "Score: " + score,
                20,
                30
        );


        // שיא
        g.drawString(
                "High Score: " + highScore,
                20,
                60
        );
    }
}