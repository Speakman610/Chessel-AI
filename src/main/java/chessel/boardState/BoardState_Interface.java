package main.java.chessel.boardState;

import java.util.List;
import java.util.Map;

public interface BoardState_Interface {

    public List<String> getPossibleMoves();

    public boolean makeMove(String notation);

    public void printCurrentBoard();

    public boolean gameEnded();

    public char getTurn();

    public Map<String, String> getStringBoard();

    public String[][] getBoardAs2DArray(); // TODO: Determine if this method is needed

    public boolean whiteIsInCheck();

    public boolean blackIsInCheck();

    public void resetBoard();
}