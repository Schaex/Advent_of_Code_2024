package com.schaex;

public class Main {
    private static final int DAYS = 15;

    public static void main(String... args) throws Exception {
        runDaysUntil();
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
