package com.schaex.days;

import com.schaex.frequently_used.Point;
import com.schaex.util.FileUtil;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day21 {
    private static final boolean TEST = false;

    // The five codes from the input file
    private static final Code[] CODES;

    private static final char[][] NUM_PAD = {
            {'7', '8', '9'},
            {'4', '5', '6'},
            {'1', '2', '3'},
            {' ', '0', 'A'}
    };

    private static final char[][] DIRECTIONAL_PAD = {
            {' ', '^', 'A'},
            {'<', 'v', '>'}
    };

    private static final Map<Character, Key> NUM_KEYS = new HashMap<>();
    private static final Map<Character, Key> DIRECTIONAL_KEYS = new HashMap<>();

    // Matches all substrings that consist of any number of keys that are not "A", followed by an "A"
    private static final Matcher MATCHER = Pattern.compile("[^A]*A").matcher("");

    static {
        try {
            CODES = FileUtil.transformFileLines(TEST ? -21 : 21, stream ->
                    stream.map(Code::new).toArray(Code[]::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Instantiate the keys so that they will be usable for the entire program
        final BiConsumer<char[][], Map<Character, Key>> filler = (grid, map) -> {
            for (int y = 0; y < grid.length; y++) {
                final char[] line = grid[y];

                for (int x = 0; x < line.length; x++) {
                    final char c = line[x];

                    map.put(c, new Key(x, y, c));
                }
            }
        };

        filler.accept(NUM_PAD, NUM_KEYS);
        filler.accept(DIRECTIONAL_PAD, DIRECTIONAL_KEYS);
    }

    public static void main(String... args) throws Exception {
        // Instantiate the two robots that will be used
        final Robot numRobot = new Robot(NUM_PAD, NUM_KEYS);
        final Robot dirRobot = new Robot(DIRECTIONAL_PAD, DIRECTIONAL_KEYS);

        // Maps an input to an array of outputs
        final Map<String, String[]> lookup = new HashMap<>();

        // Fill the lookup, we only need to mind the directional keys
        for (Map<Key, char[][]> innerMap : dirRobot.allowedPathsMap.values()) {
            for (char[][] allowedPaths : innerMap.values()) {
                for (char[] allowedPath : allowedPaths) {
                    // First, append an "A" to the path
                    final String input = new String(allowedPath) + "A";

                    System.out.println(input + ":");

                    // Perform multiple computations to only keep the shortest paths
                    Set<Code> codes = dirRobot.computeCodes(Collections.singletonList(new Code(input)));
                    codes = dirRobot.computeCodes(codes);
                    codes = dirRobot.computeCodes(codes);

                    // Iterate over the set, mapping its elements to the input two earlier
                    final String output = codes.stream()
                            .map(code -> code.parent.parent)
                            .distinct()
                            .peek(code -> System.out.println("\t" + code))
                            .map(Code::toString)
                            .toList().getLast();
                            //.orElseThrow(() -> new NoSuchElementException("No result present for input: " + input));

                    // Split the output into separate movements
                    String[] results = split(output);

                    lookup.put(input, results);

                    System.out.println();
                }
            }
        }

        Map<String, AtomicLong> alreadyFound = new HashMap<>();
        Map<String, AtomicLong> nextFound = new HashMap<>();
        Map<String, AtomicLong> swap;

        long countP1 = 0L;
        long countP2 = 0L;

        for (Code code : CODES) {
            final String input = code.input;
            final long numericalValue = 100L * (input.charAt(0) - '0') + 10L * (input.charAt(1) - '0') + (input.charAt(2) - '0');

            Set<Code> computedCodes = numRobot.computeCodes(Collections.singleton(code));
            computedCodes = dirRobot.computeCodes(computedCodes);
            computedCodes = dirRobot.computeCodes(computedCodes);

            // Find the shortest result
            final Code result = computedCodes.stream()
                    .min(Comparator.comparingInt(internalCode -> internalCode.parent.parent.input.length()))
                    .orElseThrow(() -> new RuntimeException("Error at input: " + code));

            countP1 += (result.input.length() * numericalValue);

            // Mark these as found
            for (String partOfResult : split(result.input)) {
                nextFound.computeIfAbsent(partOfResult, key -> new AtomicLong())
                        .getAndIncrement();
            }

            // Do the remaining iterations
            for (int i = 0; i < 24; i++) {
                for (Map.Entry<String, AtomicLong> entry : alreadyFound.entrySet()) {
                    final String[] allNext = lookup.get(entry.getKey());
                    final long currentCount = entry.getValue().get();

                    // Check
                    if (allNext == null) {
                        throw new RuntimeException(entry.getKey() + " in " + code);
                    }

                    for (String next : allNext) {
                        nextFound.computeIfAbsent(next, key -> new AtomicLong())
                                .getAndAdd(currentCount);
                    }
                }

                // Clear and swap references
                alreadyFound.clear();
                swap = alreadyFound;
                alreadyFound = nextFound;
                nextFound = swap;
            }

            long intermediateCount = 0L;

            // Calculate sum of all lengths
            for (Map.Entry<String, AtomicLong> entry : alreadyFound.entrySet()) {
                intermediateCount += (entry.getKey().length() * entry.getValue().get());
            }

            // Add
            countP2 += (intermediateCount * numericalValue);

            // Clear for next cycle
            alreadyFound.clear();
            nextFound.clear();
        }

        // 163920
        System.out.println("Part one: " + countP1);

        // Too high:
        // 232934196496666
        // 206690724815322
        // Too low:
        // 93054943676070
        System.out.println("Part two: " + countP2);
    }

    private static String[] split(String output) {
        return MATCHER.reset(output)
                .results()
                .map(MatchResult::group)
                .toArray(String[]::new);
    }

    private static class Robot {
        private static final char[][] NO_MOVEMENT = new char[][]{{}};

        final char[][] keyPad;
        final Map<Character, Key> keys;
        final Key startingKey;

        final Map<Key, Map<Key, char[][]>> allowedPathsMap = new HashMap<>();

        Robot(char[][] keyPad, Map<Character, Key> keys) {
            this.keyPad = keyPad;
            this.keys = keys;
            this.startingKey = keys.get('A');

            for (Key from : keys.values()) {
                if (from.forbidden) {
                    continue;
                }

                for (Key to : keys.values()) {
                    if (to.forbidden) {
                        continue;
                    }

                    // Fast computation if the current key and next key are similar
                    if (from == to) {
                        allowedPathsMap.computeIfAbsent(from, key -> new HashMap<>())
                                .put(to, NO_MOVEMENT);

                        continue;
                    }

                    final Movement movement = Movement.fromCache(from.dx(to), from.dy(to));
                    final char[][] possiblePaths = movement.possibleChars;

                    final List<char[]> allowedPaths = new ArrayList<>(possiblePaths.length);

                    // Exclude paths that are forbidden, i.e. that go over a blank key
                    loop:
                    for (char[] path : possiblePaths) {
                        int x = from.x;
                        int y = from.y;

                        for (char c : path) {
                            switch (c) {
                                case '<' -> x--;
                                case '>' -> x++;
                                case '^' -> y--;
                                case 'v' -> y++;
                            }

                            if (keys.get(keyPad[y][x]).forbidden) {
                                continue loop;
                            }
                        }

                        allowedPaths.add(path);
                    }

                    allowedPathsMap.computeIfAbsent(from, key -> new HashMap<>())
                            .put(to, allowedPaths.toArray(char[][]::new));
                }
            }
        }

        Set<Code> computeCodes(Iterable<Code> codes) {
            final Set<Code> minimalCodes = new HashSet<>();
            int minLength = Integer.MAX_VALUE;

            for (Code code : codes) {
                // We always start here
                Key currentKey = startingKey;
                final List<char[][]> movements = new ArrayList<>();

                // Lazily iterate over the characters
                PrimitiveIterator.OfInt iter = code.input.chars().iterator();

                while (iter.hasNext()) {
                    final Key nextKey = keys.get((char) iter.nextInt());
                    final char[][] allowedPaths = allowedPathsMap.get(currentKey).get(nextKey);

                    movements.add(allowedPaths);
                    currentKey = nextKey;
                }

                final Permutations permutations = new Permutations(movements.toArray(char[][][]::new));

                // Only keep the shortest paths
                while (permutations.hasNext()) {
                    final String next = permutations.next();
                    final int length = next.length();

                    if (length > minLength) {
                        continue;
                    }

                    if (length < minLength) {
                        minLength = length;
                        minimalCodes.clear();
                    }

                    minimalCodes.add(new Code(next, code));
                }
            }

            return minimalCodes;
        }
    }

    // Encapsulate the coordinates and character of a key
    private static class Key extends Point {
        final char keyStroke;
        final boolean forbidden;

        Key(int x, int y, char keyStroke) {
            super(x, y);
            this.keyStroke = keyStroke;
            forbidden = keyStroke == ' ';
        }

        @Override
        public String toString() {
            return super.toString() + " -> " + keyStroke;
        }
    }

    // Encapsulate an input string while also having custom comparability
    private record Code(String input, Code parent) implements Comparable<Code> {
        Code(String input) {
            this(input, null);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Code other)) return false;
            return input.equals(other.input);
        }

        @Override
        public int hashCode() {
            return input.hashCode();
        }

        @Override
        public int compareTo(Code other) {
            return Integer.compare(input.length(), other.input.length());
        }

        @Override
        public String toString() {
            return input;
        }
    }

    private static class Movement {
        static final int[][][][] PERMUTATIONS;
        static final Movement[][] CACHE = new Movement[7][5];

        final int dx, dy;
        final char[][] possibleChars;

        static {
            PERMUTATIONS = new int[4][3][][];

            // Hard-coded permutations. 0 = x, 1 = y
            PERMUTATIONS[0][0] = new int[][]{{}};
            PERMUTATIONS[0][1] = new int[][]{{0}};
            PERMUTATIONS[0][2] = new int[][]{{0, 0}};
            PERMUTATIONS[1][0] = new int[][]{{1}};
            PERMUTATIONS[1][1] = new int[][]{{0, 1}, {1, 0}};
            PERMUTATIONS[1][2] = new int[][]{{0, 0, 1}, {0, 1, 0}, {1, 0, 0}};
            PERMUTATIONS[2][0] = new int[][]{{1, 1}};
            PERMUTATIONS[2][1] = new int[][]{{1, 1, 0}, {1, 0, 1}, {0, 1, 1}};
            PERMUTATIONS[2][2] = new int[][]{{0, 0, 1, 1}, {0, 1, 0, 1}, {1, 0, 0, 1}, {0, 1, 1, 0}, {1, 0, 1, 0}, {1, 1, 0, 0}};
            PERMUTATIONS[3][0] = new int[][]{{1, 1, 1}};
            PERMUTATIONS[3][1] = new int[][]{{0, 1, 1, 1}, {1, 0, 1, 1}, {1, 1, 0, 1}, {1, 1, 1, 0}};
            PERMUTATIONS[3][2] = new int[][]{{1, 1, 1, 0, 0}, {1, 1, 0, 1, 0}, {1, 0, 1, 1, 0}, {0, 1, 1, 1, 0}, {1, 1, 0, 0, 1},
                                             /*{1, 0, 1, 0, 1},*/ {0, 1, 1, 0, 1}, {1, 0, 0, 1, 1}, {0, 1, 0, 1, 1}, {0, 0, 1, 1, 1}};
        }

        // Don't need to instantiate over and over
        static Movement fromCache(int dx, int dy) {
            final Movement movement = CACHE[dy + 3][dx + 2];

            if (movement != null) {
                return movement;
            }

            return CACHE[dy + 3][dx + 2] = new Movement(dx, dy);
        }

        Movement(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;

            final int absX = Math.abs(dx);
            final int absY = Math.abs(dy);

            final char xDir = dx < 0 ? '<' : '>';
            final char yDir = dy < 0 ? '^' : 'v';

            final int[][] permutations = PERMUTATIONS[absY][absX];
            this.possibleChars = new char[permutations.length][absX + absY];

            for (int i = 0; i < possibleChars.length; i++) {
                final int[] permutationLine = permutations[i];
                final char[] line = possibleChars[i];

                for (int j = 0; j < line.length; j++) {
                    line[j] = permutationLine[j] == 0 ? xDir : yDir;
                }
            }
        }
    }

    // String iterator that generates all permutations
    private static class Permutations implements Iterator<String> {
        final char[][][] movements;
        final int[] indexes;
        boolean depleted = false;

        Permutations(char[][][] movements) {
            this.movements = movements;
            this.indexes = new int[movements.length];
        }

        @Override
        public String next() {
            final StringBuilder builder = new StringBuilder();

            // Iterate by using the curring indexes
            for (int i = 0; i < movements.length; i++) {
                final char[] chars = movements[i][indexes[i]];

                builder.append(chars).append('A');
            }

            // For the next call
            prepareNext(0);

            return builder.toString();
        }

        @Override
        public boolean hasNext() {
            return !depleted;
        }

        void prepareNext(int index) {
            // If we surpassed the maximum index we are depleted
            if (index == indexes.length) {
                depleted = true;

                return;
            }

            final char[][] movement = movements[index];
            final int currentIndex = indexes[index];

            if (currentIndex + 1 == movement.length) {
                // We already are at the maximum index before calling this method
                // => reset the index and call this method on the next element
                indexes[index] = 0;
                prepareNext(index + 1);
            } else {
                // We can safely increase the index
                indexes[index]++;
            }
        }
    }
}
