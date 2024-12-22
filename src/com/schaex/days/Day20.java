package com.schaex.days;

import com.schaex.frequently_used.Direction;
import com.schaex.frequently_used.Point;
import com.schaex.util.FileUtil;
import com.schaex.util.ParamUtil;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Day20 {
    private static final boolean TEST = false;

    private static final Tile[][] MAZE;
    private static final List<Tile> MOVABLE_TILES = new ArrayList<>();

    private static final int MAX_X;
    private static final int MAX_Y;

    private static final int MAX_CHEAT_PART_1 = 2;
    private static final int MAX_CHEAT_PART_2 = 20;

    private static final int MIN_SAVE = TEST ? 2 : 100;

    private static final char WALL_TILE = '#';
    private static final char END_TILE  = 'E';

    static {
        final Tile end;

        try {
            // Use AtomicReference as a "box"
            final AtomicReference<Tile> endRef = new AtomicReference<>();

            MAZE = FileUtil.transformFileLines(TEST ? -20 : 20, stream -> {
                final AtomicInteger currentY = new AtomicInteger();

                return stream.map(String::toCharArray)
                        .map(line -> {
                            // Fetch the current y-value and increase it
                            final int y = currentY.getAndIncrement();

                            final Tile[] tiles = new Tile[line.length];

                            for (int x = 0; x < tiles.length; x++) {
                                final char type = line[x];
                                final Tile tile = new Tile(x, y, type);

                                if (type != WALL_TILE) {
                                    MOVABLE_TILES.add(tile);

                                    if (type == END_TILE) {
                                        endRef.set(tile);
                                    }
                                }

                                tiles[x] = tile;
                            }

                            return tiles;
                        })
                        .toArray(Tile[][]::new);
            });

            end = endRef.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final int height = MAZE.length;
        final int width = MAZE[0].length;

        MAX_X = width - 1;
        MAX_Y = height - 1;

        // Initialize base values
        {
            Set<Tile> mustVisitNow = new HashSet<>();
            Set<Tile> nextToVisit = new HashSet<>();
            Set<Tile> swap;

            // Initialize this value
            end.minDistanceFromEnd = 0;

            // Add to the set so that we can start the first cycle on this tile
            mustVisitNow.add(end);

            while (!mustVisitNow.isEmpty()) {
                for (Tile currentTile : mustVisitNow) {
                    final int currentX = currentTile.x;
                    final int currentY = currentTile.y;

                    // Cache the value for neighboring tiles
                    final int minDistanceFromEndP1 = currentTile.minDistanceFromEnd + 1;

                    // Go in each direction
                    for (Direction direction : Direction.values()) {
                        final int nextX = currentX + direction.dx;
                        final int nextY = currentY + direction.dy;

                        if (ParamUtil.isInRange(nextX, 0, width) && ParamUtil.isInRange(nextY, 0, height)) {
                            final Tile nextTile = MAZE[nextY][nextX];

                            if (nextTile.type == WALL_TILE) {
                                continue;
                            }

                            // If we can move to the next tile while staying on the shortest path:
                            if (nextTile.acceptMinStepsFromEnd(minDistanceFromEndP1)) {
                                nextToVisit.add(nextTile);
                            }
                        }
                    }
                }

                // Clear mustVisitNow and swap references with nextToVisit
                mustVisitNow.clear();
                swap = mustVisitNow;
                mustVisitNow = nextToVisit;
                nextToVisit = swap;
            }
        }
    }

    public static void main(String... args) throws Exception {
        // 1363
        System.out.println("Part one: " + cheat(MAX_CHEAT_PART_1));

        // 1007186
        System.out.println("Part two: " + cheat(MAX_CHEAT_PART_2));
    }

    private static int cheat(int maxCheatDistance) {
        int count = 0;

        int distance;

        for (Tile tile : MOVABLE_TILES) {
            // Fetch values
            final int currentX = tile.x;
            final int currentY = tile.y;
            final int minDistanceFromEnd = tile.minDistanceFromEnd;

            // Calculate bounds
            final int minX = Math.max(currentX - maxCheatDistance, 0);
            final int maxX = Math.min(currentX + maxCheatDistance, MAX_X);
            final int minY = Math.max(currentY - maxCheatDistance, 0);
            final int maxY = Math.min(currentY + maxCheatDistance, MAX_Y);

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    final Tile other = MAZE[y][x];

                    // We cannot end in a wall. Ending on the current tile would be nonsensical
                    if (tile == other || other.type == WALL_TILE) {
                        continue;
                    }

                    distance = tile.taxicabDistance(other);

                    // The tile must still be in reach
                    if (distance > maxCheatDistance) {
                        continue;
                    }

                    // Calculate the reduction in distance by taking into account that we still had to move
                    int difference = (minDistanceFromEnd - other.minDistanceFromEnd) - distance;

                    // Is this difference significant enough?
                    if (difference >= MIN_SAVE) {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    private static class Tile extends Point {
        final char type;
        // Initialize to a value that can only be overwritten
        int minDistanceFromEnd = Integer.MAX_VALUE;

        Tile(int x, int y, char type) {
            super(x, y);
            this.type = type;
        }

        boolean acceptMinStepsFromEnd(int value) {
            if (value < minDistanceFromEnd) {
                minDistanceFromEnd = value;

                return true;
            }

            return false;
        }
    }
}
