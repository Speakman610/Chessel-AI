package main;
import java.util.List;

import main.boardState.BoardState;

public class Chessel {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to Chessel!");
        BoardState board = BoardState.getBoardState();
        board.setPossibleMoves();
        String notationStartsWith = "";
        List<String> possibleMoves = board.getPossibleMoves('w');

        System.out.println("\nMOVES FOR WHITE: ");
        for (String move : possibleMoves) {
            if (move.startsWith(notationStartsWith)) {
                System.out.println(move);
            }
        }

        possibleMoves = board.getPossibleMoves('b');

        System.out.println("\nMOVES FOR BLACK: ");
        for (String move : possibleMoves) {
            if (move.startsWith(notationStartsWith)) {
                System.out.println(move);
            }
        }

        // System.out.println("\nCURRENT BOARD: ");
        // board.printCurrentBoard();
    }
}
