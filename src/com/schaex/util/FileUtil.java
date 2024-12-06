package com.schaex.util;

import com.schaex.arrays.ArrayUtil;
import com.schaex.days.DaysUtil;

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

    public static <T> T transformFileContent(int day, Function<Stream<String>, T> transformer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(DaysUtil.resource(day)))) {
            return transformer.apply(reader.lines());
        }
    }

    public static int[][] getIntTableFromFile(int day, String delimiter) throws IOException {
        return transformFileContent(day, stream ->
                stream.map(s -> s.split(delimiter))
                        .map(ArrayUtil::intArrayFromStrings)
                        .toArray(int[][]::new));
    }

    public static String readEntireFile(File file) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            final byte[] bytes = in.readAllBytes();

            return new String(bytes);
        }
    }
}
