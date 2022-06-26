package main.boardState.chessPieces;

import java.util.ArrayList;

import main.boardState.BoardUtils;

public class King extends Piece {
    int range = 1;

    public King(char team) {
        super(team, "K");

        // The starting position of the King is either e1 or e8
        setX_pos(5);
        if (team == 'w') setY_pos(1); else setY_pos(8);
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
