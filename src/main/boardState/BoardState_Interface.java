package main.boardState;

import java.util.List;

public interface BoardState_Interface {
    public List<String> getPossibleMoves();
    public boolean makeMove(String notation);
    public void printCurrentBoard();
    public boolean whiteInCheck();
    public boolean blackInCheck();
}
