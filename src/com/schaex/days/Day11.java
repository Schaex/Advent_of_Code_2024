package com.schaex.days;

import com.schaex.benchmark.BenchmarkRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.LongStream;

public class Day11 {
    private static final long[] POWERS_OF_TEN = LongStream.iterate(10L, value -> value > 0L, value -> value * 10L).toArray();

    public static void main(String... args) {
        final StoneMap map = new StoneMap("4022724 951333 0 21633 5857 97 702 6");

        map.blinkAll(25);

        // 211306
        System.out.println("Part one: " + map.size());

        map.blinkAll(50);

        // 250783680217283
        System.out.println("Part two: " + map.size());
    }

    private static class StoneMap {
        private Map<Long, Long> map = new HashMap<>();
        private Map<Long, Long> intermediary = new HashMap<>();

        StoneMap(String startingValues) {
            for (String value : startingValues.split(" ")) {
                map.put(Long.parseLong(value), 1L);
            }
        }

        // Convenience method to remap the current mapping or create a new mapping if it doesn't exist
        void addToIntermediary(long value, long count) {
            intermediary.compute(value, (key, currentCount) -> currentCount == null ? count : currentCount + count);
        }

        // Convenience method to simply call blinkAll() count times
        void blinkAll(int count) {
            for (int i = 0; i < count; i++) {
                blinkAll();
            }
        }

        void blinkAll() {
            for (Map.Entry<Long, Long> entry : map.entrySet()) {
                final long count = entry.getValue();
                final long numberOnStone = entry.getKey();

                if (numberOnStone == 0L) {
                    // Transform 0 into 1
                    addToIntermediary(1L, count);

                    continue;
                }

                // Find the smallest power of 10 that is larger than the number
                for (int i = 0; i < POWERS_OF_TEN.length; i++) {
                    if (numberOnStone < POWERS_OF_TEN[i]) {
                        // Is the number composed of an even number of digits?
                        if (i % 2 == 1) {
                            // Split the number by utilizing integer arithmetic
                            final long halfPower = POWERS_OF_TEN[i / 2];
                            final long leftValue = numberOnStone / halfPower;

                            // Equivalent to numberOnStone % halfPower
                            final long rightValue = numberOnStone - (leftValue * halfPower);

                            addToIntermediary(leftValue, count);
                            addToIntermediary(rightValue, count);
                        } else {
                            //
                            addToIntermediary(numberOnStone * 2024L, count);
                        }

                        break;
                    }
                }
            }

            // Store a reference to the map and clear it
            var previous = map;
            map.clear();

            // Switch references -> the intermediary map is the main map now and vice versa
            map = intermediary;
            intermediary = previous;
        }

        long size() {
            long count = 0L;

            for (Long value : map.values()) {
                count += value;
            }

            return count;
        }
    }

    // Does work for the first part but won't for the second because there will be too many allocations
    private static void firstAttempt() {
        final StoneList list = new StoneList();

        for (String value : "4022724 951333 0 21633 5857 97 702 6".split(" ")) {
            list.add(new Stone(value));
        }

        System.out.println("Start: " + list.length());

        final BenchmarkRunnable innerRunnable = () -> {
            list.blinkAll();
            System.out.println(list.length());
        };

        // 25 -> 211306
        // 75 -> MemoryError
        for (int i = 1; i <= 25; i++) {
            System.out.print("Blink " + i + ":\t");
            System.out.println(innerRunnable.formatRun() + "\n");
        }
    }

    private static class Stone {
        long value;
        Stone next;

        Stone(long value) {
            this.value = value;
        }

        Stone(String value) {
            this(Long.parseLong(value));
        }

        Stone(long value, Stone next) {
            this.value = value;
            this.next = next;
        }

        void blink() {
            if (value == 0L) {
                value = 1L;

                return;
            }

            for (int i = 0; i < POWERS_OF_TEN.length; i++) {
                if (value < POWERS_OF_TEN[i]) {

                    if (i % 2 == 1) {
                        final long halfPower = POWERS_OF_TEN[i / 2];
                        final long leftValue = value / halfPower;
                        final long rightValue = value - (leftValue * halfPower);

                        value = leftValue;
                        next = new Stone(rightValue, next);
                    } else {
                        value *= 2024L;
                    }

                    return;
                }
            }
        }

        long countStones() {
            long count = 1;

            Stone next = this.next;

            while (next != null) {
                count++;

                next = next.next;
            }

            return count;
        }
    }

    private static class StoneList {
        private Stone first;
        private Stone last;

        void add(Stone stone) {
            if (first == null) {
                first = stone;
            } else {
                last.next = stone;
            }

            last = stone;
        }

        void blinkAll(int count) {
            for (int i = 0; i < count; i++) {
                blinkAll();
            }
        }

        void blinkAll() {
            Stone current = first;
            Stone next;

            while (current != null) {
                next = current.next;
                current.blink();
                current = next;
            }
        }

        long length() {
            return first != null ? first.countStones() : 0;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            Stone current = first;
            Stone next;

            while (current != null) {
                next = current.next;
                builder.append(current.value).append(' ');
                current = next;
            }

            return builder.substring(0, builder.length() - 1);
        }
    }
}
