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

    public static <T> T getLinesFromFile(File file, Function<Stream<String>, T> transformer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return transformer.apply(reader.lines());
        }
    }

    public static List<String> getLinesFromFile(File file) throws IOException {
        return getLinesFromFile(file, Stream::toList);
    }
}
