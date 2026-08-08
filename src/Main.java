import javax.swing.*;
import java.awt.*;

public class Main {
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Snake / Robot Game");
            window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel mainPanel = new JPanel(cardLayout);

            // יצירת זירת המשחק ומסכי הניהול
            Scene gameScene = new Scene(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT, cardLayout, mainPanel);
            JPanel menuPanel = createMenuPanel(cardLayout, mainPanel, gameScene);
            JPanel instructionsPanel = createInstructionsPanel(cardLayout, mainPanel);

            mainPanel.add(menuPanel, "MENU");
            mainPanel.add(instructionsPanel, "INSTRUCTIONS");
            mainPanel.add(gameScene, "GAME");

            window.add(mainPanel);
            cardLayout.show(mainPanel, "MENU");
            window.setVisible(true);
        });
    }

    private static JPanel createMenuPanel(CardLayout cardLayout, JPanel mainPanel, Scene gameScene) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.DARK_GRAY);

        JLabel titleLabel = new JLabel("סנייק - המשחק");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        // בחירת רמות קושי (עונה על דרישת התוספות במטלה)
        JButton easyButton = new JButton("קל (מעט סלעים)");
        JButton mediumButton = new JButton("בינוני");
        JButton hardButton = new JButton("קשה (הרבה סלעים)");
        JButton instructionsButton = new JButton("הוראות משחק");

        Font btnFont = new Font("Arial", Font.PLAIN, 18);
        easyButton.setFont(btnFont);
        mediumButton.setFont(btnFont);
        hardButton.setFont(btnFont);
        instructionsButton.setFont(btnFont);

        easyButton.addActionListener(e -> startGameWithDifficulty(cardLayout, mainPanel, gameScene, 1));
        mediumButton.addActionListener(e -> startGameWithDifficulty(cardLayout, mainPanel, gameScene, 3));
        hardButton.addActionListener(e -> startGameWithDifficulty(cardLayout, mainPanel, gameScene, 6));

        instructionsButton.addActionListener(e -> cardLayout.show(mainPanel, "INSTRUCTIONS"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(10, 10, 20, 10);
        panel.add(titleLabel, gbc);

        gbc.gridy = 1; panel.add(easyButton, gbc);
        gbc.gridy = 2; panel.add(mediumButton, gbc);
        gbc.gridy = 3; panel.add(hardButton, gbc);
        gbc.gridy = 4; gbc.insets = new Insets(30, 10, 10, 10);
        panel.add(instructionsButton, gbc);

        return panel;
    }

    private static void startGameWithDifficulty(CardLayout cardLayout, JPanel mainPanel, Scene gameScene, int difficultyLevel) {
        gameScene.setDifficulty(difficultyLevel);
        cardLayout.show(mainPanel, "GAME");
        gameScene.startGame();
        gameScene.requestFocusInWindow();
    }

    private static JPanel createInstructionsPanel(CardLayout cardLayout, JPanel mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);

        // מסך ההוראות המדויק שכתבת
        JTextArea instructionsText = new JTextArea(
                "==================================================\n" +
                        "                 SNAKE / ROBOT GAME\n" +
                        "==================================================\n" +
                        "\n" +
                        "1. GAME OBJECTIVE:\n" +
                        "   * Control a small robot to collect food and score points.\n" +
                        "   * Each food collected grants 1 point.\n" +
                        "\n" +
                        "2. CONTROLS:\n" +
                        "   * W - Move Up\n" +
                        "   * A - Move Left\n" +
                        "   * S - Move Down\n" +
                        "   * D - Move Right\n" +
                        "   * Space - Pause / Stop movement\n" +
                        "\n" +
                        "3. ELEMENTS & OBSTACLES:\n" +
                        "   * Robot: The player character.\n" +
                        "   * Rocks (Obstacles): Colored BLACK. Touching a rock results in death.\n" +
                        "   * Food: Comes in random colors (excluding black). Each food item \n" +
                        "     has a random color generated dynamically.\n" +
                        "\n" +
                        "4. DIFFICULTY LEVELS (Start Screen):\n" +
                        "   * 3 Buttons available: Easy, Medium, Hard.\n" +
                        "   * Higher difficulty increases the amount of rocks on the board.\n" +
                        "\n" +
                        "5. SCORE & HIGHSCORE:\n" +
                        "   * Tracks current score during gameplay.\n" +
                        "   * Automatically saves the maximum score achieved and displays it \n" +
                        "     on the screen when the player dies.\n" +
                        "\n" +
                        "6. PAUSE / RESUME:\n" +
                        "   * Use the Spacebar to pause or resume gameplay flow.\n" +
                        "=================================================="
        );
        instructionsText.setFont(new Font("Arial", Font.PLAIN, 14));
        instructionsText.setEditable(false);
        instructionsText.setOpaque(false);
        instructionsText.setMargin(new Insets(20, 20, 20, 20));

        JButton backButton = new JButton("חזרה לתפריט הראשי");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);

        panel.add(new JScrollPane(instructionsText), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }
}
