package main.boardState.chessPieces;

import java.util.ArrayList;

public class Rook extends Piece {
    int range = 8;

    public Rook(char team, int x_pos, int y_pos) {
        super(team, "R", x_pos, y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();

        this.possibleMoves.addAll(PieceUtils.getAdjacentMoves(this, range));
    }
    
}
