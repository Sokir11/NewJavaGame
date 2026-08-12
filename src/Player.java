import java.awt.*;

public class Player {
    public static final int SIZE = 17;
    private int x;
    private int y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveRight(int speed) { this.x += speed; }
    public void moveLeft(int speed) { this.x -= speed; }
    public void moveUp(int speed) { this.y -= speed; }
    public void moveDown(int speed) { this.y += speed; }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    // HEAD
    public Rectangle getHeadRect() {
        return new Rectangle(this.x, this.y, SIZE, SIZE);
    }

    // BODY
    public Rectangle getBodyRect() {
        int shoulderWidth = SIZE / 2;
        return new Rectangle(
                this.x - shoulderWidth,
                this.y + SIZE,
                SIZE + shoulderWidth + shoulderWidth,
                SIZE + shoulderWidth + shoulderWidth
        );
    }

    public void draw(Graphics graphics) {
        graphics.setColor(Color.CYAN);
        // head
        graphics.fillRect(this.x, this.y, SIZE, SIZE);

        int shoulderWidth = SIZE / 2;
        // body
        graphics.fillRect(
                this.x - shoulderWidth,
                this.y + SIZE,
                SIZE + shoulderWidth + shoulderWidth,
                SIZE + shoulderWidth + shoulderWidth
        );
    }
    // הוסף את המתודה הזו למחלקת Player
    public Rectangle getBounds() {
        int shoulderWidth = SIZE / 2;
        int totalWidth = SIZE + shoulderWidth + shoulderWidth;
        int totalHeight = SIZE + (SIZE + shoulderWidth + shoulderWidth);
        return new Rectangle(this.x - shoulderWidth, this.y, totalWidth, totalHeight);
    }
}