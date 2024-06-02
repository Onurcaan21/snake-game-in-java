import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;

public class GameFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    CardLayout cardLayout;
    JPanel mainPanel;

    GameFrame() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        ModeSelectionPanel modeSelectionPanel = new ModeSelectionPanel(this);
        mainPanel.add(modeSelectionPanel, "ModeSelection");

        this.add(mainPanel);
        this.setTitle("Snake Game");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public void startGame(int gameMode) {
        GamePanel gamePanel = new GamePanel(this, gameMode);
        mainPanel.add(gamePanel, "Game");
        cardLayout.show(mainPanel, "Game");
        gamePanel.requestFocusInWindow(); 
    }

    public void showModeSelection() {
        cardLayout.show(mainPanel, "ModeSelection");
    }
}
