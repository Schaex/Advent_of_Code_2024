package com.schaex.days;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

public class Day17 {
    public static void main(String... args) {
        // Initialize constants from the input
        final long part1RegA = 35200350L;
        final int[] program = {2,4,1,2,7,5,4,7,1,3,5,5,0,3,3,0};

        // Initialize a computer that will be used for both tasks
        final Computer computer = new Computer(part1RegA, 0L, 0L, null, program);

        System.out.print("Part one: ");

        // 2,7,4,7,2,1,7,5,1
        {
            final StringBuilder builder = new StringBuilder();

            // Make the computer output on the StringBuilder
            computer.output = value -> builder.append(value).append(',');

            computer.makeOutput();

            // Remove the last comma
            final String output = builder.substring(0, builder.length() - 1);

            System.out.println(output);
        }

        System.out.print("Part two: ");

        /*
            The next part required analyzing the control flow of the program
            -> The program always uses the last 3 bit of register A
            -> Printing ALWAYS occurs as operation 6
            -> Right-shift of register A by 3 as operation 7
            -> Jump to the start of the program as long as the value in register A is larger than 0
         */

        // In Base_10: 37221274271220
        // In Base_2:        001 000 011 101 101 001 000 000 110 101 001 011 110 111 110 100
        // 3-wide windows:    1   0   3   5   5   1   0   0   6   5   1   3   6   7   6   4
        {
            // Sets so that we don't keep duplicates
            Set<Long> allResults = new HashSet<>();
            Set<Long> intermediates = new HashSet<>();
            Set<Long> swap;

            // First value
            allResults.add(0L);

            // Use AtomicInteger so that we can use its reference
            final AtomicInteger value = new AtomicInteger();

            // Always "prints" to the AtomicInteger's value
            computer.output = value::set;

            // Iterate from the right
            for (int index = program.length - 1; index >= 0; index--) {
                for (long result : allResults) {
                    // Initial left-shift so that we only have to do it once
                    result <<= 3;

                    // Try all windows 0b000 to 0b111 (= 0 to 7)
                    for (int window = 0; window < 0b1_000; window++) {
                        // Append the current window
                        final long currentTestA = result | window;

                        // Reset the computer and set this as initial value in register A
                        computer.reset(currentTestA);

                        // Do the first print
                        for (int i = 0; i < 6; i++) {
                            computer.next();
                        }

                        // Compare
                        if (value.get() == program[index]) {
                            intermediates.add(currentTestA);
                        }
                    }
                }

                // Clear allResults and swap references with intermediate
                allResults.clear();
                swap = allResults;
                allResults = intermediates;
                intermediates = swap;
            }

            // Initialize to a value that can only be overwritten
            long min = Long.MAX_VALUE;

            // Find minimum
            for (long result : allResults) {
                if (result < min) {
                    min = result;
                }
            }

            System.out.println(min);
        }
    }

    private static class Computer {
        // Bit mask for modulo 8
        static final long MOD_8_MASK = 0b111L;

        // Registers
        long regA;
        long regB;
        long regC;

        final int[] instructions;
        int functionPointer = 0;

        IntConsumer output;

        Computer(long regA, long regB, long regC, IntConsumer output, int... instructions) {
            this.regA = regA;
            this.regB = regB;
            this.regC = regC;
            this.output = output;
            this.instructions = instructions;
        }

        // call next() until we reach the end and no more jumps occur
        void makeOutput() {
            while (functionPointer < instructions.length) {
                next();
            }
        }

        // Advance functionPointer as we go
        void next() {
            compute(instructions[functionPointer++], instructions[functionPointer++]);
        }

        // Primarily for part 2 so that we don't have to instantiate new objects over and over again
        void reset(long newRegA) {
            regA = newRegA;
            regB = regC = 0L;
            functionPointer = 0;
        }

        long getOperand(int operand) {
            return switch (operand) {
                case 0, 1, 2, 3 -> operand;
                case 4 -> regA;
                case 5 -> regB;
                case 6 -> regC;
                default -> throw new IllegalStateException("Illegal operand: " + operand);
            };
        }

        void compute(int opcode, int comboOperand) {
            switch (opcode) {
                case 0 -> regA >>>= getOperand(comboOperand);
                case 1 -> regB ^= comboOperand;
                case 2 -> regB = getOperand(comboOperand) & MOD_8_MASK;
                case 3 -> {
                    if (regA != 0) {
                        functionPointer = comboOperand;
                    }
                }
                case 4 -> regB ^= regC;
                case 5 -> output.accept((int) (getOperand(comboOperand) & MOD_8_MASK));
                case 6 -> regB = regA >>> getOperand(comboOperand);
                case 7 -> regC = regA >>> getOperand(comboOperand);
                default -> throw new IllegalStateException("Illegal opcode: " + opcode);
            }
        }
    }
}
