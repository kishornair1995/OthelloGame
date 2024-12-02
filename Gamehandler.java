import java.io.*;
import java.net.*;

public class Gamehandler {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9999); // Start the server
        System.out.println("Othello Server is running...");

        try {
            Socket clientSocket = serverSocket.accept(); // Accept client connection
            System.out.println("Client connected from IP: " + clientSocket.getInetAddress().getHostAddress());

            // Set up communication streams
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Initialize the game
            OthelloGame game = new OthelloGame();
            game.printBoard(out); // Print the initial board to the client

            // Game loop
            while (!game.isGameOver()) {
                // Player's turn (Black)
                if (game.getCurrentPlayer() == 'B') {
                    out.println("Black's turn. Enter your move (row column):");
                    String input = in.readLine(); // Read player's input
                    if (input == null || input.isEmpty()) {
                        out.println("Invalid input. Try again.");
                        continue;
                    }

                    String[] tokens = input.split(" ");
                    if (tokens.length != 2) {
                        out.println("Invalid input format. Enter row and column.");
                        continue;
                    }

                    try {
                        int row = Integer.parseInt(tokens[0]);
                        int col = Integer.parseInt(tokens[1]);

                        if (game.makeMove(row, col, 'B')) {
                            game.printBoard(out); // Print the updated board
                        } else {
                            out.println("Invalid move. Try again.");
                            continue; // Retry Black's turn
                        }
                    } catch (NumberFormatException e) {
                        out.println("Invalid input. Row and column must be integers.");
                        continue; // Retry Black's turn
                    }
                }

                // Server's turn (White)
                if (!game.isGameOver() && game.getCurrentPlayer() == 'W') {
                    System.out.println("Server's turn as White");
                    int[] bestMove = game.getBestMove('W');
                    if (bestMove != null) {
                        game.makeMove(bestMove[0], bestMove[1], 'W');
                        out.println("White (Server) played at: " + bestMove[0] + " " + bestMove[1]);
                        game.printBoard(out);
                    } else {
                        out.println("White has no valid moves and passes its turn.");
                        game.setCurrentPlayer('B'); // Pass turn back to Black
                    }
                }
            }

            // Game over
            char winner = game.getWinner();
            out.println("Game Over! Winner: " + (winner == 'D' ? "Draw" : (winner == 'B' ? "Black" : "White")));
            clientSocket.close();
        } finally {
            serverSocket.close();
            System.out.println("Server socket closed.");
        }
    }
}
