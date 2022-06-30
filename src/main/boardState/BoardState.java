package main.boardState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.boardState.chessPieces.Bishop;
import main.boardState.chessPieces.King;
import main.boardState.chessPieces.Knight;
import main.boardState.chessPieces.Pawn;
import main.boardState.chessPieces.Piece;
import main.boardState.chessPieces.Queen;
import main.boardState.chessPieces.Rook;
import main.exceptions.InternalApplicationException;
import main.exceptions.InvalidMoveException;

public class BoardState implements BoardState_Interface {
    private static BoardState boardState = null; // instance of the board state to create a singleton class
    private Map<String, Piece> board;
    private Map<String, Integer> whiteAttackMap;
    private Map<String, Integer> blackAttackMap;
    private boolean inCheckWhite;
    private boolean inCheckBlack;
    private char turn; // the current turn in the game

    private BoardState() {
        // create the board and add all of the pieces
        board = new HashMap<>();

        // WHITE TEAM
        board.put("a2", new Pawn('w', 1, 2));
        board.put("b2", new Pawn('w', 2, 2));
        board.put("c2", new Pawn('w', 3, 2));
        board.put("d2", new Pawn('w', 4, 2));
        board.put("e2", new Pawn('w', 5, 2));
        board.put("f2", new Pawn('w', 6, 2));
        board.put("g2", new Pawn('w', 7, 2));
        board.put("h2", new Pawn('w', 8, 2));

        board.put("a1", new Rook('w', 1, 1));
        board.put("b1", new Knight('w', 2, 1));
        board.put("c1", new Bishop('w', 3, 1));
        board.put("d1", new Queen('w', 4, 1));
        board.put("e1", new King('w', 5, 1));
        board.put("f1", new Bishop('w', 6, 1));
        board.put("g1", new Knight('w', 7, 1));
        board.put("h1", new Rook('w', 8, 1));

        // BLACK TEAM
        board.put("a7", new Pawn('b', 1, 7));
        board.put("b7", new Pawn('b', 2, 7));
        board.put("c7", new Pawn('b', 3, 7));
        board.put("d7", new Pawn('b', 4, 7));
        board.put("e7", new Pawn('b', 5, 7));
        board.put("f7", new Pawn('b', 6, 7));
        board.put("g7", new Pawn('b', 7, 7));
        board.put("h7", new Pawn('b', 8, 7));

        board.put("a8", new Rook('b', 1, 8));
        board.put("b8", new Knight('b', 2, 8));
        board.put("c8", new Bishop('b', 3, 8));
        board.put("d8", new Queen('b', 4, 8));
        board.put("e8", new King('b', 5, 8));
        board.put("f8", new Bishop('b', 6, 8));
        board.put("g8", new Knight('b', 7, 8));
        board.put("h8", new Rook('b', 8, 8));

        initializeAttackMaps();
        inCheckWhite = false;
        inCheckBlack = false;
        turn = 'w';
    }

    public static BoardState getBoardState() {
        if (boardState == null) {
            boardState = new BoardState();
        }

        return boardState;
    }

    @Override
    public List<String> getPossibleMoves(char team) {
        List<String> possibleMoves = new ArrayList<>();

        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            if (entry.getValue().getTeam() == team) {
                possibleMoves.addAll(entry.getValue().getPossibleMoves());
            }
        }

        possibleMoves.addAll(BoardUtils.getCastling(team));

        return possibleMoves;
    }

    public void setPossibleMoves() {
        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            entry.getValue().setPossibleMoves();
        }
        
        /*
         * After all of the pieces have determined their
         * possible moves, recalculate moves for the
         * kings to take into account attack maps
         */
        for (Piece piece : board.values()) {
            if (piece.getNotation() == "K") {
                piece.setPossibleMoves();
                int x_pos = piece.getX_pos();
                int y_pos = piece.getY_pos();
                String location = BoardUtils.convertXYPosToNotation(x_pos, y_pos);
                if (whiteAttackMap.get(location) > 0) {
                    inCheckWhite = true;
                } else {
                    inCheckWhite = false;
                }

                if (blackAttackMap.get(location) > 0) {
                    inCheckBlack = true;
                } else {
                    inCheckBlack = false;
                }
            }
        }
    }

    @Override
    public void makeMove(String inputMove) throws InvalidMoveException, InternalApplicationException {
        if (!getPossibleMoves(turn).contains(inputMove)) throw new InvalidMoveException("The move " + inputMove + " is not a valid move.");

        String notationCoords = "";
        String pieceNotation = "";
        Piece pieceToMove = null;
        
        for (int i = 0; i < inputMove.length(); i++) {
            char c = inputMove.charAt(i);
            // 1 -> 49, 8 -> 56
            if (c >= 49 && c <= 56) {
                notationCoords = inputMove.charAt(i - 1) + "" + c;
                pieceNotation = inputMove.substring(0, i - 1);
                if (pieceNotation.length() > 1) {
                    pieceNotation = pieceNotation.substring(0, 1);
                }
            }
        }

        for (Piece piece : board.values()) {
            if (piece.getNotation() == pieceNotation &&
                    piece.getTeam() == turn &&
                    piece.getPossibleMoves().contains(inputMove)) {
                pieceToMove = piece;
                break;
            }
        }

        if (pieceToMove == null) throw new InternalApplicationException("Unable to make move due to an internal error");
        int[] xy_pos = BoardUtils.convertNotationCoordToXYCoord(notationCoords);
        board.remove(BoardUtils.convertXYPosToNotation(pieceToMove.getX_pos(), pieceToMove.getY_pos()));
        pieceToMove.movePiece(xy_pos[0], xy_pos[1]);
        board.put(notationCoords, pieceToMove);

        toggleTurn();
        setPossibleMoves();

        System.out.println("Notation Coords: " + notationCoords);
        System.out.println("Piece Notation: " + pieceNotation);

        // TODO: Finish makeMove()
        // TODO: Check to see if either side is in check or checkmate after each move
    }

    @Override
    public void printCurrentBoard() {
        // for (Map.Entry<String, Piece> entry : board.entrySet()) {
        //     System.out.println(entry.getKey() + ": " + entry.getValue().getNotation() + entry.getValue().getTeam());
        // }

        String boardPrint = "       ----==| CHESSEL |==----\n\n";
        boardPrint += "  |---|---|---|---|---|---|---|---|\n";
        for (int y = 8; y >= 1; y--) {
            boardPrint += y + " |";
            for (int x = 1; x <= 8; x++) {
                boolean squareIsBlack = ((x + y) % 2) == 0;
                String fill = " ";
                if (squareIsBlack) {
                    fill = "|";
                }
                boardPrint += fill;
                String location = BoardUtils.convertXYPosToNotation(x, y);
                Piece piece = board.get(location);
                if (piece == null) {
                    boardPrint += fill;
                } else {
                    String pieceNotation;
                    if (piece.getNotation() == "") {
                        pieceNotation = "P";
                    } else {
                        pieceNotation = piece.getNotation();
                    }
                    if (piece.getTeam() == 'b') pieceNotation = pieceNotation.toLowerCase();
                    boardPrint += pieceNotation;
                }
                boardPrint += fill + "|";
            }
            boardPrint += "\n  |---|---|---|---|---|---|---|---|\n";
        }
        boardPrint += "    a   b   c   d   e   f   g   h\n";
        System.out.println(boardPrint);
    }

    private void initializeAttackMaps() {
        blackAttackMap = new HashMap<>();
        whiteAttackMap = new HashMap<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                blackAttackMap.put(BoardUtils.convertXYPosToNotation(i, j), 0);
                whiteAttackMap.put(BoardUtils.convertXYPosToNotation(i, j), 0);
            }
        }
    }

    public Map<String,Piece> getBoard() {
        return this.board;
    }

    public char getTurn() {
        return this.turn;
    }

    private void setTurn(char turn) {
        this.turn = turn;
    }

    private void toggleTurn() {
        if (turn == 'w') {
            setTurn('b');
        } else {
            setTurn('w');
        }
    }

    public boolean whiteInCheck() {
        return inCheckWhite;
    }

    public boolean blackInCheck() {
        return inCheckBlack;
    }

    public void addToAttackMap(char team, String notationCoord) {
        if (team == 'w') {
            int attackingCount = whiteAttackMap.get(notationCoord);
            attackingCount += 1;
            whiteAttackMap.put(notationCoord, attackingCount);
        } else {
            int attackingCount = blackAttackMap.get(notationCoord);
            attackingCount += 1;
            blackAttackMap.put(notationCoord, attackingCount);
        }
    }

    public void subtractFromAttackMap(char team, String notationCoord) {
        if (team == 'w') {
            int attackingCount = whiteAttackMap.get(notationCoord);
            attackingCount -= 1;
            whiteAttackMap.put(notationCoord, attackingCount);
        } else {
            int attackingCount = blackAttackMap.get(notationCoord);
            attackingCount -= 1;
            blackAttackMap.put(notationCoord, attackingCount);
        }
    }

    public Map<String, Integer> getWhiteAttackMap() {
        return whiteAttackMap;
    }

    public Map<String, Integer> getBlackAttackMap() {
        return blackAttackMap;
    }

}
