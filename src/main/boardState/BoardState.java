package main.boardState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.ChesselUtils;
import main.boardState.chessPieces.Bishop;
import main.boardState.chessPieces.King;
import main.boardState.chessPieces.Knight;
import main.boardState.chessPieces.Pawn;
import main.boardState.chessPieces.Piece;
import main.boardState.chessPieces.PieceUtils;
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
    private boolean whiteCanCastleKingSide;
    private boolean whiteCanCastleQueenSide;
    private boolean blackCanCastleKingSide;
    private boolean blackCanCastleQueenSide;
    private char turn; // the current turn in the game

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    /*
     * Pieces should only track where they can move, excluding all information about the state of the board.
     * The board is what calculates if a move captures another piece or is valid.
     */

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

        clearAttackMaps();
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

        possibleMoves.addAll(PieceUtils.getCastling(team));

        // return filterInvalidMoves(possibleMoves);
        return possibleMoves;
    }

    public void setPossibleMoves() {
        /*
         * STEPS TO SETTING POSSIBLE MOVES
         * 
         * √ Mark all attacking moves with an x
         * √ Set if castling is possible (false if enemy is attacking castling spaces)
         * √ Remove moves on teammates
         * √ Remove move-through-piece moves
         * Specify pieces for duplicate moves
         * X Add to attack maps (obsolete)
         * Add possible castling
         * Label if in check
         * Remove still-in-check moves (only for current turn)
         */
        clearAttackMaps();
        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            if (entry.getValue().getNotation() != "K") {
                entry.getValue().setPossibleMoves();
            }
        }
        
        /*
         * After all of the pieces have determined their
         * possible moves, calculate moves for the
         * kings to take into account attack maps
         */
        for (Piece piece : board.values()) {
            if (piece.getNotation() == "K") {
                piece.setPossibleMoves();
                int x_pos = piece.getX_pos();
                int y_pos = piece.getY_pos();
                String location = ChesselUtils.convertXYPosToNotation(x_pos, y_pos);
                if (whiteAttackMap.get(location) > 0 && piece.getTeam() == 'b') {
                    inCheckBlack = true;
                } else {
                    inCheckBlack = false;
                }

                if (blackAttackMap.get(location) > 0 && piece.getTeam() == 'w') {
                    inCheckWhite = true;
                } else {
                    inCheckWhite = false;
                }
            }
        }
    }

    private List<String> calculateGeneralPieceMovement(Piece piece) {
        List<String> moveList = piece.getPossibleMoves();
        List<String> toRemove = new ArrayList<>();
        for (String move : moveList) {
            String[] splitNotation = splitMoveNotation(move);
            String notationCoords = splitNotation[0];
            if (board.containsKey(notationCoords)) {
                Piece pieceAtCoords = board.get(notationCoords);
                if (pieceAtCoords.getTeam() == piece.getTeam()) {
                    // if the pieces are on the same team the move is invalid
                    toRemove.add(move);
                } else {
                    // if the pieces are enemies, add an 'x' to the move notation
                    move = addXToNotation(move);
                    int[] xy_pos = ChesselUtils.convertNotationCoordToXYCoord(notationCoords);
                    int x_pos = xy_pos[0];
                    int y_pos = xy_pos[1];

                    if (piece.getTeam() == 'w' && y_pos == 8) {
                        if (x_pos >= 2 && x_pos <= 4) {
                            blackCanCastleQueenSide = false;
                        } else if (x_pos >= 6 && x_pos <= 7) {
                            blackCanCastleKingSide = false;
                        }
                    } else if (y_pos == 1) {
                        if (x_pos >= 2 && x_pos <= 4) {
                            whiteCanCastleQueenSide = false;
                        } else if (x_pos >= 6 && x_pos <= 7) {
                            whiteCanCastleKingSide = false;
                        }
                    }
                }
            }
        }
        moveList.removeAll(toRemove);
        return moveList;
    }

    private String addXToNotation(String move) {
        if (move.contains("x")) return move;
        return move.charAt(0) + "x" + move.substring(1);
    }

    private void setCheckFlags() {
        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            // if an enemy piece is attacking where the king is then set a check flag
        }
    }

    /**
     * Takes a move as a parameter and separates the coordinates where the 
     * piece will be moved to from the letter that signifies a given piece,
     * returning them as a String array.
     * 
     * @param move
     * @return notationCoords at index 0 and pieceNotation at index 1
     */
    private String[] splitMoveNotation(String move) {
        String notationCoords = "";
        String pieceNotation = "";

        for (int i = 0; i < move.length(); i++) {
            char c = move.charAt(i);
            // 1 -> 49, 8 -> 56
            if (c >= 49 && c <= 56) {
                notationCoords = move.charAt(i - 1) + "" + c;
                pieceNotation = "" + move.charAt(0);
            }
        }
        return new String[] {notationCoords, pieceNotation};
    }

    @Override
    public void makeMove(String inputMove) throws InvalidMoveException, InternalApplicationException {
        if (!getPossibleMoves(turn).contains(inputMove)) throw new InvalidMoveException("The move " + inputMove + " is not a valid move.");

        // String notationCoords = "";
        // String pieceNotation = "";
        // Piece pieceToMove = null;
        
        // for (int i = 0; i < inputMove.length(); i++) {
        //     char c = inputMove.charAt(i);
        //     // 1 -> 49, 8 -> 56
        //     if (c >= 49 && c <= 56) {
        //         notationCoords = inputMove.charAt(i - 1) + "" + c;
        //         pieceNotation = inputMove.substring(0, i - 1);
        //         if (pieceNotation.length() > 1) {
        //             pieceNotation = pieceNotation.substring(0, 1);
        //         }
        //     }
        // }

        // for (Piece piece : board.values()) {
        //     if (pieceNotation.equals(piece.getNotation()) &&
        //             turn == piece.getTeam() &&
        //             piece.getPossibleMoves().contains(inputMove)) {
        //         pieceToMove = piece;
        //         break;
        //     }
        // }

        // if (pieceToMove == null) throw new InternalApplicationException("Unable to make move due to an internal error");
        // int[] xy_pos = BoardUtils.convertNotationCoordToXYCoord(notationCoords);
        // board.remove(BoardUtils.convertXYPosToNotation(pieceToMove.getX_pos(), pieceToMove.getY_pos()));
        // pieceToMove.movePiece(xy_pos[0], xy_pos[1]);
        // board.put(notationCoords, pieceToMove);

        movePiece(inputMove);
        toggleTurn();
        setPossibleMoves();

        // System.out.println("Notation Coords: " + notationCoords);
        // System.out.println("Piece Notation: " + pieceNotation);

        // TODO: Add the ability to castle
        // TODO: Check to see if either side is in check or checkmate after each move
    }

    private void movePiece(String move) throws InternalApplicationException, InvalidMoveException {
        String notationCoords = "";
        String pieceNotation = "";
        Piece pieceToMove = null;
        
        for (int i = 0; i < move.length(); i++) {
            char c = move.charAt(i);
            // 1 -> 49, 8 -> 56
            if (c >= 49 && c <= 56) {
                notationCoords = move.charAt(i - 1) + "" + c;
                pieceNotation = "" + move.charAt(0);
            }
        }

        for (Piece piece : board.values()) {
            if ((pieceNotation.equals(piece.getNotation())
                || (pieceNotation.equals("" + (char) (piece.getX_pos() + 96)))) &&
                    turn == piece.getTeam() &&
                    piece.getPossibleMoves().contains(move)) {
                pieceToMove = piece;
                break;
            }
        }

        if (pieceToMove == null) throw new InternalApplicationException("Unable to make move due to an internal error");
        int[] xy_pos = ChesselUtils.convertNotationCoordToXYCoord(notationCoords);
        board.remove(ChesselUtils.convertXYPosToNotation(pieceToMove.getX_pos(), pieceToMove.getY_pos()));
        pieceToMove.movePiece(xy_pos[0], xy_pos[1]);
        board.put(notationCoords, pieceToMove);
    }

    private List<String> filterInvalidMoves(List<String> moves) {
        Map<String, Piece> board = getBoard();
        Map<String, Piece> boardCopy = new HashMap<>(board);
        
        List<String> movesToRemove = new ArrayList<>();

        for (String move : moves) {
            try {
                boolean turnWhite = turn == 'w';
                movePiece(move);
                setPossibleMoves();
                if (turnWhite && whiteInCheck()) {
                    movesToRemove.add(move);
                } else if (!turnWhite && blackInCheck()) {
                    movesToRemove.add(move);
                }
            } catch (Exception e) {
                // e.printStackTrace();
                movesToRemove.add(move);
            }
            this.board = new HashMap<>(boardCopy);
            setPossibleMoves();
        }
        moves.removeAll(movesToRemove);
        
        return moves;
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
                String location = ChesselUtils.convertXYPosToNotation(x, y);
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
                    if (piece.getTeam() == 'w') {
                        pieceNotation = ANSI_BLUE + pieceNotation + ANSI_RESET;
                    } else {
                        pieceNotation = ANSI_RED + pieceNotation + ANSI_RESET;
                    }
                    boardPrint += pieceNotation;
                }
                boardPrint += fill + "|";
            }
            boardPrint += "\n  |---|---|---|---|---|---|---|---|\n";
        }
        boardPrint += "    a   b   c   d   e   f   g   h\n";
        System.out.println(boardPrint);
    }

    private void clearAttackMaps() {
        blackAttackMap = new HashMap<>();
        whiteAttackMap = new HashMap<>();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                blackAttackMap.put(ChesselUtils.convertXYPosToNotation(i, j), 0);
                whiteAttackMap.put(ChesselUtils.convertXYPosToNotation(i, j), 0);
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

    /*
     * This method may be unnecessary
     */
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
