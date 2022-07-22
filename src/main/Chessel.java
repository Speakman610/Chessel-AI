package main;

import java.security.SecureRandom;
import java.util.List;

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
    
    public static void main(String[] args) throws Exception {
        BoardState_Interface board = BoardState.getBoardState();

        // List<String> possibleMoves = board.getPossibleMoves();
        // while (!board.gameEnded()) {
        //     SecureRandom random = new SecureRandom();
        //     board.printCurrentBoard();
        //     int chosenMove = random.nextInt(possibleMoves.size());
        //     board.makeMove(possibleMoves.get(chosenMove));
        //     possibleMoves = board.getPossibleMoves();
        // }

        // board.printCurrentBoard();
        
        List<String> possibleMoves = board.getPossibleMoves();
        while (!board.gameEnded()) {
            // String turn = "White";
            board.printCurrentBoard();
            // if (board.getTurn() == 'b') turn = "Black";
            // System.out.println(turn + " to move: ");
            if (board.getTurn() == 'w') {
                boolean invalidMove = true;
                do {
                    System.out.println("Your move: ");
                    String inputMove = System.console().readLine();
                    if (board.makeMove(inputMove)) {
                        invalidMove = false;
                    }
                } while (invalidMove);
            } else {
                possibleMoves = board.getPossibleMoves();
                SecureRandom random = new SecureRandom();
                // board.printCurrentBoard();
                int chosenMove = random.nextInt(possibleMoves.size());
                System.out.println("Chessel's move: \n" + possibleMoves.get(chosenMove) + "\n");
                board.makeMove(possibleMoves.get(chosenMove));
            }
        }
        board.printCurrentBoard();
    }
}
