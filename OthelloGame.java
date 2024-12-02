import java.io.PrintWriter;

public class OthelloGame {
    private static final int SIZE = 8; // Size of the board (8x8)
    private char[][] board;           // The game board
    private char currentPlayer;       // Current player ('B' or 'W')

    public OthelloGame() {
        board = new char[SIZE][SIZE]; // Initialize the board array
        initializeBoard();
        currentPlayer = 'B'; // Black starts first
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = '-'; // Fill the board with empty spots
            }
        }

        // Set the initial 4 pieces in the center
        board[SIZE / 2 - 1][SIZE / 2 - 1] = 'W';
        board[SIZE / 2][SIZE / 2] = 'W';
        board[SIZE / 2 - 1][SIZE / 2] = 'B';
        board[SIZE / 2][SIZE / 2 - 1] = 'B';
    }

    public char[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char player) {
        currentPlayer = player;
    }

    public boolean isGameOver() {
        return !hasValidMoves('B') && !hasValidMoves('W'); // Game over if neither player has valid moves
    }

    public char getWinner() {
        int blackCount = 0, whiteCount = 0;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 'B') blackCount++;
                if (board[i][j] == 'W') whiteCount++;
            }
        }

        if (blackCount > whiteCount) return 'B';
        if (whiteCount > blackCount) return 'W';
        return 'D'; // Draw
    }

    public boolean makeMove(int row, int col, char player) {
        if (!isValidMove(row, col, player)) {
            return false; // Invalid move
        }

        board[row][col] = player; // Place the disc
        flipDiscs(row, col, player); // Flip opponent's discs
        return true;
    }

    public boolean isValidMove(int row, int col, char player) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE || board[row][col] != '-') {
            return false; // Out of bounds or not empty
        }

        char opponent = (player == 'B') ? 'W' : 'B';

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue; // Skip self
                if (checkDirection(row, col, dr, dc, player, opponent)) {
                    return true; // Found a valid direction
                }
            }
        }

        return false; // No valid moves
    }

    private boolean checkDirection(int row, int col, int dr, int dc, char player, char opponent) {
        int x = row + dr, y = col + dc;
        boolean foundOpponent = false;

        while (x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == opponent) {
            x += dr;
            y += dc;
            foundOpponent = true;
        }

        if (foundOpponent && x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == player) {
            return true;
        }

        return false;
    }

    private void flipDiscs(int row, int col, char player) {
        char opponent = (player == 'B') ? 'W' : 'B';

        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue; // Skip self

                if (checkDirection(row, col, dr, dc, player, opponent)) {
                    flipDirection(row, col, dr, dc, player);
                }
            }
        }
    }

    private void flipDirection(int row, int col, int dr, int dc, char player) {
        int x = row + dr, y = col + dc;
        char opponent = (player == 'B') ? 'W' : 'B';

        while (x >= 0 && x < SIZE && y >= 0 && y < SIZE && board[x][y] == opponent) {
            board[x][y] = player; // Flip the disc
            x += dr;
            y += dc;
        }
    }

    public void printBoard(PrintWriter out) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                out.print(board[i][j] + " ");
            }
            out.println();
        }
        out.flush();
    }

    public boolean hasValidMoves(char player) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isValidMove(i, j, player)) {
                    return true;
                }
            }
        }
        return false; // No valid moves
    }

    public int[] getBestMove(char player) {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isValidMove(i, j, player)) {
                    return new int[] { i, j };
                }
            }
        }
        return null; // No valid moves
    }

    public void handleTurnPassing(PrintWriter out) {
        if (!hasValidMoves(currentPlayer)) {
            if (currentPlayer == 'B') {
                out.println("Black has no valid moves. Passing turn to White.");
                currentPlayer = 'W'; // Pass the turn
            } else if (currentPlayer == 'W') {
                out.println("White has no valid moves. Passing turn to Black.");
                currentPlayer = 'B'; // Pass the turn
            }
        }
    }
}
