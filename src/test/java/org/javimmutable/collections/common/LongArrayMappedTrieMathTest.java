///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import static org.javimmutable.collections.common.BitmaskMath.MAX_INDEX;
import static org.javimmutable.collections.common.LongArrayMappedTrieMath.*;

public class LongArrayMappedTrieMathTest
    extends TestCase
{
    public void testVarious()
    {
        assertEquals(11, MAX_SHIFTS);
        assertEquals(10, MAX_FULL_SHIFTS);
        final long hashCode = hash(1, 2, 3, 4, 5, 6);
        verifyEquals(hash(1, 2, 3, 4, 5, 0), baseIndexFromHashCode(hash(1, 2, 3, 4, 5, 6)));
        verifyEquals(hash(1, 2, 3, 4, 5, 0), baseIndexFromHashCode(hash(1, 2, 3, 4, 5, MAX_INDEX)));
        verifyEquals(hash(0, 1, 2, 3, 4, 5), remainderFromHashCode(hash(1, 2, 3, 4, 5, 6)));
        verifyEquals(hash(0, 1, 2, 3, 4, 5), remainderFromHashCode(hash(1, 2, 3, 4, 5, MAX_INDEX)));
        verifyEquals(hash(0, 1, 2, 3, 4, MAX_INDEX), remainderFromHashCode(hash(1, 2, 3, 4, MAX_INDEX, 6)));
        verifyEquals(hash(0, MAX_INDEX, 2, 3, 4, MAX_INDEX), remainderFromHashCode(hash(MAX_INDEX, 2, 3, 4, MAX_INDEX, 6)));
        verifyEquals(hash(0, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX), remainderFromHashCode(hash(MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, 6)));
        verifyEquals(hash(1, 2, 3, 4, 5, 0), baseIndexFromHashCode(hash(1, 2, 3, 4, 5, 6)));
        verifyEquals(6, indexFromHashCode(hash(1, 2, 3, 4, 5, 6)));
        verifyEquals(MAX_INDEX, indexFromHashCode(hash(1, 2, 3, 4, 5, MAX_INDEX)));
        verifyEquals(hash(MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, 0), liftedHashCode(hash(MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX), 0));
        verifyEquals(hash(MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, 1), liftedHashCode(hash(MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX), 1));
        assertEquals(11, maxShiftsForBitCount(64));
        assertEquals(10, maxShiftsForBitCount(60));
    }

    public void testHash()
    {
        verifyEquals(0b000001000010000011000100000101000110L, hash(1, 2, 3, 4, 5, 6));
        verifyEquals(0b000001000010000011000100000101111111L, hash(1, 2, 3, 4, 5, MAX_INDEX));
        verifyEquals(0b0111111000010000011000100000101000110L, hash(MAX_INDEX, 2, 3, 4, 5, 6));
        verifyEquals(0b0111111111111111111111111111111111111L, hash(MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX));
    }

    public void testShifts()
    {
        assertEquals(MAX_INDEX, indexAtShift(0, MAX_INDEX));
        assertEquals(MAX_INDEX, indexAtShift(0, -1));
        assertEquals(15, indexAtShift(MAX_SHIFT_NUMBER, -1));
        assertEquals(MAX_INDEX, indexAtShift(MAX_FULL_SHIFT_NUMBER, -1));
        final long hashCode = hash(3, 0b110001, 0b100001, 0b101001, 0b100101, 0b101011);
        verifyEquals(3, indexAtShift(5, hashCode));
        verifyEquals(0b110001, indexAtShift(4, hashCode));
        verifyEquals(0b100001, indexAtShift(3, hashCode));
        verifyEquals(0b101001, indexAtShift(2, hashCode));
        verifyEquals(0b100101, indexAtShift(1, hashCode));
        verifyEquals(0b101011, indexAtShift(0, hashCode));
        verifyEquals(0, baseIndexAtShift(5, hashCode));
        verifyEquals(hash(3, 0, 0, 0, 0, 0), baseIndexAtShift(4, hashCode));
        verifyEquals(hash(3, 0b110001, 0, 0, 0, 0), baseIndexAtShift(3, hashCode));
        verifyEquals(hash(3, 0b110001, 0b100001, 0, 0, 0), baseIndexAtShift(2, hashCode));
        verifyEquals(hash(3, 0b110001, 0b100001, 0b101001, 0, 0), baseIndexAtShift(1, hashCode));
        verifyEquals(hash(3, 0b110001, 0b100001, 0b101001, 0b100101, 0), baseIndexAtShift(0, hashCode));
        assertEquals(0, findMaxShiftForHashCode(0L));
        assertEquals(0, findMaxShiftForHashCode(MAX_INDEX));
        assertEquals(1, findMaxShiftForHashCode(hash(1, 0)));
        assertEquals(1, findMaxShiftForHashCode(hash(1, MAX_INDEX)));
        assertEquals(2, findMaxShiftForHashCode(hash(32, 0, 0)));
        assertEquals(2, findMaxShiftForHashCode(hash(MAX_INDEX, MAX_INDEX, MAX_INDEX)));
        assertEquals(3, findMaxShiftForHashCode(hash(32, 0, 0, 0)));
        assertEquals(3, findMaxShiftForHashCode(hash(1, MAX_INDEX, MAX_INDEX, MAX_INDEX)));
    }

    public void testHashCodeBelowShift()
    {
        final long hashCode = hash(3, 0b110001, 0b100001, 0b101001, 0b100101, 0b101011);
        assertEquals(0, hashCodeBelowShift(0, hashCode));
        assertEquals(hash(0, 0, 0, 0, 0, 0b101011), hashCodeBelowShift(1, hashCode));
        assertEquals(hash(0, 0, 0, 0, 0b100101, 0b101011), hashCodeBelowShift(2, hashCode));
        assertEquals(hash(0, 0, 0, 0b101001, 0b100101, 0b101011), hashCodeBelowShift(3, hashCode));
        assertEquals(hash(0, 0, 0b100001, 0b101001, 0b100101, 0b101011), hashCodeBelowShift(4, hashCode));
        assertEquals(hash(0, 0b110001, 0b100001, 0b101001, 0b100101, 0b101011), hashCodeBelowShift(5, hashCode));
    }

    public void testFindMinimumShiftForZeroBelowHashCode()
    {
        assertEquals(0, findMinimumShiftForZeroBelowHashCode(0));
        assertEquals(0, findMinimumShiftForZeroBelowHashCode(0b0_100000));
        assertEquals(1, findMinimumShiftForZeroBelowHashCode(0b1_000000));
        assertEquals(1, findMinimumShiftForZeroBelowHashCode(0b100000_000000));
        assertEquals(2, findMinimumShiftForZeroBelowHashCode(0b1_000000_000000));
        assertEquals(2, findMinimumShiftForZeroBelowHashCode(0b100000_000000_000000));
    }

    public void testFindMaxCommonShift()
    {
        assertEquals(0, findMaxCommonShift(MAX_SHIFT_NUMBER, 0, 0));
        assertEquals(0, findMaxCommonShift(MAX_SHIFT_NUMBER, 1, 2));
        assertEquals(5, findMaxCommonShift(MAX_SHIFT_NUMBER,
                                           hash(6, 5, 4, 3, 2, 1),
                                           hash(9, 5, 4, 3, 2, 1)));
        assertEquals(4, findMaxCommonShift(MAX_SHIFT_NUMBER,
                                           hash(6, 5, 4, 3, 2, 1),
                                           hash(6, 9, 4, 3, 2, 1)));
        assertEquals(3, findMaxCommonShift(MAX_SHIFT_NUMBER,
                                           hash(6, 5, 4, 3, 2, 1),
                                           hash(6, 5, 9, 3, 2, 1)));
        assertEquals(2, findMaxCommonShift(MAX_SHIFT_NUMBER,
                                           hash(6, 5, 4, 3, 2, 1),
                                           hash(6, 5, 4, 9, 2, 1)));
        assertEquals(1, findMaxCommonShift(MAX_SHIFT_NUMBER,
                                           hash(6, 5, 4, 3, 2, 1),
                                           hash(6, 5, 4, 3, 9, 1)));
        assertEquals(0, findMaxCommonShift(MAX_SHIFT_NUMBER,
                                           hash(6, 5, 4, 3, 2, 1),
                                           hash(6, 5, 4, 3, 2, 9)));
    }

    public void testWithIndexAtShift()
    {
        verifyEquals(hash(1), withIndexAtShift(0, 0, 1));
        verifyEquals(hash(MAX_INDEX), withIndexAtShift(0, 0, MAX_INDEX));
        verifyEquals(hash(1), withIndexAtShift(0, MAX_INDEX, 1));
        verifyEquals(hash(MAX_INDEX, MAX_INDEX, 10, MAX_INDEX), withIndexAtShift(1, hash(MAX_INDEX, MAX_INDEX, MAX_INDEX, MAX_INDEX), 10));
        verifyEquals(hash(2, 0, 0, 0, 0, 0), withIndexAtShift(5, 0, 2));
    }

    private void verifyEquals(long a,
                              long b)
    {
        assertEquals(Long.toBinaryString(a), Long.toBinaryString(b));
    }
}
