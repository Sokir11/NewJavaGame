import java.awt.*;

public class Player {
    public static final int SIZE = 17;
    private int x;
    private int y;

//builder
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }
//move right left up down
    public void moveRight(int speed) {
        this.x+=speed;
    }
    public void moveLeft(int speed) {
        this.x-=speed;
    }
    public void moveUp(int speed) {
        this.y-=speed;
    }
    public void moveDown(int speed) {this.y+=speed;}

//getters  setters
    public int getX () {return this.x;}
     public int getY() {return this.y;}


//draws the player
    public void draw(Graphics graphics) {
        graphics.setColor(Color.CYAN);
        //head
        graphics.fillRect(this.x, this.y, SIZE, SIZE);
        graphics.setColor(Color.CYAN);
        int shoulderWidth = (int) (SIZE / 2);
        //body
        graphics.fillRect(
                this.x - shoulderWidth,
                this.y + SIZE,
                SIZE + shoulderWidth + shoulderWidth,
                SIZE + shoulderWidth + shoulderWidth
        );
    }
}
