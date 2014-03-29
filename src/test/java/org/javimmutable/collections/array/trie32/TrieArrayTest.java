///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.array.trie32;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableArray;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class TrieArrayTest
        extends TestCase
{
    public void testRandom()
    {
        Random r = new Random(20);
        Map<Integer, Integer> expected = new TreeMap<Integer, Integer>();
        JImmutableArray<Integer> array = TrieArray.of();
        for (int loop = 1; loop <= 20000; ++loop) {
            int index = r.nextInt(2000) - 1000;
            int value = r.nextInt();
            array = array.assign(index, value);
            expected.put(index, value);
        }
        assertEquals(expected.size(), array.size());
        for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
            assertEquals(entry.getValue(), array.getValueOr(entry.getKey(), Integer.MAX_VALUE));
        }
    }

    public void testSequential()
    {
        Map<Integer, Integer> expected = new TreeMap<Integer, Integer>();
        JImmutableArray<Integer> array = TrieArray.of();
        for (int i = 0; i <= 20000; ++i) {
            array = array.assign(i, i);
            expected.put(i, i);
        }
        assertEquals(expected.size(), array.size());
        for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
            assertEquals(entry.getValue(), array.getValueOr(entry.getKey(), Integer.MAX_VALUE));
        }
    }

    public void testDepthTrimDoesNotBreakIndexing()
    {
        JImmutableArray<Integer> array = TrieArray.of();
        for (int i = 0; i < 10000; ++i) {
            array = array.assign(i, i);
        }

        for (int i = 9999; i >= 0; --i) {
            assertEquals((Integer)i, array.get(i));
            assertEquals(Holders.of(i), array.find(i));
            assertEquals((Integer)i, array.get(i));
            assertEquals(Holders.of(i), array.find(i));
            for (int shift = 31; shift > 20; --shift) {
                int shiftedIndex = i | (1 << shift);
                assertEquals(null, array.get(shiftedIndex));
                assertEquals(Holders.<Integer>of(), array.find(shiftedIndex));
                assertSame(array, array.delete(shiftedIndex));
            }
            JImmutableArray<Integer> deleted = array.delete(i);
            assertEquals(array.size() - 1, deleted.size());
            assertEquals(null, deleted.get(i));
        }
    }

    public void testDeleteToEmpty()
    {
        JImmutableArray<Integer> array = TrieArray.of();
        for (int i = 0; i < 10000; ++i) {
            array = array.assign(i, i);
        }

        for (int i = 9999; i >= 0; --i) {
            assertEquals((Integer)i, array.get(i));
            assertEquals(Holders.of(i), array.find(i));
            assertEquals((Integer)i, array.get(i));
            assertEquals(Holders.of(i), array.find(i));
            JImmutableArray<Integer> deleted = array.delete(i);
            assertEquals(array.size() - 1, deleted.size());
            assertEquals(null, deleted.get(i));
            array = deleted;
        }

        assertEquals(0, array.size());
    }
}
