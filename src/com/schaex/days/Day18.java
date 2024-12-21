package com.schaex.days;

import com.schaex.frequently_used.Direction;
import com.schaex.frequently_used.Point;
import com.schaex.util.FileUtil;
import com.schaex.util.ParamUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

public class Day18 {
    private static final boolean TEST = false;

    private static final int SIDE_LENGTH = TEST ? 7 : 71;
    private static final int MAX_COORDINATE = SIDE_LENGTH - 1;

    private static final Tile[][] GRID = new Tile[SIDE_LENGTH][SIDE_LENGTH];
    private static final List<Tile> FALLING_BYTES;

    static {
        // Populate the grid with safe tiles
        for (int y = 0; y < GRID.length; y++) {
            final Tile[] line = GRID[y];

            for (int x = 0; x < line.length; x++) {
                line[x] = new Tile(x, y);
            }
        }

        try {
            final int fileParam = TEST ? -18 : 18;

            // Fetch the input and parse each line as x,y-coordinates
            FALLING_BYTES = FileUtil.transformFileLines(fileParam, stream ->
                    stream.map(line -> line.split(","))
                            .map(array -> GRID[Integer.parseInt(array[1])][Integer.parseInt(array[0])])
                            .collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String... args) {
        // Fetch ListIterator so that we can easily scroll through the list
        final ListIterator<Tile> iter = FALLING_BYTES.listIterator();

        final Tile start = GRID[0][0];
        final Tile end = GRID[MAX_COORDINATE][MAX_COORDINATE];

        final Runnable traverse = () -> {
            Set<Tile> previouslyVisited = new HashSet<>();
            Set<Tile> newlyVisited = new HashSet<>();
            Set<Tile> swap;

            // Add the start tile
            previouslyVisited.add(start);

            start.minStepsFromStart = 0;

            boolean doContinue = true;

            while (doContinue) {
                // We cannot get to the end
                if (previouslyVisited.isEmpty()) {
                    return;
                }

                for (Tile tile : previouslyVisited) {
                    final int currentX = tile.x;
                    final int currentY = tile.y;
                    final int minStepsFromStartP1 = tile.minStepsFromStart + 1;

                    // Go in each direction
                    for (Direction direction : Direction.values()) {
                        final int nextX = currentX + direction.dx;
                        final int nextY = currentY + direction.dy;

                        // Are we still on the grid?
                        if (ParamUtil.isInRange(nextX, 0, SIDE_LENGTH) &&
                            ParamUtil.isInRange(nextY, 0, SIDE_LENGTH)) {
                            final Tile nextTile = GRID[nextY][nextX];

                            // Finish this cycle and then break the while (doContinue) loop
                            if (nextTile == end) {
                                doContinue = false;
                            }

                            // Can we step on this tile and would this be a minimal route?
                            if (nextTile.isSafe && nextTile.acceptMinStepsFromStart(minStepsFromStartP1)) {
                                nextTile.previous = tile;
                                newlyVisited.add(nextTile);
                            }
                        }
                    }
                }

                // Clear previouslyVisited and swap references
                previouslyVisited.clear();
                swap = previouslyVisited;
                previouslyVisited = newlyVisited;
                newlyVisited = swap;
            }
        };

        System.out.print("Part one: ");

        // 372
        {
            final int limit = TEST ? 12 : 1024;

            // Manually set tiles as dangerous
            for (int i = 0; i < limit; i++) {
                iter.next().isSafe = false;
            }

            traverse.run();

            System.out.println(end.minStepsFromStart);
        }

        System.out.print("Part two: ");

        // 25,6
        {
            // Reset the minStepsFromStart and previous fields of each safe tile
            final Runnable resetter = () -> {
                for (Tile[] tiles : GRID) {
                    for (Tile tile : tiles) {
                        if (tile.isSafe) {
                            tile.minStepsFromStart = Integer.MAX_VALUE;
                            tile.previous = null;
                        }
                    }
                }
            };

            // Mark the n previous tiles as safe
            final IntConsumer goBack = back -> {
                for (int i = 0; i < back; i++) {
                    iter.previous().isSafe = true;
                }

                resetter.run();
            };

            // Mark the n next tiles as unsafe
            final IntConsumer skipper = goForth -> {
                for (int i = 0; i < goForth; i++) {
                    iter.next().isSafe = false;
                }

                resetter.run();
            };

            // As we reset each tile's minStepsFromStart field to Integer.MAX_VALUE we can easily check whether we reached the end or not
            final BooleanSupplier canFinish = () -> end.minStepsFromStart != Integer.MAX_VALUE;

            final int limit = FALLING_BYTES.size() - 1;

            int diff = limit - iter.previousIndex();

            // Keep two variables to check whether we need to get the next or the previous element from the Iterator in the end
            boolean increasedInPreviousRound = true;
            boolean increase = true;

            // Binary search
            while ((diff /= 2) > 0) {
                increasedInPreviousRound = increase;

                if (increase) {
                    skipper.accept(diff);
                } else {
                    goBack.accept(diff);
                }

                traverse.run();

                increase = canFinish.getAsBoolean();
            }

            final Tile criticalTile = increasedInPreviousRound ? iter.previous() : iter.next();

            System.out.println(criticalTile.x + "," + criticalTile.y);
        }
    }

    private static class Tile extends Point {
        boolean isSafe = true;
        int minStepsFromStart = Integer.MAX_VALUE;
        Tile previous;

        Tile(int x, int y) {
            super(x, y);
        }

        // Does this tile already part of a shorter path?
        boolean acceptMinStepsFromStart(int value) {
            if (value < minStepsFromStart) {
                minStepsFromStart = value;

                return true;
            }

            return false;
        }
    }
}
