package com.schaex.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public final class FileUtil {
    private FileUtil() {}

    public static <T> T transformFileContent(File file, Function<Stream<String>, T> transformer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return transformer.apply(reader.lines());
        }
    }

    public static int[][] getIntTableFromFile(File file, String delimiter) throws IOException {
        return transformFileContent(file, stream ->
                stream.map(s -> s.split(delimiter))
                        .map(strArray -> {
                            final int[] ints = new int[strArray.length];

                            for (int i = 0; i < ints.length; i++) {
                                ints[i] = Integer.parseInt(strArray[i]);
                            }

                            return ints;
                        })
                        .toArray(int[][]::new));
    }

    public static List<String> getLinesFromFile(File file) throws IOException {
        return transformFileContent(file, Stream::toList);
    }
}
