///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.Temp;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.IntConsumer;

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

    public static long leastBit(long bitmask)
    {
        return Long.lowestOneBit(bitmask);
    }

    public static int indexForBit(long bit)
    {
        return Long.numberOfTrailingZeros(bit);
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

    public static int baseIndexAtShift(int shiftCount,
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

    public static void forEachIndex(long bitmask,
                                    IntConsumer proc)
    {
        while (bitmask != 0) {
            final long bit = leastBit(bitmask);
            proc.accept(indexForBit(bit));
            bitmask = removeBit(bitmask, bit);
        }
    }

    public static <S, D> void copyToCompactArrayUsingBitmask(long bitmask,
                                                             @Nonnull S[] source,
                                                             @Nonnull D[] dest,
                                                             @Nonnull Function<S, D> transforminator)
    {
        assert dest.length == bitCount(bitmask);
        final Temp.Int1 destIndex = Temp.intVar(0);
        forEachIndex(bitmask, sourceIndex -> {
            dest[destIndex.a] = transforminator.apply(source[sourceIndex]);
            destIndex.a += 1;
        });
        assert destIndex.a == dest.length;
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
            return indexForBit(bit);
        }

        @Override
        public int size()
        {
            return bitCount(bitmask);
        }
    }
}
