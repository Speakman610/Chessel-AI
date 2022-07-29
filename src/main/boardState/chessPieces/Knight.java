package main.boardState.chessPieces;

import java.util.ArrayList;

public class Knight extends Piece {

    public Knight(char team, int x_pos, int y_pos) {
        super(team, "N", x_pos, y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();
        
        this.possibleMoves.addAll(PieceUtils.getKnightMoves(this));
    }
    
}
