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

    // Static initializer so that the constants are available to everything inside this class
    static {
        try {
            AREA = FileUtil.transformFileLines(6,
                    stream -> stream.map(String::toCharArray)
                            .toArray(char[][]::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WIDTH = AREA[0].length;
        HEIGHT = AREA.length;
    }

    public static void main(String... args) {
        // Coordinates on the grid
        int x = 0;
        int y = 0;

        // Find the starting coordinates of the guard
        findingLoop:
        for (; y < HEIGHT; y++) {
            for (x = 0; x < WIDTH; x++) {
                if (AREA[y][x] == '^') {
                    break findingLoop;
                }
            }
        }

        // Cache the starting coordinates so that they can be used again later
        final int startX = x;
        final int startY = y;

        // Starting direction
        Direction dir = Direction.UP;

        // List that keeps record of all the tiles the guard steps on
        // => prevent checking for tiles that don't interfere with the guard's movement in task 2
        final List<Point> traversedPoints = new LinkedList<>();

        System.out.print("Part one: ");

        // 5208
        {
            // Initialize the counting variable to one because the guard starts on a tile
            int count = 1;

            // "While the guard is on the grid after each move"
            while (ParamUtil.isInRange(x += dir.dx, 0, WIDTH) &&
                   ParamUtil.isInRange(y += dir.dy, 0, HEIGHT)) {
                // Get the tile in front of the guard
                final char nextTile = AREA[y][x];

                if (nextTile == '#') {
                    // If it is an obstacle, move one step back and turn right
                    x -= dir.dx;
                    y -= dir.dy;
                    dir = dir.turnRight();
                } else if (nextTile == '.') {
                    // If it is a new tile, add its point to the list and mark it with the direction's symbol
                    traversedPoints.add(new Point(x, y, dir));
                    AREA[y][x] = dir.symbol;

                    // Increase because it is a new tile
                    count++;
                }
            }

            System.out.println(count);
        }

        System.out.print("Part two: ");

        // 1972
        {
            int count = 0;

            for (Point point : traversedPoints) {
                if (point.hasObstacleToTheRightOfPreviousTile()) {
                    // Reset
                    clearArea();
                    x = startX;
                    y = startY;
                    dir = Direction.UP;

                    // Marks this tile as additional obstacle so that it can be removed again with clearArea()
                    AREA[point.y][point.x] = 'O';

                    boolean resultsInInfiniteLoop = false;

                    while (ParamUtil.isInRange(x += dir.dx, 0, WIDTH) &&
                           ParamUtil.isInRange(y += dir.dy, 0, HEIGHT)) {
                        // Again get the tile in front of the guard
                        final char nextTile = AREA[y][x];

                        if (nextTile == '#' || nextTile == 'O') {
                            // We have an obstacle
                            x -= dir.dx;
                            y -= dir.dy;
                            dir = dir.turnRight();
                        } else if (nextTile == '.') {
                            // No obstacle but a new tile
                            AREA[y][x] = dir.symbol;
                        } else if (nextTile == dir.symbol) {
                            // No obstacle and a tile that was stepped on in this direction before
                            // => We went in circles
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

    // Resets all tiles that are not a permanent obstacle '#' to a "new tile" '.'
    private static void clearArea() {
        for (char[] line : AREA) {
            for (int i = 0; i < line.length; i++) {
                if (line[i] != '#') {
                    line[i] = '.';
                }
            }
        }
    }

    // Utility enum as there are only four possible directions => singletons
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

        // Returns the direction the guard faces after turning right
        Direction turnRight() {
            return switch (this) {
                case UP -> RIGHT;
                case RIGHT -> DOWN;
                case DOWN -> LEFT;
                case LEFT -> UP;
            };
        }
    }

    // Immutable container for each tile's coordinate the guard stepped on and the duration it was facing
    private record Point(int x, int y, Direction dir) {

        // Computes whether the guard will run into an obstacle after colliding
        // with an obstacle in these coordinates and turning right
        boolean hasObstacleToTheRightOfPreviousTile() {
            final Direction nextDir = dir.turnRight();

            // One step back
            int x = this.x - dir.dx;
            int y = this.y - dir.dy;

            // While the guard is on the grid
            while (ParamUtil.isInRange(x += nextDir.dx, 0, WIDTH) &&
                   ParamUtil.isInRange(y += nextDir.dy, 0, HEIGHT)) {
                if (AREA[y][x] == '#') {
                    // We found an obstacle
                    return true;
                }
            }

            return false;
        }
    }
}
