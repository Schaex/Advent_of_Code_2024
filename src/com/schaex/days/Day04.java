package com.schaex.days;

import com.schaex.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class Day04 {
    public static void main(String[] args) throws IOException {
        final File file = DaysUtil.resource("Day_04.txt");

        final char[][] matrix = FileUtil.transformFileContent(file, lines ->
                lines.map(String::toCharArray)
                        .toArray(char[][]::new));

        final int length = matrix.length;
        final int width = matrix[0].length;

        System.out.print("Part one: ");

        // 2496
        {
            int count = 0;

            for (int row = 0; row < length; row++) {
                for (int column = 0; column < width; column++) {
                    // horizontal
                    if (column < width - 3 && isXmas(matrix[row][column], matrix[row][column + 1], matrix[row][column + 2], matrix[row][column + 3]))
                        count++;

                    // vertical
                    if (row < length - 3 && isXmas(matrix[row][column], matrix[row + 1][column], matrix[row + 2][column], matrix[row + 3][column]))
                        count++;

                    // diagonal right
                    if (column < width - 3 && row < length - 3 &&
                            isXmas(matrix[row][column], matrix[row + 1][column + 1], matrix[row + 2][column + 2], matrix[row + 3][column + 3]))
                        count++;

                    // diagonal left
                    if (column > 2 && row < length - 3 &&
                            isXmas(matrix[row][column], matrix[row + 1][column - 1], matrix[row + 2][column - 2], matrix[row + 3][column - 3]))
                        count++;
                }
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        {
            int count = 0;

            for (int row = 1; row < length - 1; row++) {
                for (int column = 1; column < width - 1; column++) {
                    if (isCrossMas(matrix[row][column],
                            matrix[row - 1][column - 1], matrix[row + 1][column + 1],
                            matrix[row + 1][column - 1], matrix[row - 1][column + 1])) {
                        count++;
                    }
                }
            }

            System.out.println(count);
        }
    }

    private static boolean isXmas(char c1, char c2, char c3, char c4) {
        return (c1 == 'X' && c2 == 'M' && c3 == 'A' && c4 == 'S') ||
                (c4 == 'X' && c3 == 'M' && c2 == 'A' && c1 == 'S');
    }

    private static boolean isCrossMas(char center, char tl, char br, char bl, char tr) {
        return center == 'A' &&
                ((tl == 'M' && br == 'S') || (tl == 'S' && br == 'M')) &&
                ((bl == 'M' && tr == 'S') || (bl == 'S' && tr == 'M'));
    }
}
