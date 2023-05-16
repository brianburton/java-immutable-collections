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

package org.javimmutable.collection.tree;

import junit.framework.TestCase;
import org.javimmutable.collection.Func1;
import org.javimmutable.collection.IMap;
import org.javimmutable.collection.IMapBuilder;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.Maybe;
import org.javimmutable.collection.Proc2;
import org.javimmutable.collection.Proc2Throws;
import org.javimmutable.collection.Sum2;
import org.javimmutable.collection.Sum2Throws;
import org.javimmutable.collection.common.StandardMapTests;
import org.javimmutable.collection.common.StandardSerializableTests;
import org.javimmutable.collection.iterators.StandardIteratorTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.javimmutable.collection.common.StandardMapTests.*;
import static org.javimmutable.collection.iterators.StandardIteratorTests.emptyIteratorTest;

public class TreeMapTest
    extends TestCase
{
    public void test()
    {
        StandardMapTests.verifyMiscellaneous(TreeMap.of());
    }

    public void testInsert()
    {
        TreeMap<Integer, Integer> map = TreeMap.of();
        verifyEmptyEnumeration(map);
        emptyIteratorTest(map.iterator());
        emptyIteratorTest(map.keys().iterator());
        emptyIteratorTest(map.values().iterator());
        assertEquals(0, map.size());
        assertEquals(true, map.isEmpty());
        assertEquals(Collections.<Integer>emptyList(), map.getKeysList());
        map = add(map, 5);
        assertEquals(1, map.size());
        assertEquals(false, map.isEmpty());
        assertEquals(Arrays.asList(5), map.getKeysList());
        map = add(map, 7);
        assertEquals(2, map.size());
        assertEquals(Arrays.asList(5, 7), map.getKeysList());
        map = add(map, 3);
        assertEquals(3, map.size());
        assertEquals(Arrays.asList(3, 5, 7), map.getKeysList());

        map = TreeMap.of();
        assertSame(Maybe.absent(), map.find(10));
        map = map.assign(30, 18).assign(10, 11).assign(20, 19);
        assertEquals(Maybe.present(11), map.find(10));
        assertEquals(Arrays.asList(10, 20, 30), new ArrayList<>(map.getMap().keySet()));
        assertEquals(Arrays.asList(11, 19, 18), new ArrayList<>(map.getMap().values()));
        final List<IMapEntry<Integer, Integer>> expectedEntries = Arrays.asList(IMapEntry.of(10, 11), IMapEntry.of(20, 19), IMapEntry.of(30, 18));
        StandardIteratorTests.listIteratorTest(expectedEntries, map.iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(10, 20, 30), map.getMap().keySet().iterator());
        StandardIteratorTests.listIteratorTest(Arrays.asList(11, 19, 18), map.getMap().values().iterator());

        verifyEnumeration(expectedEntries, map);
    }

    public void testValueIdentity()
    {
        TreeMap<String, String> map = TreeMap.of();
        map = map.assign("a", "A");
        assertSame(map, map.assign("a", "A"));
        assertFalse(map == map.assign("a", "AA"));
    }

    public void testRandom1()
    {
        Random random = new Random();
        for (int loop = 0; loop < 20; ++loop) {
            Set<Integer> expected = new TreeSet<>();
            Map<Integer, Integer> treeMap = new java.util.TreeMap<>();
            TreeMap<Integer, Integer> map = TreeMap.of();
            final int size = 250 + random.nextInt(250);
            for (int i = 1; i <= size; ++i) {
                Integer value = random.nextInt(100000);
                expected.add(value);
                treeMap.put(value, value);
                map = add(map, value);
                assertEquals(new ArrayList<>(expected), map.getKeysList());
                assertEquals(expected.size(), map.size());
            }
            verifyEnumeration(treeMap, map);
            assertEquals(expected, map.getMap().keySet());

            // test value identity at all levels
            for (IMapEntry<Integer, Integer> entry : map) {
                assertSame(map, map.assign(entry.getKey(), entry.getValue()));
            }

            ArrayList<Integer> keys = new ArrayList<>(expected);
            Collections.shuffle(keys, random);
            for (Integer key : keys) {
                map = remove(map, key);
            }
            verifyEmptyEnumeration(map);
            assertEquals(0, map.size());
            assertEquals(new ArrayList<Integer>(), map.getKeysList());
        }
    }

    public void testRandom2()
    {
        final int maxKey = 500;
        Random random = new Random();
        for (int loop = 0; loop < 10; ++loop) {
            Map<Integer, Integer> expected = new java.util.TreeMap<>();
            IMap<Integer, Integer> map = TreeMap.of();
            for (int i = 1; i <= 25 * maxKey; ++i) {
                int command = random.nextInt(5);
                switch (command) {
                    case 0: {
                        Integer key = random.nextInt(maxKey);
                        Integer value = random.nextInt(1000000);
                        Integer merged = value;
                        if (expected.get(key) != null) {
                            merged = expected.get(key) ^ value;
                        }
                        map = update(map, key, value, merged);
                        expected.put(key, merged);
                        assertEquals(expected.get(key), map.get(key));
                        assertEquals(expected.get(key), map.getValueOr(key, -99));
                        Maybe<Integer> integers = map.find(key);
                        assertEquals(expected.get(key), integers.unsafeGet());
                        break;
                    }
                    case 1: {
                        Integer key = random.nextInt(maxKey);
                        Integer value = random.nextInt(1000000);
                        expected.put(key, value);
                        map = add(map, key, value);
                        assertEquals(expected.get(key), map.get(key));
                        assertEquals(expected.get(key), map.getValueOr(key, -99));
                        Maybe<Integer> integers = map.find(key);
                        assertEquals(expected.get(key), integers.unsafeGet());
                        break;
                    }
                    case 2: {
                        TreeMap<Integer, Integer> col = TreeMap.of();
                        int times = random.nextInt(3);

                        for (int rep = 0; rep < times; rep++) {
                            Integer key = random.nextInt(maxKey);
                            Integer value = random.nextInt(1000000);
                            col.assign(key, value);
                        }
                        expected.putAll(col.getMap());
                        map = (random.nextInt(2) == 0) ? addAll(map, col) : addAll(map, col.getMap());
                        break;
                    }
                    case 3: {
                        Integer key = random.nextInt(maxKey);
                        expected.remove(key);
                        map = remove(map, key);
                        assertEquals(null, map.get(key));
                        assertEquals(Integer.valueOf(-99), map.getValueOr(key, -99));
                        Maybe<Integer> integers = map.find(key);
                        assertEquals(true, integers.isAbsent());
                        break;
                    }
                    case 4: {
                        Integer key = random.nextInt(maxKey);
                        if (expected.containsKey(key)) {
                            assertEquals(expected.get(key), map.get(key));
                            assertEquals(expected.get(key), map.getValueOr(key, -99));
                            Maybe<Integer> integers = map.find(key);
                            assertEquals(expected.get(key), integers.unsafeGet());
                            Maybe<IMapEntry<Integer, Integer>> iMapEntries = map.findEntry(key);
                            assertEquals(IMapEntry.of(key, expected.get(key)), iMapEntries.unsafeGet());
                        } else {
                            assertEquals(null, map.get(key));
                            assertEquals(Integer.valueOf(-99), map.getValueOr(key, -99));
                            Maybe<Integer> integers = map.find(key);
                            assertEquals(true, integers.isAbsent());
                            Maybe<IMapEntry<Integer, Integer>> iMapEntries = map.findEntry(key);
                            assertEquals(true, iMapEntries.isAbsent());
                        }
                    }
                }
                assertEquals(expected.size(), map.size());
            }
            assertEquals(expected, map.getMap());
            assertEquals(expected.keySet(), map.getMap().keySet());
            assertEquals(new ArrayList<>(expected.values()), new ArrayList<>(map.getMap().values()));
            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                Maybe<Integer> value = map.find(entry.getKey());
                assertEquals(entry.getValue(), value.unsafeGet());
            }
            verifyEnumeration(expected, map);
        }
    }

    public void testDeleteAll()
    {
        TreeMap<Integer, Integer> map = TreeMap.of();
        map = map.assign(1, 2).assign(3, 4);
        TreeMap<Integer, Integer> cleared = map.deleteAll();
        assertSame(TreeMap.<Integer, Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        verifyEmptyEnumeration(cleared);
        emptyIteratorTest(cleared.iterator());

        map = TreeMap.of((a, b) -> -b.compareTo(a));
        map = map.assign(1, 2).assign(3, 4);
        cleared = map.deleteAll();
        assertNotSame(TreeMap.<Integer, Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        verifyEmptyEnumeration(cleared);
        emptyIteratorTest(cleared.iterator());
    }

    public void testAssignAll()
    {
        //assignAll(JImmutableMap)
        IMap<String, Number> empty = TreeMap.of();
        IMap<String, Number> map = empty;
        IMap<String, Integer> expected = TreeMap.of();
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
        Map<String, Integer> expectedMutable = new java.util.TreeMap<>();
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

    public void testIterator()
    {
        List<IMapEntry<Integer, Integer>> expected = new ArrayList<>();
        IMap<Integer, Integer> map = TreeMap.of();
        for (int i = -1000; i <= 1000; ++i) {
            expected.add(IMapEntry.of(i, 1000 + i));
            map = map.assign(i, 1000 + i);
        }
        StandardIteratorTests.verifyOrderedIterable(expected, map);
    }

    public void testStreams()
    {
        final IMap<Integer, Integer> treeMap = TreeMap.of();
        assertEquals(asList(), treeMap.stream().collect(Collectors.toList()));
        assertEquals(asList(IMapEntry.of(1, 10)), treeMap.assign(1, 10).stream().collect(Collectors.toList()));
        assertEquals(asList(IMapEntry.of(1, 10), IMapEntry.of(4, 40)), treeMap.assign(4, 40).assign(1, 10).stream().collect(Collectors.toList()));

        assertEquals(asList(), treeMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(1), treeMap.assign(1, 10).keys().stream().collect(Collectors.toList()));
        assertEquals(asList(1, 4), treeMap.assign(4, 40).assign(1, 10).keys().stream().collect(Collectors.toList()));

        assertEquals(asList(), treeMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(10), treeMap.assign(1, 10).values().stream().collect(Collectors.toList()));
        assertEquals(asList(10, 40), treeMap.assign(4, 40).assign(1, 10).values().stream().collect(Collectors.toList()));

        List<Integer> keys = new ArrayList<>();
        IMap<Integer, Integer> map = treeMap;
        Random r = new Random();
        for (int i = 1; i <= 1000; ++i) {
            final int key = r.nextInt();
            keys.add(key);
            map = map.assign(key, i);
        }
        Collections.sort(keys);
        assertEquals(keys, map.keys().parallelStream().collect(Collectors.toList()));
        assertEquals(map, map.parallelStream().collect(TreeMap.createMapCollector()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((IMap)a).iterator();
        IMap<String, String> empty = TreeMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBLL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfVNLAgoyq+o/A8C/1SMeRgYKooYrIg3xDGpuKQoMbkEm0EF5SwMDMwvga4yxWdgCdAdes75uQWJRSApKKskvwhmFhPMLCANAPGcYOjnAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMapTest::extraSerializationChecks, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBLL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfVNLAgoyq+o/A8C/1SMeRgYKooYrIg3xDGpuKQoMbkEm0EF5SwMDMwvga4yxWdgCdAdes75uQWJRSApKKskvwhmFhPMLAbGEgZGRyBOrAAANpH2Ku8AAAA=");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMapTest::extraSerializationChecks, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("G", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBLL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfVNLAgoyq+o/A8C/1SMeRgYKooYrIg3xDGpuKQoMbkEm0EF5SwMDMwvga4yxWdgCdAdes75uQWJRSApKKskvwhmFhPMLAbmEgZGRyBOBGJ3IE4C4iggTq4AAFoObC3/AAAA");

        empty = TreeMap.of(String.CASE_INSENSITIVE_ORDER);
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMapTest::extraSerializationChecks, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBLL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfVNLAgoyq+o/A8C/1SMeRgYKooYrIg3xDGpuKQoMbkEm0EF5SwMDMwvga7SAhqWqJeTmJeuF1xSlJmXruKcWJzqmVecmlecWZJZluqcn1uQWJRYkl9UzhxTGxPw9BwTzAAgDQBJXELv3AAAAA==");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMapTest::extraSerializationChecks, empty.insert(IMapEntry.of("A", "a")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBLL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/T684tSgzMSezKhHMCylKTfVNLAgoyq+o/A8C/1SMeRgYKooYrIg3xDGpuKQoMbkEm0EF5SwMDMwvga7SAhqWqJeTmJeuF1xSlJmXruKcWJzqmVecmlecWZJZluqcn1uQWJRYkl9UzhxTGxPw9BwTzAAGxhIGRkcgTqwAAIK7vBvkAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, TreeMapTest::extraSerializationChecks, empty.insertAll(asList(IMapEntry.of("A", "a"), IMapEntry.of("G", "b"), IMapEntry.of("Z", "c"))),
                                                     "H4sIAAAAAAAA/43OMQrCQBCF4dFo5zFSWWwjWNgFC7EQAlpJmkkYwspmN8yOSRS8kWfxFhYWXkFdBTsLB75imp93vsPQM0wdl2qHja6qvWBuSBXOGCpEO6s8sUajj/j5Nky0wjpl1x2e73vEkxFAxzD7P5LkXhgL+RWq2wFAdAurxiGGyqAt1VpY2zKeo6el9WS9Ft3Q3FU1MorjNspOWXq99L8BiAR6SYDBIsiDbVB0L4KwE/L0AAAA");
    }

    public static void extraSerializationChecks(Object a,
                                                Object b)
    {
        TreeMap mapA = (TreeMap)a;
        TreeMap mapB = (TreeMap)b;
        assertEquals(mapA.getComparator(), mapB.getComparator());
    }

    public void testBuilder()
    {
        final Random r = new Random(1265143000);
        for (int i = 1; i <= 1000; ++i) {
            IMapBuilder<Integer, Integer> builder = TreeMap.builder();
            IMap<Integer, Integer> expected = TreeMap.of();
            final int size = 1 + r.nextInt(4000);
            for (int k = 1; k <= size; ++k) {
                final Integer key = r.nextInt(2 * size);
                final Integer value = r.nextInt();
                builder.add(key, value);
                expected = expected.assign(key, value);
            }
            IMap<Integer, Integer> actual = builder.build();
            actual.checkInvariants();
            assertEquals(expected, actual);

            builder.clear();
            assertEquals(expected.deleteAll().assign(1, 2).assign(2, 3), builder.add(1, 2).add(2, 3).build());
        }
    }

    public void testForEach()
    {
        final StringBuilder sb = new StringBuilder();
        final Proc2<String, String> append = (k, v) -> {
            sb.append("[");
            sb.append(k);
            sb.append(",");
            sb.append(v);
            sb.append("]");
        };
        final TreeMap<String, String> empty = TreeMap.of();
        empty.forEach(append);
        assertEquals("", sb.toString());

        final TreeMap<String, String> map = empty.assign("a", "A").assign("c", "C").assign("b", "B");
        map.forEach(append);
        assertEquals("[a,A][b,B][c,C]", sb.toString());

        final Proc2Throws<String, String, IOException> appendThrows = (k, v) -> {
            sb.append("[");
            sb.append(k);
            sb.append(",");
            sb.append(v);
            sb.append("]");
            if (k.equals("b")) {
                throw new IOException();
            }
        };
        try {
            sb.delete(0, sb.length());
            map.forEachThrows(appendThrows);
            fail();
        } catch (IOException ex) {
            assertEquals("[a,A][b,B]", sb.toString());
        }
    }

    public void testReduce()
    {
        final Sum2<String, String, String> append = (s, k, v) -> {
            return s + "[" + k + "," + v + "]";
        };
        final TreeMap<String, String> empty = TreeMap.of();
        assertEquals("", empty.reduce("", append));

        final TreeMap<String, String> map = empty.assign("a", "A").assign("c", "C").assign("b", "B");
        assertEquals("[a,A][b,B][c,C]", map.reduce("", append));

        final Sum2Throws<String, String, String, IOException> appendThrows = (s, k, v) -> {
            if (k.equals("b")) {
                throw new IOException();
            } else {
                return s + "[" + k + "," + v + "]";
            }
        };
        try {
            map.reduceThrows("", appendThrows);
            fail();
        } catch (IOException ex) {
            // pass
        }
    }

    private TreeMap<Integer, Integer> add(IMap<Integer, Integer> map,
                                          Integer value)
    {
        return add(map, value, value);
    }

    private TreeMap<Integer, Integer> update(IMap<Integer, Integer> map,
                                             Integer key,
                                             Integer value,
                                             Integer merged)
    {
        TreeMap<Integer, Integer> treeMap = (TreeMap<Integer, Integer>)map;
        treeMap = treeMap.update(key, h -> h.isAbsent() ? value : Integer.valueOf(h.unsafeGet() ^ value));
        treeMap.checkInvariants();
        Maybe<Integer> integers = treeMap.find(key);
        assertEquals(true, integers.isPresent());
        Maybe<Integer> integers1 = treeMap.find(key);
        assertEquals(merged, integers1.unsafeGet());
        return treeMap;
    }

    private TreeMap<Integer, Integer> add(IMap<Integer, Integer> map,
                                          Integer key,
                                          Integer value)
    {
        TreeMap<Integer, Integer> treeMap = (TreeMap<Integer, Integer>)map;
        treeMap = treeMap.assign(key, value);
        treeMap.checkInvariants();
        Maybe<Integer> integers = treeMap.find(key);
        assertEquals(true, integers.isPresent());
        Maybe<Integer> integers1 = treeMap.find(key);
        assertEquals(value, integers1.unsafeGet());
        return treeMap;
    }

    private TreeMap<Integer, Integer> addAll(IMap<Integer, Integer> map,
                                             IMap<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        TreeMap<Integer, Integer> treeMap = (TreeMap<Integer, Integer>)map;
        treeMap.checkInvariants();
        for (IMapEntry<Integer, Integer> entry : extra) {
            Maybe<Integer> integers = treeMap.find(entry.getKey());
            assertEquals(true, integers.isPresent());
            Maybe<Integer> integers1 = treeMap.find(entry.getKey());
            assertEquals(entry.getValue(), integers1.unsafeGet());
        }
        return treeMap;
    }

    private TreeMap<Integer, Integer> addAll(IMap<Integer, Integer> map,
                                             Map<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        TreeMap<Integer, Integer> treeMap = (TreeMap<Integer, Integer>)map;
        treeMap.checkInvariants();
        for (Map.Entry<Integer, Integer> entry : extra.entrySet()) {
            Maybe<Integer> integers = treeMap.find(entry.getKey());
            assertEquals(true, integers.isPresent());
            Maybe<Integer> integers1 = treeMap.find(entry.getKey());
            assertEquals(entry.getValue(), integers1.unsafeGet());
        }
        return treeMap;
    }

    private TreeMap<Integer, Integer> remove(IMap<Integer, Integer> map,
                                             Integer value)
    {
        TreeMap<Integer, Integer> treeMap = (TreeMap<Integer, Integer>)map;
        treeMap = treeMap.delete(value);
        treeMap.checkInvariants();
        Maybe<Integer> integers = treeMap.find(value);
        assertEquals(true, integers.isAbsent());
        return treeMap;
    }
}
