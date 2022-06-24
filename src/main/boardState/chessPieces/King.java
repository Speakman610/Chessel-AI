package main.boardState.chessPieces;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {

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

        if (canMoveRight()) {
            this.possibleMoves.add(convertXYPosToNotation(getX_pos() + 1, getY_pos()));

            if (canMoveUp()) {
                this.possibleMoves.add(convertXYPosToNotation(getX_pos() + 1, getY_pos() + 1));
            }
    
            if (canMoveDown()) {
                this.possibleMoves.add(convertXYPosToNotation(getX_pos() + 1, getY_pos() - 1));
            }
        }
        
        if (canMoveLeft()) {
            this.possibleMoves.add(convertXYPosToNotation(getX_pos() - 1, getY_pos()));

            if (canMoveUp()) {
                this.possibleMoves.add(convertXYPosToNotation(getX_pos() - 1, getY_pos() + 1));
            }
    
            if (canMoveDown()) {
                this.possibleMoves.add(convertXYPosToNotation(getX_pos() - 1, getY_pos() - 1));
            }
        }

        if (canMoveUp()) {
            this.possibleMoves.add(convertXYPosToNotation(getX_pos(), getY_pos() + 1));
        }

        if (canMoveDown()) {
            this.possibleMoves.add(convertXYPosToNotation(getX_pos(), getY_pos() - 1));
        }
    }
    
}
