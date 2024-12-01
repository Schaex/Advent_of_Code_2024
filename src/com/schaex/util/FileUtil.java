package com.schaex.util;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public final class FileUtil {
    private FileUtil() {}

    public static List<String> getLinesFromFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().toList();
        }
    }

    public static List<String> getLinesFromFile() throws IOException {
        return getLinesFromFile(openFile());
    }

    public static File openFile() {
        return open(false);
    }

    public static File openDir() {
        return open(true);
    }

    public static File open(boolean directory) {
        JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        if (directory) {
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        }

        return chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ? chooser.getSelectedFile() : null;
    }
}
