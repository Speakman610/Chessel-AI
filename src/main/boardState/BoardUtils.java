package main.boardState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import main.boardState.chessPieces.Piece;

public class BoardUtils {

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

    // TODO: Fix get***Moves()

    public static List<String> getDiagonalMoves(Piece piece, int range) {
        List<String> diagonalMoves = new ArrayList<>();
        Map<String, Piece> board = BoardState.getBoardState().getBoard();

        if (piece.canMoveRight() && piece.canMoveUp()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() + i <= 8 && piece.getY_pos() + i <= 8) {
                    String notationCoord = convertXYPosToNotation(piece.getX_pos() + i, piece.getY_pos() + i);
                    if (board.containsKey(notationCoord)) {
                        if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                            diagonalMoves.add(piece.getNotation() + "x" + notationCoord);
                        }
                        break;
                    } else {
                        diagonalMoves.add(piece.getNotation() + notationCoord);
                    }
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveRight() && piece.canMoveDown()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() + i <= 8 && piece.getY_pos() - i >= 1) {
                    String notationCoord = convertXYPosToNotation(piece.getX_pos() + i, piece.getY_pos() - i);
                    if (board.containsKey(notationCoord)) {
                        if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                            diagonalMoves.add(piece.getNotation() + "x" + notationCoord);
                        }
                        break;
                    } else {
                        diagonalMoves.add(piece.getNotation() + notationCoord);
                    }
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveLeft() && piece.canMoveUp()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() - i >= 1 && piece.getY_pos() + i <= 8) {
                    String notationCoord = convertXYPosToNotation(piece.getX_pos() - i, piece.getY_pos() + i);
                    if (board.containsKey(notationCoord)) {
                        if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                            diagonalMoves.add(piece.getNotation() + "x" + notationCoord);
                        }
                        break;
                    } else {
                        diagonalMoves.add(piece.getNotation() + notationCoord);
                    }
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveLeft() && piece.canMoveDown()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() - i >= 1 && piece.getY_pos() - i >= 1) {
                    String notationCoord = convertXYPosToNotation(piece.getX_pos() - i, piece.getY_pos() - i);
                    if (board.containsKey(notationCoord)) {
                        if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                            diagonalMoves.add(piece.getNotation() + "x" + notationCoord);
                        }
                        break;
                    } else {
                        diagonalMoves.add(piece.getNotation() + notationCoord);
                    }
                } else {
                    break;
                }
            }
        }

        return diagonalMoves;
    }

    public static List<String> getAdjacentMoves(Piece piece, int range) {
        List<String> adjacentMoves = new ArrayList<>();
        Map<String, Piece> board = BoardState.getBoardState().getBoard();

        if (piece.canMoveRight()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() + i <= 8) {
                    String notationCoord = convertXYPosToNotation(piece.getX_pos() + i, piece.getY_pos());
                    if (board.containsKey(notationCoord)) {
                        if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                            adjacentMoves.add(piece.getNotation() + "x" + notationCoord);
                        }
                        break;
                    } else {
                        adjacentMoves.add(piece.getNotation() + notationCoord);
                    }
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveLeft()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getX_pos() - i >= 1) {
                    String notationCoord = convertXYPosToNotation(piece.getX_pos() - i, piece.getY_pos());
                    if (board.containsKey(notationCoord)) {
                        if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                            adjacentMoves.add(piece.getNotation() + "x" + notationCoord);
                        }
                        break;
                    } else {
                        adjacentMoves.add(piece.getNotation() + notationCoord);
                    }
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveUp()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getY_pos() + i <= 8) {
                    String notationCoord = convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + 1);
                    if (board.containsKey(notationCoord)) {
                        if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                            adjacentMoves.add(piece.getNotation() + "x" + notationCoord);
                        }
                        break;
                    } else {
                        adjacentMoves.add(piece.getNotation() + notationCoord);
                    }
                } else {
                    break;
                }
            }
        }

        if (piece.canMoveDown()) {
            for (int i = 1; i <= range; i++) {
                if (piece.getY_pos() - i >= 1) {
                    String notationCoord = convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() - 1);
                    if (board.containsKey(notationCoord)) {
                        if (board.get(notationCoord).getTeam() != piece.getTeam()) {
                            adjacentMoves.add(piece.getNotation() + "x" + notationCoord);
                        }
                        break;
                    } else {
                        adjacentMoves.add(piece.getNotation() + notationCoord);
                    }
                } else {
                    break;
                }
            }
        }

        return adjacentMoves;
    }
}
