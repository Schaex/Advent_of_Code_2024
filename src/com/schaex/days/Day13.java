package com.schaex.days;

import com.schaex.util.FileUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;

public class Day13 {
    public static void main(String... args) throws IOException {
        FileUtil.transformFileLines(13, stream -> {
            final Iterator<String> it = stream.iterator();

            int count1 = 0;
            long count2 = 0L;

            while (it.hasNext()) {
                final int dxA, dyA, dxB, dyB, prizeX, prizeY;
                int i1, i2;
                String line = it.next();

                // Parse first line
                i1 = line.indexOf('+') + 1;
                i2 = line.indexOf(',', i1);

                dxA = Integer.parseInt(line.substring(i1, i2));
                dyA = Integer.parseInt(line.substring(line.indexOf('+', i2) + 1));

                line = it.next();

                // Parse second line
                i1 = line.indexOf('+') + 1;
                i2 = line.indexOf(',', i1);

                dxB = Integer.parseInt(line.substring(i1, i2));
                dyB = Integer.parseInt(line.substring(line.indexOf('+', i2) + 1));

                line = it.next();

                // Finally, parse third line
                i1 = line.indexOf('=') + 1;
                i2 = line.indexOf(',', i1);

                prizeX = Integer.parseInt(line.substring(i1, i2));
                prizeY = Integer.parseInt(line.substring(line.indexOf('=', i2) + 1));

                final ClawMachine machine = new ClawMachine(dxA, dyA, dxB, dyB, prizeX, prizeY);

                count1 += machine.part1();
                count2 += machine.part2();

                // Dispose of the next line as it's empty
                if (it.hasNext()) {
                    it.next();
                }
            }

            // 37686
            System.out.println("Part one: " + count1);

            // 77204516023437
            System.out.println("Part two: " + count2);

            return null;
        });
    }

    // Container for the numbers
    private record ClawMachine(int dxA, int dyA, int dxB, int dyB, int prizeX, int prizeY) {
        static final int MAX_PRESSES_PER_BUTTON = 100;
        static final int A_COST = 3;
        static final int B_COST = 1;

        static final long PART_TWO_ADDEND = 10000000000000L;
        static final BigInteger BIG_A_COST = BigInteger.valueOf(A_COST);
        static final BigInteger BIG_B_COST = BigInteger.valueOf(B_COST);

        int part1() {
            for (int a = 0; a <= MAX_PRESSES_PER_BUTTON; a++) {
                for (int b = 0; b <= MAX_PRESSES_PER_BUTTON; b++) {
                    final int x = a * dxA + b * dxB;
                    final int y = a * dyA + b * dyB;

                    // Did we get the desired coordinates? Did we overshoot?
                    if (x == prizeX && y == prizeY) {
                        return a * A_COST + b * B_COST;
                    } else if (x > prizeX || y > prizeY) {
                        // There is no use to try any more iterations in this cycle
                        break;
                    }
                }
            }

            // We did not find a pair A and B
            return 0;
        }

        // Basically just a system of equations. 2 unknown variables and 2 equations mean
        // that we can find a single solution.
        // However, we still need to check whether it is an integer solution or not.
        long part2() {
            final BigInteger dxA, dyA, dxB, dyB, prizeX, prizeY;

            dxA = BigInteger.valueOf(this.dxA);
            dyA = BigInteger.valueOf(this.dyA);
            dxB = BigInteger.valueOf(this.dxB);
            dyB = BigInteger.valueOf(this.dyB);
            prizeX = BigInteger.valueOf(this.prizeX + PART_TWO_ADDEND);
            prizeY = BigInteger.valueOf(this.prizeY + PART_TWO_ADDEND);

            BigInteger enumerator = (dyA.multiply(prizeX)).subtract(dxA.multiply(prizeY));
            BigInteger denominator = (dyA.multiply(dxB)).subtract(dxA.multiply(dyB));

            final BigInteger[] bigBAndRemainder = enumerator.divideAndRemainder(denominator);

            // Is the quotient an integer or a decimal number?
            if (!bigBAndRemainder[1].equals(BigInteger.ZERO)) {
                return 0L;
            }

            final BigInteger bigB = bigBAndRemainder[0];

            enumerator = prizeX.subtract(dxB.multiply(bigB));

            final BigInteger[] bigAAndRemainder = enumerator.divideAndRemainder(dxA);

            if (!bigAAndRemainder[1].equals(BigInteger.ZERO)) {
                return 0L;
            }

            // Call to longValueExact() to make sure that the number can be expressed as a long.
            // Otherwise, an exception will be thrown.
            return BIG_A_COST.multiply(bigAAndRemainder[0])
                    .add(BIG_B_COST.multiply(bigB))
                    .longValueExact();
        }
    }
}
