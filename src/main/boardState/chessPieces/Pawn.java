package main.boardState.chessPieces;

import java.util.ArrayList;

import main.boardState.BoardUtils;

public class Pawn extends Piece {

    public Pawn(char team, int x_pos, int y_pos) {
        super(team, "", x_pos, y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();
        
        this.possibleMoves.addAll(BoardUtils.getPawnMoves(this));
    }
    
}
