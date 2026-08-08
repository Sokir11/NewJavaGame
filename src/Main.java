import javax.swing.*;
import java.util.Date;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;


    public static void main(String[] args) {
        //JFrame
        JFrame window = new JFrame("My Game");
        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setResizable(false);
    //    window.add(new Menu(0, 0, WINDOW_WIDTH / 5, WINDOW_HEIGHT));
        window.add(new Scene(0, 0, WINDOW_WIDTH  , WINDOW_HEIGHT));
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setLayout(null);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

    }


}