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

package org.javimmutable.collections.array;

import junit.framework.TestCase;
import org.javimmutable.collections.common.IntArrayMappedTrieMath;

import java.util.Random;

import static java.lang.Integer.*;
import static org.javimmutable.collections.array.TrieArrayNode.flip;

public class TrieArrayNodeTest
    extends TestCase
{
    public void testFlip()
    {
        assertEquals(0, flip(Integer.MIN_VALUE));
        assertEquals(-1, flip(Integer.MAX_VALUE));
        final Random r = new Random(10000);
        for (int i = 0; i < 50000; ++i) {
            final int a = r.nextInt();
            final int b = r.nextInt();
            final int sc = compare(a, b);
            final int uc = compareUnsigned(flip(a), flip(b));
            assertEquals(sc, uc);
            assertEquals(a, flip(flip(a)));
            assertEquals(b, flip(flip(b)));
        }
    }

    public void testCommonAncestorShift()
    {
        // 352321536 (3)
        final int rootIndex = 352321536 + 1;
        // 368460608 (0)
        final int leafIndex = 368460608 + 3;
        // 368459776 (2)
        final int assignIndex = 368459776;
        assertEquals("0,21,0,0,0,1", IntArrayMappedTrieMath.hashString(rootIndex));
        assertEquals("0,21,61,36,13,3", IntArrayMappedTrieMath.hashString(leafIndex));
        assertEquals("0,21,61,36,0,0", IntArrayMappedTrieMath.hashString(assignIndex));
        // common says 1 but index says 2
        assertEquals(0, IntArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(leafIndex));
        assertEquals(2, IntArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(assignIndex));
        assertEquals(0, IntArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(rootIndex));
        assertEquals(1, IntArrayMappedTrieMath.findMaxCommonShift(TrieArrayNode.ROOT_SHIFT_COUNT, leafIndex, assignIndex));
        assertEquals(2, TrieArrayNode.findCommonAncestorShift(leafIndex, assignIndex));
        assertEquals(3, TrieArrayNode.findCommonAncestorShift(rootIndex, assignIndex));
        assertEquals(0, TrieArrayNode.findCommonAncestorShift(1, 2));
        assertEquals(TrieArrayNode.ROOT_SHIFT_COUNT, TrieArrayNode.findCommonAncestorShift(0b11_000000_000000_000000_000000_000000,
                                                                                           0b01_000000_000000_000000_000000_000000));
        assertEquals(4, TrieArrayNode.findCommonAncestorShift(0b11_000001_000000_000000_000000_000000,
                                                              0b11_000001_000001_000000_000000_000000));
        assertEquals(3, TrieArrayNode.findCommonAncestorShift(0b11_000001_000010_000000_000000_000000,
                                                              0b11_000001_000001_000000_000000_000000));
        assertEquals(1, IntArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(0b11_000001_000011_000000_000001_000000));
        assertEquals(2, TrieArrayNode.findCommonAncestorShift(0b11_000001_000011_000000_000000_000001,
                                                              0b11_000001_000011_000001_000000_000000));

        assertEquals(3, TrieArrayNode.findCommonAncestorShift(rootIndex, leafIndex));
        assertEquals(2, TrieArrayNode.findCommonAncestorShift(leafIndex, assignIndex));
        TrieArrayNode<Integer> node = TrieArrayNode.<Integer>empty().assign(rootIndex, 1);
        node = node.assign(leafIndex, 3);
        node = node.assign(assignIndex, 2);
        assertEquals(Integer.valueOf(2), node.getValueOr(assignIndex, -1));
    }
}
