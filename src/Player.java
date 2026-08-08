import java.awt.*;

public class Player {
    public static final int SIZE = 17;
    private int x;
    private int y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void moveRight() {
        this.x+=3;
    }

    public void moveLeft() {
        this.x-=3;
    }

    public void moveUp() {
        this.y-=3;
    }

    public void moveDown() {
        this.y+=3;
    }

    public int getX () {
        return this.x;
    }
     public int getY() {
        return this.y;
    }

    public void draw(Graphics graphics) {
        graphics.setColor(Color.CYAN);
        graphics.fillRect(this.x, this.y, SIZE, SIZE); //ראש
        graphics.setColor(Color.CYAN);
        int shoulderWidth = (int) (SIZE / 2);
        graphics.fillRect(
                this.x - shoulderWidth,
                this.y + SIZE,
                SIZE + shoulderWidth + shoulderWidth,
                SIZE + shoulderWidth + shoulderWidth
        ); //בטן
    }
}
