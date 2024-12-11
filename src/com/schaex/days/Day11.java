package com.schaex.days;

import com.schaex.benchmark.BenchmarkRunnable;

import java.io.IOException;
import java.math.BigInteger;
import java.util.stream.LongStream;

public class Day11 {
    private static final long[] POWERS_OF_TEN = LongStream.iterate(10L, value -> value > 0L, value -> value * 10L).toArray();

    public static void main(String... args) throws IOException {
        final BenchmarkRunnable runnable = () -> {
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
            // 75 -> ?
            for (int i = 1; i <= 75; i++) {
                System.out.print("Blink " + i + ":\t");
                System.out.println(innerRunnable.formatRun() + "\n");
            }
        };

        System.out.println(runnable.formatRun());
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
