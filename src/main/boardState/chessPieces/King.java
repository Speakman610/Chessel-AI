package main.boardState.chessPieces;

import java.util.ArrayList;

import main.boardState.BoardUtils;

public class King extends Piece {
    int range = 1;

    public King(char team, int x_pos, int y_pos) {
        super(team, "K", x_pos, y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();

        this.possibleMoves.addAll(BoardUtils.getDiagonalMoves(this, range));
        this.possibleMoves.addAll(BoardUtils.getAdjacentMoves(this, range));
    }
    
}
