package main.boardState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.boardState.chessPieces.Bishop;
import main.boardState.chessPieces.King;
import main.boardState.chessPieces.Piece;
import main.boardState.chessPieces.Queen;
import main.boardState.chessPieces.Rook;

public class BoardState implements BoardState_Interface {
    private static BoardState boardState = null; // instance of the board state to create a singleton class
    private Map<String, Piece> board;
    private char turn; // the current turn in the game

    private BoardState() {
        // create the board and add all of the pieces
        board = new HashMap<>();
        
        // WHITE TEAM
        board.put("a1", new Rook('w', 1, 1));
        // Nb1
        board.put("c1", new Bishop('w', 3, 1));
        board.put("d1", new Queen('w', 4, 1));
        board.put("e1", new King('w', 5, 1));
        board.put("f1", new Bishop('w', 6, 1));
        // Ng1
        board.put("h1", new Rook('w', 8, 1));

        // BLACK TEAM
        board.put("a8", new Rook('b', 1, 8));
        // Nb8
        board.put("c8", new Bishop('b', 3, 8));
        board.put("d8", new Queen('b', 4, 8));
        board.put("e8", new King('b', 5, 8));
        board.put("f8", new Bishop('b', 6, 8));
        // Ng8
        board.put("h8", new Rook('b', 8, 8));

        turn = 'w';
    }

    public static BoardState getBoardState() {
        if (boardState == null) {
            boardState = new BoardState();
        }

        return boardState;
    }

    @Override
    public List<String> getPossibleMoves(char team) {
        List<String> possibleMoves = new ArrayList<>();

        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            if (entry.getValue().getTeam() == team) {
                possibleMoves.addAll(entry.getValue().getPossibleMoves());
            }
        }

        return possibleMoves;
    }

    public void setPossibleMoves() {
        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            entry.getValue().setPossibleMoves();
        }
    }

    @Override
    public boolean makeMove(String notation) {
        if (turn == 'w') {
            setTurn('b');
        } else {
            setTurn('w');
        }
        
        return false;
    }

    @Override
    public void printCurrentBoard() {
        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().getNotation() + entry.getValue().getTeam());
        }
    }


    public Map<String,Piece> getBoard() {
        return this.board;
    }

    public char getTurn() {
        return this.turn;
    }

    private void setTurn(char turn) {
        this.turn = turn;
    }

    
}
