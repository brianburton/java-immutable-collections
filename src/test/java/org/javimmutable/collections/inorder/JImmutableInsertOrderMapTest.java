///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MapBuilderTestAdaptor;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.StandardJImmutableMapTests;
import org.javimmutable.collections.common.StandardSerializableTests;
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

public class JImmutableInsertOrderMapTest
    extends TestCase
{
    public void test()
    {
        StandardJImmutableMapTests.verifyMiscellaneous(JImmutableInsertOrderMap.of());
    }

    public void testIterators()
    {
        List<JImmutableMap.Entry<String, String>> expectedEntries = new ArrayList<>();
        List<String> expectedKeys = new ArrayList<>();
        List<String> expectedValues = new ArrayList<>();
        JImmutableInsertOrderMap<String, String> map = JImmutableInsertOrderMap.of();

        StandardJImmutableMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.assign("x", "X");
        expectedEntries.add(MapEntry.of("x", "X"));
        expectedKeys.add("x");
        expectedValues.add("X");
        StandardJImmutableMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.assign("d", "D");
        expectedEntries.add(MapEntry.of("d", "D"));
        expectedKeys.add("d");
        expectedValues.add("D");
        StandardJImmutableMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.assign("c", "C");
        expectedEntries.add(MapEntry.of("c", "C"));
        expectedKeys.add("c");
        expectedValues.add("C");
        StandardJImmutableMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.delete("d");
        expectedEntries.remove(1);
        expectedKeys.remove(1);
        expectedValues.remove(1);
        StandardJImmutableMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = (JImmutableInsertOrderMap<String, String>)map.insert(MapEntry.of("d", "D"));
        expectedEntries.add(MapEntry.of("d", "D"));
        expectedKeys.add("d");
        expectedValues.add("D");
        StandardJImmutableMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());

        map = map.assign("x", "XX");
        expectedEntries.set(0, MapEntry.of("x", "XX"));
        expectedValues.set(0, "XX");
        StandardJImmutableMapTests.verifyEnumeration(expectedEntries, map);
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(expectedKeys, map.keys().iterator());
        StandardIteratorTests.listIteratorTest(expectedValues, map.values().iterator());
    }

    public void testRandomAdds()
    {
        Random r = new Random(0L);
        for (int loop = 1; loop <= 20; ++loop) {
            JImmutableMap<Integer, Integer> map = JImmutableInsertOrderMap.of();
            Map<Integer, Integer> expected = new LinkedHashMap<>();
            for (int i = 0; i < 2500; ++i) {
                int command = r.nextInt(3);
                switch (command) {
                    case 0: {
                        int key = r.nextInt(500);
                        int value = r.nextInt(500);
                        int merged = value;
                        map = map.update(key, h -> h.isEmpty() ? value : h.getValue() ^ value);
                        if (expected.get(key) != null) {
                            merged = expected.get(key) ^ value;
                        }
                        expected.put(key, merged);
                        //noinspection ConstantConditions
                        assertEquals(merged, (int)map.get(key));
                        assertEquals(merged, (int)map.getValueOr(key, value - 1000));
                        assertEquals(merged, (int)map.find(key).getValueOrNull());
                        assertEquals(Holders.of(merged), map.find(key));
                        assertEquals(MapEntry.of(key, merged), map.findEntry(key).getValue());
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
                        assertEquals(value, (int)map.find(key).getValueOrNull());
                        assertEquals(Holders.of(value), map.find(key));
                        assertEquals(MapEntry.of(key, value), map.findEntry(key).getValue());
                        break;
                    }
                    case 2: {
                        JImmutableInsertOrderMap<Integer, Integer> col = JImmutableInsertOrderMap.of();
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
            List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<>();
            List<Integer> keys = new ArrayList<>();
            List<Integer> values = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                entries.add(MapEntry.of(entry));
                keys.add(entry.getKey());
                values.add(entry.getValue());
            }
            StandardJImmutableMapTests.verifyEnumeration(entries, map);
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
                assertEquals(null, map.find(key).getValueOrNull());
                assertEquals(keys.size(), map.size());
            }
            assertTrue(map.isEmpty());
        }
    }

    public void testAssignAll()
    {
        //assignAll(JImmutableMap)
        JImmutableMap<String, Number> empty = JImmutableInsertOrderMap.of();
        JImmutableMap<String, Number> map = empty;
        JImmutableMap<String, Integer> expected = JImmutableInsertOrderMap.of();
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
        final JImmutableMap<Integer, Integer> inOrderMap = JImmutableInsertOrderMap.of();
        assertEquals(asList(), inOrderMap.stream().collect(Collectors.toList()));
        assertEquals(asList(MapEntry.of(1, 10)), inOrderMap.assign(1, 10).stream().collect(Collectors.toList()));
        assertEquals(asList(MapEntry.of(4, 40), MapEntry.of(1, 10)), inOrderMap.assign(4, 40).assign(1, 10).stream().collect(Collectors.toList()));

        assertEquals(asList(), inOrderMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(1), inOrderMap.assign(1, 10).keys().stream().collect(Collectors.toList()));
        assertEquals(asList(4, 1), inOrderMap.assign(4, 40).assign(1, 10).keys().stream().collect(Collectors.toList()));

        assertEquals(asList(), inOrderMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(10), inOrderMap.assign(1, 10).values().stream().collect(Collectors.toList()));
        assertEquals(asList(40, 10), inOrderMap.assign(4, 40).assign(1, 10).values().stream().collect(Collectors.toList()));

        List<Integer> keys = new ArrayList<>();
        JImmutableMap<Integer, Integer> map = inOrderMap;
        Random r = new Random();
        for (int i = 1; i <= 1000; ++i) {
            final int key = r.nextInt();
            keys.add(key);
            map = map.assign(key, i);
        }
        assertEquals(keys, map.keys().parallelStream().collect(Collectors.toList()));

        map = keys.parallelStream().map(i -> MapEntry.of(i, -i)).collect(inOrderMap.mapCollector());
        assertEquals(keys, map.keys().parallelStream().collect(Collectors.toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((JImmutableMap)a).iterator();
        final JImmutableMap<Integer, String> empty = JImmutableInsertOrderMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvkmFgQU5VdU/geBfyrGPAwMFUUMriQY65hUXFKUmFyCMB6bmQXlHAwMzC8ZgKACANcYyRO8AAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert(MapEntry.of(1, "a")),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvkmFgQU5VdU/geBfyrGPAwMFUUMriQY65hUXFKUmFyCMB6bmQXlHAwMzC8ZGBgYgc4WBJqdqJeTmJeu55lXkpqeWiT0aMGS743tFkwMjJ4MrGWJOaWpQHcIINT5leYmpRa1rZkqyz3lQTcTyEiQYSUMjIkVAEaTh0oNAQAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList(MapEntry.of(Integer.MIN_VALUE, "a"), MapEntry.of(1, "b"), MapEntry.of(Integer.MAX_VALUE, "c"))),
                                                     "H4sIAAAAAAAAAFvzloG1uIjBI78oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXz8oQp8swDypT4F6WkFvkmFgQU5VdU/geBfyrGPAwMFUUMriQY65hUXFKUmFyCMB6bmQXlHAwMzC8ZgATQ2YJAsxP1chLz0vU880pS01OLhB4tWPK9sd2CiYHRk4G1LDGnNBXoDgGEOr/S3KTUorY1U2W5pzzoZgIZ2QA0rYSBMbG4kKGOgRnIYQTykiC8eqDVQF5yBQAzIFETKQEAAA==");
    }

    public void testBuilder()
    {
        final Random r = new Random(1265143000);
        for (int i = 1; i <= 100; ++i) {
            JImmutableMap.Builder<Integer, Integer> builder = JImmutableInsertOrderMap.builder();
            JImmutableMap<Integer, Integer> expected = JImmutableInsertOrderMap.of();
            final int size = 1 + r.nextInt(2000);
            for (int k = 1; k <= size; ++k) {
                final Integer key = r.nextInt(5 * size);
                final Integer value = r.nextInt();
                builder.add(key, value);
                expected = expected.assign(key, value);
            }
            JImmutableMap<Integer, Integer> actual = builder.build();
            actual.checkInvariants();
            assertEquals(expected, actual);
        }
    }

    public void testStandardBuilderTests()
        throws InterruptedException
    {
        final List<JImmutableMap.Entry<Integer, Integer>> values = new ArrayList<>();
        for (int i = 1; i <= 5000; ++i) {
            values.add(MapEntry.of(i, 5001 - i));
        }
        Collections.shuffle(values);
        StandardBuilderTests.verifyBuilder(values, this::stdBuilderTestAdaptor, this::stdBuilderTestComparator, new JImmutableMap.Entry[0]);
        values.sort(MapEntry::compareKeys);
        StandardBuilderTests.verifyThreadSafety(values, MapEntry::compareKeys, this::stdBuilderTestAdaptor, a -> a);
    }

    private MapBuilderTestAdaptor<Integer, Integer> stdBuilderTestAdaptor()
    {
        return new MapBuilderTestAdaptor<>(JImmutableInsertOrderMap.builder());
    }

    private Boolean stdBuilderTestComparator(List<JImmutableMap.Entry<Integer, Integer>> expected,
                                             JImmutableMap<Integer, Integer> actual)
    {
        List<JImmutableMap.Entry<Integer, Integer>> sorted = new ArrayList<>(expected);
        assertEquals(sorted, actual.stream().sorted(MapEntry::compareKeys).collect(Collectors.toList()));
        return true;
    }

    private JImmutableMap<Integer, Integer> addAll(JImmutableMap<Integer, Integer> map,
                                                   JImmutableMap<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        for (JImmutableMap.Entry<Integer, Integer> entry : extra) {
            assertEquals(entry.getValue(), map.get(entry.getKey()));
            assertEquals(entry.getValue(), map.getValueOr(entry.getKey(), entry.getValue() - 1000));
            assertEquals(entry.getValue(), map.find(entry.getKey()).getValueOrNull());
            assertEquals(Holders.of(entry.getValue()), map.find(entry.getKey()));
            assertEquals(MapEntry.of(entry.getKey(), entry.getValue()), map.findEntry(entry.getKey()).getValue());
        }
        return map;
    }

    private JImmutableMap<Integer, Integer> addAll(JImmutableMap<Integer, Integer> map,
                                                   Map<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        for (Map.Entry<Integer, Integer> entry : extra.entrySet()) {
            assertEquals(entry.getValue(), map.get(entry.getKey()));
            assertEquals(entry.getValue(), map.getValueOr(entry.getKey(), entry.getValue() - 1000));
            assertEquals(entry.getValue(), map.find(entry.getKey()).getValueOrNull());
            assertEquals(Holders.of(entry.getValue()), map.find(entry.getKey()));
            assertEquals(MapEntry.of(entry.getKey(), entry.getValue()), map.findEntry(entry.getKey()).getValue());
        }
        return map;
    }
}
