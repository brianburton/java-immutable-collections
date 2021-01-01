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

package org.javimmutable.collections.array;

import junit.framework.TestCase;
import org.javimmutable.collections.common.LongArrayMappedTrieMath;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static java.lang.Long.*;
import static org.javimmutable.collections.array.TrieLongArrayNode.flip;

public class TrieLongArrayNodeTest
    extends TestCase
{
    public void testRandom()
    {
        final Random r = new Random(234567);
        final Map<Long, Integer> known = new TreeMap<>();
        final List<Long> indexes = new ArrayList<>();
        final List<Integer> values = new ArrayList<>();
        TrieLongArrayNode<Integer> array = TrieLongArrayNode.empty();
        int length = 7;
        for (int loop = 1; loop <= 5; ++loop) {
            for (Long index : new ArrayList<>(known.keySet())) {
                if (r.nextInt(3) == 1) {
                    known.put(index, r.nextInt());
                }
            }
            for (int i = known.size(); i < length; ++i) {
                known.put(r.nextLong(), r.nextInt());
            }
            indexes.clear();
            indexes.addAll(known.keySet());
            for (Long index : indexes) {
                array = array.assign(index, known.get(index));
            }
            values.clear();
            values.addAll(known.values());

            array.checkInvariants();
            StandardIteratorTests.listIteratorTest(indexes, array.keys().iterator());
            StandardIteratorTests.listIteratorTest(values, array.values().iterator());
            length = 17 * length;
            values.clear();
        }

        Collections.shuffle(indexes, r);
        final int checkInterval = indexes.size() / 7;
        for (Long index : indexes) {
            known.remove(index);
            array = array.delete(index);
            if (known.size() % checkInterval == 0) {
                array.checkInvariants();
                StandardIteratorTests.listIteratorTest(new ArrayList<>(known.keySet()), array.keys().iterator());
                StandardIteratorTests.listIteratorTest(new ArrayList<>(known.values()), array.values().iterator());
            }
        }
        assertEquals(0, array.size());
    }

    public void testFlip()
    {
        assertEquals(0L, flip(Long.MIN_VALUE));
        assertEquals(-1L, flip(Long.MAX_VALUE));
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
        assertEquals("0,0,0,0,0,0,21,0,0,0,1", LongArrayMappedTrieMath.hashString(rootIndex));
        assertEquals("0,0,0,0,0,0,21,61,36,13,3", LongArrayMappedTrieMath.hashString(leafIndex));
        assertEquals("0,0,0,0,0,0,21,61,36,0,0", LongArrayMappedTrieMath.hashString(assignIndex));
        // common says 1 but index says 2
        assertEquals(0, LongArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(leafIndex));
        assertEquals(2, LongArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(assignIndex));
        assertEquals(0, LongArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(rootIndex));
        assertEquals(1, LongArrayMappedTrieMath.findMaxCommonShift(TrieLongArrayNode.ROOT_SHIFT_COUNT, leafIndex, assignIndex));
        assertEquals(2, TrieLongArrayNode.findCommonAncestorShift(leafIndex, assignIndex));
        assertEquals(3, TrieLongArrayNode.findCommonAncestorShift(rootIndex, assignIndex));
        assertEquals(0, TrieLongArrayNode.findCommonAncestorShift(1, 2));
        assertEquals(TrieLongArrayNode.ROOT_SHIFT_COUNT, TrieLongArrayNode.findCommonAncestorShift(0b11_000000_000000_000000_000000_000000,
                                                                                                   0b01_000000_000000_000000_000000_000000));
        assertEquals(4, TrieLongArrayNode.findCommonAncestorShift(0b11_000001_000000_000000_000000_000000,
                                                                  0b11_000001_000001_000000_000000_000000));
        assertEquals(3, TrieLongArrayNode.findCommonAncestorShift(0b11_000001_000010_000000_000000_000000,
                                                                  0b11_000001_000001_000000_000000_000000));
        assertEquals(1, LongArrayMappedTrieMath.findMinimumShiftForZeroBelowHashCode(0b11_000001_000011_000000_000001_000000));
        assertEquals(2, TrieLongArrayNode.findCommonAncestorShift(0b11_000001_000011_000000_000000_000001,
                                                                  0b11_000001_000011_000001_000000_000000));

        assertEquals(3, TrieLongArrayNode.findCommonAncestorShift(rootIndex, leafIndex));
        assertEquals(2, TrieLongArrayNode.findCommonAncestorShift(leafIndex, assignIndex));
        TrieLongArrayNode<Long> node = TrieLongArrayNode.<Long>empty().assign(rootIndex, 1L);
        node = node.assign(leafIndex, 3L);
        node = node.assign(assignIndex, 2L);
        assertEquals(Long.valueOf(2), node.getValueOr(assignIndex, -1L));
    }
}
