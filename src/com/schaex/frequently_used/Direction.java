package com.schaex.frequently_used;

public enum Direction {
    WEST(-1, 0),
    EAST(1, 0),
    NORTH(0, -1),
    SOUTH(0, 1);

    public final int dx, dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Direction turnLeft() {
        return switch (this) {
            case WEST -> SOUTH;
            case EAST -> NORTH;
            case NORTH -> WEST;
            case SOUTH -> EAST;
        };
    }

    public Direction turnRight() {
        return switch (this) {
            case WEST -> NORTH;
            case EAST -> SOUTH;
            case NORTH -> EAST;
            case SOUTH -> WEST;
        };
    }
}
