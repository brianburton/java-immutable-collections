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

package org.javimmutable.collections.inorder;

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapBuilder;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.common.MapBuilderTestAdapter;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.StandardMapTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.common.StandardStreamableTests;
import org.javimmutable.collections.iterators.StandardIteratorTests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class OrderedMapTest
    extends TestCase
{
    public void test()
    {
        StandardMapTests.verifyMiscellaneous(OrderedMap.of());
    }

    public void testIterators()
    {
        List<IMapEntry<String, String>> expectedEntries = new ArrayList<>();
        List<String> expectedKeys = new ArrayList<>();
        List<String> expectedValues = new ArrayList<>();
        OrderedMap<String, String> map = OrderedMap.of();

        StandardMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.assign("x", "X");
        expectedEntries.add(IMapEntry.of("x", "X"));
        expectedKeys.add("x");
        expectedValues.add("X");
        StandardMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.assign("d", "D");
        expectedEntries.add(IMapEntry.of("d", "D"));
        expectedKeys.add("d");
        expectedValues.add("D");
        StandardMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.assign("c", "C");
        expectedEntries.add(IMapEntry.of("c", "C"));
        expectedKeys.add("c");
        expectedValues.add("C");
        StandardMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.delete("d");
        expectedEntries.remove(1);
        expectedKeys.remove(1);
        expectedValues.remove(1);
        StandardMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = (OrderedMap<String, String>)map.insert(IMapEntry.of("d", "D"));
        expectedEntries.add(IMapEntry.of("d", "D"));
        expectedKeys.add("d");
        expectedValues.add("D");
        StandardMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.assign("x", "XX");
        expectedEntries.set(0, IMapEntry.of("x", "XX"));
        expectedValues.set(0, "XX");
        StandardMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());
    }

    public void testRandomAdds()
    {
        Random r = new Random(0L);
        for (int loop = 1; loop <= 20; ++loop) {
            IMap<Integer, Integer> map = OrderedMap.of();
            Map<Integer, Integer> expected = new LinkedHashMap<>();
            for (int i = 0; i < 2500; ++i) {
                int command = r.nextInt(3);
                switch (command) {
                    case 0: {
                        int key = r.nextInt(500);
                        int value = r.nextInt(500);
                        int merged = value;
                        map = map.update(key, h -> h.isEmpty() ? value : h.unsafeGet() ^ value);
                        if (expected.get(key) != null) {
                            merged = expected.get(key) ^ value;
                        }
                        expected.put(key, merged);
                        //noinspection ConstantConditions
                        assertEquals(merged, (int)map.get(key));
                        assertEquals(merged, (int)map.getValueOr(key, value - 1000));
                        Maybe<Integer> integers = map.find(key);
                        assertEquals(merged, (int)integers.getOrNull());
                        assertEquals(Maybe.of(merged), map.find(key));
                        Maybe<IMapEntry<Integer, Integer>> iMapEntries = map.findEntry(key);
                        assertEquals(IMapEntry.of(key, merged), iMapEntries.unsafeGet());
                        break;
                    }
                    case 1: {
                        int key = r.nextInt(500);
                        int value = r.nextInt(500);
                        map = map.assign(key, value);
                        expected.put(key, value);
                        //noinspection ConstantConditions
                        assertEquals(value, (int)map.get(key));
                        assertEquals(value, (int)map.getValueOr(key, value - 1000));
                        Maybe<Integer> integers = map.find(key);
                        assertEquals(value, (int)integers.getOrNull());
                        assertEquals(Maybe.of(value), map.find(key));
                        Maybe<IMapEntry<Integer, Integer>> iMapEntries = map.findEntry(key);
                        assertEquals(IMapEntry.of(key, value), iMapEntries.unsafeGet());
                        break;
                    }
                    case 2: {
                        OrderedMap<Integer, Integer> col = OrderedMap.of();
                        int times = r.nextInt(3);

                        for (int rep = 0; rep < times; rep++) {
                            int key = r.nextInt(500);
                            int value = r.nextInt(500);
                            col = col.assign(key, value);
                        }
                        expected.putAll(col.getMap());
                        map = (r.nextInt(2) == 0) ? addAll(map, col) : addAll(map, col.getMap());
                        break;
                    }
                }
            }
            map.checkInvariants();

            assertEquals(expected, map.getMap());
            List<IMapEntry<Integer, Integer>> entries = new ArrayList<>();
            List<Integer> keys = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                entries.add(IMapEntry.of(entry));
                keys.add(entry.getKey());
                values.add(entry.getValue());
            }
            StandardMapTests.verifyEnumeration(entries, map);
            StandardIteratorTests.listIteratorTest(entries, map.iterator());
            StandardIteratorTests.listIteratorTest(keys, map.keys().iterator());
            StandardIteratorTests.listIteratorTest(values, map.values().iterator());
            while (!keys.isEmpty()) {
                assertFalse(map.isEmpty());
                Integer key = keys.remove(0);
                assertNotNull(map.get(key));
                map = map.delete(key);
                assertEquals(key - 99, (int)map.getValueOr(key, key - 99));
                assertEquals(null, map.getValueOr(key, null));
                Maybe<Integer> integers = map.find(key);
                assertEquals(null, integers.getOrNull());
                assertEquals(keys.size(), map.size());
            }
            assertTrue(map.isEmpty());
        }
    }

    public void testAssignAll()
    {
        //assignAll(JImmutableMap)
        IMap<String, Number> empty = OrderedMap.of();
        IMap<String, Number> map = empty;
        IMap<String, Integer> expected = OrderedMap.of();
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());

        expected = expected.assign("a", 10);
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertEquals(1, map.size());
        assertEquals(10, map.get("a"));

        assertEquals(map, map.assignAll(empty));

        expected = expected.assign("a", 8).assign("b", 12).assign("c", 14);
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertEquals(3, map.size());
        assertEquals(8, map.get("a"));

        //assignAll(Map)
        map = empty;
        Map<String, Integer> expectedMutable = new LinkedHashMap<>();
        map = map.assignAll(expectedMutable);
        assertEquals(expectedMutable, map.getMap());
        assertEquals(0, map.size());

        expectedMutable.put("a", 10);
        map = map.assignAll(expectedMutable);
        assertEquals(expectedMutable, map.getMap());
        assertEquals(1, map.size());
        assertEquals(10, map.get("a"));

        assertEquals(map, map.assignAll(Collections.<String, Integer>emptyMap()));

        expectedMutable.put("a", 8);
        expectedMutable.put("b", 12);
        expectedMutable.put("c", 14);
        map = map.assignAll(expectedMutable);
        assertEquals(expectedMutable, map.getMap());
        assertEquals(3, map.size());
        assertEquals(8, map.get("a"));

    }

    public void testStreams()
    {
        final IMap<Integer, Integer> inOrderMap = OrderedMap.of();
        assertEquals(asList(), inOrderMap.stream().collect(Collectors.toList()));
        assertEquals(asList(IMapEntry.of(1, 10)), inOrderMap.assign(1, 10).stream().collect(Collectors.toList()));
        assertEquals(asList(IMapEntry.of(4, 40), IMapEntry.of(1, 10)), inOrderMap.assign(4, 40).assign(1, 10).stream().collect(Collectors.toList()));

        assertEquals(asList(), inOrderMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(1), inOrderMap.assign(1, 10).keys().stream().collect(Collectors.toList()));
        assertEquals(asList(4, 1), inOrderMap.assign(4, 40).assign(1, 10).keys().stream().collect(Collectors.toList()));

        assertEquals(asList(), inOrderMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(10), inOrderMap.assign(1, 10).values().stream().collect(Collectors.toList()));
        assertEquals(asList(40, 10), inOrderMap.assign(4, 40).assign(1, 10).values().stream().collect(Collectors.toList()));

        List<Integer> keys = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        List<IMapEntry<Integer, Integer>> entries = new ArrayList<>();
        IMap<Integer, Integer> map = inOrderMap;
        Random r = new Random();
        for (int i = 1; i <= 1000; ++i) {
            final int key = r.nextInt();
            keys.add(key);
            values.add(i);
            entries.add(IMapEntry.of(key, i));
            map = map.assign(key, i);
        }
        assertEquals(keys, map.keys().parallelStream().collect(Collectors.toList()));
        StandardStreamableTests.verifyOrderedUsingCollection(keys, map.keys());
        StandardStreamableTests.verifyOrderedUsingCollection(values, map.values());
        StandardStreamableTests.verifyOrderedUsingCollection(entries, map);

        map = keys.parallelStream().map(i -> IMapEntry.of(i, -i)).collect(inOrderMap.mapCollector());
        assertEquals(keys, map.keys().parallelStream().collect(Collectors.toList()));
        assertEquals(map, map.parallelStream().collect(OrderedMap.createMapCollector()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((IMap)a).iterator();
        final IMap<Integer, String> empty = OrderedMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzL0pJLUpN8U0sCCjKr6j8DwL/VIx5GBgqihisSTDIMam4pCgxuQSbSQXlHAwMzC8ZgKACAAKul++kAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert(IMapEntry.of(1, "a")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzL0pJLUpN8U0sCCjKr6j8DwL/VIx5GBgqihisSTDIMam4pCgxuQSbSQXlHAwMzC8ZGBgYgc4TBJqYqJeTmJeu55lXkpqeWiT0aMGS743tFkwMjJ4MrGWJOaWpQNsFEOr8SnOTUova1kyV5Z7yoJsJZCTIsBIGxsQKAO/g/631AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList(IMapEntry.of(Integer.MIN_VALUE, "a"), IMapEntry.of(1, "b"), IMapEntry.of(Integer.MAX_VALUE, "c"))),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBKr8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzL0pJLUpN8U0sCCjKr6j8DwL/VIx5GBgqihisSTDIMam4pCgxuQSbSQXlHAwMzC8ZgATQeYJAExP1chLz0vU880pS01OLhB4tWPK9sd2CiYHRk4G1LDGnNBVouwBCnV9pblJqUduaqbLcUx50M4GMbACaVsLAmFhcyFDHwAzkMAJ5SRBePdBqIC+5AgCn2q3cEQEAAA==");
    }

    public void testBuilder()
    {
        final Random r = new Random(1265143000);
        for (int i = 1; i <= 100; ++i) {
            IMapBuilder<Integer, Integer> builder = OrderedMap.builder();
            IMap<Integer, Integer> expected = OrderedMap.of();
            final int size = 1 + r.nextInt(2000);
            for (int k = 1; k <= size; ++k) {
                final Integer key = r.nextInt(5 * size);
                final Integer value = r.nextInt();
                builder.add(key, value);
                expected = expected.assign(key, value);
            }
            IMap<Integer, Integer> actual = builder.build();
            actual.checkInvariants();
            assertEquals(expected, actual);

            assertEquals(expected.deleteAll().assign(8, 3), builder.clear().add(8, 3).build());
        }
    }

    public void testStandardBuilderTests()
        throws InterruptedException
    {
        final List<IMapEntry<Integer, Integer>> values = new ArrayList<>();
        for (int i = 1; i <= 5000; ++i) {
            values.add(IMapEntry.of(i, 5001 - i));
        }
        Collections.shuffle(values);
        StandardBuilderTests.verifyBuilder(values, this::stdBuilderTestAdapter, this::stdBuilderTestComparator, new IMapEntry[0]);
        values.sort(MapEntry::compareKeys);
        StandardBuilderTests.verifyThreadSafety(values, MapEntry::compareKeys, this::stdBuilderTestAdapter, a -> a);
    }

    private MapBuilderTestAdapter<Integer, Integer> stdBuilderTestAdapter()
    {
        return new MapBuilderTestAdapter<>(OrderedMap.builder());
    }

    private Boolean stdBuilderTestComparator(List<IMapEntry<Integer, Integer>> expected,
                                             IMap<Integer, Integer> actual)
    {
        assertEquals(expected, actual.stream().collect(Collectors.toList()));
        return true;
    }

    private IMap<Integer, Integer> addAll(IMap<Integer, Integer> map,
                                          IMap<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        for (IMapEntry<Integer, Integer> entry : extra) {
            assertEquals(entry.getValue(), map.get(entry.getKey()));
            assertEquals(entry.getValue(), map.getValueOr(entry.getKey(), entry.getValue() - 1000));
            Maybe<Integer> integers = map.find(entry.getKey());
            assertEquals(entry.getValue(), integers.getOrNull());
            Integer value = entry.getValue();
            assertEquals(Maybe.of(value), map.find(entry.getKey()));
            Maybe<IMapEntry<Integer, Integer>> iMapEntries = map.findEntry(entry.getKey());
            assertEquals(IMapEntry.of(entry.getKey(), entry.getValue()), iMapEntries.unsafeGet());
        }
        return map;
    }

    private IMap<Integer, Integer> addAll(IMap<Integer, Integer> map,
                                          Map<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        for (Map.Entry<Integer, Integer> entry : extra.entrySet()) {
            assertEquals(entry.getValue(), map.get(entry.getKey()));
            assertEquals(entry.getValue(), map.getValueOr(entry.getKey(), entry.getValue() - 1000));
            Maybe<Integer> integers = map.find(entry.getKey());
            assertEquals(entry.getValue(), integers.getOrNull());
            assertEquals(Maybe.of(entry.getValue()), map.find(entry.getKey()));
            Maybe<IMapEntry<Integer, Integer>> iMapEntries = map.findEntry(entry.getKey());
            assertEquals(IMapEntry.of(entry.getKey(), entry.getValue()), iMapEntries.unsafeGet());
        }
        return map;
    }
}
