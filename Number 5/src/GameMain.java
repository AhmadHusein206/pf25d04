import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.BLACK;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = Color.RED;
    public static final Color COLOR_NOUGHT = Color.BLUE;
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private static State currentState;
    private static Seed currentPlayer;
    private JLabel statusBar;
    private JButton restartButton;
    private JComboBox<String> difficultySelect;

    private Random rand = new Random();
    private boolean vsComputer = true;
    private String aiDifficulty = "Easy";

    private int scoreX = 0;
    private int scoreO = 0;

    public GameMain() {
        super.setLayout(new BorderLayout());

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == State.PLAYING) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int row = mouseY / Cell.SIZE;
                    int col = mouseX / Cell.SIZE;

                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {

                        currentState = board.stepGame(currentPlayer, row, col);
                        updateScoreIfNeeded();
                        repaint();

                        if (currentState == State.PLAYING && vsComputer && currentPlayer == Seed.CROSS) {
                            currentPlayer = Seed.NOUGHT;
                            makeAIMove();
                        } else if (currentState == State.PLAYING) {
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        }
                    }
                } else {
                    newGame();
                    repaint();
                }
            }
        });

        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> {
            newGame();
            repaint();
        });

        difficultySelect = new JComboBox<>(new String[]{"Easy"});
        difficultySelect.addActionListener(e -> aiDifficulty = (String) difficultySelect.getSelectedItem());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.add(new JLabel("AI Difficulty:"));
        controlPanel.add(difficultySelect);
        controlPanel.add(restartButton);

        bottomPanel.add(statusBar, BorderLayout.CENTER);
        bottomPanel.add(controlPanel, BorderLayout.EAST);
        super.add(bottomPanel, BorderLayout.PAGE_END);

        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    public void initGame() {
        board = new Board();
    }

    public void newGame() {
        board.newGame();
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
    }

    private void makeAIMove() {
        int[] move = null;
        move = getRandomMove();

        currentState = board.stepGame(Seed.NOUGHT, move[0], move[1]);
        updateScoreIfNeeded();

        if (currentState == State.PLAYING) {
            currentPlayer = Seed.CROSS;
        }
        repaint();
    }

    private int[] getRandomMove() {
        int row, col;
        do {
            row = rand.nextInt(Board.ROWS);
            col = rand.nextInt(Board.COLS);
        } while (board.cells[row][col].content != Seed.NO_SEED);
        return new int[]{row, col};
    }

    private void updateScoreIfNeeded() {
        if (currentState == State.CROSS_WON) {
            scoreX++;
        } else if (currentState == State.NOUGHT_WON) {
            scoreO++;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        board.paint(g);

        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.WHITE);
            statusBar.setText((currentPlayer == Seed.CROSS ? "X's Turn" : "O's Turn") +
                    " | Score: X = " + scoreX + ", O = " + scoreO);
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again. | Score: X = " + scoreX + ", O = " + scoreO);
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again. | Score: X = " + scoreX + ", O = " + scoreO);
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again. | Score: X = " + scoreX + ", O = " + scoreO);
        }
    }

    public static State getCurrentState() {
        return currentState;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setContentPane(new GameMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}