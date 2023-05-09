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

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.Temp;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.IntConsumer;

import static org.javimmutable.collections.Holder.none;
import static org.javimmutable.collections.Holders.nullable;

/**
 * Helper class with static methods to manipulate long bitmasks.
 * Used in array map trie calculations.
 * Methods are short to allow inlining.
 */
public final class BitmaskMath
{
    public static final int ARRAY_SIZE = 64;
    public static final int MAX_INDEX = ARRAY_SIZE - 1;
    public static final long ALL_BITS = -1L;

    private BitmaskMath()
    {
    }

    public static long bitFromIndex(int index)
    {
        return 1L << index;
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
            long bit = findBit(index);
            if (bit == 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            } else {
                return indexForBit(bit);
            }
        }

        @Nonnull
        @Override
        public Holder<Integer> find(int index)
        {
            long bit = findBit(index);
            if (bit == 0) {
                return none();
            } else {
                return nullable(indexForBit(bit));
            }
        }

        @Override
        public int size()
        {
            return bitCount(bitmask);
        }

        private long findBit(int index)
        {
            long remaining = bitmask;
            while (index > 0) {
                remaining = remaining & ~Long.lowestOneBit(remaining);
                index -= 1;
            }
            return Long.lowestOneBit(remaining);
        }
    }
}
