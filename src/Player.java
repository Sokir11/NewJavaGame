import java.awt.*;

public class Player {
    public static final int SIZE = 17;

    private int x;
    private int y;
    private boolean isHit = false; // משתנה שמציין האם השחקן נפגע

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

    // פונקציה לסימון שהשחקן נפגע (מופתע)
    public void setHit(boolean hit) {
        this.isHit = hit;
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
    // CHECK FOOD COLLISION
    // =========================

    public boolean touchesFood(Rectangle food) {

        Rectangle head = getHeadRect();
        Rectangle body = getBodyRect();

        int shoulderWidth = SIZE / 2;

        int bodyX = this.x - shoulderWidth;
        int bodyY = this.y + SIZE;

        int bodyWidth = SIZE + shoulderWidth + shoulderWidth;
        int bodyHeight = SIZE + shoulderWidth + shoulderWidth;

        // LEFT ARM
        Rectangle leftArm = new Rectangle(
                bodyX - 8,
                bodyY + 4,
                8,
                bodyHeight - 8
        );

        // RIGHT ARM
        Rectangle rightArm = new Rectangle(
                bodyX + bodyWidth,
                bodyY + 4,
                8,
                bodyHeight - 8
        );

        // LEFT LEG
        Rectangle leftLeg = new Rectangle(
                bodyX + 3,
                bodyY + bodyHeight,
                7,
                15
        );

        // RIGHT LEG
        Rectangle rightLeg = new Rectangle(
                bodyX + bodyWidth - 10,
                bodyY + bodyHeight,
                7,
                15
        );

        return head.intersects(food)
                || body.intersects(food)
                || leftArm.intersects(food)
                || rightArm.intersects(food)
                || leftLeg.intersects(food)
                || rightLeg.intersects(food);
    }

    // =========================
    // DRAW ROBOT
    // =========================

    public void draw(Graphics graphics) {

        // HEAD
        graphics.setColor(Color.CYAN);

        graphics.fillRect(
                this.x,
                this.y,
                SIZE,
                SIZE
        );

        // EYES
        graphics.setColor(Color.BLACK);
        graphics.fillRect(this.x + 3, this.y + 5, 3, 3); // עין שמאלית
        graphics.fillRect(this.x + 11, this.y + 5, 3, 3); // עין ימנית

        // MOUTH (אם השחקן נפגע - מציגים עיגול מופתע, אחרת - חיוך רגיל)
        if (isHit) {
            graphics.fillOval(this.x + 6, this.y + 10, 5, 5); // פה עגול ומופתע
        } else {
            graphics.fillRect(this.x + 5, this.y + 11, 7, 2); // חיוך רגיל
        }

        // BODY
        int shoulderWidth = SIZE / 2;

        int bodyX = this.x - shoulderWidth;
        int bodyY = this.y + SIZE;
        int bodyWidth = SIZE + shoulderWidth + shoulderWidth;
        int bodyHeight = SIZE + shoulderWidth + shoulderWidth;

        graphics.setColor(Color.CYAN);

        graphics.fillRect(
                bodyX,
                bodyY,
                bodyWidth,
                bodyHeight
        );

        // ARMS
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

        // LEGS
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