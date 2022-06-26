package main.boardState;

import java.util.List;

public interface BoardState_Interface {
    // TODO: Get list of possible moves
    // TODO: Make move
    // TODO: Print board state

    public List<String> getPossibleMoves(char team);
    public boolean makeMove(String notation);
    public void printCurrentBoard();
}
