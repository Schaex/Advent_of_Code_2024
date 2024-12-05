package com.schaex.util;

import com.schaex.arrays.ArrayUtil;

import java.io.*;
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
                        .map(ArrayUtil::intArrayFromStrings)
                        .toArray(int[][]::new));
    }

    public static List<String> getLinesFromFile(File file) throws IOException {
        return transformFileContent(file, Stream::toList);
    }

    public static String readEntireFile(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            final byte[] bytes = in.readAllBytes();

            return new String(bytes);
        }
    }
}
