import java.io.*;
import java.net.*;

public class OthelloServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9999, 50, InetAddress.getByName("192.168.68.67"))) {
            System.out.println("Server is running on IP 192.168.68.67 and port 9999...");
            OthelloGame game = new OthelloGame();

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                    // Log client connection details
                    String clientIP = clientSocket.getInetAddress().getHostAddress();
                    System.out.println("Client connected from IP: " + clientIP);
                    System.out.println("Client port: " + clientSocket.getPort());

                    game.setCurrentPlayer('B'); // Start with Black

                    while (!game.isGameOver()) {
                        // Handle turn passing if the current player has no valid moves
                        game.handleTurnPassing(out); // This will pass the turn if needed

                        game.printBoard(out);
                        char currentPlayer = game.getCurrentPlayer();

                        if (currentPlayer == 'B') {
                            out.println("Your turn (enter row and column):");
                            String input = in.readLine();

                            if (input == null)
                                break; // Client disconnected

                            String[] parts = input.split(" ");
                            if (parts.length != 2) {
                                out.println("Invalid input. Enter row and column separated by a space.");
                                continue;
                            }

                            try {
                                int row = Integer.parseInt(parts[0]);
                                int col = Integer.parseInt(parts[1]);

                                if (!game.makeMove(row, col, 'B')) {
                                    out.println("Invalid move.try again.");
                                } else {
                                    game.setCurrentPlayer('W'); // Switch to White
                                }
                            } catch (NumberFormatException e) {
                                out.println("Invalid input. Enter numeric values for row and column.");
                            }
                        } else {
                            // Server (AI) makes a move
                            int[] serverMove = game.getBestMove('W');
                            if (serverMove != null) {
                                game.makeMove(serverMove[0], serverMove[1], 'W');
                                out.println("Server (White) played at: " + serverMove[0] + " " + serverMove[1]);
                            } else {
                                out.println("White has no valid moves.");
                            }
                            game.setCurrentPlayer('B'); // Switch to Black
                        }
                    }

                    game.printBoard(out);
                    char winner = game.getWinner();
                    out.println(winner == 'D' ? "Game over! It's a draw."
                            : "Game over! The winner is " + (winner == 'B' ? "Black" : "White") + ".");
                } catch (SocketException e) {
                    System.out.println("Client disconnected.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
