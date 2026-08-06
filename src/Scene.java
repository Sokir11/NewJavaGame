import javax.swing.*;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

public class Scene extends JFrame {

    // מחלקה פנימית סטטית כדי שהכל יעבוד בקובץ אחד בלי בעיות
    public static class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            graphics.setColor(Color.RED);
            graphics.fillRect(100, 100, 100, 100);
        }
    }

    public static void main(String[] args) {
        JFrame window = new JFrame("Game");
        window.setSize(1500, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLocationRelativeTo(null);

        GamePanel panel = new GamePanel();
        window.add(panel);

        window.setVisible(true);
    }
}
