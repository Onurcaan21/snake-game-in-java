import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ModeSelectionPanel extends JPanel {

    private static final long serialUID = 1L;

    ModeSelectionPanel(GameFrame gameFrame) {
        this.setPreferredSize(new Dimension(500, 500));
        this.setBackground(new Color(34, 45, 50)); 

        
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

    
        JLabel titleLabel = new JLabel("Snake Game");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        this.add(titleLabel, gbc);

    
        JLabel subtitleLabel = new JLabel("Select Game Mode");
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        gbc.gridy = 1;
        this.add(subtitleLabel, gbc);

        
        JButton normalModeButton = createButton("Normal Mode");
        normalModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.startGame(0); 
            }
        });
        gbc.gridy = 2;
        this.add(normalModeButton, gbc);

    
        JButton SpeedModeButton = createButton("Speed Mode");
        SpeedModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.startGame(1); 
            }
        });
        gbc.gridy = 3;
        this.add(SpeedModeButton, gbc);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(250, 50));
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(57, 106, 125));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 2),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        return button;
    }
}
