package main.boardState.chessPieces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.ChesselUtils;
import main.boardState.BoardState;

public class PieceUtils {
    enum Direction {
        POSITIVE,
        ZERO,
        NEGATIVE
    }
    enum CastlingSide {
        QUEEN,
        KING
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

    // // Only accepts the location (i.e. a3, e4, etc.), not the piece moving or any extra notation
    // public static int[] convertNotationCoordToXYCoord(String notation) {
    //     if (notation.length() != 2) throw new InvalidParameterException("Cannot convert notation: Notation expected to be of length 2 but was actually of length " + notation.length());
    //     char xChar = notation.charAt(0);
    //     char yChar = notation.charAt(1);

    //     int x_pos = (int) (xChar - 96);
    //     int y_pos = (int) (yChar - 48);

    //     return new int[] {x_pos, y_pos};
    // }

    // public static String convertXYPosToNotation(int x_pos, int y_pos) {
    //     char xChar = (char) (x_pos + 96);
    //     char yChar = (char) (y_pos + 48);
        
    //     return "" + xChar + yChar;
    // }

    public static boolean notCastlingThroughCheck(char team, CastlingSide castlingSide) {
        char[] x_positions;

        if (castlingSide == CastlingSide.QUEEN) {
            x_positions = new char[] {'b', 'c', 'd'};
        } else {
            x_positions = new char[] {'f', 'g'};
        }

        if (team == 'w') {
            Map<String, Integer> blackAttackMap = BoardState.getBoardState().getBlackAttackMap();
            for (char x_pos : x_positions) {
                if (blackAttackMap.get(x_pos + "1") > 0) return false;
            }
        } else {
            Map<String, Integer> whiteAttackMap = BoardState.getBoardState().getWhiteAttackMap();
            for (char x_pos : x_positions) {
                if (whiteAttackMap.get(x_pos + "8") > 0) return false;
            }
        }

        return true;
    }

    public static List<String> getCastling(char team) {
        List<String> castlingOptions = new ArrayList<>();
        Map<String, Piece> board = BoardState.getBoardState().getBoard();

        boolean kingCanCastle = false;
        boolean rookACanCastle = false;
        boolean rookHCanCastle = false;
        boolean queenSideClear = false;
        boolean kingSideClear = false;
        boolean notInCheck = false;
        int rowToCheck = 1;

        if (team == 'b') rowToCheck = 8;

        if (board.containsKey("e" + rowToCheck)) kingCanCastle = !board.get("e" + rowToCheck).hasMoved();
        if (board.containsKey("a" + rowToCheck)) rookACanCastle = !board.get("a" + rowToCheck).hasMoved();
        if (board.containsKey("h" + rowToCheck)) rookHCanCastle = !board.get("h" + rowToCheck).hasMoved();
        queenSideClear = !(board.containsKey("d" + rowToCheck) || board.containsKey("c" + rowToCheck) || board.containsKey("b" + rowToCheck)) && notCastlingThroughCheck(team, CastlingSide.QUEEN);
        kingSideClear = !(board.containsKey("f" + rowToCheck) || board.containsKey("g" + rowToCheck)) && notCastlingThroughCheck(team, CastlingSide.KING);

        if (team == 'w') {
            notInCheck = !BoardState.getBoardState().whiteInCheck();
        } else {
            notInCheck = !BoardState.getBoardState().blackInCheck();
        }

        if (kingCanCastle && notInCheck) {
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

    private static List<String> getMovesInDirection(Piece piece, int range, Direction x_axis, Direction y_axis) {
        List<String> directionalMoves = new ArrayList<>();
        Map<String, Piece> board = BoardState.getBoardState().getBoard();

        int horizontalInt = getSign(x_axis);
        int verticalInt = getSign(y_axis);

        for (int i = 1; i <= range; i++) {
            if (piece.getX_pos() + (i * horizontalInt) <= 8 &&
                    piece.getX_pos() + (i * horizontalInt) >= 1 &&
                    piece.getY_pos() + (i * verticalInt) <= 8 && 
                    piece.getY_pos() + (i * verticalInt) >= 1) {
                String notationCoord = ChesselUtils.convertXYPosToNotation(piece.getX_pos() + (i * horizontalInt), piece.getY_pos() + (i * verticalInt));

                if (board.containsKey(notationCoord)) {
                    directionalMoves.add(piece.getNotation() + notationCoord);
                    break;
                }// else {
                //     if (!kingMovingIntoCheck(piece, notationCoord)) {
                //         directionalMoves.add(piece.getNotation() + notationCoord);
                //     }
                //     BoardState.getBoardState().addToAttackMap(piece.getTeam(), notationCoord);
                // }
            } else {
                break;
            }
        }

        return directionalMoves;
    }

    private static boolean kingMovingIntoCheck(Piece piece, String notation) {
        if (piece.getNotation() != "K") return false;
        if (piece.getTeam() == 'w') {
            return BoardState.getBoardState().getBlackAttackMap().get(notation) > 0;
        } else {
            return BoardState.getBoardState().getWhiteAttackMap().get(notation) > 0;
        }
    }

    public static List<String> getKnightMoves(Piece piece) {
        List<String> knightMoves = new ArrayList<>();

        if (piece.canMoveRight() && piece.canMoveUp()) {
            knightMoves.addAll(calculateKnightMovesForDirection(piece, Direction.POSITIVE, Direction.POSITIVE));
        }

        if (piece.canMoveRight() && piece.canMoveDown()) {
            knightMoves.addAll(calculateKnightMovesForDirection(piece, Direction.POSITIVE, Direction.NEGATIVE));
        }

        if (piece.canMoveLeft() && piece.canMoveUp()) {
            knightMoves.addAll(calculateKnightMovesForDirection(piece, Direction.NEGATIVE, Direction.POSITIVE));
        }

        if (piece.canMoveLeft() && piece.canMoveDown()) {
            knightMoves.addAll(calculateKnightMovesForDirection(piece, Direction.NEGATIVE, Direction.NEGATIVE));
        }

        return knightMoves;
    }

    private static List<String> calculateKnightMovesForDirection(Piece piece, Direction x_axis, Direction y_axis) {
        List<String> moveList = new ArrayList<>();
        // Map<String, Piece> board = BoardState.getBoardState().getBoard();

        int x_direction = getSign(x_axis);
        int y_direction = getSign(y_axis);

        if (piece.getX_pos() + (2 * x_direction) <= 8 &&
                piece.getX_pos() + (2 * x_direction) >= 1 &&
                piece.getY_pos() + (1 * y_direction) <= 8 && 
                piece.getY_pos() + (1 * y_direction) >= 1) {
            String notationCoord = ChesselUtils.convertXYPosToNotation(piece.getX_pos() + (2 * x_direction), piece.getY_pos() + (1 * y_direction));
            moveList.add(piece.getNotation() + notationCoord);

            // if (board.containsKey(notationCoord)) {
            //     if (board.get(notationCoord).getTeam() != piece.getTeam()) {
            //         moveList.add(piece.getNotation() + "x" + notationCoord);
            //     }
            // } else {
            //     moveList.add(piece.getNotation() + notationCoord);
            // }
            // BoardState.getBoardState().addToAttackMap(piece.getTeam(), notationCoord);
        }

        if (piece.getX_pos() + (1 * x_direction) <= 8 &&
                piece.getX_pos() + (1 * x_direction) >= 1 &&
                piece.getY_pos() + (2 * y_direction) <= 8 && 
                piece.getY_pos() + (2 * y_direction) >= 1) {
            String notationCoord = ChesselUtils.convertXYPosToNotation(piece.getX_pos() + (1 * x_direction), piece.getY_pos() + (2 * y_direction));
            moveList.add(piece.getNotation() + notationCoord);

            // if (board.containsKey(notationCoord)) {
            //     if (board.get(notationCoord).getTeam() != piece.getTeam()) {
            //         moveList.add(piece.getNotation() + "x" + notationCoord);
            //     }
            // } else {
            //     moveList.add(piece.getNotation() + notationCoord);
            // }
            // BoardState.getBoardState().addToAttackMap(piece.getTeam(), notationCoord);
        }

        return moveList;
    }

    public static List<String> getPawnMoves(Piece piece) {
        List<String> pawnMoves = new ArrayList<>();
        // Map<String, Piece> board = BoardState.getBoardState().getBoard();

        Direction direction = Direction.POSITIVE;
        if (piece.getTeam() == 'b') direction = Direction.NEGATIVE;

        int movementDirection = getSign(direction);

        String inFrontOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + (1 * movementDirection));
        String twoInFrontOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + (2 * movementDirection));
        pawnMoves.add(inFrontOfPawn);
        pawnMoves.add(twoInFrontOfPawn);

        // if (!board.containsKey(inFrontOfPawn)) {
        //     pawnMoves.add(inFrontOfPawn);
        //     String twoInFrontOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + (2 * movementDirection));
        //     if (!piece.hasMoved() && !board.containsKey(twoInFrontOfPawn)) {
        //         pawnMoves.add(twoInFrontOfPawn);
        //     }
        // }

        char currentFile = (char) (piece.getX_pos() + 96);
        String forwardRightOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos() + 1, piece.getY_pos() + (1 * movementDirection));
        String forwardLeftOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos() - 1, piece.getY_pos() + (1 * movementDirection));
        pawnMoves.add(currentFile + "x" + forwardRightOfPawn);
        pawnMoves.add(currentFile + "x" + forwardLeftOfPawn);

        // if (board.containsKey(forwardRightOfPawn)) {
        //     if (board.get(forwardRightOfPawn).getTeam() != piece.getTeam()) {
        //         pawnMoves.add(currentFile + "x" + forwardRightOfPawn);
        //     }
        //     BoardState.getBoardState().addToAttackMap(piece.getTeam(), forwardRightOfPawn);
        // }
        // if (board.containsKey(forwardLeftOfPawn)) {
        //     if (board.get(forwardLeftOfPawn).getTeam() != piece.getTeam()) {
        //         pawnMoves.add(currentFile + "x" + forwardLeftOfPawn);
        //     }
        //     BoardState.getBoardState().addToAttackMap(piece.getTeam(), forwardLeftOfPawn);
        // }

        return pawnMoves;
    }

    private static int getSign(Direction direction) {
        switch (direction) {
            case POSITIVE:
                return 1;
            case NEGATIVE:
                return -1;
            default:
                return 0;
        }
    }
}
