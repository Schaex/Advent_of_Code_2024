package com.schaex.days;

import com.schaex.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Day03 {
    public static void main(String[] args) throws IOException {
        final File file = DaysUtil.resource("Day_03.txt");

        final String text = FileUtil.readEntireFile(file);

        System.out.print("Part one: ");

        // 173419328
        {
            final AtomicInteger count = new AtomicInteger();

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
            final AtomicInteger count = new AtomicInteger();
            final AtomicBoolean enabled = new AtomicBoolean(true);

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
