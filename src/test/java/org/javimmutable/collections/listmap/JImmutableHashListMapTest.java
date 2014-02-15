package org.javimmutable.collections.listmap;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Arrays;

public class JImmutableHashListMapTest
        extends AbstractJImmutableListMapTestTestCase
{
    @SuppressWarnings("unchecked")
    public void test()
    {
        JImmutableListMap<Integer, Integer> map = verifyOperations(JImmutableHashListMap.<Integer, Integer>of());
        StandardCursorTest.listCursorTest(Arrays.asList(1, 2, 3), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableList<Integer>>>asList(MapEntry.of(1, map.getList(1)),
                                                                                                               MapEntry.of(2, map.getList(2)),
                                                                                                               MapEntry.of(3, map.getList(3))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableList<Integer>>>asList(MapEntry.of(1, map.getList(1)),
                                                                                                                 MapEntry.of(2, map.getList(2)),
                                                                                                                 MapEntry.of(3, map.getList(3))),
                                            map.iterator());
    }
}
