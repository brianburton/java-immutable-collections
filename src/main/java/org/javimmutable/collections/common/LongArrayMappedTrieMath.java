///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.common;

import javax.annotation.Nonnull;

/**
 * Utility class that supports math related to Array Mapped Tries with
 * long hash codes and 64 element arrays.  All of the methods are static
 * and short so they should wind up being inlined by compiler or jit.
 */
public final class LongArrayMappedTrieMath
{
    private LongArrayMappedTrieMath()
    {
    }

    public static final int MAX_SHIFTS = maxShiftsForBitCount(64);
    public static final int MAX_FULL_SHIFTS = maxShiftsForBitCount(60);
    public static final int MAX_SHIFT_NUMBER = MAX_SHIFTS - 1;
    public static final int MAX_FULL_SHIFT_NUMBER = MAX_SHIFT_NUMBER - 1;

    private static final int SHIFT = 6;
    private static final long MASK = 0x3f;
    private static final long BASE_INDEX_MASK = ~MASK;

    public static long baseIndexFromHashCode(long hashCode)
    {
        return hashCode & BASE_INDEX_MASK;
    }

    public static long remainderFromHashCode(long hashCode)
    {
        return hashCode >>> SHIFT;
    }

    public static int findMaxCommonShift(int maxAllowedShift,
                                         long hashCode1,
                                         long hashCode2)
    {
        assert maxAllowedShift >= 0;
        assert maxAllowedShift < MAX_SHIFTS;

        int shift = maxAllowedShift;
        while (shift > 0) {
            final long index1 = indexAtShift(shift, hashCode1);
            final long index2 = indexAtShift(shift, hashCode2);
            if (index1 != index2) {
                return shift;
            }
            shift -= 1;
        }
        return 0;
    }

    public static int indexFromHashCode(long hashCode)
    {
        return (int)(hashCode & MASK);
    }

    public static long liftedHashCode(long hashCode,
                                      int index)
    {
        return hashCode << SHIFT | index;
    }

    public static int maxShiftsForBitCount(int bitCount)
    {
        return (bitCount + SHIFT - 1) / SHIFT;
    }

    public static int indexAtShift(int shiftCount,
                                   long hashCode)
    {
        return (int)((hashCode >>> (shiftCount * SHIFT)) & MASK);
    }

    public static long baseIndexAtShift(int shiftCount,
                                        long hashCode)
    {
        return shiftCount > MAX_FULL_SHIFT_NUMBER ? 0L : hashCode & (-1L << SHIFT * (1 + shiftCount));
    }

    public static long withIndexAtShift(int shiftCount,
                                        long hashCode,
                                        int index)
    {
        final int shift = shiftCount * SHIFT;
        final long mask = MASK << shift;
        final long bits = ((long)index) << shift;
        return (hashCode & ~mask) | bits;
    }

    public static long hashCodeBelowShift(int shiftCount,
                                          long hashCode)
    {
        return hashCode & ((1L << shiftCount * SHIFT) - 1);
    }

    public static int findMinimumShiftForZeroBelowHashCode(long hashCode)
    {
        final int bitNumber = (hashCode == 0) ? 1 : Long.numberOfTrailingZeros(Long.lowestOneBit(hashCode));
        return bitNumber / SHIFT;
    }

    public static int findMaxShiftForHashCode(long hashCode)
    {
        final int bitNumber = (hashCode == 0) ? 1 : Long.numberOfTrailingZeros(Long.highestOneBit(hashCode));
        return bitNumber / SHIFT;
    }

    public static long shift(int shiftCount,
                             long value)
    {
        return value << shiftCount * SHIFT;
    }

    public static long hash(int... values)
    {
        assert values.length <= MAX_SHIFTS;
        long value = 0;
        boolean started = false;
        for (int v : values) {
            if (started || v != 0) {
                value = liftedHashCode(value, v);
                started = true;
            }
        }
        return value;
    }

    @Nonnull
    public static String hashString(long hashCode)
    {
        final StringBuilder sb = new StringBuilder();
        for (int shiftCount = MAX_SHIFT_NUMBER; shiftCount >= 0; shiftCount -= 1) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(indexAtShift(shiftCount, hashCode));
        }
        return sb.toString();
    }
}
