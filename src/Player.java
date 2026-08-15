import java.awt.*;

public class Player {
    public static final int SIZE = 17;

    private int x;
    private int y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveRight(int speed) {
        this.x += speed;
    }

    public void moveLeft(int speed) {
        this.x -= speed;
    }

    public void moveUp(int speed) {
        this.y -= speed;
    }

    public void moveDown(int speed) {
        this.y += speed;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    // =========================
    // HEAD HITBOX
    // =========================
    public Rectangle getHeadRect() {
        return new Rectangle(
                this.x,
                this.y,
                SIZE,
                SIZE
        );
    }

    // =========================
    // BODY HITBOX
    // =========================
    public Rectangle getBodyRect() {
        int shoulderWidth = SIZE / 2;

        return new Rectangle(
                this.x - shoulderWidth,
                this.y + SIZE,
                SIZE + shoulderWidth + shoulderWidth,
                SIZE + shoulderWidth + shoulderWidth
        );
    }

    // =========================
    // DRAW ROBOT
    // =========================
    public void draw(Graphics graphics) {

        // -------------------------
        // HEAD COLOR
        // -------------------------
        // כאן קובעים את הצבע של הראש
        graphics.setColor(Color.CYAN);

        // HEAD
        graphics.fillRect(
                this.x,
                this.y,
                SIZE,
                SIZE
        );

        // -------------------------
        // BODY
        // -------------------------
        int shoulderWidth = SIZE / 2;

        int bodyX = this.x - shoulderWidth;
        int bodyY = this.y + SIZE;
        int bodyWidth = SIZE + shoulderWidth + shoulderWidth;
        int bodyHeight = SIZE + shoulderWidth + shoulderWidth;

        //כאן קובעים את הצבע של הגוף
        graphics.setColor(Color.CYAN);
        graphics.fillRect(
                bodyX,
                bodyY,
                bodyWidth,
                bodyHeight
        );

        // -------------------------
        // ARMS COLOR
        // -------------------------
        // כאן קובעים את הצבע של שתי הידיים
        graphics.setColor(Color.BLUE);

        // LEFT ARM
        graphics.fillRect(
                bodyX - 8,
                bodyY + 4,
                8,
                bodyHeight - 8
        );

        // RIGHT ARM
        graphics.fillRect(
                bodyX + bodyWidth,
                bodyY + 4,
                8,
                bodyHeight - 8
        );

        // -------------------------
        // LEGS
        // -------------------------
        // כאן קובעים את הצבע של שתי רגליים
        graphics.setColor(Color.BLUE);

        // LEFT LEG
        graphics.fillRect(
                bodyX + 3,
                bodyY + bodyHeight,
                7,
                15
        );

        // RIGHT LEG
        graphics.fillRect(
                bodyX + bodyWidth - 10,
                bodyY + bodyHeight,
                7,
                15
        );
    }

    // =========================
    // FULL ROBOT BOUNDS
    // =========================
    public Rectangle getBounds() {

        int shoulderWidth = SIZE / 2;

        int bodyX = this.x - shoulderWidth;
        int bodyY = this.y + SIZE;

        int bodyWidth = SIZE + shoulderWidth + shoulderWidth;
        int bodyHeight = SIZE + shoulderWidth + shoulderWidth;

        // Include arms
        int totalX = bodyX - 8;
        int totalWidth = bodyWidth + 16;

        // Include legs
        int totalHeight = SIZE + bodyHeight + 15;

        return new Rectangle(
                totalX,
                this.y,
                totalWidth,
                totalHeight
        );
    }
}