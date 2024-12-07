package com.schaex;

import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String... args) throws Exception {
        runDaysUntil(7);
    }

    private static void runDaysUntil(int day) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for (int i = 1; i <= day; i++) {
            final String name = i < 10 ? "Day0" + i : "Day" + i;

            System.out.println(name);

            Class.forName("com.schaex.days." + name)
                    .getDeclaredMethod("main", String[].class)
                    .invoke(null, (Object) null);

            System.out.println();
        }
    }
}
