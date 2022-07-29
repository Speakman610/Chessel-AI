package test.boardState;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import main.ChesselUtils;
import main.boardState.BoardState;

public class BoardStateTest {
    BoardState boardState = BoardState.getBoardState();

    /* Things to test:
        √ List<String> getPossibleMoves();
        √ boolean makeMove(String notation);
        √ boolean gameEnded();
        √ char getTurn();
        √ Map<String, String> getStringBoard();
        ? String[][] getBoardAs2DArray();
        √ boolean whiteIsInCheck();
        √ boolean blackIsInCheck();

        √ En Passant
        √ Invalid movement
        √ Castling Queenside
        √ Castling Kingside
        √ Trying to castle through check
        √ Trying to castle through other pieces
        √ Capturing an enemy piece
        √ Capturing an allied piece
        √ Pawn capturing a piece
        √ Knight capturing a piece
        √ Check
        √ Checkmate White
        √ Checkmate Black
        √ Stalemate
     */

    @BeforeEach
    void setup() {
        boardState.resetBoard();
    }

    @Test
    @DisplayName("Board should be in intitial board state at start")
    void when_justStartedUp_should_beInInitialBoardState() {
        Assertions.assertFalse(boardState.gameEnded());
        Assertions.assertEquals('w', boardState.getTurn());
        Assertions.assertFalse(boardState.whiteIsInCheck());
        Assertions.assertFalse(boardState.blackIsInCheck());

        String[] movesAtStart = {"h3", "h4", "f3", "f4", "d3", "d4", "Nc3", "Na3", "b3", "b4", "Nh3", "Nf3", "g3", "g4", "e3", "e4", "c3", "c4", "a3", "a4"};
        List<String> expectedMoves = new ArrayList<>();
        for (String move : movesAtStart) {
            expectedMoves.add(move);
        }
        Assertions.assertEquals(expectedMoves, boardState.getPossibleMoves());

        Map<String, String> stringBoard = boardState.getStringBoard();
        Assertions.assertEquals("wR", stringBoard.get("a1"));
        Assertions.assertEquals("wN", stringBoard.get("b1"));
        Assertions.assertEquals("wB", stringBoard.get("c1"));
        Assertions.assertEquals("wQ", stringBoard.get("d1"));
        Assertions.assertEquals("wK", stringBoard.get("e1"));
        Assertions.assertEquals("wB", stringBoard.get("f1"));
        Assertions.assertEquals("wN", stringBoard.get("g1"));
        Assertions.assertEquals("wR", stringBoard.get("h1"));
        for (int i = 1; i <= 8; i++) {
            Assertions.assertEquals("wP", stringBoard.get((char) (i + 96) + "2"), "Expecting white pawn at " + (char) (i + 96) + "2");
        }

        Assertions.assertEquals("bR", stringBoard.get("a8"));
        Assertions.assertEquals("bN", stringBoard.get("b8"));
        Assertions.assertEquals("bB", stringBoard.get("c8"));
        Assertions.assertEquals("bQ", stringBoard.get("d8"));
        Assertions.assertEquals("bK", stringBoard.get("e8"));
        Assertions.assertEquals("bB", stringBoard.get("f8"));
        Assertions.assertEquals("bN", stringBoard.get("g8"));
        Assertions.assertEquals("bR", stringBoard.get("h8"));
        for (int i = 1; i <= 8; i++) {
            Assertions.assertEquals("bP", stringBoard.get((char) (i + 96) + "7"), "Expecting black pawn at " + (char) (i + 96) + "7");
        }

        for (int x = 1; x <= 8; x++) {
            for (int y = 3; y <= 6; y++) {
                Assertions.assertNull(stringBoard.get(ChesselUtils.convertXYPosToNotation(x, y)), "Expecting empty spaces to be null");
            }
        }
    }

    @Test
    @DisplayName("Board should swap turns after a move is made")
    void when_aMoveIsMade_should_swapTurns() {
        Assertions.assertEquals('w', boardState.getTurn());
        Assertions.assertTrue(boardState.makeMove("e4"));
        Assertions.assertEquals('b', boardState.getTurn());
        Assertions.assertTrue(boardState.makeMove("e5"));
        Assertions.assertEquals('w', boardState.getTurn());
    }

    @Test
    @DisplayName("When a piece is moved then it should be found at the new location on the board")
    void when_aPieceIsMoved_should_beAtNewLocationNotOldLocation() {
        String[][] boardAs2DArray = boardState.getBoardAs2DArray();
        Assertions.assertEquals("wN", boardAs2DArray[6][0]); // Knight at g1
        Assertions.assertTrue(boardState.makeMove("Nf3"));
        boardAs2DArray = boardState.getBoardAs2DArray();
        Assertions.assertEquals("wN", boardAs2DArray[5][2]); // Should now be at f3
        Assertions.assertEquals("", boardAs2DArray[6][0]); // There shouldn't be a piece at g1
    }

    @Test
    @DisplayName("Stalemate: Only Knights/Bishops left and not enough pieces")
    void when_thereAreNotPiecesThatCanCheckmateRemaining_should_endGame() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("g4", "wK");
        customBoard.put("c2", "wB");
        customBoard.put("f6", "bK");
        customBoard.put("d5", "bN");
        boardState.setCustomBoard(customBoard, 'w');
        Assertions.assertTrue(boardState.gameEnded());
    }

    @Test
    @DisplayName("Stalemate: No moves are possible but not checkmate")
    void when_thereAreNoPossibleMovesButNotInCheckmate_should_beStalemateAndEndGame() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("h2", "wK");
        customBoard.put("f2", "bK");
        customBoard.put("g2", "bB");
        customBoard.put("g8", "bR");

        boardState.setCustomBoard(customBoard, 'w');
        Assertions.assertEquals(0, boardState.getPossibleMoves().size());
        Assertions.assertTrue(boardState.gameEnded());
        Assertions.assertFalse(boardState.whiteIsInCheck());
        Assertions.assertFalse(boardState.blackIsInCheck());
    }


    @Test
    @DisplayName("If king is in checkmate the game ends and no moves are possible")
    void when_kingInCheckmate_should_endGame() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("h1", "wK");
        customBoard.put("f2", "bK");
        customBoard.put("h5", "bR");
        boardState.setCustomBoard(customBoard, 'w');
        Assertions.assertTrue(boardState.gameEnded());
        Assertions.assertTrue(boardState.whiteIsInCheck());
        Assertions.assertEquals(0, boardState.getPossibleMoves().size());
    }

    @Test
    @DisplayName("Moves that put the king in check should not be possible")
    void when_movePutsKingInCheck_should_notAllowThatMove() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("e1", "wK");
        customBoard.put("f1", "wN");
        customBoard.put("e2", "wN");
        customBoard.put("g2", "bK");
        customBoard.put("e3", "bQ");
        customBoard.put("h1", "bR");
        boardState.setCustomBoard(customBoard, 'w');
        Assertions.assertEquals(1, boardState.getPossibleMoves().size());
        Assertions.assertEquals("Kd1", boardState.getPossibleMoves().get(0));
    }

    @Test
    @DisplayName("Every move listed as a possible move should be possible")
    void when_makingAMoveInPossibleMoveList_should_successfullyMakeTheMove() {
        List<String> possibleMoves = boardState.getPossibleMoves();
        for (String move : possibleMoves) {
            Assertions.assertTrue(boardState.makeMove(move), "Making move (part 1): " + move);
            boardState.resetBoard();
        }

        Assertions.assertTrue(boardState.makeMove("e4"));
        Assertions.assertTrue(boardState.makeMove("c5"));
        Assertions.assertTrue(boardState.makeMove("d3"));
        Assertions.assertTrue(boardState.makeMove("Nc6"));
        Assertions.assertTrue(boardState.makeMove("g3"));
        Assertions.assertTrue(boardState.makeMove("e5"));
        Assertions.assertTrue(boardState.makeMove("Bg2"));
        Assertions.assertTrue(boardState.makeMove("d6"));
        Assertions.assertTrue(boardState.makeMove("Qf3"));
        Assertions.assertTrue(boardState.makeMove("Nf6"));
        Assertions.assertTrue(boardState.makeMove("Ne2"));

        possibleMoves = boardState.getPossibleMoves();
        Map<String, String> savedBoard = boardState.getStringBoard();
        char savedTurn = boardState.getTurn();
        for (String move : possibleMoves) {
            Assertions.assertTrue(boardState.makeMove(move), "Making move (part 2): " + move);
            boardState.setCustomBoard(savedBoard, savedTurn);
        }
    }

    @Test
    @DisplayName("Invalid inputs won't change the board state")
    void when_enteringInvalidMoves_should_notChangeTheBoard() {
        List<String> possibleMoves = boardState.getPossibleMoves();
        Map<String, String> savedBoard = boardState.getStringBoard();

        Assertions.assertFalse(possibleMoves.contains("ae7"));
        Assertions.assertFalse(boardState.makeMove("ae7"));
        Assertions.assertEquals(savedBoard, boardState.getStringBoard());

        Assertions.assertFalse(possibleMoves.contains("d8"));
        Assertions.assertFalse(boardState.makeMove("d8"));
        Assertions.assertEquals(savedBoard, boardState.getStringBoard());

        Assertions.assertFalse(possibleMoves.contains("\ne4"));
        Assertions.assertFalse(boardState.makeMove("\ne4"));
        Assertions.assertEquals(savedBoard, boardState.getStringBoard());

        Assertions.assertFalse(possibleMoves.contains("exd3"));
        Assertions.assertFalse(boardState.makeMove("exd3"));
        Assertions.assertEquals(savedBoard, boardState.getStringBoard());

        Assertions.assertFalse(possibleMoves.contains("O-O"));
        Assertions.assertFalse(boardState.makeMove("O-O"));
        Assertions.assertEquals(savedBoard, boardState.getStringBoard());

        Assertions.assertFalse(possibleMoves.contains("aasfdjkl"));
        Assertions.assertFalse(boardState.makeMove("aasfdjkl"));
        Assertions.assertEquals(savedBoard, boardState.getStringBoard());
    }

    @Test
    @DisplayName("The getStringBoard method should give the proper locations of the pieces")
    void when_callingGetStringBoard_should_accuratelyRepresentBoard() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("e1", "wK");
        customBoard.put("f1", "wN");
        customBoard.put("e2", "wN");
        customBoard.put("g2", "bK");
        customBoard.put("e3", "bQ");
        customBoard.put("h1", "bR");
        boardState.setCustomBoard(customBoard, 'w');

        Assertions.assertEquals(customBoard, boardState.getStringBoard());

        // Make a move
        Assertions.assertTrue(boardState.makeMove("Kd1"));

        // Simulate the move on our map
        customBoard.put("d1", "wK");
        customBoard.remove("e1");

        Assertions.assertEquals(customBoard, boardState.getStringBoard());
    }

    @Test
    @DisplayName("When in-check methods are called should be accurate")
    void when_kingIsInCheck_should_showTrueOnInCheckMethods() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("f2", "wK");
        customBoard.put("f4", "bK");
        customBoard.put("d4", "bQ");

        boardState.setCustomBoard(customBoard, 'w');
        Assertions.assertTrue(boardState.whiteIsInCheck());
        Assertions.assertFalse(boardState.blackIsInCheck());

        customBoard = new HashMap<>();
        customBoard.put("f2", "wK");
        customBoard.put("f4", "bK");
        customBoard.put("d4", "wQ");

        boardState.setCustomBoard(customBoard, 'b');
        Assertions.assertFalse(boardState.whiteIsInCheck());
        Assertions.assertTrue(boardState.blackIsInCheck());

        customBoard = new HashMap<>();
        customBoard.put("f2", "wK");
        customBoard.put("f4", "bK");

        boardState.setCustomBoard(customBoard, 'b');
        Assertions.assertFalse(boardState.whiteIsInCheck());
        Assertions.assertFalse(boardState.blackIsInCheck());
    }

    @Test
    @DisplayName("Able to castle queen-side if requirements are met")
    void when_castlingQueenSideIsPossible_should_beAbleToCastleSuccessfully() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("a1", "wR");
        customBoard.put("e1", "wK");
        customBoard.put("e8", "bK");

        boardState.setCustomBoard(customBoard, 'w');
        Assertions.assertTrue(boardState.getPossibleMoves().contains("O-O-O"));
        Assertions.assertTrue(boardState.makeMove("O-O-O"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("wK", currentBoard.get("c1"));
        Assertions.assertEquals("wR", currentBoard.get("d1"));
        Assertions.assertNull(currentBoard.get("e1"));
        Assertions.assertNull(currentBoard.get("a1"));
    }

    @Test
    @DisplayName("Able to castle king-side if requirements are met")
    void when_castlingKingSideIsPossible_should_beAbleToCastleSuccessfully() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("h8", "bR");
        customBoard.put("e1", "wK");
        customBoard.put("e8", "bK");

        boardState.setCustomBoard(customBoard, 'b');
        Assertions.assertTrue(boardState.getPossibleMoves().contains("O-O"));
        Assertions.assertTrue(boardState.makeMove("O-O"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("bK", currentBoard.get("g8"));
        Assertions.assertEquals("bR", currentBoard.get("f8"));
        Assertions.assertNull(currentBoard.get("e8"));
        Assertions.assertNull(currentBoard.get("h8"));
    }

    @Test
    @DisplayName("Cannot castle through pieces")
    void when_aPieceIsInTheWay_should_notBeAbleToCastle() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("h8", "bR");
        customBoard.put("e1", "wK");
        customBoard.put("e8", "bK");
        customBoard.put("g8", "bN");

        boardState.setCustomBoard(customBoard, 'b');
        Assertions.assertFalse(boardState.getPossibleMoves().contains("O-O"));
        Assertions.assertFalse(boardState.makeMove("O-O"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("bK", currentBoard.get("e8"));
        Assertions.assertEquals("bR", currentBoard.get("h8"));
        Assertions.assertEquals("bN", currentBoard.get("g8"));
        Assertions.assertNull(currentBoard.get("f8"));
    }

    @Test
    @DisplayName("Cannot castle through check")
    void when_kingWouldPassThroughCheck_should_notBeAbleToCastle() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("e1", "wK");
        customBoard.put("d5", "wQ");
        customBoard.put("e8", "bK");
        customBoard.put("h8", "bR");
        customBoard.put("a8", "bR");

        boardState.setCustomBoard(customBoard, 'b');
        Assertions.assertFalse(boardState.getPossibleMoves().contains("O-O"));
        Assertions.assertFalse(boardState.makeMove("O-O"));
        Assertions.assertFalse(boardState.getPossibleMoves().contains("O-O-O"));
        Assertions.assertFalse(boardState.makeMove("O-O-O"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("bK", currentBoard.get("e8"));
        Assertions.assertEquals("bR", currentBoard.get("h8"));
        Assertions.assertEquals("bR", currentBoard.get("a8"));
        Assertions.assertNull(currentBoard.get("b8"));
        Assertions.assertNull(currentBoard.get("c8"));
        Assertions.assertNull(currentBoard.get("d8"));
        Assertions.assertNull(currentBoard.get("f8"));
        Assertions.assertNull(currentBoard.get("g8"));
    }

    @Test
    @DisplayName("Pieces are able to capture enemy pieces when it's valid")
    void when_ableToCaptureAnEnemyPiece_should_returnTrueOnMakeMove() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("e1", "wK");
        customBoard.put("b2", "wQ");
        customBoard.put("c3", "bP");
        customBoard.put("e8", "bK");

        boardState.setCustomBoard(customBoard, 'w');
        Assertions.assertTrue(boardState.getPossibleMoves().contains("Qxc3"));
        Assertions.assertTrue(boardState.makeMove("Qxc3"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("wQ", currentBoard.get("c3"));
        Assertions.assertNull(currentBoard.get("b2"));
    }

    @Test
    @DisplayName("Pieces cannot capture allied pieces")
    void when_tryingToCaptureAnAlliedPiece_should_failToMakeMove() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("e1", "wK");
        customBoard.put("b2", "wQ");
        customBoard.put("c3", "wP");
        customBoard.put("e8", "bK");

        boardState.setCustomBoard(customBoard, 'w');
        Assertions.assertFalse(boardState.getPossibleMoves().contains("Qxc3"));
        Assertions.assertFalse(boardState.makeMove("Qxc3"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("wQ", currentBoard.get("b2"));
        Assertions.assertEquals("wP", currentBoard.get("c3"));
    }

    @Test
    @DisplayName("Knights are able to capture enemy pieces when it's valid")
    void when_knightIsAbleToCaptureAnEnemyPiece_should_returnTrueOnMakeMove() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("e1", "wK");
        customBoard.put("b2", "wQ");
        customBoard.put("c4", "bN");
        customBoard.put("e8", "bK");

        boardState.setCustomBoard(customBoard, 'b');
        Assertions.assertTrue(boardState.getPossibleMoves().contains("Nxb2"));
        Assertions.assertTrue(boardState.makeMove("Nxb2"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("bN", currentBoard.get("b2"));
        Assertions.assertNull(currentBoard.get("c4"));
    }

    @Test
    @DisplayName("Pawns are able to capture enemy pieces when it's valid")
    void when_pawnIsAbleToCaptureAnEnemyPiece_should_returnTrueOnMakeMove() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("e1", "wK");
        customBoard.put("b2", "wQ");
        customBoard.put("c3", "bP");
        customBoard.put("e8", "bK");

        boardState.setCustomBoard(customBoard, 'b');
        Assertions.assertTrue(boardState.getPossibleMoves().contains("cxb2"));
        Assertions.assertTrue(boardState.makeMove("cxb2"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("bP", currentBoard.get("b2"));
        Assertions.assertNull(currentBoard.get("c3"));
    }

    @Test
    @DisplayName("Pawns are able to capture enemy pieces en passant")
    void when_enPassantIsValid_should_allowPawnToCapture() {
        Map<String, String> customBoard = new HashMap<>();
        customBoard.put("e1", "wK");
        customBoard.put("e5", "wP");
        customBoard.put("f7", "bP");
        customBoard.put("e8", "bK");

        boardState.setCustomBoard(customBoard, 'b');
        Assertions.assertTrue(boardState.getPossibleMoves().contains("f5"));
        Assertions.assertTrue(boardState.makeMove("f5"));

        Map<String, String> currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("bP", currentBoard.get("f5"));
        Assertions.assertEquals("wP", currentBoard.get("e5"));

        Assertions.assertTrue(boardState.getPossibleMoves().contains("exf6"));
        Assertions.assertTrue(boardState.makeMove("exf6"));

        currentBoard = boardState.getStringBoard();
        Assertions.assertEquals("wP", currentBoard.get("f6"));
        Assertions.assertNull(currentBoard.get("f5"));
    }

    @Test
    @RepeatedTest(29) // Will run a total of 30 times
    @DisplayName("Simulate several games to find unexpected errors")
    void when_playingAGame_should_notFindAnyErrors() {
        List<String> possiblesMoves = boardState.getPossibleMoves();
        while (!boardState.gameEnded()) {
            SecureRandom random = new SecureRandom();
            String chosenMove = possiblesMoves.get(random.nextInt(possiblesMoves.size()));
            Assertions.assertTrue(boardState.makeMove(chosenMove), "Move made: " + chosenMove);
            possiblesMoves = boardState.getPossibleMoves();
        }
        boolean whiteWins = boardState.blackIsInCheck() && !boardState.whiteIsInCheck();
        boolean blackWins = boardState.whiteIsInCheck() && !boardState.blackIsInCheck();
        boolean stalemate = !boardState.whiteIsInCheck() && !boardState.blackIsInCheck();
        Assertions.assertTrue(whiteWins || blackWins || stalemate, "White wins: " + whiteWins + ", black wins: " + blackWins + ", stalemate: " + stalemate);
    }
}
