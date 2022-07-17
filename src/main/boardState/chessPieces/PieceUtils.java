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

                directionalMoves.add(piece.getNotation() + notationCoord);
                if (board.containsKey(notationCoord)) {
                    break;
                }
            } else {
                break;
            }
        }

        return directionalMoves;
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
        }

        if (piece.getX_pos() + (1 * x_direction) <= 8 &&
                piece.getX_pos() + (1 * x_direction) >= 1 &&
                piece.getY_pos() + (2 * y_direction) <= 8 && 
                piece.getY_pos() + (2 * y_direction) >= 1) {
            String notationCoord = ChesselUtils.convertXYPosToNotation(piece.getX_pos() + (1 * x_direction), piece.getY_pos() + (2 * y_direction));
            moveList.add(piece.getNotation() + notationCoord);
        }

        return moveList;
    }

    public static List<String> getPawnMoves(Piece piece) {
        List<String> pawnMoves = new ArrayList<>();
        Map<String, Piece> board = BoardState.getBoardState().getBoard();

        Direction direction = Direction.POSITIVE;
        if (piece.getTeam() == 'b') direction = Direction.NEGATIVE;

        int movementDirection = getSign(direction);

        String inFrontOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + (1 * movementDirection));
        if (inFrontOfPawn.charAt(1) == '8' || inFrontOfPawn.charAt(1) == '1') {
            pawnMoves.add(inFrontOfPawn + "Q");
            pawnMoves.add(inFrontOfPawn + "R");
            pawnMoves.add(inFrontOfPawn + "B");
            pawnMoves.add(inFrontOfPawn + "N");
        } else {
            pawnMoves.add(inFrontOfPawn);
        }

        if (!piece.hasMoved()) {
            String twoInFrontOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + (2 * movementDirection));
            if (!board.containsKey(ChesselUtils.convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + (1 * movementDirection)))) {
                pawnMoves.add(twoInFrontOfPawn);
            }
        }

        char currentFile = (char) (piece.getX_pos() + 96);
        if (piece.getX_pos() + 1 <= 8) {
            String forwardRightOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos() + 1, piece.getY_pos() + (1 * movementDirection));
            if (forwardRightOfPawn.charAt(1) == '8' || forwardRightOfPawn.charAt(1) == '1') {
                pawnMoves.add(currentFile + "x" + forwardRightOfPawn + "Q");
                pawnMoves.add(currentFile + "x" + forwardRightOfPawn + "R");
                pawnMoves.add(currentFile + "x" + forwardRightOfPawn + "B");
                pawnMoves.add(currentFile + "x" + forwardRightOfPawn + "N");
            } else {
                pawnMoves.add(currentFile + "x" + forwardRightOfPawn);
            }
            
        }
        
        if (piece.getX_pos() - 1 >= 1) {
            String forwardLeftOfPawn = ChesselUtils.convertXYPosToNotation(piece.getX_pos() - 1, piece.getY_pos() + (1 * movementDirection));
            if (forwardLeftOfPawn.charAt(1) == '8' || forwardLeftOfPawn.charAt(1) == '1') {
                pawnMoves.add(currentFile + "x" + forwardLeftOfPawn + "Q");
                pawnMoves.add(currentFile + "x" + forwardLeftOfPawn + "R");
                pawnMoves.add(currentFile + "x" + forwardLeftOfPawn + "B");
                pawnMoves.add(currentFile + "x" + forwardLeftOfPawn + "N");
            } else {
                pawnMoves.add(currentFile + "x" + forwardLeftOfPawn);
            }
        }

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
