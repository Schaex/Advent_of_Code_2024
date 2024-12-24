package com.schaex.frequently_used;

import com.schaex.util.PublicCloneable;

public class Point implements PublicCloneable<Point> {
    public final int x, y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int taxicabDistance(int x, int y) {
        return Math.abs(this.x - x) + Math.abs(this.y - y);
    }

    public int taxicabDistance(Point other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    public int dx(int x) {
        return x - this.x;
    }

    public int dx(Point other) {
        return other.x - this.x;
    }

    public int dy(int y) {
        return y - this.y;
    }

    public int dy(Point other) {
        return other.y - this.y;
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Point point)) return false;
        return x == point.x && y == point.y;
    }

    // For HashMap and HashSet
    @Override
    public int hashCode() {
        return (y << 16) + x;
    }

    // For debugging
    @Override
    public String toString() {
        return "[" + x + "," + y + "]";
    }

    @Override
    public Point clone() throws CloneNotSupportedException {
        return (Point) super.clone();
    }
}
