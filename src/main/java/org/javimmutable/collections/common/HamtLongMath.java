package org.javimmutable.collections.common;

import org.javimmutable.collections.Indexed;

import javax.annotation.Nonnull;

/**
 * Utility class that supports math related to Hash Array Mapped Tries
 * that use 64 element arrays.  All of the methods are static and short
 * so they should wind up being inlined by compiler or jit.
 */
public final class HamtLongMath
{
    private HamtLongMath()
    {
    }

    public static final int ARRAY_SIZE = 64;
    public static final int MAX_INDEX = ARRAY_SIZE - 1;
    public static final int MAX_SHIFTS = maxShiftsForBitCount(32);
    public static final int MAX_FULL_SHIFTS = maxShiftsForBitCount(30);
    public static final int MAX_SHIFT_NUMBER = MAX_SHIFTS - 1;
    public static final int MAX_FULL_SHIFT_NUMBER = MAX_SHIFT_NUMBER - 1;

    private static final int SHIFT = 6;
    private static final int MASK = 0x3f;
    private static final int BASE_INDEX_MASK = ~MASK;

    public static int baseIndexFromHashCode(int hashCode)
    {
        return hashCode & BASE_INDEX_MASK;
    }

    public static int remainderFromHashCode(int hashCode)
    {
        return hashCode >>> SHIFT;
    }

    public static int findMaxCommonShift(int maxAllowedShift,
                                         int hashCode1,
                                         int hashCode2)
    {
        int shift = maxAllowedShift;
        while (shift > 0) {
            final int index1 = indexAtShift(shift, hashCode1);
            final int index2 = indexAtShift(shift, hashCode2);
            if (index1 != index2) {
                return shift;
            }
            shift -= 1;
        }
        assert hashCode1 != hashCode2;
        return 0;
    }

    public static int indexFromHashCode(int hashCode)
    {
        return hashCode & MASK;
    }

    public static long bitFromIndex(int index)
    {
        return 1L << index;
    }

    public static int liftedHashCode(int hashCode,
                                     int index)
    {
        return hashCode << SHIFT | index;
    }

    public static boolean bitIsAbsent(long bitmask,
                                      long bit)
    {
        return (bitmask & bit) == 0L;
    }

    public static boolean bitIsPresent(long bitmask,
                                       long bit)
    {
        return (bitmask & bit) != 0L;
    }

    public static long addBit(long bitmask,
                              long bit)
    {
        return bitmask | bit;
    }

    public static long removeBit(long bitmask,
                                 long bit)
    {
        return bitmask & ~bit;
    }

    public static int arrayIndexForBit(long bitmask,
                                       long bit)
    {
        return Long.bitCount(bitmask & (bit - 1));
    }

    public static int maxShiftsForBitCount(int bitCount)
    {
        return (bitCount + SHIFT - 1) / SHIFT;
    }

    public static int indexAtShift(int shiftCount,
                                   int hashCode)
    {
        return (hashCode >>> (shiftCount * SHIFT)) & MASK;
    }

    public static int remainderAtShift(int shiftCount,
                                       int hashCode)
    {
        final int bitShift = (1 + shiftCount) * SHIFT;
        return (hashCode >>> bitShift) << bitShift;
    }

    public static int bitCount(long bitmask)
    {
        return Long.bitCount(bitmask);
    }

    @Nonnull
    public static Indexed<Integer> indices(long bitmask)
    {
        return new Indexes(bitmask);
    }

    public static int hash(int shift5,
                           int shift4,
                           int shift3,
                           int shift2,
                           int shift1,
                           int shift0)
    {
        int answer = (shift5 << SHIFT) | shift4;
        answer = (answer << SHIFT) | shift3;
        answer = (answer << SHIFT) | shift2;
        answer = (answer << SHIFT) | shift1;
        return (answer << SHIFT) | shift0;
    }

    public static long bitmask(int... indices)
    {
        long answer = 0;
        for (int index : indices) {
            answer |= bitFromIndex(index);
        }
        return answer;
    }

    private static class Indexes
        implements Indexed<Integer>
    {
        private final long bitmask;

        private Indexes(long bitmask)
        {
            this.bitmask = bitmask;
        }

        @Override
        public Integer get(int index)
        {
            long remaining = bitmask;
            while (index > 0) {
                remaining = remaining & ~Long.lowestOneBit(remaining);
                index -= 1;
            }
            long bit = Long.lowestOneBit(remaining);
            if (bit == 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return Long.numberOfTrailingZeros(bit);
        }

        @Override
        public int size()
        {
            return bitCount(bitmask);
        }
    }
}
