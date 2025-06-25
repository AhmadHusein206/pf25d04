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

    private static final int AI_DELAY_MS = 1000; // 1 detik delay
    private Timer aiTimer;
    private boolean isAIThinking = false;

    private int scoreX = 0;
    private int scoreO = 0;
    private int drawScore = 0;
    private String playerXName = "Player X";
    private String playerOName = "Player O";
    private boolean twoPlayers = false;

    private int maxRounds = 1;
    private int roundsPlayed = 0;
    private boolean gameOver = false;

    public GameMain() {
        showPlayerSelectionDialog();

        super.setLayout(new BorderLayout());

        // Inisialisasi timer untuk AI
        aiTimer = new Timer(AI_DELAY_MS, e -> {
            if (isAIThinking) {
                makeAIMove();
                isAIThinking = false;
                repaint();
            }
        });
        aiTimer.setRepeats(false); // Hanya eksekusi sekali

        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentState == State.PLAYING && !isAIThinking) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int row = mouseY / Cell.SIZE;
                    int col = mouseX / Cell.SIZE;

                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {

                        currentState = board.stepGame(currentPlayer, row, col);

                        if (currentState != State.PLAYING && currentState != State.DRAW) {
                            roundsPlayed++;
                        }

                        updateScoreIfNeeded();
                        repaint();

                        if (currentState != State.PLAYING) {
                            if (roundsPlayed >= maxRounds) {
                                gameOver = true;
                                showFinalResult();
                            } else {
                                showNextRoundDialog(getRoundResultMessage());
                            }
                        } else {
                            currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                            if (vsComputer && currentPlayer == Seed.NOUGHT) {
                                isAIThinking = true;
                                aiTimer.start();
                            }
                        }
                    }
                }
            }
        });

        restartButton = new JButton("Restart");
        restartButton.addActionListener(e -> {
            showPlayerSelectionDialog();
            initGame();
            newGame();
            repaint();
        });

        difficultySelect = new JComboBox<>(new String[]{"Easy"});
        difficultySelect.addActionListener(e -> aiDifficulty = (String) difficultySelect.getSelectedItem());

        setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT));
        setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    private void showNextRoundDialog(String message) {
        int response = JOptionPane.showOptionDialog(this,
                message + "\nKlik NEXT untuk menuju ronde berikutnya.",
                "Ronde Selesai",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"NEXT"},
                "NEXT");
        if (response == 0) {
            newGame();
            repaint();
        }
    }

    private String getRoundResultMessage() {
        if (currentState == State.CROSS_WON) {
            return playerXName + " menang ronde ini!";
        } else if (currentState == State.NOUGHT_WON) {
            return playerOName + " menang ronde ini!";
        } else {
            return "Ronde ini berakhir tanpa pemenang.";
        }
    }

    private void showFinalResult() {
        String resultMessage;
        if (scoreX > scoreO) {
            resultMessage = playerXName + " menang pertandingan!";
        } else if (scoreO > scoreX) {
            resultMessage = playerOName + " menang pertandingan!";
        } else {
            int response = JOptionPane.showConfirmDialog(this,
                    "Skor akhir imbang. Apakah ingin menambah 1 ronde penentu?",
                    "Ronde Tambahan",
                    JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                maxRounds++;
                gameOver = false;
                newGame();
                repaint();
                return;
            } else {
                resultMessage = "Pertandingan berakhir seri!";
            }
        }

        JOptionPane.showMessageDialog(this,
                "Permainan selesai!\n\nSkor akhir:\n" +
                        playerXName + ": " + scoreX + "\n" +
                        playerOName + ": " + scoreO + "\n\n" + resultMessage,
                "Hasil Akhir",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public JPanel getControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(new JLabel("AI Difficulty:"));
        panel.add(difficultySelect);
        panel.add(restartButton);
        return panel;
    }

    public void setStatusBar(JLabel statusBar) {
        this.statusBar = statusBar;
    }

    private void showPlayerSelectionDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));

        JRadioButton singlePlayerBtn = new JRadioButton("Single Player (vs Computer)", true);
        JRadioButton twoPlayerBtn = new JRadioButton("Two Players");
        ButtonGroup group = new ButtonGroup();
        group.add(singlePlayerBtn);
        group.add(twoPlayerBtn);

        JLabel player1Label = new JLabel("Player X Name:");
        JTextField player1Field = new JTextField("Player 1");

        JLabel player2Label = new JLabel("Player O Name:");
        JTextField player2Field = new JTextField("Computer");
        player2Field.setEnabled(false);

        singlePlayerBtn.addItemListener(e -> {
            vsComputer = true;
            twoPlayers = false;
            player2Field.setEnabled(false);
            player2Field.setText("Computer");
        });

        twoPlayerBtn.addItemListener(e -> {
            vsComputer = false;
            twoPlayers = true;
            player2Field.setEnabled(true);
            player2Field.setText("Player 2");
        });


        panel.add(singlePlayerBtn);
        panel.add(twoPlayerBtn);
        panel.add(player1Label);
        panel.add(player1Field);
        panel.add(player2Label);
        panel.add(player2Field);

        int result = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Player Setup",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            playerXName = player1Field.getText().trim();
            playerOName = player2Field.getText().trim();

            if (playerXName.isEmpty()) playerXName = "Player X";
            if (playerOName.isEmpty()) playerOName = twoPlayers ? "Player O" : "Computer";
        }

    String inputRounds = JOptionPane.showInputDialog(
            null,
            "Berapa poin maksimal dalam game?",
            "Poin maksimal",
            JOptionPane.QUESTION_MESSAGE
    );

        try {
        maxRounds = Integer.parseInt(inputRounds);
        if (maxRounds <= 0) maxRounds = 1;
    } catch (Exception e) {
        maxRounds = 1;
    }
}

    public void initGame() {
        board = new Board();
        roundsPlayed = 0;
        scoreX = 0;
        scoreO = 0;
        drawScore = 0;
        gameOver = false;
    }

    public void newGame() {
        board.newGame();
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
        isAIThinking = false; // Reset status AI
        aiTimer.stop(); // Hentikan timer jika ada
    }

    private void makeAIMove() {
        int[] move = getRandomMove();
        currentState = board.stepGame(Seed.NOUGHT, move[0], move[1]);

        if (currentState != State.PLAYING && currentState != State.DRAW) {
            roundsPlayed++;
        }

        updateScoreIfNeeded();

        if (currentState == State.PLAYING) {
            currentPlayer = Seed.CROSS;
        } else {
            if (roundsPlayed >= maxRounds) {
                gameOver = true;
                showFinalResult();
            } else {
                showNextRoundDialog(getRoundResultMessage());
            }
        }
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
        board.paintComponent(g);
    }



    public static State getCurrentState() {
        return currentState;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            GameMain gamePanel = new GameMain();

            JLabel statusBar = new JLabel("Have Fun!");
            statusBar.setFont(FONT_STATUS);
            statusBar.setBackground(COLOR_BG_STATUS);
            statusBar.setOpaque(true);
            statusBar.setPreferredSize(new Dimension(300, 30));
            statusBar.setHorizontalAlignment(JLabel.LEFT);
            statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

            gamePanel.setStatusBar(statusBar);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.add(gamePanel.getControlPanel(), BorderLayout.EAST);
            bottomPanel.add(statusBar, BorderLayout.CENTER);

            frame.add(gamePanel, BorderLayout.CENTER);
            frame.add(bottomPanel, BorderLayout.SOUTH);

            frame.setSize(Board.CANVAS_WIDTH + 13, Board.CANVAS_HEIGHT + 70);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}