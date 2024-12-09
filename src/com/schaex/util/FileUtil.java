package com.schaex.util;

import com.schaex.arrays.ArrayUtil;
import com.schaex.days.DaysUtil;

import java.io.*;
import java.util.stream.Stream;

public final class FileUtil {
    private FileUtil() {}

    public static <T> T transformFileLines(int day, Transformer<Stream<String>, T> transformer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(DaysUtil.resource(day)))) {
            return transformer.transform(reader.lines());
        }
    }

    public static int[][] getIntTableFromFile(int day, String delimiter) throws IOException {
        return transformFileLines(day, stream ->
                stream.map(s -> s.split(delimiter))
                        .map(ArrayUtil::intArrayFromStrings)
                        .toArray(int[][]::new));
    }

    public static <T> T transformFileInputStream(int day, Transformer<FileInputStream, T> transformer) throws IOException {
        try (FileInputStream in = new FileInputStream(DaysUtil.resource(day))) {
            return transformer.transform(in);
        }
    }

    public static String readEntireFile(int day) throws IOException {
        return transformFileInputStream(day, in -> new String(in.readAllBytes()));
    }

    @FunctionalInterface
    public interface Transformer<I, O> {
        O transform(I input) throws IOException;
    }
}
