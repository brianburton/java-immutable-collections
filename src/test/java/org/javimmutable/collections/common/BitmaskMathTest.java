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

import junit.framework.TestCase;
import org.javimmutable.collections.Indexed;

public class BitmaskMathTest
    extends TestCase
{
    public void testIndices()
    {
        long bitmask = 0;
        Indexed<Integer> indices = BitmaskMath.indices(bitmask);
        assertEquals(0, indices.size());

        bitmask = BitmaskMath.addBit(bitmask, BitmaskMath.bitFromIndex(3));
        bitmask = BitmaskMath.addBit(bitmask, BitmaskMath.bitFromIndex(9));
        bitmask = BitmaskMath.addBit(bitmask, BitmaskMath.bitFromIndex(15));
        bitmask = BitmaskMath.addBit(bitmask, BitmaskMath.bitFromIndex(27));
        indices = BitmaskMath.indices(bitmask);
        assertEquals(4, indices.size());
        assertEquals(3, (int)indices.get(0));
        assertEquals(9, (int)indices.get(1));
        assertEquals(15, (int)indices.get(2));
        assertEquals(27, (int)indices.get(3));

        for (int index = 0; index < BitmaskMath.ARRAY_SIZE; ++index) {
            long bit = BitmaskMath.bitFromIndex(index);
            indices = BitmaskMath.indices(bit);
            assertEquals(1, indices.size());
            assertEquals(index, (int)indices.get(0));
        }

        indices = BitmaskMath.indices(-1);
        assertEquals(BitmaskMath.ARRAY_SIZE, indices.size());
        for (int index = 0; index < BitmaskMath.ARRAY_SIZE; ++index) {
            assertEquals(index, (int)indices.get(index));
        }
    }

    public void testVarious()
    {
        assertEquals(0, BitmaskMath.bitCount(0L));
        assertEquals(64, BitmaskMath.bitCount(-1L));
        assertEquals(4, BitmaskMath.bitCount(BitmaskMath.bitmask(1, 5, 30, 63)));
    }

    public void testBits()
    {
        for (int i = 0; i <= BitmaskMath.MAX_INDEX; ++i) {
            final long iBit = BitmaskMath.bitFromIndex(i);
            final long iMask = BitmaskMath.addBit(0, iBit);
            assertEquals(0L, BitmaskMath.removeBit(iMask, iBit));
            assertEquals(0, BitmaskMath.arrayIndexForBit(iMask, iBit));
            for (int n = 0; n <= BitmaskMath.MAX_INDEX; ++n) {
                final long nBit = BitmaskMath.bitFromIndex(n);
                final long nMask = BitmaskMath.addBit(iMask, nBit);
                if (n != i) {
                    assertEquals(false, BitmaskMath.bitIsPresent(iMask, nBit));
                    assertEquals(true, BitmaskMath.bitIsPresent(nMask, nBit));
                    assertEquals(true, BitmaskMath.bitIsAbsent(iMask, nBit));
                    assertEquals(false, BitmaskMath.bitIsAbsent(nMask, nBit));
                    assertEquals(iMask, BitmaskMath.removeBit(nMask, nBit));
                    if (n < i) {
                        assertEquals(0, BitmaskMath.arrayIndexForBit(nMask, nBit));
                        assertEquals(1, BitmaskMath.arrayIndexForBit(nMask, iBit));
                    } else {
                        assertEquals(0, BitmaskMath.arrayIndexForBit(nMask, iBit));
                        assertEquals(1, BitmaskMath.arrayIndexForBit(nMask, nBit));
                    }
                } else {
                    assertEquals(true, BitmaskMath.bitIsPresent(iMask, nBit));
                    assertEquals(false, BitmaskMath.bitIsAbsent(iMask, nBit));
                }
            }
        }
    }

    public void testBitmask()
    {
        verifyEquals(1L << 31, BitmaskMath.bitmask(31));
        verifyEquals(1L << 63, BitmaskMath.bitmask(63));
        verifyEquals(1L, BitmaskMath.bitmask(0));
    }

    private void verifyEquals(long a,
                              long b)
    {
        assertEquals(Long.toBinaryString(a), Long.toBinaryString(b));
    }
}
