package com.schaex.days;

import com.schaex.util.FileUtil;
import com.schaex.util.ParamUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Day06 {
    private static final char[][] AREA;
    private static final int WIDTH;
    private static final int HEIGHT;

    static {
        try {
            AREA = FileUtil.transformFileContent(6,
                    stream -> stream.map(String::toCharArray).toArray(char[][]::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WIDTH = AREA[0].length;
        HEIGHT = AREA.length;
    }

    public static void main(String... args) throws IOException {
        Direction dir = Direction.UP;

        int x = 0;
        int y = 0;

        findingLoop:
        for (; y < HEIGHT; y++) {
            for (x = 0; x < WIDTH; x++) {
                if (AREA[y][x] == '^') {
                    break findingLoop;
                }
            }
        }

        final int startX = x;
        final int startY = y;

        final List<Point> traversedPoints = new LinkedList<>();

        System.out.print("Part one: ");

        // 5208
        {
            AREA[y][x] = dir.symbol;
            int count = 1;

            while (ParamUtil.isInRange(x += dir.dx, 0, WIDTH) &&
                   ParamUtil.isInRange(y += dir.dy, 0, HEIGHT)) {
                final char nextTile = AREA[y][x];

                if (nextTile == '#') {
                    x -= dir.dx;
                    y -= dir.dy;
                    dir = dir.turnRight();
                } else if (nextTile == '.') {
                    traversedPoints.add(new Point(x, y, dir));
                    AREA[y][x] = dir.symbol;
                    count++;
                }
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        {
            int count = 0;

            for (Point point : traversedPoints) {
                if (point.hasObstacleToTheRightOfPreviousTile()) {
                    // Reset
                    clearArea();
                    x = startX;
                    y = startY;
                    dir = Direction.UP;

                    AREA[point.y][point.x] = 'O';

                    boolean resultsInInfiniteLoop = false;

                    while (ParamUtil.isInRange(x += dir.dx, 0, WIDTH) &&
                           ParamUtil.isInRange(y += dir.dy, 0, HEIGHT)) {
                        final char nextTile = AREA[y][x];

                        if (nextTile == '#' || nextTile == 'O') {
                            x -= dir.dx;
                            y -= dir.dy;
                            dir = dir.turnRight();
                        } else if (nextTile == '.') {
                            AREA[y][x] = dir.symbol;
                        } else if (nextTile == dir.symbol) {
                            resultsInInfiniteLoop = true;
                            break;
                        }
                    }

                    if (resultsInInfiniteLoop) {
                        count++;
                    }
                }
            }

            System.out.println(count);
        }
    }

    private static void clearArea() {
        for (char[] line : AREA) {
            for (int i = 0; i < line.length; i++) {
                if (line[i] != '#') {
                    line[i] = '.';
                }
            }
        }
    }

    private enum Direction {
        UP(0, -1, '^'),
        RIGHT(1, 0, '>'),
        DOWN(0, 1, 'v'),
        LEFT(-1,0, '<');

        final int dx;
        final int dy;
        final char symbol;

        Direction(int dx, int dy, char symbol) {
            this.dx = dx;
            this.dy = dy;
            this.symbol = symbol;
        }

        Direction turnRight() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }
    }

    private record Point(int x, int y, Direction dir) {
        boolean hasObstacleToTheRightOfPreviousTile() {
            final Direction nextDir = dir.turnRight();

            // One step back
            int x = this.x - dir.dx;
            int y = this.y - dir.dy;

            while (ParamUtil.isInRange(x += nextDir.dx, 0, WIDTH) &&
                   ParamUtil.isInRange(y += nextDir.dy, 0, HEIGHT)) {
                if (AREA[y][x] == '#') {
                    return true;
                }
            }

            return false;
        }
    }
}
