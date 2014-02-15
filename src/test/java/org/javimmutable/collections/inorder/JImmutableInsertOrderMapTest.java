package org.javimmutable.collections.inorder;

import junit.framework.TestCase;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JImmutableInsertOrderMapTest
        extends TestCase
{
    public void testEntryCursor()
    {
        List<JImmutableMap.Entry<String, String>> expectedEntries = new ArrayList<JImmutableMap.Entry<String, String>>();
        List<String> expectedKeys = new ArrayList<String>();
        List<String> expectedValues = new ArrayList<String>();
        JImmutableInsertOrderMap<String, String> map = JImmutableInsertOrderMap.of();

        StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
        StandardCursorTest.listCursorTest(expectedKeys, map.keysCursor());
        StandardCursorTest.listCursorTest(expectedValues, map.valuesCursor());

        map = map.assign("x", "X");
        expectedEntries.add(MapEntry.of("x", "X"));
        expectedKeys.add("x");
        expectedValues.add("X");
        StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
        StandardCursorTest.listCursorTest(expectedKeys, map.keysCursor());
        StandardCursorTest.listCursorTest(expectedValues, map.valuesCursor());

        map = map.assign("d", "D");
        expectedEntries.add(MapEntry.of("d", "D"));
        expectedKeys.add("d");
        expectedValues.add("D");
        StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
        StandardCursorTest.listCursorTest(expectedKeys, map.keysCursor());
        StandardCursorTest.listCursorTest(expectedValues, map.valuesCursor());

        map = map.assign("c", "C");
        expectedEntries.add(MapEntry.of("c", "C"));
        expectedKeys.add("c");
        expectedValues.add("C");
        StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
        StandardCursorTest.listCursorTest(expectedKeys, map.keysCursor());
        StandardCursorTest.listCursorTest(expectedValues, map.valuesCursor());

        map = map.delete("d");
        expectedEntries.remove(1);
        expectedKeys.remove(1);
        expectedValues.remove(1);
        StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
        StandardCursorTest.listCursorTest(expectedKeys, map.keysCursor());
        StandardCursorTest.listCursorTest(expectedValues, map.valuesCursor());

        map = (JImmutableInsertOrderMap<String, String>)map.insert(MapEntry.of("d", "D"));
        expectedEntries.add(MapEntry.of("d", "D"));
        expectedKeys.add("d");
        expectedValues.add("D");
        StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
        StandardCursorTest.listCursorTest(expectedKeys, map.keysCursor());
        StandardCursorTest.listCursorTest(expectedValues, map.valuesCursor());

        map = map.assign("x", "XX");
        expectedEntries.set(0, MapEntry.of("x", "XX"));
        expectedValues.set(0, "XX");
        StandardCursorTest.listCursorTest(expectedEntries, map.cursor());
        StandardCursorTest.listCursorTest(expectedKeys, map.keysCursor());
        StandardCursorTest.listCursorTest(expectedValues, map.valuesCursor());
    }

    public void testRandomAdds()
    {
        Random r = new Random(0);
        for (int loop = 1; loop <= 20; ++loop) {
            JImmutableInsertOrderMap<Integer, Integer> map = JImmutableInsertOrderMap.of();
            Map<Integer, Integer> expected = new LinkedHashMap<Integer, Integer>();
            for (int i = 0; i < 2500; ++i) {
                int key = r.nextInt(500);
                int value = r.nextInt(500);
                map = map.assign(key, value);
                expected.put(key, value);
                assertEquals(value, (int)map.get(key));
                assertEquals(Holders.of(value), map.find(key));
                assertEquals(MapEntry.of(key, value), map.findEntry(key).getValue());
            }
            assertEquals(expected, map.getMap());
            List<JImmutableMap.Entry<Integer, Integer>> entries = new ArrayList<JImmutableMap.Entry<Integer, Integer>>();
            List<Integer> keys = new ArrayList<Integer>();
            List<Integer> values = new ArrayList<Integer>();
            for (Map.Entry<Integer, Integer> entry : expected.entrySet()) {
                entries.add(MapEntry.of(entry));
                keys.add(entry.getKey());
                values.add(entry.getValue());
            }
            StandardCursorTest.listIteratorTest(entries, map.iterator());
            StandardCursorTest.listCursorTest(entries, map.cursor());
            StandardCursorTest.listCursorTest(keys, map.keysCursor());
            StandardCursorTest.listCursorTest(values, map.valuesCursor());
            while (keys.size() > 0) {
                assertFalse(map.isEmpty());
                Integer key = keys.remove(0);
                assertNotNull(map.get(key));
                map = map.delete(key);
                assertEquals(keys.size(), map.size());
            }
            assertTrue(map.isEmpty());
        }
    }
}
