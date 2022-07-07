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

        int x_pos = (int) (xChar - 96);
        int y_pos = (int) (yChar - 48);

        return new int[] {x_pos, y_pos};
    }

    public static String convertXYPosToNotation(int x_pos, int y_pos) {
        char xChar = (char) (x_pos + 96);
        char yChar = (char) (y_pos + 48);
        
        return "" + xChar + yChar;
    }
}
