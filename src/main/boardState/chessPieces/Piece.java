package main.boardState.chessPieces;

import java.io.EOFException;
import java.util.List;

import main.exceptions.InvalidMoveException;

public abstract class Piece {
    protected char team;
    protected int x_pos;
    protected int y_pos;
    protected String notation;
    protected List<String> currentlyAttacking;
    protected List<String> possibleMoves;
    protected boolean hasMoved; // has this piece moved this game

    Piece(char team, String notation) {
        this.team = team;
        this.notation = notation;
        this.hasMoved = false;
    }
    
    public abstract void setCurrentlyAttacking();
    public abstract void setPossibleMoves();

    protected boolean movePiece(int x_pos, int y_pos) throws InvalidMoveException {
        String inputMove = PieceUtils.convertXYPosToNotation(x_pos, y_pos);
        for (String move : getPossibleMoves()) {
            if(inputMove.equals(move)) {
                this.x_pos = x_pos;
                this.y_pos = y_pos;
                hasMoved = true;
                return true;
            }
        }

        throw new InvalidMoveException("Invalid Move: The move " + inputMove + " is not a valid move.");
    }

    protected boolean canMoveRight() { // Right is horizontal in the positive direction
        return getX_pos() + 1 <= 8;
    }

    protected boolean canMoveLeft() { // Left is horizontal in the negative direction
        return getX_pos() - 1 >= 1;
    }

    protected boolean canMoveUp() { // Up is vertical in the positive direction
        return getY_pos() + 1 <= 8;
    }

    protected boolean canMoveDown() { // Down is vertical in the negative direction
        return getY_pos() - 1 >= 1;
    }

    public void printLocation() {
        System.out.println(PieceUtils.convertXYPosToNotation(this.x_pos, this.y_pos));
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

    public List<String> getCurrentlyAttacking() {
        return this.currentlyAttacking;
    }

}
