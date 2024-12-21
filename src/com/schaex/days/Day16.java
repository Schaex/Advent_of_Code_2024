package com.schaex.days;

import com.schaex.frequently_used.Direction;
import com.schaex.swing.SwingUtil;
import com.schaex.util.FileUtil;
import com.schaex.util.tuples.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16 {
    private static final char[][] MAZE;

    private static final int HEIGHT;
    private static final int WIDTH;

    private static final char BARRIER    = '#';
    private static final char START_TILE = 'S';
    //private static final char END_TILE   = 'E';

    private static final Point START;
    private static final Point END;

    private static final int TURNING_PENALTY = 1000;

    // For UI
    private static final Point[] POINTS;
    private static final JPanel[][] PANEL_TILES;

    static {
        try {
            MAZE = FileUtil.getCharTableFromFile(16);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        HEIGHT = MAZE.length;
        WIDTH = MAZE[0].length;

        START = new Point(1, HEIGHT - 2);
        END = new Point(WIDTH - 2, 1);

        // Don't need to do any more if we only want the results
        if (DaysUtil.JUST_SHOW_RESULTS) {
            POINTS = null;
            PANEL_TILES = null;
        } else {
            // Make a grid
            final JPanel mainPanel = new JPanel(new GridLayout(HEIGHT, WIDTH, 1, 1));
            mainPanel.setBackground(Color.BLACK);

            PANEL_TILES = new JPanel[HEIGHT][WIDTH];
            final List<Point> pointList = new ArrayList<>();

            // Iterate over the entire grid
            for (int y = 0; y < HEIGHT; y++) {
                final JPanel[] row = PANEL_TILES[y];

                for (int x = 0; x < WIDTH; x++) {
                    final JPanel panel = new JPanel();

                    panel.setSize(2, 2);

                    final Color color;

                    // Barriers are black, the is white and will be added to the list
                    if (MAZE[y][x] == BARRIER) {
                        color = Color.BLACK;
                    } else {
                        color = Color.WHITE;
                        pointList.add(new Point(x, y));
                    }

                    panel.setBackground(color);

                    mainPanel.add(panel);

                    row[x] = panel;
                }
            }

            POINTS = pointList.toArray(Point[]::new);

            SwingUtil.display("Day 16", mainPanel);
        }
    }

    public static void main(String... args) {
        final Pair<Integer, Stream<Point>> result = new Traversal().run();

        assert result != null;

        // 143580
        System.out.println("Part one: " + result.left());

        final Set<Point> points = result.right().collect(Collectors.toSet());

        // 645
        System.out.println("Part two: " + points.size());

        if (DaysUtil.JUST_SHOW_RESULTS) {
            return;
        }

        for (Point point : POINTS) {
            PANEL_TILES[point.y][point.x].setBackground(Color.WHITE);
        }

        for (Point point : points) {
            PANEL_TILES[point.y][point.x].setBackground(Color.MAGENTA);
        }
    }

    private static class Traversal {
        // Initiated to their maximum values so that following comparisons can only decrease them
        static int MIN_SCORE_FOUND = Integer.MAX_VALUE;
        static int MAX_TIMES_TURNED = Integer.MAX_VALUE;

        // Information on all tiles, shared with children
        final CrossNodeMatrix crossNodes;
        // All instances other than the "root" are in lists corresponding to their timesTurned field, shared
        final Map<Integer, List<Traversal>> children;
        // All points that have already been stepped on, only copied not shared
        final Set<Point> traversedPoints;

        Direction dir;
        Point pos;
        final int timesTurned;
        int score;

        // This constructor is called by the main method
        Traversal() {
            crossNodes = new CrossNodeMatrix(WIDTH, HEIGHT);
            children = new HashMap<>();
            traversedPoints = new HashSet<>();
            dir = Direction.EAST;
            pos = START;
            timesTurned = 0;

            traversedPoints.add(pos);
        }

        // Copy constructor to instantiate a child which is immediately added to the map
        Traversal(Traversal parent) {
            crossNodes = parent.crossNodes;
            children = parent.children;
            traversedPoints = new HashSet<>(parent.traversedPoints);
            pos = parent.pos;
            dir = parent.dir;
            timesTurned = parent.timesTurned + 1;
            score = parent.score;

            children.computeIfAbsent(timesTurned, (i) -> new LinkedList<>())
                    .add(this);
        }

        void makeChildren(CrossNode node) {
            final Direction leftDir = dir.turnLeft();
            final Direction rightDir = dir.turnRight();

            final Point nextLeft = pos.next(leftDir);
            final Point nextRight = pos.next(rightDir);

            // Check which one is closer to the end tile and instantiate this one first
            // -> Earlier call of its run() method
            // See run() for more information
            if (nextLeft.taxicabDistanceToEnd() < nextRight.taxicabDistanceToEnd()) {
                if (isValidTile(nextLeft) && (node.canSetMinScore(leftDir, score + TURNING_PENALTY))) {
                    final Traversal child = new Traversal(this);
                    child.turnLeft();
                    child.stepForward(nextLeft);
                }

                if (isValidTile(nextRight) && (node.canSetMinScore(rightDir, score + TURNING_PENALTY))) {
                    final Traversal child = new Traversal(this);
                    child.turnRight();
                    child.stepForward(nextRight);
                }
            } else {
                if (isValidTile(nextRight) && (node.canSetMinScore(rightDir, score + TURNING_PENALTY))) {
                    final Traversal child = new Traversal(this);
                    child.turnRight();
                    child.stepForward(nextRight);
                }

                if (isValidTile(nextLeft) && (node.canSetMinScore(leftDir, score + TURNING_PENALTY))) {
                    final Traversal child = new Traversal(this);
                    child.turnLeft();
                    child.stepForward(nextLeft);
                }
            }
        }

        Pair<Integer, Stream<Point>> fallback() {
            final List<Traversal> remaining = children.get(timesTurned + 1);

            if (remaining == null) {
                return null;
            }

            // Instantiate an empty stream that can be concatenated
            Stream<Point> allPoints = Stream.empty();
            int minScore = Integer.MAX_VALUE;

            final Iterator<Traversal> it = remaining.iterator();

            while (it.hasNext()) {
                // Fetch the next element and immediately remove it from the list
                final Pair<Integer, Stream<Point>> nextResult = it.next().run();
                it.remove();

                if (nextResult == null) {
                    continue;
                }

                final Stream<Point> nextPoints = nextResult.right();

                if (nextPoints == null) {
                    continue;
                }

                final int nextMinScore = nextResult.left();

                // Concatenate the streams if this score is equal to the currently lowest score.
                // If this score is even lower, overwrite
                if (nextMinScore == minScore) {
                    allPoints = Stream.concat(allPoints, nextPoints);
                } else if (nextMinScore < minScore) {
                    minScore = nextMinScore;
                    allPoints = nextPoints;
                }
            }

            return new Pair<>(minScore, allPoints);
        }

        Pair<Integer, Stream<Point>> run() {
            // Loop until we reach the end tile
            while (!pos.equals(END)) {
                // Fast return if we already have a larger score than the currently lowest one
                if (score > MIN_SCORE_FOUND) {
                    return null;
                }

                // Fetches the information for the current tile
                final CrossNode node = crossNodes.getNode(pos);

                // If we can still afford turning
                if (MAX_TIMES_TURNED >= timesTurned) {
                    makeChildren(node);
                }

                final Point nextForward = pos.next(dir);

                // Only make another step if the next tile can be stepped on
                // AND if it allows our current score, computes the children otherwise
                if (isValidTile(nextForward) && node.canSetMinScore(dir, score)) {
                    stepForward(nextForward);
                } else {
                    return fallback();
                }
            }

            // Last check
            if (timesTurned > MAX_TIMES_TURNED || score > MIN_SCORE_FOUND) {
                return null;
            }

            MAX_TIMES_TURNED = timesTurned;
            MIN_SCORE_FOUND = score;

            return new Pair<>(score, traversedPoints.stream());
        }

        // Checks whether this tile is a barrier or the start and has not yet been stepped in
        boolean isValidTile(Point point) {
            final char tile = MAZE[point.y][point.x];
            return tile != BARRIER && tile != START_TILE && !traversedPoints.contains(point);
        }

        void stepForward(Point next) {
            traversedPoints.add(next);

            if (!DaysUtil.JUST_SHOW_RESULTS) {
                PANEL_TILES[next.y][next.x].setBackground(Color.BLUE);
            }

            pos = next;
            score++;
        }

        void turnLeft() {
            dir = dir.turnLeft();
            score += TURNING_PENALTY;
        }

        void turnRight() {
            dir = dir.turnRight();
            score += TURNING_PENALTY;
        }
    }

    private static class CrossNodeMatrix {
        final CrossNode[][] nodes;

        CrossNodeMatrix(int width, int height) {
            this.nodes = new CrossNode[height][width];
        }

        CrossNode getNode(Point coordinates) {
            CrossNode node;

            if ((node = nodes[coordinates.y][coordinates.x]) == null) {
                node = nodes[coordinates.y][coordinates.x] = new CrossNode();
            }

            return node;
        }
    }

    // Container to encapsulate the behavior of a point that can move in one of the cardinal directions
    private static class Point extends com.schaex.frequently_used.Point {
        public Point(int x, int y) {
            super(x, y);
        }

        // For heuristics
        int taxicabDistanceToEnd() {
            return taxicabDistance(END);
        }

        Point next(Direction inDir) {
            return new Point(x + inDir.dx, y + inDir.dy);
        }
    }

    // Container for an int array that holds the minimum score that was recorded
    // from stepping on these, one entry for each cardinal direction
    private record CrossNode(int[] minScoresWhenTraversed) {
        CrossNode() {
            this(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE});
        }

        boolean canSetMinScore(Direction dir, int value) {
            final int index = dir.ordinal();

            if (value <= minScoresWhenTraversed[index]) {
                minScoresWhenTraversed[index] = value;

                return true;
            }

            return false;
        }
    }
}
