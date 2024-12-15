package com.schaex.util;

import com.schaex.arrays.ArrayUtil;
import com.schaex.days.DaysUtil;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Iterator;
import java.util.stream.Stream;

public final class FileUtil {
    private FileUtil() {}

    public static <T> T transformFileLines(File file, Transformer<Stream<String>, T> transformer) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return transformer.transform(reader.lines());
        }
    }

    public static <T> T transformFileLines(int day, Transformer<Stream<String>, T> transformer) throws IOException {
        return transformFileLines(DaysUtil.resource(day), transformer);
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

    public static void dumpToFile(File file, Iterator<?> it) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            while (it.hasNext()) {
                writer.write(String.valueOf(it.next()));
                writer.newLine();
            }
        }
    }

    public static File openFile() {
        return open(false);
    }

    public static File openDir() {
        return open(true);
    }

    public static File open(boolean directory) {
        final JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        if (directory) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        return chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }

    @FunctionalInterface
    public interface Transformer<I, O> {
        O transform(I input) throws IOException;
    }
}
