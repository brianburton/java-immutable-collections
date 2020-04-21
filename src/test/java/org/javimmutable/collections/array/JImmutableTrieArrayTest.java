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
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static java.util.Arrays.asList;

public class JImmutableTrieArrayTest
    extends TestCase
{
    // this method is intended for use in debugger to watch the structural changes
    public void testTrimming()
    {
        JImmutableArray<Integer> array = JImmutableTrieArray.of();
        array = array.assign(0, 0);
//        array = array.assign(-1, -1);
        array = array.assign(33, 33);
        array = array.assign(65, 65);
        array = array.assign(97, 97);
        assertEquals("[0=0,33=33,65=65,97=97]", array.toString());
        assertEquals(33559365, array.hashCode());
        array = array.delete(-1);
        array = array.delete(33);
        array = array.delete(97);
        array = array.delete(65);
        array = array.delete(0);
        assertEquals("[]", array.toString());
        assertEquals(0, array.hashCode());
    }

    public void testRandom()
    {
        Random r = new Random(20L);
        Map<Integer, Integer> expected = new TreeMap<>();
        JImmutableArray<Integer> array = JImmutableTrieArray.of();
        for (int loop = 1; loop <= 20000; ++loop) {
            int index = r.nextInt(2000) - 1000;
            int value = r.nextInt();
            switch (r.nextInt(4)) {
                case 0:
                case 1:
                    array = array.assign(index, value);
                    expected.put(index, value);
                    array.checkInvariants();
                    break;
                case 2:
                    array = array.delete(index);
                    expected.remove(index);
                    array.checkInvariants();
                    break;
                case 3:
                    assertEquals(array.find(index).getValueOrNull(), array.get(index));
                    break;
            }
            assertEquals(array.isEmpty(), expected.isEmpty());
            assertEquals(array.isNonEmpty(), !expected.isEmpty());
        }
        assertEquals(expected.size(), array.size());
        for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
            assertEquals(entry.getValue(), array.getValueOr(entry.getKey(), Integer.MAX_VALUE));
        }
        array.checkInvariants();
    }

    public void testCollector()
    {
        Random r = new Random(20L);
        JImmutableArray<Integer> array = JImmutableTrieArray.of();
        int size = 0;
        while (size < 50_000) {
            size += r.nextInt(250);
            while (array.size() < size) {
                final int value = r.nextInt();
                array = array.assign(array.size(), value);
            }
            assertEquals(array, array.values().parallelStream().collect(JImmutableTrieArray.collector()));
        }
    }

    public void testSequential()
    {
        Map<Integer, Integer> expected = new TreeMap<>();
        JImmutableArray<Integer> array = JImmutableTrieArray.of();
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
        JImmutableArray<Integer> array = JImmutableTrieArray.of();
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
        JImmutableArray<Integer> array = JImmutableTrieArray.of();
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

    public void testIterator()
    {
        JImmutableArray<Integer> array = JImmutableTrieArray.of();
        array = array.assign(-500, -5001).assign(-10, -101).assign(-1, -11).assign(0, 0).assign(1, 11).assign(10, 101).assign(500, 5001);

        List<Integer> indexes = Arrays.asList(-500, -10, -1, 0, 1, 10, 500);
        StandardIteratorTests.listIteratorTest(indexes, array.keys().iterator());

        List<Integer> values = Arrays.asList(-5001, -101, -11, 0, 11, 101, 5001);
        StandardIteratorTests.listIteratorTest(values, array.values().iterator());

        List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<>();
        entries.add(MapEntry.of(-500, -5001));
        entries.add(MapEntry.of(-10, -101));
        entries.add(MapEntry.of(-1, -11));
        entries.add(MapEntry.of(0, 0));
        entries.add(MapEntry.of(1, 11));
        entries.add(MapEntry.of(10, 101));
        entries.add(MapEntry.of(500, 5001));
        StandardIteratorTests.listIteratorTest(entries, array.iterator());

        entries.clear();
        array = JImmutableTrieArray.of();
        for (int i = -1000; i <= 1000; ++i) {
            entries.add(MapEntry.of(i, 1000 + i));
            array = array.assign(i, 1000 + i);
        }
        StandardIteratorTests.verifyOrderedIterable(entries, array);
    }

    public void testSignedOrderIteration()
    {
        final int numLoops = 100000;
        final int increment = (Integer.MAX_VALUE / numLoops) * 2;
        JImmutableArray<Integer> array = JImmutableTrieArray.of();
        List<JImmutableMap.Entry<Integer, Integer>> expected = new ArrayList<>();
        int index = Integer.MIN_VALUE;
        for (int i = 0; i < numLoops; ++i) {
            expected.add(MapEntry.of(index, -index));
            array = array.assign(index, -index);
            index += increment;
        }
        StandardIteratorTests.listIteratorTest(expected, array.iterator());
    }

    public void testVarious()
    {
        List<Integer> indexes = createBranchIndexes();
        for (int length = indexes.size(); length > 0; --length) {
            Map<Integer, Integer> map = new TreeMap<>();
            List<Integer> keys = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            JImmutableArray<Integer> array = JImmutableTrieArray.of();
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                array = array.assign(index, i);
                keys.add(index);
                values.add(i);
                map.put(index, i);
                assertEquals(i + 1, array.size());
            }
            array.checkInvariants();
            assertEquals(array.getMap(), map);
            StandardIteratorTests.listIteratorTest(keys, array.keys().iterator());
            StandardIteratorTests.listIteratorTest(values, array.values().iterator());
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                assertEquals(Integer.valueOf(i), array.get(index));
                assertEquals(Integer.valueOf(i), array.getValueOr(index, -99));
                assertEquals(Holders.of(i), array.find(index));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(index, i)), array.findEntry(index));
            }
            array.checkInvariants();
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                array = array.assign(index, i - 1);
                assertEquals(Integer.valueOf(i - 1), array.get(index));
                assertEquals(Integer.valueOf(i - 1), array.getValueOr(index, -99));
                assertEquals(Holders.of(i - 1), array.find(index));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(MapEntry.of(index, i - 1)), array.findEntry(index));
            }
            array.checkInvariants();
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                array = array.delete(index);
                assertEquals(length - i - 1, array.size());
                assertEquals(null, array.get(index));
                assertEquals(Integer.valueOf(-99), array.getValueOr(index, -99));
                assertEquals(Holders.<Integer>of(), array.find(index));
                assertEquals(Holders.<JImmutableMap.Entry<Integer, Integer>>of(), array.findEntry(index));
            }
            array.checkInvariants();
        }
    }

    @SuppressWarnings("deprecation")
    public void testIndexedConstructor()
    {
        List<Integer> source = new ArrayList<>();
        for (int length = 1; length <= 1026; ++length) {
            source.add(length);
            JImmutableArray<Integer> array = JImmutableTrieArray.of(IndexedList.retained(source), 0, source.size());
            assertEquals(length, array.size());
            for (int i = 0; i < source.size(); ++i) {
                assertEquals(source.get(i), array.get(i));
            }

            if (length > 1) {
                array = JImmutableTrieArray.of(IndexedList.retained(source), 1, length);
                assertEquals(length - 1, array.size());
                for (int i = 0; i < array.size(); ++i) {
                    assertEquals(source.get(i + 1), array.get(i));
                }
            }
        }
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableArray)a).iterator();
        final JImmutableArray<String> empty = JImmutableTrieArray.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpciwqSqwMKMqvqPwPAv9UjHkYGCoKyjkYGJhfMgBBBQBbOGFFXwAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert(MapEntry.of(1, "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpciwqSqwMKMqvqPwPAv9UjHkYGCoKyoEk80sGBgZGEC5hYEysAACKUbzuZwAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList(MapEntry.of(Integer.MIN_VALUE, "a"), MapEntry.of(1, "b"), MapEntry.of(Integer.MAX_VALUE, "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpciwqSqwMKMqvqPwPAv9UjHkYGCoKyoEk80sGINEAJEoYGBPLWYAMRiArqZylHqgSyEquAABirp0EewAAAA==");
    }

    public void testBuilder()
        throws Exception
    {
        assertSame(JImmutableTrieArray.of(), JImmutableTrieArray.builder().build());

        final List<Integer> expected = new ArrayList<>();
        final JImmutableTrieArray.Builder<Integer> builder = JImmutableTrieArray.builder();
        JImmutableArray<Integer> manual = JImmutableTrieArray.of();
        for (int length = 1; length <= 1024; ++length) {
            expected.add(length);
            manual = manual.assign(length - 1, length);
            JImmutableArray<Integer> array = builder.add(length).build();
            assertEquals(array.getMap(), manual.getMap());
            assertEquals(length, array.size());
            for (int i = 0; i < expected.size(); ++i) {
                assertEquals(expected.get(i), array.get(i));
            }
            array.checkInvariants();
        }
        assertEquals(manual, builder.build());
        for (int length = 1025; length <= 10000; ++length) {
            expected.add(length);
            manual = manual.assign(length - 1, length);
            JImmutableArray<Integer> array = builder.add(length).build();
            assertEquals(length, array.size());
            for (int i = 0; i < expected.size(); ++i) {
                assertEquals(expected.get(i), array.get(i));
            }
            array.checkInvariants();
        }
        assertEquals(manual, builder.build());

        Func0<JImmutableArray.Builder<Integer>> factory = () -> JImmutableTrieArray.builder();

        Func2<List<Integer>, JImmutableArray<Integer>, Boolean> comparator = (list, tree) -> {
            for (int i = 0; i < list.size(); ++i) {
                assertEquals(list.get(i), tree.get(i));
            }
            return true;
        };

        StandardBuilderTests.verifyBuilder(expected, this::builder, comparator, new Integer[0]);
        StandardBuilderTests.verifyThreadSafety(this::builder, a -> a.values());
    }

    static List<Integer> createBranchIndexes()
    {
        List<Integer> answer = new ArrayList<>();
        answer.add(0);
        answer.add(1 << 30);
        answer.add(2 << 30);
        answer.add(3 << 30);
        int shift = 25;
        while (shift >= 0) {
            for (Integer base : new ArrayList<>(answer)) {
                answer.add(base | (1 << shift));
            }
            shift -= 5;
        }
        Collections.sort(answer);
        return answer;
    }

    private BuilderTestAdapter<Integer> builder()
    {
        return new BuilderTestAdapter<>(JImmutableTrieArray.builder());
    }

    private static class BuilderTestAdapter<T>
        implements StandardBuilderTests.BuilderAdapter<T, JImmutableArray<T>>
    {
        private final JImmutableArray.Builder<T> builder;

        public BuilderTestAdapter(JImmutableArray.Builder<T> builder)
        {
            this.builder = builder;
        }

        @Override
        public JImmutableArray<T> build()
        {
            return builder.build();
        }

        @Override
        public void clear()
        {
            builder.clear();
        }

        @Override
        public int size()
        {
            return builder.size();
        }

        @Override
        public void add(T value)
        {
            builder.add(value);
        }

        @Override
        public void add(Iterator<? extends T> source)
        {
            builder.add(source);
        }

        @Override
        public void add(Iterable<? extends T> source)
        {
            builder.add(source);
        }

        @Override
        public <K extends T> void add(K... source)
        {
            builder.add(source);
        }

        @Override
        public void add(Indexed<? extends T> source,
                        int offset,
                        int limit)
        {
            builder.add(source, offset, limit);
        }

        @Override
        public void add(Indexed<? extends T> source)
        {
            builder.add(source);
        }
    }
}
