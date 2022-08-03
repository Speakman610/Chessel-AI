package main.java.chessel.boardState.chessPieces;

import java.util.ArrayList;

public class Queen extends Piece {
    int range = 8;

    public Queen(char team, int x_pos, int y_pos) {
        super(team, "Q", x_pos, y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();

        this.possibleMoves.addAll(PieceUtils.getDiagonalMoves(this, range));
        this.possibleMoves.addAll(PieceUtils.getAdjacentMoves(this, range));
    }
    
}
