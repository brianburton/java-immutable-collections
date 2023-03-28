///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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
 * integer hash codes and 64 element arrays.  All of the methods are static
 * and short so they should wind up being inlined by compiler or jit.
 */
public final class IntArrayMappedTrieMath
{
    private IntArrayMappedTrieMath()
    {
    }

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
        assert maxAllowedShift >= 0;
        assert maxAllowedShift < MAX_SHIFTS;

        int shift = maxAllowedShift;
        while (shift > 0) {
            final int index1 = indexAtShift(shift, hashCode1);
            final int index2 = indexAtShift(shift, hashCode2);
            if (index1 != index2) {
                return shift;
            }
            shift -= 1;
        }
        return 0;
    }

    public static int indexFromHashCode(int hashCode)
    {
        return hashCode & MASK;
    }

    public static int liftedHashCode(int hashCode,
                                     int index)
    {
        return hashCode << SHIFT | index;
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

    public static int baseIndexAtShift(int shiftCount,
                                       int hashCode)
    {
        return shiftCount > MAX_FULL_SHIFT_NUMBER ? 0 : hashCode & (-1 << SHIFT * (1 + shiftCount));
    }

    public static int hashCodeBelowShift(int shiftCount,
                                         int hashCode)
    {
        return hashCode & ((1 << shiftCount * SHIFT) - 1);
    }

    public static int findMinimumShiftForZeroBelowHashCode(int hashCode)
    {
        final int bitNumber = (hashCode == 0) ? 1 : Integer.numberOfTrailingZeros(Integer.lowestOneBit(hashCode));
        return bitNumber / SHIFT;
    }

    public static int shift(int shiftCount,
                            int value)
    {
        return value << shiftCount * SHIFT;
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

    @Nonnull
    public static String hashString(int hashCode)
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
