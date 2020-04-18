package org.javimmutable.collections.common;

/**
 * Utility class that supports math related to Hash Array Mapped Tries
 * that use 64 element arrays.  All of the methods are static and short
 * so they should wind up being inlined by compiler of jit.
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
}
