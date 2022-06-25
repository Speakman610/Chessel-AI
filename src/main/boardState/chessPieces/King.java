package main.boardState.chessPieces;

import java.util.ArrayList;

public class King extends Piece {
    int range = 1;

    public King(char team) {
        super(team, "K");

        // The starting position of the King is either e1 or e8
        setX_pos(5);
        if (team == 'w') setY_pos(1); else setY_pos(8);
        setPossibleMoves();
    }

    @Override
    public void setCurrentlyAttacking() {
        this.currentlyAttacking = this.possibleMoves;
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();

        this.possibleMoves.addAll(PieceUtils.getDiagonalMoves(this, range));
        this.possibleMoves.addAll(PieceUtils.getAdjacentMoves(this, range));

        if (!hasMoved) {
            this.possibleMoves.add(PieceUtils.convertXYPosToNotation(3, 1));
            this.possibleMoves.add(PieceUtils.convertXYPosToNotation(7, 1));
        }
    }
    
}
