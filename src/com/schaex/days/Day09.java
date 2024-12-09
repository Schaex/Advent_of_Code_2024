package com.schaex.days;

import com.schaex.arrays.ArrayUtil;
import com.schaex.util.FileUtil;
import com.schaex.util.PublicCloneable;

import java.io.IOException;

public class Day09 {
    public static void main(String... args) throws IOException {
        final MemoryRegion[][] drive = FileUtil.transformFileInputStream(9, in -> {
            // Difference between the character representation and the int value
            final int difference = '0'; // == '0' - 0

            // One byte = one character => one int
            final int totalBytes = (int) in.getChannel().size();
            final int totalGaps = totalBytes / 2;

            final MemoryRegion[] files = new MemoryRegion[totalBytes - totalGaps];
            final MemoryRegion[] gaps = new MemoryRegion[totalGaps];

            boolean isEntry = true;

            // We already know how many bytes must be read and that they alternate between "file" and "gap"
            for (int index = 0, id = 0, next; (next = in.read()) != -1; isEntry = !isEntry) {
                int nextVal = next - difference;

                if (isEntry) {
                    files[index] = new MemoryRegion(id, id += nextVal, nextVal);
                } else {
                    gaps[index++] = new MemoryRegion(id, id += nextVal, 0);
                }
            }

            return new MemoryRegion[][]{files, gaps};
        });

        System.out.print("Part one: ");

        // 6432869891895
        {
            // Clone so that we don't need to read the file again
            final MemoryRegion[] files = ArrayUtil.cloneEntries(drive[0]);
            final MemoryRegion[] gaps = ArrayUtil.cloneEntries(drive[1]);

            int backFileCursor = files.length - 1;
            int idCursor = 0;

            long checksum = 0L;

            checksumLoop:
            for (int i = 0; i < files.length; i++) {
                final MemoryRegion nextFile = files[i];
                final int dataLength = nextFile.dataLength;

                // We arrive at the last file that was moved
                if (dataLength == 0) {
                    break;
                }

                for (int j = 0; j < dataLength; j++) {
                    checksum += (long) i * idCursor++;
                }

                // Mark consumed
                files[i].dataLength = 0;

                MemoryRegion nextGap = gaps[i];

                int remainingSpace;
                
                // Fill the gap as long as it has space for more
                while ((remainingSpace = nextGap.remainingSpace()) > 0) {
                    final MemoryRegion lastValidFile = files[backFileCursor];
                    final int difference = remainingSpace - lastValidFile.dataLength;

                    // Does the entire file fit inside this gap?
                    if (difference >= 0) {
                        // Consume file
                        nextGap.dataLength += lastValidFile.dataLength;

                        for (int j = 0; j < lastValidFile.dataLength; j++) {
                            checksum += (long) backFileCursor * idCursor++;
                        }

                        // Mark consumed
                        lastValidFile.dataLength = 0;

                        // Get next entry from the back in the following cycle
                        backFileCursor--;

                        // Break out if the next file would come before this gap,
                        // indicating that all previous gaps have been closed
                        if (backFileCursor < i) {
                            break checksumLoop;
                        }
                    } else {
                        // Consume gap
                        nextGap.dataLength += remainingSpace;
                        lastValidFile.dataLength -= remainingSpace;

                        for (int j = 0; j < remainingSpace; j++) {
                            checksum += (long) backFileCursor * idCursor++;
                        }

                        // The gap is now full, thus we don't need to run any more cycles
                        break;
                    }
                }
            }

            System.out.println(checksum);
        }

        System.out.print("Part two: ");

        // 6467290479134
        {
            // We can use the objects now
            final MemoryRegion[] files = drive[0];
            final MemoryRegion[] gaps = drive[1];

            long checksum = 0L;

            for (int fileIndex = files.length - 1; fileIndex >= 0; fileIndex--) {
                final MemoryRegion file = files[fileIndex];
                final int fileLength = file.dataLength;

                // Find a gap that comes before this file
                for (int gapIndex = 0; gapIndex < fileIndex && gapIndex < gaps.length; gapIndex++) {
                    final MemoryRegion gap = gaps[gapIndex];

                    // Find first gap that could hold this file
                    if (gap.remainingSpace() >= fileLength) {
                        for (int id = gap.fromID + gap.dataLength, i = 0; i < fileLength; id++, i++) {
                            checksum += (long) fileIndex * id;
                        }

                        // Mark file consumed and gap (partially) filled
                        file.dataLength = 0;
                        gap.dataLength += fileLength;

                        // We found a leftmost gap, no need to look any further
                        break;
                    }
                }
            }

            // Finally, evaluate the checksum of the files that have not been moved
            for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
                final MemoryRegion file = files[fileIndex];
                final int fileLength = file.dataLength;

                // "If the file has not been moved into a gap"
                if (fileLength > 0) {
                    for (int id = file.fromID; id < file.toID; id++) {
                        checksum += (long) fileIndex * id;
                    }
                }
            }

            System.out.println(checksum);
        }
    }

    // Container for four integers
    private static class MemoryRegion implements PublicCloneable<MemoryRegion> {
        final int fromID, toID, totalLength;
        int dataLength;

        MemoryRegion(int fromID, int toID, int dataLength) {
            this.fromID = fromID;
            this.toID = toID;
            this.dataLength = dataLength;

            this.totalLength = toID - fromID;
        }

        int remainingSpace() {
            return totalLength - dataLength;
        }

        @Override
        public MemoryRegion clone() throws CloneNotSupportedException {
            return (MemoryRegion) super.clone();
        }

        // For debugging
        @Override
        public String toString() {
            return "[" + fromID + "," + toID + "] -> " + dataLength + "/" + totalLength;
        }
    }
}
