package main.boardState;

import java.util.List;

import main.exceptions.InternalApplicationException;
import main.exceptions.InvalidMoveException;

public interface BoardState_Interface {
    public List<String> getPossibleMoves(char team);
    public void makeMove(String notation) throws InvalidMoveException, InternalApplicationException;
    public void printCurrentBoard();
}
