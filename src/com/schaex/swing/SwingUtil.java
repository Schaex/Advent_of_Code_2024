package com.schaex.swing;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public final class SwingUtil {
    private SwingUtil() {}

    public static JFrame makeFrame(Component topComponent) {
        final JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(topComponent);

        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        return frame;
    }

    public static void display(Component topComponent) {
        final JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(topComponent);

        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());

        frame.setVisible(true);
    }
}
