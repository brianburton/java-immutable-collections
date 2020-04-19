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

    private static final int SHIFT = 6;
    private static final int MASK = 0x3f;

    public static int remainderFromHashCode(int hashCode)
    {
        return hashCode >>> SHIFT;
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

    public static int bitCount(long bitmask)
    {
        return Long.bitCount(bitmask);
    }

    @Nonnull
    public static Indexed<Integer> indices(long bitmask)
    {
        return new Indexes(bitmask);
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
