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

package org.javimmutable.collections.array;

import junit.framework.TestCase;
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IArray;
import org.javimmutable.collections.IArrayBuilder;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.Temp;
import org.javimmutable.collections.common.IntArrayMappedTrieMath;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.common.StandardStreamableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.lang.Integer.*;
import static java.util.Arrays.asList;

public class TrieArrayTest
    extends TestCase
{
    // this method is intended for use in debugger to watch the structural changes
    public void testTrimming()
    {
        IArray<Integer> array = TrieArray.of();
        array = array.assign(0, 2);
//        array = array.assign(-1, -1);
        array = array.assign(33, 3);
        array = array.assign(97, 5);
        array = array.assign(-1234567, 1);
        array = array.assign(65, 4);
        assertEquals("[-1234567=1,0=2,33=3,65=4,97=5]", array.toString());
        assertEquals(-1983045993, array.hashCode());
        assertSame(array, array.delete(-1));
        assertEquals("[-1234567=1,0=2,33=3,65=4,97=5]", array.toString());
        array = array.delete(33);
        assertEquals("[-1234567=1,0=2,65=4,97=5]", array.toString());
        array = array.delete(97);
        assertEquals("[-1234567=1,0=2,65=4]", array.toString());
        array = array.delete(-1234567);
        assertEquals("[0=2,65=4]", array.toString());
        array = array.delete(65);
        assertEquals("[0=2]", array.toString());
        array = array.delete(0);
        assertEquals("[]", array.toString());
        assertEquals(0, array.hashCode());
    }

    public void testOrder()
    {
        final int hash10 = IntArrayMappedTrieMath.hash(0, 1, 0, 0, 0, 0);
        final int hash11 = IntArrayMappedTrieMath.hash(0, 1, 0, 0, 0, 1);
        final int hash12 = IntArrayMappedTrieMath.hash(0, 1, 0, 0, 1, 1);
        final int hash13 = IntArrayMappedTrieMath.hash(0, 1, 0, 0, 2, 1);
        final int hash20 = IntArrayMappedTrieMath.hash(0, 2, 0, 0, 0, 0);
        final int hash21 = IntArrayMappedTrieMath.hash(0, 2, 0, 0, 0, 1);
        final int hash22 = IntArrayMappedTrieMath.hash(0, 2, 0, 0, 1, 1);
        final int hash23 = IntArrayMappedTrieMath.hash(0, 2, 0, 0, 2, 1);

        IArray<Integer> a = TrieArray.<Integer>of()
            .assign(hash10, 10)
            .assign(hash11, 11)
            .assign(hash12, 12)
            .assign(hash13, 13)
            .assign(hash20, 20)
            .assign(hash21, 21)
            .assign(hash22, 22)
            .assign(hash23, 23);
        List<Integer> keys = a.keys().stream().collect(Collectors.toList());
        assertEquals(Arrays.asList(hash10, hash11, hash12, hash13, hash20, hash21, hash22, hash23), keys);
    }

    public void testRandom()
    {
        Random r = new Random(20L);
        for (int outerLoop = 1; outerLoop <= 10; ++outerLoop) {
            Map<Integer, Integer> expected = new TreeMap<>();
            IArray<Integer> array = TrieArray.of();
            for (int loop = 1; loop <= 30000; ++loop) {
                int index = r.nextInt(200000) - 100000;
                int value = r.nextInt();
                switch (r.nextInt(5)) {
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
                        Holder<Integer> integers = array.find(index);
                        assertEquals(integers.getOrNull(), array.get(index));
                        break;
                    case 4:
                        assertEquals(array.getValueOr(index, null), array.get(index));
                        break;
                }
                assertEquals(array.isEmpty(), expected.isEmpty());
                assertEquals(array.isNonEmpty(), !expected.isEmpty());
            }
            assertEquals(expected.size(), array.size());
            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                assertEquals(entry.getValue(), array.getValueOr(entry.getKey(), MAX_VALUE));
            }
            array.checkInvariants();
            final Temp.Int1 count = Temp.intVar(0);
            array.forEach(e -> {
                assertEquals(e.getValue(), expected.get(e.getKey()));
                count.a++;
            });
            assertEquals(expected.size(), count.a);
            count.a = 0;
            array.forEachThrows(e -> {
                assertEquals(e.getValue(), expected.get(e.getKey()));
                count.a++;
            });
            assertEquals(expected.size(), count.a);
            count.a = 0;
            array.forEach((k, v) -> {
                assertEquals(v, expected.get(k));
                count.a++;
            });
            assertEquals(expected.size(), count.a);
            count.a = 0;
            array.forEachThrows((k, v) -> {
                assertEquals(v, expected.get(k));
                count.a++;
            });
            assertEquals(expected.size(), count.a);
        }
    }

    public void testCollector()
    {
        Random r = new Random(20L);
        IArray<Integer> array = TrieArray.of();
        int size = 0;
        while (size < 50_000) {
            size += r.nextInt(250);
            while (array.size() < size) {
                final int value = r.nextInt();
                array = array.assign(array.size(), value);
            }
            assertEquals(array, array.values().parallelStream().collect(TrieArray.collector()));
        }
    }

    public void testSequential()
    {
        Map<Integer, Integer> expected = new TreeMap<>();
        IArray<Integer> array = TrieArray.of();
        for (int i = 0; i <= 20000; ++i) {
            array = array.assign(i, i);
            expected.put(i, i);
        }
        assertEquals(expected.size(), array.size());
        for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
            assertEquals(entry.getValue(), array.getValueOr(entry.getKey(), MAX_VALUE));
        }
    }

    public void testDepthTrimDoesNotBreakIndexing()
    {
        IArray<Integer> array = TrieArray.of();
        for (int i = 0; i < 10000; ++i) {
            array = array.assign(i, i);
        }

        for (int i = 9999; i >= 0; --i) {
            assertEquals((Integer)i, array.get(i));
            assertEquals(Holders.nullable(i), array.find(i));
            assertEquals((Integer)i, array.get(i));
            assertEquals(Holders.nullable(i), array.find(i));
            for (int shift = 31; shift > 20; --shift) {
                int shiftedIndex = i | (1 << shift);
                assertEquals(null, array.get(shiftedIndex));
                assertEquals(Holder.<Integer>none(), array.find(shiftedIndex));
                assertSame(array, array.delete(shiftedIndex));
            }
            IArray<Integer> deleted = array.delete(i);
            assertEquals(array.size() - 1, deleted.size());
            assertEquals(null, deleted.get(i));
        }
    }

    public void testDeleteToEmpty()
    {
        IArray<Integer> array = TrieArray.of();
        for (int i = 0; i < 10000; ++i) {
            array = array.assign(i, i);
        }

        for (int i = 9999; i >= 0; --i) {
            assertEquals((Integer)i, array.get(i));
            assertEquals(Holders.nullable(i), array.find(i));
            assertEquals((Integer)i, array.get(i));
            assertEquals(Holders.nullable(i), array.find(i));
            IArray<Integer> deleted = array.delete(i);
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
        IArray<Integer> array = TrieArray.of();
        array = array.assign(-500, -5001).assign(-10, -101).assign(-1, -11).assign(0, 0).assign(1, 11).assign(10, 101).assign(500, 5001);

        List<Integer> indexes = asList(-500, -10, -1, 0, 1, 10, 500);
        StandardIteratorTests.listIteratorTest(indexes, array.keys().iterator());

        List<Integer> values = asList(-5001, -101, -11, 0, 11, 101, 5001);
        StandardIteratorTests.listIteratorTest(values, array.values().iterator());

        List<IMapEntry<Integer, Integer>> entries = new ArrayList<>();
        entries.add(IMapEntry.of(-500, -5001));
        entries.add(IMapEntry.of(-10, -101));
        entries.add(IMapEntry.of(-1, -11));
        entries.add(IMapEntry.of(0, 0));
        entries.add(IMapEntry.of(1, 11));
        entries.add(IMapEntry.of(10, 101));
        entries.add(IMapEntry.of(500, 5001));
        StandardIteratorTests.listIteratorTest(entries, array.iterator());

        entries.clear();
        array = TrieArray.of();
        for (int i = -1000; i <= 1000; ++i) {
            entries.add(IMapEntry.of(i, 1000 + i));
            array = array.assign(i, 1000 + i);
        }
        StandardIteratorTests.verifyOrderedIterable(entries, array);
    }

    public void testSignedOrderIteration()
    {
        final int numLoops = 100000;
        final int increment = (MAX_VALUE / numLoops) * 2;
        IArray<Integer> array = TrieArray.of();
        List<Integer> keys = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        List<IMapEntry<Integer, Integer>> entries = new ArrayList<>();
        int index = MIN_VALUE;
        for (int i = 0; i < numLoops; ++i) {
            keys.add(index);
            values.add(-index);
            entries.add(IMapEntry.of(index, -index));
            array = array.assign(index, -index);
            index += increment;
        }
        StandardIteratorTests.listIteratorTest(keys, array.keys().iterator());
        StandardIteratorTests.listIteratorTest(values, array.values().iterator());
        StandardIteratorTests.listIteratorTest(entries, array.iterator());
        StandardStreamableTests.verifyOrderedUsingCollection(keys, array.keys());
        StandardStreamableTests.verifyOrderedUsingCollection(values, array.values());
        StandardStreamableTests.verifyOrderedUsingCollection(entries, array);
    }

    public void testVarious()
    {
        List<Integer> indexes = createBranchIndexes();
        for (int length = indexes.size(); length > 0; --length) {
            Map<Integer, Integer> map = new TreeMap<>();
            List<Integer> keys = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            IArray<Integer> array = TrieArray.of();
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
                assertEquals(Holders.nullable(i), array.find(index));
                assertEquals(Holders.<IMapEntry<Integer, Integer>>nullable(IMapEntry.of(index, i)), array.findEntry(index));
            }
            array.checkInvariants();
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                array = array.assign(index, i - 1);
                assertEquals(Integer.valueOf(i - 1), array.get(index));
                assertEquals(Integer.valueOf(i - 1), array.getValueOr(index, -99));
                assertEquals(Holders.nullable(i - 1), array.find(index));
                assertEquals(Holders.<IMapEntry<Integer, Integer>>nullable(IMapEntry.of(index, i - 1)), array.findEntry(index));
            }
            array.checkInvariants();
            for (int i = 0; i < length; ++i) {
                final Integer index = indexes.get(i);
                array = array.delete(index);
                assertEquals(length - i - 1, array.size());
                assertEquals(null, array.get(index));
                assertEquals(Integer.valueOf(-99), array.getValueOr(index, -99));
                assertEquals(Holder.<Integer>none(), array.find(index));
                assertEquals(Holder.<IMapEntry<Integer, Integer>>none(), array.findEntry(index));
            }
            array.checkInvariants();
        }
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((IArray)a).iterator();
        final IArray<String> empty = TrieArray.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpciwqSqwMKMqvqPwPAv9UjHkYGCoKyjkYGJhfMgBBBQBbOGFFXwAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert(IMapEntry.of(1, "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpciwqSqwMKMqvqPwPAv9UjHkYGCoKyoEk80sGBgZGEC5hYEysAACKUbzuZwAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList(IMapEntry.of(Integer.MIN_VALUE, "a"), IMapEntry.of(1, "b"), IMapEntry.of(Integer.MAX_VALUE, "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBPr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQpciwqSqwMKMqvqPwPAv9UjHkYGCoKyoEk80sGINEAJEoYGBPLWYAMRiArqZylHqgSyEquAABirp0EewAAAA==");
    }

    public void testBuilder()
        throws Exception
    {
        assertSame(TrieArray.of(), TrieArray.builder().build());

        final List<Integer> expected = new ArrayList<>();
        final IArrayBuilder<Integer> builder = TrieArray.builder();
        IArray<Integer> array;
        IArray<Integer> manual = TrieArray.of();
        for (int length = 1; length <= 1024; ++length) {
            expected.add(length);
            manual = manual.assign(length - 1, length);
            array = builder.add(length).build();
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
            array = builder.add(length).build();
            assertEquals(length, array.size());
            for (int i = 0; i < expected.size(); ++i) {
                assertEquals(expected.get(i), array.get(i));
            }
            array.checkInvariants();
        }
        array = builder.build();
        assertEquals(manual, array);

        Func0<IArrayBuilder<Integer>> factory = () -> TrieArray.builder();

        Func2<List<Integer>, IArray<Integer>, Boolean> comparator = (list, tree) -> {
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
        return new BuilderTestAdapter<>(TrieArray.builder());
    }

    private static class BuilderTestAdapter<T>
        implements StandardBuilderTests.BuilderAdapter<T, IArray<T>>
    {
        private final IArrayBuilder<T> builder;

        public BuilderTestAdapter(IArrayBuilder<T> builder)
        {
            this.builder = builder;
        }

        @Override
        public IArray<T> build()
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
