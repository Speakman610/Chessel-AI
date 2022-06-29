package main.boardState.chessPieces;

import java.util.List;

import main.boardState.BoardUtils;
import main.exceptions.InvalidMoveException;

public abstract class Piece {
    protected char team;
    protected int x_pos;
    protected int y_pos;
    protected String notation;
    protected List<String> possibleMoves;
    protected boolean hasMoved; // has this piece moved this game

    Piece(char team, String notation, int x_pos, int y_pos) {
        this.team = team;
        this.notation = notation;
        this.hasMoved = false;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
    }
    
    public abstract void setPossibleMoves();

    public void movePiece(int x_pos, int y_pos) throws InvalidMoveException {
        // String inputMove = notation + BoardUtils.convertXYPosToNotation(x_pos, y_pos);
        // for (String possibleMove : getPossibleMoves()) {
        //     if(inputMove.equals(possibleMove)) {
        //         this.x_pos = x_pos;
        //         this.y_pos = y_pos;
        //         this.setPossibleMoves();
        //         hasMoved = true;
        //     }
        // }

        this.x_pos = x_pos;
        this.y_pos = y_pos;
        this.setPossibleMoves();
        hasMoved = true;

        // throw new InvalidMoveException("The move " + notation + inputMove + " is not a valid move.");
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public boolean canMoveRight() { // Right is horizontal in the positive direction
        return getX_pos() + 1 <= 8;
    }

    public boolean canMoveLeft() { // Left is horizontal in the negative direction
        return getX_pos() - 1 >= 1;
    }

    public boolean canMoveUp() { // Up is vertical in the positive direction
        return getY_pos() + 1 <= 8;
    }

    public boolean canMoveDown() { // Down is vertical in the negative direction
        return getY_pos() - 1 >= 1;
    }

    public void printLocation() {
        System.out.println(BoardUtils.convertXYPosToNotation(this.x_pos, this.y_pos));
    }

    public char getTeam() {
        return this.team;
    }

    protected void setTeam(char team) {
        this.team = team;
    }

    public int getX_pos() {
        return this.x_pos;
    }

    protected void setX_pos(int x_pos) {
        this.x_pos = x_pos;
    }

    public int getY_pos() {
        return this.y_pos;
    }

    protected void setY_pos(int y_pos) {
        this.y_pos = y_pos;
    }

    public String getNotation() {
        return this.notation;
    }

    protected void setNotation(String notation) {
        this.notation = notation;
    }

    public List<String> getPossibleMoves() {
        return this.possibleMoves;
    }

}
