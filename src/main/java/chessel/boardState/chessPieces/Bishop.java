package main.java.chessel.boardState.chessPieces;

import java.util.ArrayList;

public class Bishop extends Piece {
    int range = 8;

    public Bishop(char team, int x_pos, int y_pos) {
        super(team, "B", x_pos, y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();

        this.possibleMoves.addAll(PieceUtils.getDiagonalMoves(this, range));
    }
}
