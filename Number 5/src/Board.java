import java.awt.*;
import javax.swing.*;

class Board extends JPanel {
    public static final int ROWS = 3;
    public static final int COLS = 3;
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS;
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS;
    public static final int GRID_WIDTH = 8;
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final Color COLOR_GRID = Color.LIGHT_GRAY;

    Cell[][] cells;

    private int winStartRow = -1, winStartCol = -1;
    private int winEndRow = -1, winEndCol = -1;

    public Board() {
        setPreferredSize(new Dimension(Cell.SIZE * COLS, Cell.SIZE * ROWS));
        initGame();
    }

    public void initGame() {
        cells = new Cell[ROWS][COLS];
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col] = new Cell(row, col);
            }
        }
    }

    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].newGame();
            }
        }
        winStartRow = winStartCol = winEndRow = winEndCol = -1;
    }

    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        cells[selectedRow][selectedCol].content = player;

        for (int row = 0; row < ROWS; row++) {
            if (cells[row][0].content == player && cells[row][1].content == player && cells[row][2].content == player) {
                winStartRow = row; winStartCol = 0;
                winEndRow = row; winEndCol = 2;
                return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
            }
        }


        // Check columns for win
        for (int col = 0; col < COLS; col++) {
            if (cells[0][col].content == player && cells[1][col].content == player && cells[2][col].content == player) {
                winStartRow = 0; winStartCol = col;
                winEndRow = 2; winEndCol = col;
                return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
            }
        }

        // check diagonal
        if (cells[0][0].content == player && cells[1][1].content == player && cells[2][2].content == player) {
            winStartRow = 0; winStartCol = 0;
            winEndRow = 2; winEndCol = 2;
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        }

        if (cells[0][2].content == player && cells[1][1].content == player && cells[2][0].content == player) {
            winStartRow = 0;
            winStartCol = 2;
            winEndRow = 2;
            winEndCol = 0;
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        }

        // check draw
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    return State.PLAYING;
                }
            }
        }
        return State.DRAW;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw grid
        g.setColor(COLOR_GRID);
        for (int row = 1; row < ROWS; ++row) {
            g.fillRoundRect(0, Cell.SIZE * row - GRID_WIDTH_HALF,
                    Cell.SIZE * COLS - 1, GRID_WIDTH,
                    GRID_WIDTH, GRID_WIDTH);
        }
        for (int col = 1; col < COLS; ++col) {
            g.fillRoundRect(Cell.SIZE * col - GRID_WIDTH_HALF, 0,
                    GRID_WIDTH, Cell.SIZE * ROWS - 1,
                    GRID_WIDTH, GRID_WIDTH);
        }

        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].paint(g);
            }
        }

        if ((GameMain.getCurrentState() == State.CROSS_WON || GameMain.getCurrentState() == State.NOUGHT_WON)
                && winStartRow != -1) {
            g.setColor(Color.RED);
            int x1 = winStartCol * Cell.SIZE + Cell.SIZE / 2;
            int y1 = winStartRow * Cell.SIZE + Cell.SIZE / 2;
            int x2 = winEndCol * Cell.SIZE + Cell.SIZE / 2;
            int y2 = winEndRow * Cell.SIZE + Cell.SIZE / 2;
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(5));
            g2.drawLine(x1, y1, x2, y2);
        }
    }
}