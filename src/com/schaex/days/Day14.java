package com.schaex.days;

import com.schaex.swing.SwingUtil;
import com.schaex.util.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class Day14 {
    private static final int WIDTH = 101;
    private static final int HEIGHT = 103;

    public static void main(String... args) throws IOException {
        final Robot[] robots = FileUtil.transformFileLines(14, stream ->
                stream.map(line -> {
                    final int x, y, dx, dy;
                    int i1, i2;

                    i1 = line.indexOf('=') + 1;
                    i2 = line.indexOf(',', i1);

                    x = Integer.parseInt(line.substring(i1, i2));
                    y = Integer.parseInt(line.substring(i2 + 1, line.indexOf(' ')));

                    i1 = line.indexOf('=', i2) + 1;
                    i2 = line.indexOf(',', i1);

                    dx = Integer.parseInt(line.substring(i1, i2));
                    dy = Integer.parseInt(line.substring(i2 + 1));

                    return new Robot(x, y, dx, dy);
                }).toArray(Robot[]::new)
        );

        System.out.print("Part one: ");

        // 232589280
        {
            int q1 = 0, q2 = 0, q3 = 0, q4 = 0;

            for (Robot robot : robots) {
                robot.elapse(100);

                switch (findQuadrant(robot.currentX, robot.currentY)) {
                    case 1 -> q1++;
                    case 2 -> q2++;
                    case 3 -> q3++;
                    case 4 -> q4++;
                }

                robot.reset();
            }

            System.out.println(q1 * q2 * q3 * q4);
        }

        if (DaysUtil.JUST_SHOW_RESULTS) {
            System.out.println("Part two: " + 7569);
            return;
        }

        {
            final int max = WIDTH * HEIGHT;

            final Scroller scroller = new Scroller(robots);

            final JLabel label = new JLabel("", JLabel.CENTER);

            final TextArea textArea = new TextArea();
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            textArea.setFocusable(false);

            final Consumer<char[][]> textSetter = area -> {
                final StringBuilder builder = new StringBuilder();

                builder.append(area[0]);

                for (int i = 1; i < area.length; i++) {
                    builder.append(System.lineSeparator())
                            .append(area[i]);
                }

                label.setText(scroller.values[scroller.cursor] + "/" + max);
                textArea.setText(builder.toString().replace('\0', ' '));
            };

            textSetter.accept(scroller.make());

            final JPanel mainPanel = new JPanel(new BorderLayout(0,2));
            mainPanel.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    final int code = e.getKeyCode();

                    if (code == KeyEvent.VK_LEFT && scroller.hasPrevious()) {
                        textSetter.accept(scroller.previous());
                    } else if ((code == KeyEvent.VK_RIGHT  || code == KeyEvent.VK_SPACE) && scroller.hasNext()) {
                        textSetter.accept(scroller.next());
                    }
                }
            });

            final JButton leftButton = new JButton("Previous");
            final JButton rightButton = new JButton("Next");

            leftButton.addActionListener(action -> {
                if (scroller.hasPrevious()) {
                    textSetter.accept(scroller.previous());
                }
            });

            rightButton.addActionListener(action -> {
                if (scroller.hasNext()) {
                    textSetter.accept(scroller.next());
                }
            });


            mainPanel.add(label, BorderLayout.NORTH);
            mainPanel.add(textArea, BorderLayout.CENTER);
            mainPanel.add(leftButton, BorderLayout.WEST);
            mainPanel.add(rightButton, BorderLayout.EAST);

            SwingUtil.display(mainPanel);
        }
    }

    private static int findQuadrant(int x, int y) {
        if (x == WIDTH / 2 || y == HEIGHT / 2) {
            return -1;
        }

        if (y < WIDTH / 2 + 1) {
            return x < WIDTH / 2 ? 2 : 1;
        } else {
            return x < WIDTH / 2 ? 3 : 4;
        }
    }

    private static class Robot {
        private final int startX, startY, dx, dy;
        private int currentX, currentY;

        public Robot(int startX, int startY, int dx, int dy) {
            this.startX = startX;
            this.startY = startY;
            this.dx = dx;
            this.dy = dy;

            currentX = startX;
            currentY = startY;
        }

        void reset() {
            currentX = startX;
            currentY = startY;
        }

        void elapse(int seconds) {
            currentX = (currentX + dx * seconds) % WIDTH;
            currentY = (currentY + dy * seconds) % HEIGHT;

            if (currentX < 0) {
                currentX = WIDTH + currentX;
            }

            if (currentY < 0) {
                currentY = HEIGHT + currentY;
            }
        }
    }

    private static class Scroller implements ListIterator<char[][]> {
        final Robot[] robots;
        final int[] values;
        int cursor;

        Scroller(Robot[] robots) throws IOException {
            this.robots = robots;
            this.values = FileUtil.transformFileLines(DaysUtil.resource("Day_14_variance.txt"), stream ->
                    stream.map(line -> line.split("\t"))
                            .filter(split -> split[1].charAt(0) < '4' || split[2].charAt(0) < '4')
                            .mapToInt(split -> Integer.parseInt(split[0]))
                            .toArray()
            );
        }

        char[][] make() {
            final char[][] area = new char[WIDTH][HEIGHT];

            for (Robot robot : robots) {
                robot.reset();
                robot.elapse(values[cursor]);

                area[robot.currentX][robot.currentY] = '#';
            }

            return area;
        }

        @Override
        public boolean hasNext() {
            return cursor < values.length - 1;
        }

        @Override
        public char[][] next() {
            cursor++;
            return make();
        }

        @Override
        public boolean hasPrevious() {
            return cursor > 0;
        }

        @Override
        public char[][] previous() {
            cursor--;
            return make();
        }

        @Override
        public int nextIndex() {
            return cursor + 1;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        // Dead weight
        @Override
        public void remove() {

        }

        @Override
        public void set(char[][] chars) {

        }

        @Override
        public void add(char[][] chars) {

        }
    }
}
