///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.common.StandardJImmutableMapTests.*;
import static org.javimmutable.collections.cursors.StandardCursorTest.emptyCursorTest;

public class JImmutableTreeMapTest
    extends TestCase
{
    @SuppressWarnings("unchecked")
    public void testInsert()
    {
        JImmutableTreeMap<Integer, Integer> map = JImmutableTreeMap.of();
        verifyEmptyEnumeration(map);
        emptyCursorTest(map.cursor());
        emptyCursorTest(map.keysCursor());
        emptyCursorTest(map.valuesCursor());
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

        map = JImmutableTreeMap.of();
        map = map.assign(30, 18).assign(10, 11).assign(20, 19);
        assertEquals(Arrays.asList(10, 20, 30), new ArrayList<>(map.getMap().keySet()));
        assertEquals(Arrays.asList(11, 19, 18), new ArrayList<>(map.getMap().values()));
        final List<JImmutableMap.Entry<Integer, Integer>> expectedEntries = Arrays.asList(MapEntry.of(10, 11), MapEntry.of(20, 19), MapEntry.of(30, 18));
        StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
        StandardCursorTest.listCursorTest(Arrays.asList(10, 20, 30), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.asList(11, 19, 18), map.valuesCursor());
        StandardCursorTest.listIteratorTest(expectedEntries, map.iterator());
        StandardCursorTest.listIteratorTest(Arrays.asList(10, 20, 30), map.getMap().keySet().iterator());
        StandardCursorTest.listIteratorTest(Arrays.asList(11, 19, 18), map.getMap().values().iterator());

        verifyEnumeration(expectedEntries, map);
    }

    public void testValueIdentity()
    {
        JImmutableTreeMap<String, String> map = JImmutableTreeMap.of();
        map = map.assign("a", "A");
        assertSame(map, map.assign("a", "A"));
        assertFalse(map == map.assign("a", "AA"));
    }

    public void testRandom1()
    {
        Random random = new Random();
        for (int loop = 0; loop < 20; ++loop) {
            Set<Integer> expected = new TreeSet<>();
            Map<Integer, Integer> treeMap = new TreeMap<>();
            JImmutableTreeMap<Integer, Integer> map = JImmutableTreeMap.of();
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
            for (JImmutableMap.Entry<Integer, Integer> entry : map) {
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
            Map<Integer, Integer> expected = new TreeMap<>();
            JImmutableMap<Integer, Integer> map = JImmutableTreeMap.of();
            for (int i = 1; i <= 25 * maxKey; ++i) {
                int command = random.nextInt(5);
                switch (command) {
                case 0:
                case 1:
                    Integer key = random.nextInt(maxKey);
                    Integer value = random.nextInt(1000000);
                    expected.put(key, value);
                    map = add(map, key, value);
                    assertEquals(expected.get(key), map.get(key));
                    assertEquals(expected.get(key), map.getValueOr(key, -99));
                    assertEquals(expected.get(key), map.find(key).getValue());
                    break;
                case 2:
                    JImmutableTreeMap<Integer, Integer> col = JImmutableTreeMap.of();
                    int times = random.nextInt(3);

                    for (int rep = 0; rep < times; rep++) {
                        key = random.nextInt(maxKey);
                        value = random.nextInt(1000000);
                        col.assign(key, value);
                    }
                    expected.putAll(col.getMap());
                    map = (random.nextInt(2) == 0) ? addAll(map, col) : addAll(map, col.getMap());
                    break;
                case 3:
                    key = random.nextInt(maxKey);
                    expected.remove(key);
                    map = remove(map, key);
                    assertEquals(null, map.get(key));
                    assertEquals(Integer.valueOf(-99), map.getValueOr(key, -99));
                    assertEquals(true, map.find(key).isEmpty());
                    break;
                case 4:
                    key = random.nextInt(maxKey);
                    if (expected.containsKey(key)) {
                        assertEquals(expected.get(key), map.get(key));
                        assertEquals(expected.get(key), map.getValueOr(key, -99));
                        assertEquals(expected.get(key), map.find(key).getValue());
                    } else {
                        assertEquals(null, map.get(key));
                        assertEquals(Integer.valueOf(-99), map.getValueOr(key, -99));
                        assertEquals(true, map.find(key).isEmpty());
                    }

                }
                assertEquals(expected.size(), map.size());
            }
            assertEquals(expected, map.getMap());
            assertEquals(expected.keySet(), map.getMap().keySet());
            assertEquals(new ArrayList<>(expected.values()), new ArrayList<>(map.getMap().values()));
            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                Holder<Integer> value = map.find(entry.getKey());
                assertEquals(entry.getValue(), value.getValue());
            }
            verifyEnumeration(expected, map);
        }
    }

    public void testDeleteAll()
    {
        JImmutableTreeMap<Integer, Integer> map = JImmutableTreeMap.of();
        map = map.assign(1, 2).assign(3, 4);
        JImmutableTreeMap<Integer, Integer> cleared = map.deleteAll();
        assertSame(JImmutableTreeMap.<Integer, Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        verifyEmptyEnumeration(cleared);
        emptyCursorTest(cleared.cursor());

        map = JImmutableTreeMap.of((a, b) -> -b.compareTo(a));
        map = map.assign(1, 2).assign(3, 4);
        cleared = map.deleteAll();
        assertNotSame(JImmutableTreeMap.<Integer, Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        verifyEmptyEnumeration(cleared);
        emptyCursorTest(cleared.cursor());
    }

    public void testAssignAll()
    {
        //assignAll(JImmutableMap)
        JImmutableMap<String, Number> empty = JImmutableTreeMap.of();
        JImmutableMap<String, Number> map = empty;
        JImmutableMap<String, Integer> expected = JImmutableTreeMap.of();
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
        Map<String, Integer> expectedMutable = new TreeMap<>();
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
        final JImmutableMap<Integer, Integer> treeMap = JImmutableTreeMap.of();
        assertEquals(asList(), treeMap.stream().collect(Collectors.toList()));
        assertEquals(asList(MapEntry.of(1, 10)), treeMap.assign(1, 10).stream().collect(Collectors.toList()));
        assertEquals(asList(MapEntry.of(1, 10), MapEntry.of(4, 40)), treeMap.assign(4, 40).assign(1, 10).stream().collect(Collectors.toList()));

        assertEquals(asList(), treeMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(1), treeMap.assign(1, 10).keys().stream().collect(Collectors.toList()));
        assertEquals(asList(1, 4), treeMap.assign(4, 40).assign(1, 10).keys().stream().collect(Collectors.toList()));

        assertEquals(asList(), treeMap.keys().stream().collect(Collectors.toList()));
        assertEquals(asList(10), treeMap.assign(1, 10).values().stream().collect(Collectors.toList()));
        assertEquals(asList(10, 40), treeMap.assign(4, 40).assign(1, 10).values().stream().collect(Collectors.toList()));
    }

    private JImmutableTreeMap<Integer, Integer> add(JImmutableMap<Integer, Integer> map,
                                                    Integer value)
    {
        return add(map, value, value);
    }

    private JImmutableTreeMap<Integer, Integer> add(JImmutableMap<Integer, Integer> map,
                                                    Integer key,
                                                    Integer value)
    {
        JImmutableTreeMap<Integer, Integer> treeMap = (JImmutableTreeMap<Integer, Integer>)map;
        treeMap = treeMap.assign(key, value);
        treeMap.checkInvariants();
        assertEquals(true, treeMap.find(key).isFilled());
        assertEquals(value, treeMap.find(key).getValue());
        return treeMap;
    }

    private JImmutableTreeMap<Integer, Integer> addAll(JImmutableMap<Integer, Integer> map,
                                                       JImmutableMap<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        JImmutableTreeMap<Integer, Integer> treeMap = (JImmutableTreeMap<Integer, Integer>)map;
        treeMap.checkInvariants();
        for (JImmutableMap.Entry<Integer, Integer> entry : extra) {
            assertEquals(true, treeMap.find(entry.getKey()).isFilled());
            assertEquals(entry.getValue(), treeMap.find(entry.getKey()).getValue());
        }
        return treeMap;
    }

    private JImmutableTreeMap<Integer, Integer> addAll(JImmutableMap<Integer, Integer> map,
                                                       Map<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        JImmutableTreeMap<Integer, Integer> treeMap = (JImmutableTreeMap<Integer, Integer>)map;
        treeMap.checkInvariants();
        for (Map.Entry<Integer, Integer> entry : extra.entrySet()) {
            assertEquals(true, treeMap.find(entry.getKey()).isFilled());
            assertEquals(entry.getValue(), treeMap.find(entry.getKey()).getValue());
        }
        return treeMap;
    }

    private JImmutableTreeMap<Integer, Integer> remove(JImmutableMap<Integer, Integer> map,
                                                       Integer value)
    {
        JImmutableTreeMap<Integer, Integer> treeMap = (JImmutableTreeMap<Integer, Integer>)map;
        treeMap = treeMap.delete(value);
        treeMap.checkInvariants();
        assertEquals(true, treeMap.find(value).isEmpty());
        return treeMap;
    }
}
