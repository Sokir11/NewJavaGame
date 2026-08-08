import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Scene extends JPanel {
    private Player player;
    private int width;
    private int height;
    private  Integer direction=null;
    public void setDirection(Integer direction){
        this.direction=direction;
    }


    public Scene (int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        this.setBounds(x, y, width, height);
        this.player = new Player(100, 100);


        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(new MovementListener(this.player,this));


        this.mainGameLoop();
    }

    public void mainGameLoop () {
        new Thread(() -> {
            Random R1 = new Random();
            while (true) {
                System.out.println(this.player.getX());
                if (direction!=null) {
                    if (direction == 0) {
                        if (this.player.getX() < this.width - 5) {
                            this.player.moveRight();
                        } else {
                            direction = 1;
                            this.player.moveLeft();
                        }
                    }
                    if (direction == 1) {
                        if (this.player.getX() >= 5) {
                            this.player.moveLeft();
                        } else {
                            direction = 0;
                        }
                    }
                    if (direction == 2) {
                        this.player.moveDown();
                    }
                    if (direction == 3) {
                        this.player.moveUp();
                    }
                }
                try {
                    Thread.sleep(10);
                    this.repaint();
                } catch (InterruptedException e) {}
            }
        }).start();
    }

    public void paintComponent (Graphics graphics) {
        super.paintComponent(graphics);
        this.player.draw(graphics);

    }
}
