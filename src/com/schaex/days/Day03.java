package com.schaex.days;

import com.schaex.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day03 {
    public static void main(String... args) throws IOException {
        final File file = DaysUtil.resource(3);

        // Get entire file as a single string
        final String text = FileUtil.readEntireFile(file);

        System.out.print("Part one: ");

        // 173419328
        {
            // AtomicInteger to be able to increment using a reference
            final AtomicInteger count = new AtomicInteger();

            // Results must be "mul(num1,num2)" with num1 and num2 as a 1-3 digit integer
            // Compute lazily using a stream
            Pattern.compile("mul\\([0-9]{1,3},[0-9]{1,3}\\)")
                    .matcher(text)
                    .results()
                    .map(MatchResult::group)
                    .map(result -> {
                        final int leftPar = result.indexOf('(');
                        final int comma = result.indexOf(',', leftPar);

                        final int first = Integer.parseInt(result.substring(leftPar + 1, comma));
                        final int second = Integer.parseInt(result.substring(comma + 1, result.length() - 1));

                        return first * second;
                    })
                    .forEach(count::addAndGet);

            System.out.println(count);
        }

        System.out.print("Part two: ");

        // 90669332
        {
            // Again, an AtomicInteger to be able to increment using a reference
            final AtomicInteger count = new AtomicInteger();

            // AtomicBoolean to have a switch to account for "do()" and "don't()"
            final AtomicBoolean enabled = new AtomicBoolean(true);

            // Results must be "mul(num1,num2)" with num1 and num2 as a 1-3 digit integer (as before) or "do()" or "don't()"
            // Again, compute lazily using a stream
            Pattern.compile("mul\\([0-9]{1,3},[0-9]{1,3}\\)|do\\(\\)|don't\\(\\)")
                    .matcher(text)
                    .results()
                    .map(MatchResult::group)
                    .forEach(result -> {
                        switch (result) {
                            case "do()" -> enabled.set(true);
                            case "don't()" -> enabled.set(false);
                            default -> {
                                if (!enabled.get()) {
                                    break;
                                }

                                final int leftPar = result.indexOf('(');
                                final int comma = result.indexOf(',', leftPar);

                                final int first = Integer.parseInt(result.substring(leftPar + 1, comma));
                                final int second = Integer.parseInt(result.substring(comma + 1, result.length() - 1));

                                count.addAndGet(first * second);
                            }
                        }
                    });

            System.out.println(count);
        }
    }
}
