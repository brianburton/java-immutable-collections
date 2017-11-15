package org.javimmutable.collections.btree_map;

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

public class JImmutableBtreeMapTest
    extends TestCase
{
    @SuppressWarnings("unchecked")
    public void testInsert()
    {
        JImmutableBtreeMap<Integer, Integer> map = JImmutableBtreeMap.of();
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

        map = JImmutableBtreeMap.of();
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
        JImmutableBtreeMap<String, String> map = JImmutableBtreeMap.of();
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
            JImmutableBtreeMap<Integer, Integer> map = JImmutableBtreeMap.of();
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
            JImmutableMap<Integer, Integer> map = JImmutableBtreeMap.of();
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
                    JImmutableBtreeMap<Integer, Integer> col = JImmutableBtreeMap.of();
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
        JImmutableBtreeMap<Integer, Integer> map = JImmutableBtreeMap.of();
        map = map.assign(1, 2).assign(3, 4);
        JImmutableBtreeMap<Integer, Integer> cleared = map.deleteAll();
        assertSame(JImmutableBtreeMap.<Integer, Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        verifyEmptyEnumeration(cleared);
        emptyCursorTest(cleared.cursor());

        map = JImmutableBtreeMap.of((a, b) -> -b.compareTo(a));
        map = map.assign(1, 2).assign(3, 4);
        cleared = map.deleteAll();
        assertNotSame(JImmutableBtreeMap.<Integer, Integer>of(), cleared);
        assertEquals(0, cleared.size());
        assertSame(map.getComparator(), cleared.getComparator());
        verifyEmptyEnumeration(cleared);
        emptyCursorTest(cleared.cursor());
    }

    public void testAssignAll()
    {
        //assignAll(JImmutableMap)
        JImmutableMap<String, Number> empty = JImmutableBtreeMap.of();
        JImmutableMap<String, Number> map = empty;
        JImmutableMap<String, Integer> expected = JImmutableBtreeMap.of();
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
        final JImmutableMap<Integer, Integer> treeMap = JImmutableBtreeMap.of();
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

    private JImmutableBtreeMap<Integer, Integer> add(JImmutableMap<Integer, Integer> map,
                                                     Integer value)
    {
        return add(map, value, value);
    }

    private JImmutableBtreeMap<Integer, Integer> add(JImmutableMap<Integer, Integer> map,
                                                     Integer key,
                                                     Integer value)
    {
        JImmutableBtreeMap<Integer, Integer> treeMap = (JImmutableBtreeMap<Integer, Integer>)map;
        treeMap = treeMap.assign(key, value);
        treeMap.checkInvariants();
        assertEquals(true, treeMap.find(key).isFilled());
        assertEquals(value, treeMap.find(key).getValue());
        return treeMap;
    }

    private JImmutableBtreeMap<Integer, Integer> addAll(JImmutableMap<Integer, Integer> map,
                                                        JImmutableMap<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        JImmutableBtreeMap<Integer, Integer> treeMap = (JImmutableBtreeMap<Integer, Integer>)map;
        treeMap.checkInvariants();
        for (JImmutableMap.Entry<Integer, Integer> entry : extra) {
            assertEquals(true, treeMap.find(entry.getKey()).isFilled());
            assertEquals(entry.getValue(), treeMap.find(entry.getKey()).getValue());
        }
        return treeMap;
    }

    private JImmutableBtreeMap<Integer, Integer> addAll(JImmutableMap<Integer, Integer> map,
                                                        Map<Integer, Integer> extra)
    {
        map = map.assignAll(extra);
        JImmutableBtreeMap<Integer, Integer> treeMap = (JImmutableBtreeMap<Integer, Integer>)map;
        treeMap.checkInvariants();
        for (Map.Entry<Integer, Integer> entry : extra.entrySet()) {
            assertEquals(true, treeMap.find(entry.getKey()).isFilled());
            assertEquals(entry.getValue(), treeMap.find(entry.getKey()).getValue());
        }
        return treeMap;
    }

    private JImmutableBtreeMap<Integer, Integer> remove(JImmutableMap<Integer, Integer> map,
                                                        Integer value)
    {
        JImmutableBtreeMap<Integer, Integer> treeMap = (JImmutableBtreeMap<Integer, Integer>)map;
        treeMap = treeMap.delete(value);
        treeMap.checkInvariants();
        assertEquals(true, treeMap.find(value).isEmpty());
        return treeMap;
    }
}
