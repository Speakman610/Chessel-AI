package main;

import java.util.List;

import main.boardState.BoardState;

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
        BoardState board = BoardState.getBoardState();
        board.setPossibleMoves();
        
        for (int i = 0; i < 100; i++) {
            String turn = "White";
            board.printCurrentBoard();
            if (board.getTurn() == 'b') turn = "Black";
            System.out.println(turn + " to move: ");
            boolean invalidMove = true;
            do {
                String inputMove = System.console().readLine();
                try {
                    board.makeMove(inputMove);
                    invalidMove = false;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("Please try again: ");
                }
            } while (invalidMove);
            System.out.println("\nPOSSIBLE MOVES:");
            List<String> possibleMoves = board.getPossibleMoves();
            for (String move : possibleMoves) {
                System.out.println(move);
            }
        }
    }
}
