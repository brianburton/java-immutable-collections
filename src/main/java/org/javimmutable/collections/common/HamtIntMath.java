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

import javax.annotation.Nonnull;

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

    public static int maxShiftsForBitCount(int bitCount)
    {
        return (bitCount + SHIFT - 1) / SHIFT;
    }

    public static int indexAtShift(int shiftCount,
                                   int hashCode)
    {
        return (hashCode >>> (shiftCount * SHIFT)) & MASK;
    }

    public static int bitCount(int bitmask)
    {
        return Integer.bitCount(bitmask);
    }

    @Nonnull
    public static Indexed<Integer> indices(int bitmask)
    {
        return new Indexes(bitmask);
    }

    private static class Indexes
        implements Indexed<Integer>
    {
        private final int bitmask;

        private Indexes(int bitmask)
        {
            this.bitmask = bitmask;
        }

        @Override
        public Integer get(int index)
        {
            int remaining = bitmask;
            while (index > 0) {
                remaining = remaining & ~Integer.lowestOneBit(remaining);
                index -= 1;
            }
            int bit = Integer.lowestOneBit(remaining);
            if (bit == 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return Integer.numberOfTrailingZeros(bit);
        }

        @Override
        public int size()
        {
            return bitCount(bitmask);
        }
    }
}
