package org.javimmutable.collections.common;

/**
 * Utility class that supports math related to Hash Array Mapped Tries
 * that use 32 element arrays.  All of the methods are static and short
 * so they should wind up being inlined by compiler or jit.
 */
public final class HamtIntMath
{
    private HamtIntMath()
    {
    }

    public static final int ARRAY_SIZE = 32;

    private static final int SHIFT = 5;
    private static final int MASK = 0x1f;

    public static int remainderFromHashCode(int hashCode)
    {
        return hashCode >>> SHIFT;
    }

    public static int indexFromHashCode(int hashCode)
    {
        return hashCode & MASK;
    }

    public static int bitFromIndex(int index)
    {
        return 1 << index;
    }

    public static int liftedHashCode(int hashCode,
                                     int index)
    {
        return hashCode << SHIFT | index;
    }

    public static boolean bitIsAbsent(int bitmask,
                                      int bit)
    {
        return (bitmask & bit) == 0L;
    }

    public static boolean bitIsPresent(int bitmask,
                                       int bit)
    {
        return (bitmask & bit) != 0L;
    }

    public static int addBit(int bitmask,
                             int bit)
    {
        return bitmask | bit;
    }

    public static int removeBit(int bitmask,
                                int bit)
    {
        return bitmask & ~bit;
    }

    public static int arrayIndexForBit(int bitmask,
                                       int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }

    public static int bitCount(int bitmask)
    {
        int count = 0;
        while (bitmask != 0) {
            if (bitIsPresent(bitmask, 1)) {
                count += 1;
            }
            bitmask >>>= 1;
        }
        return count;
    }
}
