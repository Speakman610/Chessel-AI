package main.java.chessel;

import java.security.InvalidParameterException;

public class ChesselUtils {

    /**
     * Converts a location String into an integer array
     * 
     * @param notation that represents the location a piece will be moved without any extra notation
     * @return an int array containing the x coordinate at index 0 and the y coordinate at index 1
     */
    public static int[] convertNotationCoordToXYCoord(String notation) {
        if (notation.length() != 2) throw new InvalidParameterException("Cannot convert notation: Notation expected to be of length 2 but was actually of length " + notation.length());
        char xChar = notation.charAt(0);
        char yChar = notation.charAt(1);

        int x_pos = convertAlphabetCharToInt(xChar);
        int y_pos = convertNumericalCharToInt(yChar);

        return new int[] {x_pos, y_pos};
    }

    public static String convertXYPosToNotation(int x_pos, int y_pos) {
        char xChar = convertIntToAlphabetChar(x_pos);
        char yChar = convertIntToNumericalChar(y_pos);
        
        return "" + xChar + yChar;
    }

    public static char convertIntToAlphabetChar(int integer) {
        return convertIntToChar(integer, 96);
    }

    public static char convertIntToNumericalChar(int integer) {
        return convertIntToChar(integer, 48);
    }

    private static char convertIntToChar(int integer, int addValue) {
        return (char) (integer + addValue);
    }

    public static int convertAlphabetCharToInt(char character) {
        return convertCharToInt(character, 96);
    }

    public static int convertNumericalCharToInt(char character) {
        return convertCharToInt(character, 48);
    }

    private static int convertCharToInt(char character, int subValue) {
        return (int) (character - subValue);
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
    public static String[] splitMoveNotation(String move) {
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
                                specialIdentifier = specialIdentifier.substring(0, specialIdentifier.length() - 2);
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
}
