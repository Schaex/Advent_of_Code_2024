package com.schaex.swing;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public final class SwingUtil {
    private SwingUtil() {}

    public static JFrame makeFrame(String title, Component topComponent) {
        final JFrame frame = new JFrame(title);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(topComponent);

        frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);

        return frame;
    }

    public static void display(String title, Component topComponent) {
        final JFrame frame = makeFrame(title, topComponent);

        frame.setVisible(true);
    }
}
