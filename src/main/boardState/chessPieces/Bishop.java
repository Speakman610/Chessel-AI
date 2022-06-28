package main.boardState.chessPieces;

import java.util.ArrayList;

import main.boardState.BoardUtils;

public class Bishop extends Piece {
    int range = 8;

    public Bishop(char team, int x_pos, int y_pos) {
        super(team, "B");

        // The starting position of the Queen is either d1 or d8
        setX_pos(x_pos);
        setY_pos(y_pos);
    }

    @Override
    public void setPossibleMoves() {
        this.possibleMoves = new ArrayList<>();

        this.possibleMoves.addAll(BoardUtils.getDiagonalMoves(this, range));
    }
}
