package main.boardState.chessPieces;

import java.util.ArrayList;

import main.boardState.BoardUtils;

public class Rook extends Piece {
    int range = 8;

    public Rook(char team, int x_pos, int y_pos) {
        super(team, "R");
        
        setX_pos(x_pos);
        setY_pos(y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();

        this.possibleMoves.addAll(BoardUtils.getAdjacentMoves(this, range));
    }
    
}
