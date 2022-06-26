package main;
import java.util.List;

import main.boardState.BoardState;

public class Chessel {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to Chessel!");
        BoardState board = BoardState.getBoardState();
        board.setPossibleMoves();
        List<String> possibleMoves = board.getPossibleMoves('w');

        for (String move : possibleMoves) {
            System.out.println(move);
        }

        board.makeMove("a1");

        possibleMoves = board.getPossibleMoves('w');

        for (String move : possibleMoves) {
            System.out.println(move);
        }
    }
}
