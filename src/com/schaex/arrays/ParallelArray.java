package com.schaex.arrays;

import java.util.Iterator;

public class ParallelArray<T> implements Iterable<ParallelArray<T>.Slice>, Iterator<ParallelArray<T>.Slice> {
    private final Slice slice = new Slice();
    private final T[][] arrays;
    private final int maxCursor;
    private int cursor = -1;

    @SafeVarargs
    public ParallelArray(T[]... arrays) {
        this.arrays = arrays;
        this.maxCursor = arrays[0].length - 1;
    }

    @Override
    public Iterator<Slice> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return cursor < maxCursor;
    }

    @Override
    public Slice next() {
        cursor++;
        return slice;
    }

    public class Slice {
        public T get(int column) {
            return arrays[column][cursor];
        }

        @Override
        public String toString() {
            final int columns = arrays.length;

            return switch (columns) {
                case 0 -> "";
                case 1 -> String.valueOf(arrays[0][cursor]);
                default -> {
                    final char[] delimiter = {',', ' '};
                    final int colM1 = columns - 1;

                    final StringBuilder builder = new StringBuilder();

                    for (int i = 0; i < colM1; i++) {
                        builder.append(arrays[i][cursor])
                                .append(delimiter, 0, 2);
                    }

                    builder.append(arrays[colM1][cursor]);

                    yield builder.toString();
                }
            };
        }
    }
}
