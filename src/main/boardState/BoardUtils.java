package main.boardState;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.boardState.chessPieces.Piece;

public class BoardUtils {
    enum Direction {
        POSITIVE,
        ZERO,
        NEGATIVE
    }

    /*
     * NOTATION NOTES:
     * 
     * Notation Prefix:
     * K - King
     * Q - Queen
     * B - Bishop
     * N - Knight
     * R - Rook
     * a-h - Pawn movement (d4 or )
     * O - Castling (O-O or O-O-O)
     * 
     * Notation Base:
     * a-h + 1-8 - Location to move to (at the end if there is no suffix)
     * x - Capture notation for pieces (Qxh8 or dxe5 for pawns)
     * 
     * Notation Suffix:
     * + - Check (Nc6+)
     * # - Checkmate (Ra7#)
     * e.p. - En Passant (exf6 e.p.)
     */

    // Only accepts the location (i.e. a3, e4, etc.), not the piece moving or any extra notation
    public static int[] convertNotationCoordToXYCoord(String notation) {
        if (notation.length() != 2) throw new InvalidParameterException("Cannot convert notation: Notation expected to be of length 2 but was actually of length " + notation.length());
        char xChar = notation.charAt(0);
        char yChar = notation.charAt(1);

        int x_pos = (int) (xChar - 96);
        int y_pos = (int) (yChar - 48);

        return new int[] {x_pos, y_pos};
    }

    public static String convertXYPosToNotation(int x_pos, int y_pos) {
        char xChar = (char) (x_pos + 96);
        char yChar = (char) (y_pos + 48);
        
        return "" + xChar + yChar;
    }

    public static boolean canCastle(char team) {
        Map<String, Piece> board = BoardState.getBoardState().getBoard();

        boolean kingCanCastle = false;
        boolean rookACanCastle = false;
        boolean rookHCanCastle = false;
        boolean queenSideClear = false;
        boolean kingSideClear = false;

        if (team == 'w') {
            if (board.containsKey("e1")) kingCanCastle = !board.get("e1").hasMoved();
            if (board.containsKey("a1")) rookACanCastle = !board.get("a1").hasMoved();
            if (board.containsKey("h1")) rookHCanCastle = !board.get("h1").hasMoved();
            queenSideClear = !(board.containsKey("d1") && board.containsKey("c1") && board.containsKey("b1"));
            kingSideClear = !(board.containsKey("f1") && board.containsKey("g1"));
        }

        return kingCanCastle && ((rookACanCastle && queenSideClear) || (rookHCanCastle && kingSideClear));
    }

    public static List<String> getCastling(char team) {
        List<String> castlingOptions = new ArrayList<>();
        Map<String, Piece> board = BoardState.getBoardState().getBoard();

        boolean kingCanCastle = false;
        boolean rookACanCastle = false;
        boolean rookHCanCastle = false;
        boolean queenSideClear = false;
        boolean kingSideClear = false;

        if (team == 'w') {
            if (board.containsKey("e1")) kingCanCastle = !board.get("e1").hasMoved();
            if (board.containsKey("a1")) rookACanCastle = !board.get("a1").hasMoved();
            if (board.containsKey("h1")) rookHCanCastle = !board.get("h1").hasMoved();
            queenSideClear = !(board.containsKey("d1") || board.containsKey("c1") || board.containsKey("b1"));
            kingSideClear = !(board.containsKey("f1") || board.containsKey("g1"));
        } else {
            if (board.containsKey("e8")) kingCanCastle = !board.get("e8").hasMoved();
            if (board.containsKey("a8")) rookACanCastle = !board.get("a8").hasMoved();
            if (board.containsKey("h8")) rookHCanCastle = !board.get("h8").hasMoved();
            queenSideClear = !(board.containsKey("d8") || board.containsKey("c8") || board.containsKey("b8"));
            kingSideClear = !(board.containsKey("f8") || board.containsKey("g8"));
        }

        if (kingCanCastle) {
            if (rookACanCastle && queenSideClear) castlingOptions.add("O-O-O");
            if (rookHCanCastle && kingSideClear) castlingOptions.add("O-O");
        }

        return castlingOptions;
    }

    public static List<String> getDiagonalMoves(Piece piece, int range) {
        List<String> diagonalMoves = new ArrayList<>();

        if (piece.canMoveRight() && piece.canMoveUp()) {
            diagonalMoves.addAll(getMovesInDirection(piece, range, Direction.POSITIVE, Direction.POSITIVE));
        }

        if (piece.canMoveRight() && piece.canMoveDown()) {
            diagonalMoves.addAll(getMovesInDirection(piece, range, Direction.POSITIVE, Direction.NEGATIVE));
        }

        if (piece.canMoveLeft() && piece.canMoveUp()) {
            diagonalMoves.addAll(getMovesInDirection(piece, range, Direction.NEGATIVE, Direction.POSITIVE));
        }

        if (piece.canMoveLeft() && piece.canMoveDown()) {
            diagonalMoves.addAll(getMovesInDirection(piece, range, Direction.NEGATIVE, Direction.NEGATIVE));
        }

        return diagonalMoves;
    }

    public static List<String> getAdjacentMoves(Piece piece, int range) {
        List<String> adjacentMoves = new ArrayList<>();

        if (piece.canMoveRight()) {
            adjacentMoves.addAll(getMovesInDirection(piece, range, Direction.POSITIVE, Direction.ZERO));
        }

        if (piece.canMoveLeft()) {
            adjacentMoves.addAll(getMovesInDirection(piece, range, Direction.NEGATIVE, Direction.ZERO));
        }

        if (piece.canMoveUp()) {
            adjacentMoves.addAll(getMovesInDirection(piece, range, Direction.ZERO, Direction.POSITIVE));
        }

        if (piece.canMoveDown()) {
            adjacentMoves.addAll(getMovesInDirection(piece, range, Direction.ZERO, Direction.NEGATIVE));
        }

        return adjacentMoves;
    }

    private static List<String> getMovesInDirection(Piece piece, int range, Direction horizontal, Direction vertical) {
        List<String> directionalMoves = new ArrayList<>();
        Map<String, Piece> board = BoardState.getBoardState().getBoard();

        int horizontalInt;
        int verticalInt;

        switch (horizontal) {
            case POSITIVE:
                horizontalInt = 1;
                break;
            case NEGATIVE:
                horizontalInt = -1;
                break;
            default:
                horizontalInt = 0;
                break;
        }

        switch (vertical) {
            case POSITIVE:
                verticalInt = 1;
                break;
            case NEGATIVE:
                verticalInt = -1;
                break;
            default:
                verticalInt = 0;
                break;
        }


        for (int i = 1; i <= range; i++) {
            if (piece.getX_pos() + (i * horizontalInt) <= 8 &&
                    piece.getX_pos() + (i * horizontalInt) >= 1 &&
                    piece.getY_pos() + (i * verticalInt) <= 8 && 
                    piece.getY_pos() + (i * verticalInt) >= 1) {
                String notationCoord = convertXYPosToNotation(piece.getX_pos() + (i * horizontalInt), piece.getY_pos() + (i * verticalInt));
                if (board.containsKey(notationCoord)) {
                    if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                        directionalMoves.add(piece.getNotation() + "x" + notationCoord);
                    }
                    break;
                } else {
                    directionalMoves.add(piece.getNotation() + notationCoord);
                }
            } else {
                break;
            }
        }

        return directionalMoves;
    }
}
