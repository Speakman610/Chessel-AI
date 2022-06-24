package main;
import java.util.List;

import main.boardState.chessPieces.King;

public class Chessel {
    public static void main(String[] args) throws Exception {
        System.out.println("Welcome to Chessel!");
        King king = new King('b');
        // king.printLocation();
        List<String> possibleMoves = king.getPossibleMoves();

        for (String move : possibleMoves) {
            System.out.println(move);
        }
    }
}
