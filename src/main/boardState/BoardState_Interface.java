package main.boardState;

import java.util.List;

public interface BoardState_Interface {
    public List<String> getPossibleMoves(char team);
    public boolean makeMove(String notation);
    public void printCurrentBoard();
}
