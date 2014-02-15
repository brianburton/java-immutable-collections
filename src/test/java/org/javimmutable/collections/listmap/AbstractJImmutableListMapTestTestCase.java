package org.javimmutable.collections.listmap;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.list.JImmutableArrayList;

import java.util.Arrays;
import java.util.Collections;

public abstract class AbstractJImmutableListMapTestTestCase
        extends TestCase
{
    public JImmutableListMap<Integer, Integer> verifyOperations(JImmutableListMap<Integer, Integer> map)
    {
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        assertNull(map.get(1));
        assertEquals(0, map.getList(1).size());

        map = map.insert(1, 100);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertSame(map.getList(1), map.get(1));
        assertEquals(1, map.getList(1).size());

        map = map.insert(1, 18);
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        assertEquals(Arrays.asList(100, 18), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(2, map.getList(1).size());

        map = (JImmutableListMap<Integer, Integer>)map.insert(MapEntry.of(3, 87));
        map = (JImmutableListMap<Integer, Integer>)map.insert(MapEntry.of(2, 87));
        map = (JImmutableListMap<Integer, Integer>)map.insert(MapEntry.of(1, 87));
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals(Arrays.asList(100, 18, 87), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(Arrays.asList(87), map.getList(2).getList());
        assertSame(map.getList(2), map.get(2));
        assertEquals(Arrays.asList(87), map.getList(3).getList());
        assertSame(map.getList(3), map.get(3));

        map = map.assign(3, JImmutableArrayList.<Integer>of().insert(300).insert(7).insert(7).insert(14));
        assertFalse(map.isEmpty());
        assertEquals(3, map.size());
        assertEquals(Arrays.asList(100, 18, 87), map.getList(1).getList());
        assertSame(map.getList(1), map.get(1));
        assertEquals(Arrays.asList(87), map.getList(2).getList());
        assertSame(map.getList(2), map.get(2));
        assertEquals(Arrays.asList(300, 7, 7, 14), map.getList(3).getList());
        assertSame(map.getList(3), map.get(3));

        assertTrue(map.find(8).isEmpty());
        assertSame(map.get(3), map.find(3).getValue());
        assertTrue(map.deleteAll().isEmpty());
        assertTrue(map.delete(3).delete(2).delete(1).delete(0).isEmpty());

        StandardCursorTest.listCursorTest(Arrays.asList(100, 18, 87), map.valuesCursor(1));
        StandardCursorTest.listCursorTest(Arrays.asList(87), map.valuesCursor(2));
        StandardCursorTest.listCursorTest(Arrays.asList(300, 7, 7, 14), map.valuesCursor(3));
        StandardCursorTest.listCursorTest(Collections.<Integer>emptyList(), map.valuesCursor(4));
        return map;
    }
}
