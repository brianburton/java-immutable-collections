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

package org.javimmutable.collections.hash;

import junit.framework.TestCase;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapBuilder;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.MapBuilderTestAdapter;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.StandardMapTests;
import org.javimmutable.collections.common.StandardSerializableTests;
import org.javimmutable.collections.tree.TreeCollisionMap;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.common.StandardMapTests.verifyEnumeration;
import static org.javimmutable.collections.iterators.StandardIteratorTests.*;

public class HashMapTest
    extends TestCase
{
    public void test()
    {
        IMap<Integer, Integer> map = HashMap.usingList();
        Maybe<Integer> integers2 = map.find(10);
        assertEquals(true, integers2.isEmpty());
        assertEquals(0, map.size());
        assertEquals(true, map.isEmpty());
        map = map.assign(10, 20);
        assertEquals(1, map.size());
        assertEquals(false, map.isEmpty());
        Maybe<Integer> integers1 = map.find(10);
        assertEquals(false, integers1.isEmpty());
        Maybe<Integer> integers3 = map.find(10);
        assertEquals(20, (int)integers3.unsafeGet());
        assertEquals(20, (int)map.getValueOr(10, -99));
        assertEquals(-99, (int)map.getValueOr(72, -99));
        map = map.delete(10);
        Maybe<Integer> integers = map.find(10);
        assertEquals(true, integers.isEmpty());
        assertEquals(0, map.size());

        StandardMapTests.verifyMiscellaneous(HashMap.usingList());
        StandardMapTests.verifyMiscellaneous(HashMap.usingTree());
    }

    public void testCollisionMapAssignment()
    {
        assertSame(HashMap.usingList(), HashMap.of(Object.class));
        assertSame(HashMap.usingTree(), HashMap.of(String.class));

        assertSame(HashMap.usingList(), HashMap.forKey(new Object()));
        assertSame(HashMap.usingTree(), HashMap.forKey("able"));

        assertSame(((HashMap)HashMap.usingList()).getCollisionMap(), ((HashMap)HashMap.of().assign(new Object(), new Object())).getCollisionMap());
        assertSame(((HashMap)HashMap.usingTree()).getCollisionMap(), ((HashMap)HashMap.of().assign("able", "baker")).getCollisionMap());
    }

    public void testValueIdentity()
    {
        IMap<Integer, String> map = HashMap.usingList();
        map = map.assign(10, "ab");
        assertSame(map, map.assign(10, "ab"));
        for (int i = 100; i <= 15000; ++i) {
            map = map.assign(i, Integer.toString(i));
        }
        map = map.assign(14000, "aaa");
        assertSame(map, map.assign(14000, "aaa"));
    }

    public void testRandom1()
    {
        Random random = new Random(100L);
        for (int maxKeyLoop = 0; maxKeyLoop < 2; ++maxKeyLoop) {
            final int maxKey = (maxKeyLoop == 0) ? 10000 : 99999999;
            for (int loop = 0; loop < 1000; ++loop) {
                java.util.HashMap<ManualHashKey, Integer> expected = new java.util.HashMap<>();
                IMap<ManualHashKey, Integer> map = (loop % 2 == 0) ? HashMap.usingTree() : HashMap.usingList();
                final int size = 250 + random.nextInt(250);
                for (int i = 1; i <= size; ++i) {
                    int command = random.nextInt(6);
                    switch (command) {
                        case 0: {
                            ManualHashKey key = createManualHashKey(maxKey, random);
                            Integer value = random.nextInt(1000000);
                            int merged = value;
                            map = map.update(key, h -> h.isEmpty() ? value : Integer.valueOf(h.unsafeGet() ^ value));
                            if (expected.get(key) != null) {
                                merged = expected.get(key) ^ value;
                            }
                            expected.put(key, merged);
                            assertEquals(expected.size(), map.size());
                            break;
                        }
                        case 1: {
                            ManualHashKey key = createManualHashKey(maxKey, random);
                            Integer value = random.nextInt(1000000);
                            expected.put(key, value);
                            map = map.assign(key, value);
                            assertEquals(expected.size(), map.size());
                            break;
                        }
                        case 2: {
                            IMap<ManualHashKey, Integer> col = HashMap.usingTree();
                            int times = random.nextInt(3);
                            for (int rep = 0; rep < times; rep++) {
                                ManualHashKey key = createManualHashKey(maxKey, random);
                                Integer value = random.nextInt(1000000);
                                col = col.assign(key, value);
                            }
                            expected.putAll(col.getMap());
                            map = (random.nextBoolean()) ? map.assignAll(col) : map.assignAll(col.getMap());
                            assertEquals(expected.size(), map.size());
                            break;
                        }
                        case 3: {
                            ManualHashKey key = createManualHashKey(maxKey, random);
                            expected.remove(key);
                            map = map.delete(key);
                            assertEquals(expected.size(), map.size());
                            break;
                        }
                        case 4: {
                            ManualHashKey key = createManualHashKey(maxKey, random);
                            Maybe<Integer> integers = map.find(key);
                            assertEquals(expected.get(key), integers.getOrNull());
                            assertEquals(expected.size(), map.size());
                            map.checkInvariants();
                            break;
                        }
                        case 5: {
                            ManualHashKey key = createManualHashKey(maxKey, random);
                            Integer value = random.nextInt(1000000);
                            Integer currentValue = map.get(key);
                            if (currentValue == null) {
                                map = map.update(key, h -> h.isEmpty() ? value : Integer.valueOf(-h.unsafeGet()));
                            } else {
                                map = map.update(key, h -> h.isEmpty() ? -value : value);
                            }
                            expected.put(key, value);
                            assertEquals(expected.size(), map.size());
                            break;
                        }
                    }
                }

                map.checkInvariants();
                verifyEnumeration(expected, map);

                for (Map.Entry<ManualHashKey, Integer> entry : expected.entrySet()) {
                    Maybe<Integer> mapValue = map.find(entry.getKey());
                    assertEquals(true, mapValue.isFull());
                    assertEquals(entry.getValue(), mapValue.unsafeGet());
                }

                // verify the iterator worked properly
                final List<IMapEntry<ManualHashKey, Integer>> entries = new ArrayList<>();
                Map<ManualHashKey, Integer> fromIterator = new java.util.HashMap<>();
                for (IMapEntry<ManualHashKey, Integer> entry : map) {
                    entries.add(entry);
                    fromIterator.put(entry.getKey(), entry.getValue());
                }
                assertEquals(expected, fromIterator);
                listIteratorTest(entries, map.iterator());
                iteratorTest(value -> entries.get(value).getKey(), entries.size(), map.getMap().keySet().iterator());
                iteratorTest(value -> entries.get(value).getValue(), entries.size(), map.getMap().values().iterator());

                // verify the Map adaptor worked properly
                assertEquals(expected, map.getMap());
                assertEquals(expected.keySet(), map.getMap().keySet());
                assertEquals(expected.entrySet(), map.getMap().entrySet());
                ArrayList<Integer> jvalues = new ArrayList<>(expected.values());
                ArrayList<Integer> pvalues = new ArrayList<>(map.getMap().values());
                Collections.sort(jvalues);
                Collections.sort(pvalues);
                assertEquals(jvalues, pvalues);

                // verify the map can remove all keys
                ArrayList<ManualHashKey> keys = new ArrayList<>(expected.keySet());
                Collections.shuffle(keys, random);
                for (ManualHashKey key : keys) {
                    Maybe<Integer> integers1 = map.find(key);
                    assertEquals(false, integers1.isEmpty());
                    map = map.delete(key);
                    Maybe<Integer> integers = map.find(key);
                    assertEquals(true, integers.isEmpty());
                }
                assertEquals(0, map.size());
            }
        }
    }

    @Nonnull
    private ManualHashKey createManualHashKey(int maxKey,
                                              Random random)
    {
        int keyValue = random.nextInt(maxKey);
        return new ManualHashKey(keyValue % 100, String.valueOf(keyValue));
    }

    public void testEquals()
    {
        IMap<Integer, Integer> map1 = HashMap.<Integer, Integer>usingList().assign(1, 3).assign(2, 4).assign(3, 5);
        IMap<Integer, Integer> map2 = HashMap.<Integer, Integer>usingList().assign(1, 3).assign(2, 4).assign(3, 5);
        assertEquals(map1.hashCode(), map2.hashCode());
        assertEquals(map1, map2);
    }

    public void testDeleteAll()
    {
        IMap<Integer, Integer> map1 = HashMap.<Integer, Integer>usingList().assign(1, 3).assign(2, 4).assign(3, 5);
        assertSame(HashMap.of(), map1.deleteAll());
    }

    public void testAssignAll()
    {
        //assignAll(JImmutableMap)
        IMap<String, Number> empty = HashMap.of();
        IMap<String, Number> map = empty;
        IMap<String, Integer> expected = HashMap.usingList();
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());

        expected = expected.assign("a", 10);
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertEquals(1, map.size());
        assertEquals(10, map.get("a"));
        assertSame(TreeCollisionMap.instance(), ((HashMap)map).getCollisionMap());

        assertEquals(map, map.assignAll(empty));

        expected = expected.assign("a", 8).assign("b", 12).assign("c", 14);
        map = map.assignAll(expected);
        assertEquals(expected, map);
        assertEquals(3, map.size());
        assertEquals(8, map.get("a"));
        assertSame(TreeCollisionMap.instance(), ((HashMap)map).getCollisionMap());

        //assignAll(Map)
        map = empty;
        Map<String, Integer> expectedMutable = new java.util.HashMap<>();
        map = map.assignAll(expectedMutable);
        assertEquals(expectedMutable, map.getMap());
        assertEquals(0, map.size());

        expectedMutable.put("a", 10);
        map = map.assignAll(expectedMutable);
        assertEquals(expectedMutable, map.getMap());
        assertEquals(1, map.size());
        assertEquals(10, map.get("a"));
        assertSame(TreeCollisionMap.instance(), ((HashMap)map).getCollisionMap());

        assertEquals(map, map.assignAll(Collections.<String, Integer>emptyMap()));

        expectedMutable.put("a", 8);
        expectedMutable.put("b", 12);
        expectedMutable.put("c", 14);
        map = map.assignAll(expectedMutable);
        assertEquals(expectedMutable, map.getMap());
        assertEquals(3, map.size());
        assertEquals(8, map.get("a"));
        assertSame(TreeCollisionMap.instance(), ((HashMap)map).getCollisionMap());

    }

    public void testEnumeration()
    {
        IMap<Integer, Integer> map = HashMap.usingList();
        java.util.HashMap<Integer, Integer> expected = new java.util.HashMap<>();
        for (int i = 0; i < 100000; ++i) {
            map = map.assign(i, 2 * i);
            expected.put(i, 2 * i);
            assertEquals(expected.size(), map.size());
        }
        verifyEnumeration(expected, map);
    }

    public void testHashCollisions()
    {
        ManualHashKey key1 = new ManualHashKey(1000, "a");
        ManualHashKey key2 = new ManualHashKey(1000, "b");
        ManualHashKey key3 = new ManualHashKey(1000, "c");
        IMap<ManualHashKey, String> map = HashMap.usingList();
        map = map.assign(key1, "1").assign(key2, "2").assign(key3, "3");
        assertEquals(3, map.size());
        assertEquals("1", map.get(key1));
        assertEquals("2", map.get(key2));
        assertEquals("3", map.get(key3));
        assertEquals("1", map.getValueOr(key1, "X"));
        assertEquals("2", map.getValueOr(key2, "X"));
        assertEquals("3", map.getValueOr(key3, "X"));
        Maybe<String> strings11 = map.find(key1);
        assertEquals("1", strings11.get("X"));
        Maybe<String> strings10 = map.find(key2);
        assertEquals("2", strings10.get("X"));
        Maybe<String> strings9 = map.find(key3);
        assertEquals("3", strings9.get("X"));
        map = map.delete(key2);
        assertEquals(2, map.size());
        assertEquals("1", map.get(key1));
        assertEquals(null, map.get(key2));
        assertEquals("3", map.get(key3));
        assertEquals("1", map.getValueOr(key1, "X"));
        assertEquals("X", map.getValueOr(key2, "X"));
        assertEquals("3", map.getValueOr(key3, "X"));
        Maybe<String> strings8 = map.find(key1);
        assertEquals("1", strings8.get("X"));
        Maybe<String> strings7 = map.find(key2);
        assertEquals("X", strings7.get("X"));
        Maybe<String> strings6 = map.find(key3);
        assertEquals("3", strings6.get("X"));
        map = map.delete(key1);
        assertEquals(1, map.size());
        assertEquals(null, map.get(key1));
        assertEquals(null, map.get(key2));
        assertEquals("3", map.get(key3));
        assertEquals("X", map.getValueOr(key1, "X"));
        assertEquals("X", map.getValueOr(key2, "X"));
        assertEquals("3", map.getValueOr(key3, "X"));
        Maybe<String> strings5 = map.find(key1);
        assertEquals("X", strings5.get("X"));
        Maybe<String> strings4 = map.find(key2);
        assertEquals("X", strings4.get("X"));
        Maybe<String> strings3 = map.find(key3);
        assertEquals("3", strings3.get("X"));
        map = map.delete(key3);
        assertEquals(0, map.size());
        assertEquals(null, map.get(key1));
        assertEquals(null, map.get(key2));
        assertEquals(null, map.get(key3));
        assertEquals("X", map.getValueOr(key1, "X"));
        assertEquals("X", map.getValueOr(key2, "X"));
        assertEquals("X", map.getValueOr(key3, "X"));
        Maybe<String> strings2 = map.find(key1);
        assertEquals("X", strings2.get("X"));
        Maybe<String> strings1 = map.find(key2);
        assertEquals("X", strings1.get("X"));
        Maybe<String> strings = map.find(key3);
        assertEquals("X", strings.get("X"));
        assertSame(HashMap.of(), map);
    }

    public void testTransformSelection()
    {
        assertSame(HashMap.LIST_EMPTY, HashMap.forKey(new Object()));
        assertSame(HashMap.TREE_EMPTY, HashMap.forKey(100));
        assertSame(HashMap.TREE_EMPTY, HashMap.forKey("testing"));
    }

    public void testStreams()
    {
        final EmptyHashMap<Integer, Integer> hashMap = HashMap.of();
        assertEquals(asList(), hashMap.stream().collect(Collectors.toList()));
        assertEquals(asList(IMapEntry.of(1, 10)), hashMap.assign(1, 10).stream().collect(Collectors.toList()));
        assertEquals(asList(IMapEntry.of(1, 10), IMapEntry.of(4, 40)), hashMap.assign(1, 10).assign(4, 40).stream().collect(Collectors.toList()));

        assertEquals(asList(), hashMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(1), hashMap.assign(1, 10).keys().stream().collect(Collectors.toList()));
        assertEquals(asList(1, 4), hashMap.assign(1, 10).assign(4, 40).keys().stream().collect(Collectors.toList()));

        assertEquals(asList(), hashMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(10), hashMap.assign(1, 10).values().stream().collect(Collectors.toList()));
        assertEquals(asList(10, 40), hashMap.assign(1, 10).assign(4, 40).values().stream().collect(Collectors.toList()));
    }

    public void testSerialization()
        throws Exception
    {
        final Func1<Object, Iterator> iteratorFactory = a -> ((IMap)a).iterator();
        final IMap<Integer, String> empty = HashMap.of();
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty,
                                                     "H4sIAAAAAAAA/1vzloG1uIjBPL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzSCzO8E0sCCjKr6j8DwL/VIx5GBgqihisSTDFMam4pCgxuQSbSQXlHAwMzC8ZgKACAJwqmKWhAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insert(IMapEntry.of(1, "a")),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBPL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzSCzO8E0sCCjKr6j8DwL/VIx5GBgqihisSTDFMam4pCgxuQSbSQXlHAwMzC8ZGBgYgW4TBJqYqJeTmJeu55lXkpqeWiT0aMGS743tFkwMjJ4MrGWJOaWpQNsFEOr8SnOTUova1kyV5Z7yoJsJZCTIsBIGxsQKAGFh0ZTyAAAA");
        StandardSerializableTests.verifySerializable(iteratorFactory, null, empty.insertAll(asList(IMapEntry.of(Integer.MIN_VALUE, "a"), IMapEntry.of(1, "b"), IMapEntry.of(Integer.MAX_VALUE, "c"))),
                                                     "H4sIAAAAAAAA/1vzloG1uIjBPL8oXS8rsSwzN7e0JDEpJ1UvOT8nJzW5JDM/r1ivOLUoMzEnsyoRxNXzSCzO8E0sCCjKr6j8DwL/VIx5GBgqihisSTDFMam4pCgxuQSbSQXlHAwMzC8ZgATQbYJAExP1chLz0vU880pS01OLhB4tWPK9sd2CiYHRk4G1LDGnNBVouwBCnV9pblJqUduaqbLcUx50M4GMbACaVsLAmFhcyFDHwAzkMAJ5SRBePdBqIC+5AgAc02F6DgEAAA==");
    }

    public void testBuilder()
    {
        final Random r = new Random(1265143000);
        for (int i = 1; i <= 2000; ++i) {
            IMapBuilder<Integer, Integer> builder = HashMap.builder();
            IMap<Integer, Integer> expected = HashMap.of();
            final int size = 1 + r.nextInt(5000);
            for (int k = 1; k <= size; ++k) {
                final Integer key = r.nextInt(5 * size);
                final Integer value = r.nextInt();
                builder.add(key, value);
                expected = expected.assign(key, value);
            }
            IMap<Integer, Integer> actual = builder.build();
            actual.checkInvariants();
            assertEquals(expected, actual);
            assertEquals(actual, actual.parallelStream().collect(HashMap.createMapCollector()));

            assertEquals(expected.deleteAll().assign(1, 2).assign(3, 4), builder.clear().add(1, 2).add(3, 4).build());
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
        StandardBuilderTests.verifyBuilder(values, this::stdBuilderTestAdaptor, this::stdBuilderTestComparator, new IMapEntry[0]);
        values.sort(MapEntry::compareKeys);
        StandardBuilderTests.verifyThreadSafety(values, MapEntry::compareKeys, this::stdBuilderTestAdaptor, a -> a);
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
        final IMap<String, String> empty = HashMap.of();
        empty.forEach(append);
        assertEquals("", sb.toString());

        final IMap<String, String> map = empty.assign("a", "A").assign("c", "C").assign("b", "B");
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
        final IMap<String, String> empty = HashMap.of();
        assertEquals("", empty.reduce("", append));

        final IMap<String, String> map = empty.assign("a", "A").assign("c", "C").assign("b", "B");
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

    private MapBuilderTestAdapter<Integer, Integer> stdBuilderTestAdaptor()
    {
        return new MapBuilderTestAdapter<>(HashMap.builder());
    }

    private Boolean stdBuilderTestComparator(List<IMapEntry<Integer, Integer>> expected,
                                             IMap<Integer, Integer> actual)
    {
        List<IMapEntry<Integer, Integer>> sorted = new ArrayList<>(expected);
        sorted.sort(MapEntry::compareKeys);
        assertEquals(sorted, actual.stream().sorted(MapEntry::compareKeys).collect(Collectors.toList()));
        return true;
    }

    private static class ManualHashKey
        implements Comparable<ManualHashKey>
    {
        private final int hash;
        private final String value;

        private ManualHashKey(int hash,
                              String value)
        {
            this.hash = hash;
            this.value = value;
        }

        @Override
        public int hashCode()
        {
            return hash;
        }

        @Override
        public boolean equals(Object o)
        {
            if (!(o instanceof ManualHashKey)) {
                return false;
            }
            ManualHashKey other = (ManualHashKey)o;
            return (other.hash == hash) && other.value.equals(value);
        }

        @Override
        public int compareTo(@Nonnull ManualHashKey o)
        {
            return value.compareTo(o.value);
        }

        @Override
        public String toString()
        {
            return Integer.toString(hash);
        }
    }
}
