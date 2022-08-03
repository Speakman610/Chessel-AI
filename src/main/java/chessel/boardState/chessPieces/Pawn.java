package main.java.chessel.boardState.chessPieces;

import java.util.ArrayList;

import main.java.chessel.exceptions.InvalidMoveException;

public class Pawn extends Piece {

    public Pawn(char team, int x_pos, int y_pos) {
        super(team, "" + (char) (x_pos + 96), x_pos, y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();
        
        this.possibleMoves.addAll(PieceUtils.getPawnMoves(this));
    }

    @Override
    public void movePiece(int x_pos, int y_pos) throws InvalidMoveException {
        super.movePiece(x_pos, y_pos);
        setNotation("" + (char) (x_pos + 96));
    }
    
}
