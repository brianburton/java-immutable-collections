///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.common.IndexedList;
import org.javimmutable.collections.common.StandardMutableBuilderTests;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class TrieArrayTest
        extends TestCase
{
    // this method is intended for use in debugger to what the structural changes
    @SuppressWarnings("UnusedAssignment")
    public void testTrimming()
    {
        JImmutableArray<Integer> array = TrieArray.of();
        array = array.assign(0, 0);
//        array = array.assign(-1, -1);
        array = array.assign(33, 33);
        array = array.assign(65, 65);
        array = array.assign(97, 97);
        array = array.delete(-1);
        array = array.delete(33);
        array = array.delete(97);
        array = array.delete(65);
        array = array.delete(0);
    }

    public void testRandom()
    {
        Random r = new Random(20L);
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

    public void testShift()
    {
        int shift = 0;
        assertEquals(0xff00, (0xff00 >>> shift));
        shift = 4;
        assertEquals(0x0ff0, (0xff00 >>> shift));
    }

    public void testCursor()
    {
        JImmutableArray<Integer> array = TrieArray.of();
        array = array.assign(-500, -5001).assign(-10, -101).assign(-1, -11).assign(0, 0).assign(1, 11).assign(10, 101).assign(500, 5001);

        List<Integer> indexes = Arrays.asList(-500, -10, -1, 0, 1, 10, 500);
        StandardCursorTest.listCursorTest(indexes, array.keysCursor());

        List<Integer> values = Arrays.asList(-5001, -101, -11, 0, 11, 101, 5001);
        StandardCursorTest.listCursorTest(values, array.valuesCursor());

        List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
        entries.add(MapEntry.of(-500, -5001));
        entries.add(MapEntry.of(-10, -101));
        entries.add(MapEntry.of(-1, -11));
        entries.add(MapEntry.of(0, 0));
        entries.add(MapEntry.of(1, 11));
        entries.add(MapEntry.of(10, 101));
        entries.add(MapEntry.of(500, 5001));
        StandardCursorTest.listCursorTest(entries, array.cursor());
    }

    public void testVarious()
    {
        List<Integer> indexes = createBranchIndexes();
        for (int length = indexes.size(); length > 0; --length) {
            Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
            List<Integer> keys = new ArrayList<Integer>();
            List<Integer> values = new ArrayList<Integer>();
            JImmutableArray<Integer> array = TrieArray.of();
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                array = array.assign(index, i);
                keys.add(index);
                values.add(i);
                map.put(index, i);
                assertEquals(i + 1, array.size());
            }
            assertEquals(array.getMap(), map);
            StandardCursorTest.listCursorTest(keys, array.keysCursor());
            StandardCursorTest.listCursorTest(values, array.valuesCursor());
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                assertEquals(Integer.valueOf(i), array.get(index));
                assertEquals(Integer.valueOf(i), array.getValueOr(index, -99));
                assertEquals(Holders.of(i), array.find(index));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(index, i)), array.findEntry(index));
            }
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                array = array.assign(index, i - 1);
                assertEquals(Integer.valueOf(i - 1), array.get(index));
                assertEquals(Integer.valueOf(i - 1), array.getValueOr(index, -99));
                assertEquals(Holders.of(i - 1), array.find(index));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(index, i - 1)), array.findEntry(index));
            }
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                array = array.delete(index);
                assertEquals(length - i - 1, array.size());
                assertEquals(null, array.get(index));
                assertEquals(Integer.valueOf(-99), array.getValueOr(index, -99));
                assertEquals(Holders.<Integer>of(), array.find(index));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(), array.findEntry(index));
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void testIndexedConstructor()
    {
        List<Integer> source = new ArrayList<Integer>();
        for (int length = 1; length <= 1026; ++length) {
            source.add(length);
            JImmutableArray<Integer> array = TrieArray.of(IndexedList.retained(source), 0, source.size());
            assertEquals(length, array.size());
            for (int i = 0; i < source.size(); ++i) {
                assertEquals(source.get(i), array.get(i));
            }

            if (length > 1) {
                array = TrieArray.of(IndexedList.retained(source), 1, length);
                assertEquals(length - 1, array.size());
                for (int i = 0; i < array.size(); ++i) {
                    assertEquals(source.get(i + 1), array.get(i));
                }
            }
        }
    }

    public void testBuilder()
    {
        List<Integer> source = new ArrayList<Integer>();
        for (int length = 1; length <= 1024; ++length) {
            source.add(length);
            TrieArray.Builder<Integer> builder = TrieArray.builder();
            builder.add(source);
            JImmutableArray<Integer> array = builder.build();
            JImmutableArray<Integer> stdarray = TrieArray.oldof(IndexedList.retained(source), 0, source.size());
            assertEquals(array.getMap(), stdarray.getMap());
            assertEquals(length, array.size());
            for (int i = 0; i < source.size(); ++i) {
                assertEquals(source.get(i), array.get(i));
            }
        }
        for (int length = 1025; length <= 10000; ++length) {
            source.add(length);
            JImmutableArray<Integer> array = TrieArray.<Integer>builder().add(source).build();
            assertEquals(length, array.size());
            for (int i = 0; i < source.size(); ++i) {
                assertEquals(source.get(i), array.get(i));
            }
        }

        Func0<MutableBuilder<Integer, TrieArray<Integer>>> factory = new Func0<MutableBuilder<Integer, TrieArray<Integer>>>()
        {
            @Override
            public MutableBuilder<Integer, TrieArray<Integer>> apply()
            {
                return TrieArray.builder();
            }
        };

        Func2<List<Integer>, TrieArray<Integer>, Boolean> comparator = new Func2<List<Integer>, TrieArray<Integer>, Boolean>()
        {
            @Override
            public Boolean apply(List<Integer> list,
                                 TrieArray<Integer> tree)
            {
                for (int i = 0; i < list.size(); ++i) {
                    assertEquals(list.get(i), tree.get(i));
                }
                return true;
            }
        };

        StandardMutableBuilderTests.verifyBuilder(source, factory, comparator);
    }

    static List<Integer> createBranchIndexes()
    {
        List<Integer> answer = new ArrayList<Integer>();
        answer.add(0);
        answer.add(1 << 30);
        answer.add(2 << 30);
        answer.add(3 << 30);
        int shift = 25;
        while (shift >= 0) {
            for (Integer base : new ArrayList<Integer>(answer)) {
                answer.add(base | (1 << shift));
            }
            shift -= 5;
        }
        Collections.sort(answer);
        return answer;
    }
}
