package com.schaex.benchmark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.ToLongBiFunction;
import java.util.stream.Collectors;

public class TimerList implements BenchmarkRunnable {
    private final BenchmarkRunnable runnable;
    private final List<Long> results = new ArrayList<>();

    public TimerList(BenchmarkRunnable runnable) {
        this.runnable = runnable;
    }

    public List<Long> getResults() {
        return Collections.unmodifiableList(results);
    }

    public List<Long> getResultsSorted() {
        return results.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public void runWithException() throws Throwable {
        runnable.runWithException();
    }

    public void nIterations(int number, ToLongBiFunction<long[], Throwable> exceptionHandler) {
        for (int i = 0; i < number; i++) {
            results.add(benchmark(exceptionHandler));
        }
    }

    public void nIterations(int number) {
        nIterations(number, (startEnd, throwable) -> -1);
    }
}