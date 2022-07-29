package main;

import java.security.SecureRandom;
import java.util.List;
import java.util.Scanner;

import main.boardState.BoardState;
import main.boardState.BoardState_Interface;

public class Chessel {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREY = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_CYAN = "\u001B[32m";
    public static final String ANSI_ORANGE = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PINK = "\u001B[35m";
    public static final String ANSI_PURPLE = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    private static BoardState_Interface board;

    private enum PlayerType {
        PLAYER,
        CHESSEL
    }
    
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int choice = -1;

        while (choice != 5) {
            printMenu();
            choice = scan.nextInt();

            switch (choice) {
                case 1:
                    // Player vs. Chessel
                    playGame(PlayerType.PLAYER, PlayerType.CHESSEL);
                    board.resetBoard();
                    break;
                case 2:
                    // Player vs. Player
                    playGame(PlayerType.PLAYER, PlayerType.PLAYER);
                    board.resetBoard();
                    break;
                case 3:
                    // Chessel vs. Chessel
                    playGame(PlayerType.CHESSEL, PlayerType.CHESSEL);
                    board.resetBoard();
                    break;
                case 4:
                    // Train Chessel
                    System.out.println("\nThis option is not yet supported.");
                    break;
                case 5:
                    // Exit
                    break;
                default:
                    break;
            }
        }

        scan.close();
    }

    private static void printMenu() {
        System.out.print(ANSI_ORANGE + "\nChessel Menu : Enter one of the following...\n" + ANSI_RESET +
                ANSI_CYAN + "1) Player vs. Chessel\n" + ANSI_RESET +
                ANSI_CYAN + "2) Player vs. Player\n" + ANSI_RESET +
                ANSI_CYAN + "3) Chessel vs. Chessel\n" + ANSI_RESET +
                ANSI_GREY + "4) Train Chessel\n" + ANSI_RESET +
                ANSI_CYAN + "5) Exit\n\n" + ANSI_RESET +
                ":");
    }

    private static void playGame(PlayerType white, PlayerType black) {
        board = BoardState.getBoardState();
        
        while (!board.gameEnded()) {
            board.printCurrentBoard();

            if (board.getTurn() == 'w') {
                System.out.print("White to move: ");
                if (white == PlayerType.PLAYER) {
                    playerMove();
                } else {
                    chesselMove();
                }
                
            } else {
                System.out.print("Black to move: ");
                if (black == PlayerType.PLAYER) {
                    playerMove();
                } else {
                    chesselMove();
                }
            }
            System.out.println("\n");
        }
        board.printCurrentBoard();
    }

    private static void playerMove() {
        boolean invalidMove = true;
        do {
            String inputMove = System.console().readLine();
            if (board.makeMove(inputMove)) {
                invalidMove = false;
            } else {
                System.out.print(" (Invalid move, please try again\n:");
            }
        } while (invalidMove);
    }
    
    private static void chesselMove() {
        List<String> possibleMoves = board.getPossibleMoves();
        SecureRandom random = new SecureRandom();
        int chosenMove = random.nextInt(possibleMoves.size());
        System.out.print(possibleMoves.get(chosenMove) + "\n");
        board.makeMove(possibleMoves.get(chosenMove));
    }
}
