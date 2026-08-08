import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MovementListener implements KeyListener {
    private Player player;
    private Scene scene;

    public MovementListener (Player player,Scene scene) {
        this.player = player;
        this.scene=scene;
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            this.scene.setDirection(1);
        } else if (e.getKeyCode() == KeyEvent.VK_D) {
            this.scene.setDirection(0);
        } else if (e.getKeyCode() == KeyEvent.VK_W) {
            this.scene.setDirection(3);

        } else if (e.getKeyCode() == KeyEvent.VK_S) {
            this.scene.setDirection(2);

        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            this.scene.setDirection(null);
        }}

    public void keyReleased(KeyEvent e) {
      //  System.out.println("Released");
    }
}
