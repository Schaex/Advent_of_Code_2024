package com.schaex.days;

import com.schaex.util.FileUtil;
import com.schaex.util.tuples.Pair;

import java.io.IOException;
import java.util.*;

public class Day19 {
    private static final Map<Character, List<char[]>> AVAILABLE_PATTERNS;
    private static final char[][] NEEDED_PATTERNS;

    static {
        try {
            final Pair<Map<Character, List<char[]>>, char[][]> input = FileUtil.transformFileLines(19, stream -> {
                // Convert to iterator for more complex behavior
                final Iterator<String> it = stream.iterator();

                final Map<Character, List<char[]>> available = new HashMap<>();

                // Split first line at ", " and collect in the map
                for (String pattern : it.next().split(", ")) {
                    final char[] stripes = pattern.toCharArray();

                    available.computeIfAbsent(stripes[0], key -> new ArrayList<>())
                            .add(stripes);
                }

                // Skip next line
                it.next();

                final List<char[]> needed = new ArrayList<>();

                // Collect the rest
                while (it.hasNext()) {
                    needed.add(it.next().toCharArray());
                }

                return new Pair<>(available, needed.toArray(char[][]::new));
            });

            AVAILABLE_PATTERNS = input.left();
            NEEDED_PATTERNS = input.right();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Sort by length so that we can perform an early break from a loop later
        for (List<char[]> list : AVAILABLE_PATTERNS.values()) {
            list.sort(Comparator.comparingInt(stripes -> stripes.length));
        }
    }

    public static void main(String... args) {
        long intermediate;

        int countPart1 = 0;
        long countPart2 = 0L;

        for (char[] pattern : NEEDED_PATTERNS) {
            if ((intermediate = numberOfWays(pattern)) > 0L) {
                countPart1++;
                countPart2 += intermediate;
            }
        }

        // 206
        System.out.println("Part one: " + countPart1);

        // 622121814629343
        System.out.println("Part two: " + countPart2);
    }

    private static long numberOfWays(char[] stripes) {
        // Keep track of the next indexes and the ones that have already been visited
        final Set<Integer> needToVisit = new HashSet<>();
        final Set<Integer> alreadyVisited = new HashSet<>();

        // Store the scores as Longs because we would get an integer overflow otherwise
        final Map<Integer, Long> scores = new HashMap<>();

        // Start at index 0 with default score 1
        needToVisit.add(0);
        scores.put(0, 1L);

        Optional<Integer> next;

        // Loop until we can't go on anymore, from the bottom up
        while ((next = needToVisit.stream().min(Integer::compare)).isPresent()) {
            final int fromIndex = next.get();

            // This only happens if we are able to make the pattern
            if (fromIndex == stripes.length) {
                break;
            }

            // No need to check this index again
            needToVisit.remove(fromIndex);

            if (!alreadyVisited.add(fromIndex)) {
                continue;
            }

            final List<char[]> patterns = AVAILABLE_PATTERNS.get(stripes[fromIndex]);

            if (patterns == null) {
                continue;
            }

            // Fetch the score for this index. This is bound to succeed
            final long score = scores.get(fromIndex);

            for (char[] pattern : patterns) {
                final int toIndex = fromIndex + pattern.length;

                // The pattern and the following ones are too long
                if (toIndex > stripes.length) {
                    break;
                }

                // Compare the specified range in "stripes" with the pattern
                if (Arrays.equals(stripes, fromIndex, toIndex, pattern, 0, pattern.length)) {
                    // Increment the score at toIndex
                    scores.compute(toIndex, (key, value) -> {
                        if (value == null) {
                            // We can reach "toIndex" but have not been there yet
                            needToVisit.add(toIndex);

                            return score;
                        }

                        // We have been there before so we just need to increment the value
                        return value + score;
                    });
                }
            }
        }

        // Return the score if this mapping exists, 0 otherwise
        return scores.getOrDefault(stripes.length, 0L);
    }
}
