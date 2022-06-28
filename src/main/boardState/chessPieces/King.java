package main.boardState.chessPieces;

import java.util.ArrayList;

import main.boardState.BoardUtils;

public class King extends Piece {
    int range = 1;

    public King(char team, int x_pos, int y_pos) {
        super(team, "K");

        // The starting position of the King is either e1 or e8
        setX_pos(x_pos);
        setY_pos(y_pos);
    }

    @Override
    public void setCurrentlyAttacking() {
        this.currentlyAttacking = this.possibleMoves;
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();

        this.possibleMoves.addAll(BoardUtils.getDiagonalMoves(this, range));
        this.possibleMoves.addAll(BoardUtils.getAdjacentMoves(this, range));
    }
    
}
