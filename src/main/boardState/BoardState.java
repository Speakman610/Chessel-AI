package main.boardState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.ChesselUtils;
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
    private boolean inCheckWhite;
    private boolean inCheckBlack;
    private boolean whiteCanCastleKingSide;
    private boolean whiteCanCastleQueenSide;
    private boolean blackCanCastleKingSide;
    private boolean blackCanCastleQueenSide;
    private String whiteKingCoords;
    private String blackKingCoords;
    private String enPassantCoords;
    private List<String> possibleMoves = null;
    private char turn; // the current turn in the game

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREY = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[32m";
    private static final String ANSI_ORANGE = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PINK = "\u001B[35m";
    private static final String ANSI_PURPLE = "\u001B[36m";
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

        inCheckWhite = false;
        inCheckBlack = false;
        turn = 'w';
    }

    /**
     * Since BoardState is a singleton class, only one instance 
     * of the class should exist at any given moment
     * 
     * @return the instance of the BoardState or create an instance
     * if one does not exist
     */
    public static BoardState getBoardState() {
        if (boardState == null) {
            boardState = new BoardState();
        }

        return boardState;
    }

    @Override
    public List<String> getPossibleMoves() {
        if (possibleMoves == null) {
            setPossibleMoves();
        }
        return possibleMoves;
    }

    /**
     * Set all possible moves that can be taken in the given turn
     */
    public void setPossibleMoves() {
        /*
         * STEPS TO SETTING POSSIBLE MOVES
         * 
         * Mark all attacking moves with an x
         * Set if castling is possible (false if enemy is attacking castling spaces)
         * Specify pieces for duplicate moves
         * Remove still-in-check moves (only for current turn)
         * Add possible castling
         * Set in check flags
         */

        possibleMoves = new ArrayList<>();
        whiteCanCastleKingSide = true;
        whiteCanCastleQueenSide = true;
        blackCanCastleKingSide = true;
        blackCanCastleQueenSide = true;

        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            // it is still necessary to calculate moves even for the enemy team so that proper flags can be set
            Piece piece = entry.getValue();
            List<String> temporaryMoveList = calculateGeneralPieceMovement(piece);
            if (piece.getNotation() == "K") {
                if (piece.getTeam() == 'w') {
                    whiteKingCoords = piece.getNotationCoords();
                } else {
                    blackKingCoords = piece.getNotationCoords();
                }
            }
            if (piece.getTeam() == turn) {
                possibleMoves.addAll(temporaryMoveList);
            }
        }

        identifyDuplicateMoves(); // add piece identification to duplicate moves

        List<String> tempList = new ArrayList<>();
        tempList.addAll(possibleMoves); // this to prevent editing the possibleMoves list while also iterating over it
        try {
            possibleMoves = removeStillInCheckMoves(tempList);
        } catch (InternalApplicationException e) {
            possibleMoves = tempList;
        }

        possibleMoves.addAll(getCastling());
        setCheckFlags();
    }

    /**
     * Takes a piece as a parameter and determines all possible moves for the piece.
     * Additionally, this method sets some flags for whether or not castling is possible.
     * 
     * @param piece is the piece to calculate movement for
     * @return a list of valid moves for the given piece
     */
    private List<String> calculateGeneralPieceMovement(Piece piece) {
        piece.setPossibleMoves();
        List<String> moveList = piece.getPossibleMoves();
        List<String> validMoves = new ArrayList<>();
        for (String move : moveList) {
            String[] splitNotation = splitMoveNotation(move);
            String notationCoords = splitNotation[0];
            if (board.containsKey(notationCoords)) {
                Piece pieceAtCoords = board.get(notationCoords);
                if (pieceAtCoords.getTeam() != piece.getTeam()) {
                    if (piece.getClass() == Pawn.class && move.contains("x")) {
                        validMoves.add(move);
                    } else if (piece.getClass() != Pawn.class) {
                        // if there is a piece at that space but the pieces are enemies, 
                        // add an 'x' to the move notation
                        validMoves.add(addXToNotation(move));
                    }
                }
            } else {
                // if there is no piece on the given space the piece can move there.
                // Additionally, pawns can capture diagonally, but should not be able to 
                // capture if there is not a piece at the diagonal location 
                // unless it is the En Passant space.
                if (!move.contains("x") || notationCoords.equals(enPassantCoords)) {
                    validMoves.add(move);
                }
            }

            // Check to see if the move lands on one of the castling spaces of the enemy team
            // If it does, mark that castling is not possible 
            int[] xy_pos = ChesselUtils.convertNotationCoordToXYCoord(notationCoords);
            int x_pos = xy_pos[0];
            int y_pos = xy_pos[1];

            if (validMoves.contains(move)) {
                if (piece.getTeam() == 'w' && y_pos == 8) {
                    if (x_pos >= 2 && x_pos <= 4) {
                        blackCanCastleQueenSide = false;
                    } else if (x_pos >= 6 && x_pos <= 7) {
                        blackCanCastleKingSide = false;
                    }
                } else if (piece.getTeam() == 'b' && y_pos == 1) {
                    if (x_pos >= 2 && x_pos <= 4) {
                        whiteCanCastleQueenSide = false;
                    } else if (x_pos >= 6 && x_pos <= 7) {
                        whiteCanCastleKingSide = false;
                    }
                }
            }
        }
        return validMoves;
    }

    private void identifyDuplicateMoves() {
        HashSet<String> duplicatecheck = new HashSet<>();
        List<String>duplicateMoves = new ArrayList<>();
        for (String move : possibleMoves) {
            if (!duplicatecheck.add(move)) {
                duplicateMoves.add(move);
            }
        }

        for (String move : duplicateMoves) {
            String[] splitNotation = splitMoveNotation(move);
            String notationCoords = splitNotation[0];
            String pieceNotation = splitNotation[1];
            List<Piece> validPieces = new ArrayList<>();
            for (Map.Entry<String, Piece> entry : board.entrySet()) {
                Piece piece = entry.getValue();
                if (piece.getTeam() == turn &&
                    piece.getNotation().equals(pieceNotation) &&
                    piece.getPossibleMoves().contains(pieceNotation + notationCoords)) {
                    validPieces.add(piece);
                }
            }

            if (validPieces.size() == 2) {
                Piece piece0 = validPieces.get(0);
                Piece piece1 = validPieces.get(1);
                if (piece0.getX_pos() != piece1.getX_pos()) {
                    addSpecialIdentifiers(move, validPieces, true, false);
                } else if (piece0.getY_pos() != piece1.getY_pos()) {
                    addSpecialIdentifiers(move, validPieces, false, true);
                }
            } else if (validPieces.size() > 2) {
                boolean useX = true;
                boolean useY = true;
                for (int i = validPieces.size() - 1; i > 0; i--) {
                    for (int j = 0; j < i; j++) {
                        if (validPieces.get(i).getX_pos() == validPieces.get(j).getX_pos()) {
                            useX = false;
                        }
                        if (validPieces.get(i).getY_pos() == validPieces.get(j).getY_pos()) {
                            useY = false;
                        }
                    }
                }
                if (useX) {
                    addSpecialIdentifiers(move, validPieces, true, false);
                } else if (useY) {
                    addSpecialIdentifiers(move, validPieces, false, true);
                } else {
                    addSpecialIdentifiers(move, validPieces, true, true);
                }
            }
        }
    }

    /**
     * When capturing another piece, an 'x' is put in the 
     * notation to show that a piece was captured
     * 
     * @param move is the move we need to add 'x' to
     * @return the given move with 'x' added in the appropriate place
     * in the notation
     */
    private String addXToNotation(String move) {
        if (move.contains("x")) return move;
        return move.charAt(0) + "x" + move.substring(1);
    }

    private void addSpecialIdentifiers(String move, List<Piece> pieces, boolean useX, boolean useY) {
        int pieceIndex = 0;
        while(possibleMoves.contains(move)) {
            int index = possibleMoves.indexOf(move); // returns the lowest index that contains the given move
            String currentNotation = possibleMoves.get(index);
            String newMoveNotation = "" + currentNotation.charAt(0);
            if (useX) {
                char x_char = ChesselUtils.convertIntToAlphabetChar(pieces.get(pieceIndex).getX_pos());
                newMoveNotation += x_char;
            }
            if (useY) {
                char y_char = ChesselUtils.convertIntToNumericalChar(pieces.get(pieceIndex).getY_pos());
                newMoveNotation += y_char;
            }
            newMoveNotation += currentNotation.substring(1);
            possibleMoves.set(index, newMoveNotation);
            pieceIndex++;
        }
    }

    /**
     * Set the flags indicating whether white or black are in check
     */
    private void setCheckFlags() {
        inCheckWhite = whiteIsInCheck();
        inCheckBlack = blackIsInCheck();
    }

    /**
     * This method looks to see if the white king is in check.
     * 
     * @return boolean. True if white king is in check.
     */
    private boolean whiteIsInCheck() {
        return kingIsInCheck('w', whiteKingCoords);
    }

    /**
     * This method looks to see if the black king is in check.
     * 
     * @return boolean. True if black king is in check.
     */
    private boolean blackIsInCheck() {
        return kingIsInCheck('b', blackKingCoords);
    }

    /**
     * This method looks to see if a given king is in check.
     * 
     * @param team is the team of the king to check for.
     * @param kingCoords is the location of the king to check for.
     * @return true if the given king is in check.
     */
    private boolean kingIsInCheck(char team, String kingCoords) {
        char enemyTeam = 'w';
        if (team == 'w') enemyTeam = 'b';
        for (Map.Entry<String, Piece> entry : board.entrySet()) {
            Piece piece = entry.getValue();
            if (piece.getTeam() == enemyTeam) {
                for (String move : piece.getPossibleMoves()) {
                    String notationCoords = splitMoveNotation(move)[0];
                    if (notationCoords.equals(kingCoords)) {
                        if (piece.getClass() != Pawn.class || move.contains("x")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Filters out all invalid moves from a given list
     * 
     * @param moveList is a list of moves to filter
     * @return a list with all invalid moves filtered out
     * @throws InternalApplicationException when the copyMap() method fails to clone a piece properly
     */
    private List<String> removeStillInCheckMoves(List<String> moveList) throws InternalApplicationException {
        Map<String, Piece> boardCopy = copyMap(board);
        List<String> validMoves = new ArrayList<>();
        boolean inCheckWhiteTemp = inCheckWhite;
        boolean inCheckBlackTemp = inCheckBlack;
        boolean whiteCanCastleKingSideTemp = whiteCanCastleKingSide;
        boolean whiteCanCastleQueenSideTemp = whiteCanCastleQueenSide;
        boolean blackCanCastleQueenSideTemp = blackCanCastleQueenSide;
        boolean blackCanCastleKingSideTemp = blackCanCastleKingSide;
        String whiteKingCoordsTemp = whiteKingCoords;
        String blackKingCoordsTemp = blackKingCoords;
        String enPassantCoordsTemp = enPassantCoords;
        char team = turn;

        for (String move : moveList) {
            try {
                movePiece(move);
                for (Map.Entry<String, Piece> entry : board.entrySet()) {
                    // it is still necessary to calculate moves even for the enemy team so that proper flags can be set
                    Piece piece = entry.getValue();
                    List<String> temporaryMoveList = calculateGeneralPieceMovement(piece);
                    if (piece.getNotation() == "K") {
                        if (piece.getTeam() == 'w') {
                            whiteKingCoords = piece.getNotationCoords();
                        } else {
                            blackKingCoords = piece.getNotationCoords();
                        }
                    }
                    if (piece.getTeam() == turn) {
                        possibleMoves.addAll(temporaryMoveList);
                    }
                }

                setCheckFlags();
                if (team == 'w') {
                    if (!whiteInCheck()) validMoves.add(move);
                } else {
                    if (!blackInCheck()) validMoves.add(move);
                }
                
                board = copyMap(boardCopy);
                inCheckWhite = inCheckWhiteTemp;
                inCheckBlack = inCheckBlackTemp;
                whiteCanCastleKingSide = whiteCanCastleKingSideTemp;
                whiteCanCastleQueenSide = whiteCanCastleQueenSideTemp;
                blackCanCastleQueenSide = blackCanCastleQueenSideTemp;
                blackCanCastleKingSide = blackCanCastleKingSideTemp;
                whiteKingCoords = whiteKingCoordsTemp;
                blackKingCoords = blackKingCoordsTemp;
                enPassantCoords = enPassantCoordsTemp;
            } catch (InvalidMoveException e) {
                // TODO: Add exception handling
            }
        }

        return validMoves;
    }

    /**
     * Clones a given map of type Map<String, Piece>
     * 
     * @param original is the map to copy
     * @return a clone of the original map
     * @throws InternalApplicationException when a piece fails to clone properly
     */
    private Map<String, Piece> copyMap(Map<String, Piece> original) throws InternalApplicationException {
        Map<String, Piece> copy = new HashMap<>();
        for (Entry<String, Piece> entry : original.entrySet()) {
            try {
                copy.put(entry.getKey(), entry.getValue().clone());
            } catch (CloneNotSupportedException e) {
                throw new InternalApplicationException("Failure to copy current board");
            }
        }

        return copy;
    }

    /**
     * Creates a list of castling options for the current turn
     * 
     * @return a list of castling options for the current turn
     */
    private List<String> getCastling() {
        List<String> castlingOptions = new ArrayList<>();

        boolean kingCanCastle = false;
        boolean rookACanCastle = false;
        boolean rookHCanCastle = false;
        boolean queenSideClear = false;
        boolean kingSideClear = false;
        boolean notInCheck = false;
        
        int rowToCheck = 1;
        if (turnBlack()) rowToCheck = 8;

        if (board.containsKey("e" + rowToCheck)) kingCanCastle = !board.get("e" + rowToCheck).hasMoved();
        if (board.containsKey("a" + rowToCheck)) rookACanCastle = !board.get("a" + rowToCheck).hasMoved();
        if (board.containsKey("h" + rowToCheck)) rookHCanCastle = !board.get("h" + rowToCheck).hasMoved();
        queenSideClear = !(board.containsKey("d" + rowToCheck) || board.containsKey("c" + rowToCheck) || board.containsKey("b" + rowToCheck)) && safeToCastle("QUEEN");
        kingSideClear = !(board.containsKey("f" + rowToCheck) || board.containsKey("g" + rowToCheck)) && safeToCastle("KING");

        if (turnWhite()) {
            notInCheck = !inCheckWhite;
        } else {
            notInCheck = !inCheckBlack;
        }

        if (kingCanCastle && notInCheck) {
            if (rookACanCastle && queenSideClear) castlingOptions.add("O-O-O");
            if (rookHCanCastle && kingSideClear) castlingOptions.add("O-O");
        }

        return castlingOptions;
    }

    /**
     * Determines whether or not a castling side is valid
     *
     * @param castlingSide is the side to check for (KING or QUEEN)
     * @return whether or not that side is safe to castle on (true means it is safe)
     */
    private boolean safeToCastle(String castlingSide) {

        if (castlingSide == "QUEEN") {
            if (turnWhite()) {
                return whiteCanCastleQueenSide;
            } else {
                return blackCanCastleQueenSide;
            }
        } else {
            if (turnWhite()) {
                return whiteCanCastleKingSide;
            } else {
                return blackCanCastleKingSide;
            }
        }
    }

    /**
     * Takes a move as a parameter and separates the coordinates where the 
     * piece will be moved to from the letter that signifies a given piece,
     * returning them as a String array.
     * 
     * @param move is the move to be made
     * @return a string array with the notationCoords at index 0, the pieceNotation at index 1
     * and the  specialIdentifier at 2
     */
    private String[] splitMoveNotation(String move) {
        String notationCoords = "";
        String pieceNotation = "";
        String specialIdentifier = "";

        for (int i = move.length() - 1; i >= 0; i--) {
            char currentChar = move.charAt(i);
            // 1 -> 49, 8 -> 56
            if (currentChar >= 49 && currentChar <= 56) {
                char previousChar = move.charAt(i - 1);
                // a -> 97, h -> 104
                if (previousChar >= 97 && previousChar <= 104) {
                    notationCoords = previousChar + "" + currentChar;
                    pieceNotation = "" + move.charAt(0);
                    if (move.length() > 3) {
                        specialIdentifier = move.substring(1, i - 1);
                        if (specialIdentifier.endsWith("x")) {
                            if (specialIdentifier.length() > 1) {
                                specialIdentifier = specialIdentifier.substring(0, specialIdentifier.charAt(specialIdentifier.length() - 1));
                            } else {
                                specialIdentifier = "";
                            }
                        }
                    }
                }
            }
        }
        return new String[] {notationCoords, pieceNotation, specialIdentifier};
    }

    @Override
    public void makeMove(String inputMove) throws InvalidMoveException, InternalApplicationException {
        if (!possibleMoves.contains(inputMove)) throw new InvalidMoveException("The move " + inputMove + " is not a valid move.");

        System.out.println("Special Identifier: " + splitMoveNotation(inputMove)[2]);

        movePiece(inputMove);
        toggleTurn();
        setPossibleMoves();
    }

    private void movePiece(String move) throws InternalApplicationException, InvalidMoveException {

        // if this is a castling move it has to be handled differently than other moves
        if (move.charAt(0) == 'O') {
            Piece king = null;
            Piece rook = null;
            int row;
            if (turnWhite()) {
                row = 1;
            } else {
                row = 8;
            }
            king = board.get("e" + row);
            if (move.equals("O-O")) {
                rook = board.get("h" + row);

                placePiece(king, "g" + row);
                placePiece(rook, "f" + row);
            } else if (move.equals("O-O-O")) {
                rook = board.get("a" + row);

                placePiece(king, "c" + row);
                placePiece(rook, "d" + row);
            } else {
                throw new InternalApplicationException("Unable to castle due to an internal error");
            }
            enPassantCoords = "";
            return;
        }

        String[] splitNotation = splitMoveNotation(move);
        String notationCoords = splitNotation[0];
        Piece pieceToMove = null;

        for (Piece piece : board.values()) {
            if (validPiece(piece, move)) {
                if (piece.getPossibleMoves().contains(piece.getNotation() + notationCoords)) {
                    pieceToMove = piece;
                    enPassantCoords = "";
                    break;
                } else if (piece.getClass() == Pawn.class && piece.getPossibleMoves().contains(move)) {
                    pieceToMove = piece;

                    // special En Passant and Promotion considerations
                    int[] xy_pos = ChesselUtils.convertNotationCoordToXYCoord(notationCoords);
                    int x_pos = xy_pos[0];
                    int y_pos = xy_pos[1];

                    // if a pawn captures on the En Passant coords then the pawn that jumped 2 spaces must be captured
                    // move.contains("x") is probably unnecessary but it adds a safety check to the if statement
                    if (notationCoords.equals(enPassantCoords)) {
                        if (turnWhite()) {
                            board.remove(ChesselUtils.convertXYPosToNotation(x_pos, y_pos - 1));
                        } else {
                            board.remove(ChesselUtils.convertXYPosToNotation(x_pos, y_pos + 1));
                        }
                    }

                    // reset En Passant coords and then check if their are new coords to add
                    enPassantCoords = "";

                    // set En Passant coordinates if a pawn moves two spaces
                    if (!piece.hasMoved()) {
                        if (y_pos == piece.getY_pos() + 2) {
                            enPassantCoords = ChesselUtils.convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() + 1);
                        } else if (y_pos == piece.getY_pos() - 2) {
                            enPassantCoords = ChesselUtils.convertXYPosToNotation(piece.getX_pos(), piece.getY_pos() - 1);
                        }
                    }
                } else if (piece.getPossibleMoves().contains(piece.getNotation() + "x" + notationCoords)) {
                    pieceToMove = piece;
                    enPassantCoords = "";

                    break;
                }
            }
        }

        if (pieceToMove == null) throw new InternalApplicationException("Unable to make move " + move + " due to an internal error");
        placePiece(pieceToMove, notationCoords);
        if (pieceToMove.getClass() == Pawn.class &&
            (pieceToMove.getY_pos() == 8 || pieceToMove.getY_pos() == 1)) {
                switch (move.charAt(move.length() - 1)) {
                    case 'Q':
                        board.put(pieceToMove.getNotationCoords(), new Queen(pieceToMove.getTeam(), pieceToMove.getX_pos(), pieceToMove.getY_pos()));
                        break;
                    case 'R':
                    board.put(pieceToMove.getNotationCoords(), new Rook(pieceToMove.getTeam(), pieceToMove.getX_pos(), pieceToMove.getY_pos()));
                        break;
                    case 'B':
                        board.put(pieceToMove.getNotationCoords(), new Bishop(pieceToMove.getTeam(), pieceToMove.getX_pos(), pieceToMove.getY_pos()));
                        break;
                    case 'N':
                    board.put(pieceToMove.getNotationCoords(), new Knight(pieceToMove.getTeam(), pieceToMove.getX_pos(), pieceToMove.getY_pos()));
                        break;
                }
        }
    }

    private void placePiece(Piece piece, String newCoords) throws InvalidMoveException {
        int[] xy_pos = ChesselUtils.convertNotationCoordToXYCoord(newCoords);
        board.remove(piece.getNotationCoords());
        piece.movePiece(xy_pos[0], xy_pos[1]);
        board.put(newCoords, piece);
    }

    private boolean validPiece(Piece piece, String move) {
        boolean valid = true;
        String[] splitNotation = splitMoveNotation(move);
        String pieceNotation = splitNotation[1];
        String specialIdentifier = splitNotation[2];

        valid = valid && piece.getTeam() == turn && pieceNotation.equals(piece.getNotation());
        switch(specialIdentifier.length()) {
            case 1:
                String pieceLocation = piece.getNotationCoords();
                valid = valid && (pieceLocation.charAt(0) == specialIdentifier.charAt(0) || pieceLocation.charAt(1) == specialIdentifier.charAt(0));
                break;
            case 2:
                valid = valid && piece.getNotationCoords() == specialIdentifier;
                break;
        }

        return valid;
    }

    @Override
    public void printCurrentBoard() {
        String boardPrint = ANSI_ORANGE + "       ----==| CHESSEL |==----" + ANSI_RESET + "\n\n";
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
                    switch (piece.getNotation()) {
                        case "K":
                        case "Q":
                        case "B":
                        case "N":
                        case "R":
                            pieceNotation = piece.getNotation();
                            break;
                        default:
                            pieceNotation = "P";
                    }

                    if (piece.getTeam() == 'w') {
                        if (piece.getNotation() == "K" && whiteInCheck()) {
                            pieceNotation = ANSI_CYAN + pieceNotation + ANSI_RESET;
                        } else {
                            pieceNotation = ANSI_BLUE + pieceNotation + ANSI_RESET;
                        }
                    } else {
                        if (piece.getNotation() == "K" && blackInCheck()) {
                            pieceNotation = ANSI_ORANGE + pieceNotation + ANSI_RESET;
                        } else {
                            pieceNotation = ANSI_RED + pieceNotation + ANSI_RESET;
                        }
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
        if (turnWhite()) {
            setTurn('b');
        } else {
            setTurn('w');
        }
    }

    private boolean whiteInCheck() {
        return inCheckWhite;
    }

    private boolean blackInCheck() {
        return inCheckBlack;
    }

    /**
     * Check to see if it is white's turn
     * 
     * @return true if it is currently white's turn
     */
    private boolean turnWhite() {
        return turn == 'w';
    }

    /**
     * Check to see if it is black's turn
     * 
     * @return true if it is currently black's turn
     */
    private boolean turnBlack() {
        return turn == 'b';
    }

}
