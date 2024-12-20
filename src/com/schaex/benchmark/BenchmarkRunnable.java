package com.schaex.benchmark;

import java.util.Locale;
import java.util.function.ToLongBiFunction;

@SuppressWarnings("unused")
@FunctionalInterface
public interface BenchmarkRunnable extends Runnable {
    void runWithException() throws Throwable;

    @Override
    default void run() {
        try {
            runWithException();
        } catch (Throwable ignored) {}
    }

    default long benchmark(ToLongBiFunction<long[], Throwable> exceptionHandler) {
        final long start = System.nanoTime();

        try {
            runWithException();
        } catch (Throwable throwable) {
            return exceptionHandler.applyAsLong(new long[]{start, System.nanoTime()}, throwable);
        }

        return System.nanoTime() - start;
    }

    default long benchmark() {
        return benchmark((startEnd, throwable) -> {
            throw new RuntimeException(throwable);
        });
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

    static void doFormatRun(BenchmarkRunnable runnable) {
        System.out.println(runnable.formatRun());
    }
}
