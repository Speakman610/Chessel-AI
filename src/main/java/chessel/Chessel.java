package main.java.chessel;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.chessel.boardState.BoardState;
import main.java.chessel.boardState.BoardState_Interface;

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
    private static List<String> moveList = null;

    private enum PlayerType {
        PLAYER,
        CHESSEL
    }

    // TODO: Could sorted sets work to create LTM? With a sortable move class?
    
    public static void main(String[] args) throws IOException {
        Scanner scan = new Scanner(System.in);
        int choice = -1;

        while (choice != 5) {
            printMenu();
            choice = scan.nextInt();

            switch (choice) {
                case 1:
                    // Player vs. Chessel
                    int team = -1;
                    while(team != 1 && team != 2 && team != 3) {
                        clearTerminal();
                        System.out.print(ANSI_ORANGE + "\nWhich team will the player play as?\n" + ANSI_RESET +
                            "1) " + ANSI_BLUE + "White\n" + ANSI_RESET + 
                            "2) " + ANSI_RED + "Black\n" + ANSI_RESET +
                            "3) Exit\n" +
                            ":");
                        team = scan.nextInt();
                    }
                    if (team == 1) {
                        playGame(PlayerType.PLAYER, PlayerType.CHESSEL);
                    } else if (team == 2) {
                        playGame(PlayerType.CHESSEL, PlayerType.PLAYER);
                    } else {
                        break;
                    }
                    
                    waitToContinue();
                    board.resetBoard();
                    break;
                case 2:
                    // Player vs. Player
                    playGame(PlayerType.PLAYER, PlayerType.PLAYER);
                    waitToContinue();
                    board.resetBoard();
                    break;
                case 3:
                    // Chessel vs. Chessel
                    playGame(PlayerType.CHESSEL, PlayerType.CHESSEL);
                    waitToContinue();
                    board.resetBoard();
                    break;
                case 4:
                    // Train Chessel
                    System.out.println("\nThis option is not yet supported.");
                    waitToContinue();
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
        clearTerminal();
        System.out.print(ANSI_ORANGE + "\nChessel Menu - Enter one of the following:\n" + ANSI_RESET +
                "1) Player vs. Chessel\n" +
                "2) Player vs. Player\n" +
                "3) Chessel vs. Chessel\n" +
                ANSI_GREY + "4) Train Chessel\n" + ANSI_RESET +
                "5) Exit\n" +
                ":");
    }

    private static void playGame(PlayerType white, PlayerType black) {
        board = BoardState.getBoardState();
        moveList = new ArrayList<>();
        
        while (!board.gameEnded()) {
            clearTerminal();
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
        clearTerminal();
        board.printCurrentBoard();
    }

    private static void playerMove() {
        boolean invalidMove = true;
        do {
            String inputMove = System.console().readLine();
            if (board.makeMove(inputMove)) {
                invalidMove = false;
                moveList.add(inputMove);
            } else {
                System.out.print("(Invalid move, please try again)\n:");
            }
        } while (invalidMove);
    }
    
    private static void chesselMove() {
        List<String> possibleMoves = board.getPossibleMoves();
        SecureRandom random = new SecureRandom();
        int chosenMove = random.nextInt(possibleMoves.size());
        System.out.print(possibleMoves.get(chosenMove) + "\n");
        board.makeMove(possibleMoves.get(chosenMove));
        moveList.add(possibleMoves.get(chosenMove));
    }

    private static void waitToContinue() throws IOException {
        System.out.print("Press enter to continue: ");
        System.in.read();
    }

    private static void clearTerminal() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }
}
