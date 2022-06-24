package main.boardState.chessPieces;

import java.util.List;

public abstract class Piece {
    char team;
    int x_pos;
    int y_pos;
    String notation;
    List<String> currentlyAttacking;
    List<String> possibleMoves;

    Piece(char team, String notation) {
        this.team = team;
        this.notation = notation;
    }
    
    public abstract void setCurrentlyAttacking();
    public abstract void setPossibleMoves();

    protected boolean movePiece(int x_pos, int y_pos) {

        return false;
    }

    public String convertXYPosToNotation(int x_pos, int y_pos) {
        char xChar = (char) (x_pos + 96);
        char yChar = (char) (y_pos + 48);
        
        return "" + xChar + yChar;
    }

    protected boolean canMoveRight() {
        return getX_pos() + 1 <= 8;
    }

    protected boolean canMoveLeft() { 
        return getX_pos() - 1 >= 1;
    }

    protected boolean canMoveUp() {
        return getY_pos() + 1 <= 8;
    }

    protected boolean canMoveDown() {
        return getY_pos() - 1 >= 1;
    }

    public void printLocation() {
        System.out.println(convertXYPosToNotation(this.x_pos, this.y_pos));
    }

    public char getTeam() {
        return this.team;
    }

    public void setTeam(char team) {
        this.team = team;
    }

    public int getX_pos() {
        return this.x_pos;
    }

    public void setX_pos(int x_pos) {
        this.x_pos = x_pos;
    }

    public int getY_pos() {
        return this.y_pos;
    }

    public void setY_pos(int y_pos) {
        this.y_pos = y_pos;
    }

    public String getNotation() {
        return this.notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public List<String> getPossibleMoves() {
        return this.possibleMoves;
    }

    public List<String> getCurrentlyAttacking() {
        return this.currentlyAttacking;
    }

}
