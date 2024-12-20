package com.schaex;

import com.schaex.benchmark.BenchmarkRunnable;

public class Main {
    private static final int DAYS = 16;

    public static void main(String... args) throws Exception {
        System.out.println(BenchmarkRunnable.formatNanoTime(-1));
    }

    private static void runDaysUntil() throws ReflectiveOperationException {
        for (int i = 1; i <= DAYS; i++) {
            final String name = i < 10 ? "Day0" + i : "Day" + i;

            System.out.println("Day " + i);

            Class.forName("com.schaex.days." + name)
                    .getDeclaredMethod("main", String[].class)
                    .invoke(null, (Object) null);

            System.out.println();
        }
    }
}
