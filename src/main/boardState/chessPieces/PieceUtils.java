package main.boardState.chessPieces;

import java.util.ArrayList;
import java.util.List;

public class PieceUtils {

    public static List<String> getDiagonalMoves(Piece piece, int range) {
        List<String> diagonalMoves = new ArrayList<>();

        if (piece.canMoveRight() && piece.canMoveUp()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() + i <= 8 && piece.getY_pos() + i <= 8) {
                    diagonalMoves.add(convertXYPosToNotation(piece.getX_pos() + i, piece.getY_pos() + i));
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveRight() && piece.canMoveDown()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() + i <= 8 && piece.getY_pos() - i >= 1) {
                    diagonalMoves.add(convertXYPosToNotation(piece.getX_pos() + i, piece.getY_pos() - i));
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveLeft() && piece.canMoveUp()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() - i >= 1 && piece.getY_pos() + i <= 8) {
                    diagonalMoves.add(convertXYPosToNotation(piece.getX_pos() - i, piece.getY_pos() + i));
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveLeft() && piece.canMoveDown()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() - i >= 1 && piece.getY_pos() - i >= 1) {
                    diagonalMoves.add(convertXYPosToNotation(piece.getX_pos() - i, piece.getY_pos() - i));
                } else {
                    break;
                }
            }
        }

        return diagonalMoves;
    }

    public static List<String> getAdjacentMoves(Piece piece, int range) {
        List<String> adjacentMoves = new ArrayList<>();

        if (piece.canMoveRight()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() + i <= 8) {
                    adjacentMoves.add(convertXYPosToNotation(piece.getX_pos() + i, piece.getY_pos()));
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveLeft()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() - i >= 1) {
                    adjacentMoves.add(convertXYPosToNotation(piece.getX_pos() - i, piece.getY_pos()));
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveUp()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getY_pos() + i <= 8) {
                    adjacentMoves.add(convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + i));
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveDown()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getY_pos() - i >= 1) {
                    adjacentMoves.add(convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() - i));
                } else {
                    break;
                }
            }
        }

        return adjacentMoves;
    }

    public static String convertXYPosToNotation(int x_pos, int y_pos) {
        char xChar = (char) (x_pos + 96);
        char yChar = (char) (y_pos + 48);
        
        return "" + xChar + yChar;
    }
}
