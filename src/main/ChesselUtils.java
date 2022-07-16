package main;

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
}
