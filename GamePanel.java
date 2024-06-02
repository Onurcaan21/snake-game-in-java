import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.prefs.Preferences;
import java.io.File;
import javax.sound.sampled.*;

public class GamePanel extends JPanel implements ActionListener {

    private static final long serialUID = 1L;

    static final int WIDTH = 500;
    static final int HEIGHT = 500;
    static final int UNIT_SIZE = 20;

    LinkedList<Point> snake;
    Set<Point> snakeSet;
    int length = 5;
    int foodEaten;
    int foodX;
    int foodY;
    char direction = 'D';
    boolean running = false;
    Random random;
    Timer timer;
    Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    HashMap<String, Integer> highScores;
    JButton playAgainButton;
    JButton modeSelectionButton;
    JButton resetScoresButton; 
    int gameMode; 
    GameFrame gameFrame;
    boolean changedDirection = false; 

    GamePanel(GameFrame gameFrame, int gameMode) {
        this.gameFrame = gameFrame;
        this.gameMode = gameMode;
        random = new Random();
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.DARK_GRAY);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        snake = new LinkedList<>();
        snakeSet = new HashSet<>();
        for (int i = 0; i < length; i++) {
            Point p = new Point(0, 0);
            snake.add(p);
            snakeSet.add(p);
        }

        highScores = new HashMap<>();
        highScores.put("normal", prefs.getInt("HighScore", 0));
        highScores.put("SpeedMode", prefs.getInt("SpeedModeHighScore", 0));

        playAgainButton = new JButton("Play again");
        playAgainButton.setFocusable(false);
        playAgainButton.setBackground(new Color(239, 83, 80)); 
        playAgainButton.setForeground(Color.WHITE); 
        playAgainButton.setFont(new Font("SansSerif", Font.BOLD, 20)); 
        playAgainButton.setFocusPainted(false);
        playAgainButton.setBorderPainted(false);
        playAgainButton.setPreferredSize(new Dimension(200, 50));
        playAgainButton.setContentAreaFilled(false);
        playAgainButton.setOpaque(true);
        playAgainButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });

        modeSelectionButton = new JButton("Change Mode");
        modeSelectionButton.setFocusable(false);
        modeSelectionButton.setBackground(new Color(239, 83, 80)); 
        modeSelectionButton.setForeground(Color.WHITE); 
        modeSelectionButton.setFont(new Font("SansSerif", Font.BOLD, 20)); 
        modeSelectionButton.setFocusPainted(false);
        modeSelectionButton.setBorderPainted(false);
        modeSelectionButton.setPreferredSize(new Dimension(200, 50));
        modeSelectionButton.setContentAreaFilled(false);
        modeSelectionButton.setOpaque(true);
        modeSelectionButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        modeSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameFrame.showModeSelection();
            }
        });

        resetScoresButton = new JButton("Reset Scores");
        resetScoresButton.setFocusable(false);
        resetScoresButton.setBackground(new Color(239, 83, 80));
        resetScoresButton.setForeground(Color.WHITE);
        resetScoresButton.setFont(new Font("SansSerif", Font.BOLD, 20));
        resetScoresButton.setFocusPainted(false);
        resetScoresButton.setBorderPainted(false);
        resetScoresButton.setPreferredSize(new Dimension(200, 50));
        resetScoresButton.setContentAreaFilled(false);
        resetScoresButton.setOpaque(true);
        resetScoresButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        resetScoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetHighScores();
            }
        });

        this.setLayout(null); 
        play();
    }

    public void play() {
        addFood();
        running = true;
        timer = new Timer(80, this);
        timer.start();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (running) {
            drawBackground(graphics);
        }
        draw(graphics);
    }

    public void drawBackground(Graphics graphics) {
        for (int row = 0; row < HEIGHT / UNIT_SIZE; row++) {
            for (int col = 0; col < WIDTH / UNIT_SIZE; col++) {
                if ((row + col) % 2 == 0) {
                    graphics.setColor(new Color(173, 216, 230)); 
                } else {
                    graphics.setColor(new Color(0, 191, 255)); 
                }
                graphics.fillRect(col * UNIT_SIZE, row * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        }
    }

    public void move() {
        Point newHead = new Point(snake.get(0));
        if (direction == 'L') {
            newHead.x -= UNIT_SIZE;
        } else if (direction == 'R') {
            newHead.x += UNIT_SIZE;
        } else if (direction == 'U') {
            newHead.y -= UNIT_SIZE;
        } else if (direction == 'D') {
            newHead.y += UNIT_SIZE;
        }

        snake.addFirst(newHead);
        snakeSet.add(newHead);

        if (snake.size() > length) {
            Point tail = snake.removeLast();
            snakeSet.remove(tail);
        }

        changedDirection = false; 

        if (newHead.x < 0 || newHead.x >= WIDTH || newHead.y < 0 || newHead.y >= HEIGHT) {
            running = false;
        }
    }

    public void checkFood() {
        if (snake.get(0).equals(new Point(foodX, foodY))) {
            foodEaten++;
            playEatSound(); 
            addFood();
            length++;
            if (gameMode == 0 && foodEaten > highScores.get("normal")) {
                highScores.put("normal", foodEaten);
                prefs.putInt("HighScore", highScores.get("normal")); 
            } else if (gameMode == 1 && foodEaten > highScores.get("SpeedMode")) {
                highScores.put("SpeedMode", foodEaten);
                prefs.putInt("SpeedModeHighScore", highScores.get("SpeedMode"));
            }
            if (gameMode == 1) {
                int newDelay = Math.max(10, timer.getDelay() - 5); 
                timer.setDelay(newDelay);
            }
        }
    }

    public void draw(Graphics graphics) {
        if (running) {
            graphics.setColor(Color.red);
            graphics.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    graphics.setColor(Color.green); 
                    graphics.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
                    graphics.setColor(Color.black);
                    graphics.fillOval(p.x + 4, p.y + 4, 4, 4);
                    graphics.fillOval(p.x + 12, p.y + 4, 4, 4);
                } else {
                    graphics.setColor(new Color(34, 139, 34)); 
                    graphics.fillRect(p.x, p.y, UNIT_SIZE, UNIT_SIZE);
                }
            }

            graphics.setColor(Color.white);
            graphics.setFont(new Font("SansSerif", Font.PLAIN, 25));
            FontMetrics metrics = getFontMetrics(graphics.getFont());
            graphics.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, graphics.getFont().getSize());

        } else {
            setBackground(Color.DARK_GRAY); 
            gameOver(graphics);
        }
    }

    public void addFood() {
        boolean validPosition = false;
        while (!validPosition) {
            foodX = random.nextInt((int) (WIDTH / UNIT_SIZE)) * UNIT_SIZE;
            foodY = random.nextInt((int) (HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
            Point foodPoint = new Point(foodX, foodY);
            if (!snakeSet.contains(foodPoint)) {
                validPosition = true;
            }
        }
    }

    public void checkHit() {
        Point head = snake.get(0);
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics graphics) {
        graphics.setColor(Color.red);
        graphics.setFont(new Font("SansSerif", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2 - 100);

        graphics.setColor(Color.white);
        graphics.setFont(new Font("SansSerif", Font.PLAIN, 25));
        metrics = getFontMetrics(graphics.getFont());
        graphics.drawString("Score: " + foodEaten, (WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, HEIGHT / 2 - 50);
        if (gameMode == 0) {
            graphics.drawString("HighScore: " + highScores.get("normal"), (WIDTH - metrics.stringWidth("HighScore: " + highScores.get("normal"))) / 2, HEIGHT / 2);
        } else if (gameMode == 1) {
            graphics.drawString("HighScore: " + highScores.get("SpeedMode"), (WIDTH - metrics.stringWidth("HighScore: " + highScores.get("SpeedMode"))) / 2, HEIGHT / 2);
        }

        playAgainButton.setBounds((WIDTH - 200) / 2, HEIGHT / 2 + 50, 200, 50); 
        modeSelectionButton.setBounds((WIDTH - 200) / 2, HEIGHT / 2 + 110, 200, 50); 
        resetScoresButton.setBounds((WIDTH - 200) / 2, HEIGHT / 2 + 170, 200, 50); 
        this.add(playAgainButton);
        this.add(modeSelectionButton);
        this.add(resetScoresButton);
        this.revalidate();
        this.repaint();
    }

    public void resetGame() {
        this.remove(playAgainButton);
        this.remove(modeSelectionButton);
        this.remove(resetScoresButton);
        length = 5;
        foodEaten = 0;
        direction = 'D';
        snake.clear();
        snakeSet.clear();
        for (int i = 0; i < length; i++) {
            Point p = new Point(0, 0);
            snake.add(p);
            snakeSet.add(p);
        }
        setBackground(Color.DARK_GRAY); 
        play();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (running) {
            move();
            checkFood();
            checkHit();
        }
        repaint();
    }

    public void playEatSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Bite_sound_effect_sliced_1sec.wav").getAbsoluteFile());
            AudioFormat baseFormat = audioInputStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    baseFormat.getEncoding(),
                    baseFormat.getSampleRate() * 2, 
                    baseFormat.getSampleSizeInBits(),
                    baseFormat.getChannels(),
                    baseFormat.getFrameSize(),
                    baseFormat.getFrameRate() * 2, 
                    baseFormat.isBigEndian()
            );
            AudioInputStream fastAudioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            Clip clip = AudioSystem.getClip();
            clip.open(fastAudioInputStream);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void resetHighScores() {
        highScores.put("normal", 0);
        highScores.put("SpeedMode", 0);
        prefs.putInt("HighScore", 0);
        prefs.putInt("SpeedModeHighScore", 0);
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (changedDirection) return; 

            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                        changedDirection = true;
                    }
                    break;

                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                        changedDirection = true;
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                        changedDirection = true;
                    }
                    break;

                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                        changedDirection = true;
                    }
                    break;
            }
        }
    }
}

