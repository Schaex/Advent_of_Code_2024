package com.schaex.benchmark;

import java.util.Locale;

@FunctionalInterface
public interface BenchmarkRunnable {
    void run();

    default long benchmark() {
        final long start = System.nanoTime();

        run();

        return System.nanoTime() - start;
    }

    default String formatRun() {
        return "Total run time: " + formatNanoTime(benchmark());
    }

    static String formatNanoTime(long nanos) {
        final long h = nanos / 3600000000000L;
        nanos %= 3600000000000L;
        final long min = nanos / 60000000000L;
        nanos %= 60000000000L;
        final long s = nanos / 1000000000L;
        nanos %= 1000000000L;
        final long ms = nanos / 1000000L;
        nanos %= 1000000L;
        final long us = nanos / 1000L;
        nanos %= 1000L;

        return String.format(Locale.US, "%02d h %02d min %02d.%03d_%03d_%03d s", h, min, s, ms, us, nanos);
    }
}
