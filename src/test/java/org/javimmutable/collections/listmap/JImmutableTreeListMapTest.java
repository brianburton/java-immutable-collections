package org.javimmutable.collections.listmap;

import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.StandardCursorTest;

import java.util.Arrays;
import java.util.Comparator;

public class JImmutableTreeListMapTest
        extends AbstractJImmutableListMapTestTestCase
{
    @SuppressWarnings("unchecked")
    public void testNormalOrder()
    {
        JImmutableListMap<Integer, Integer> map = verifyOperations(JImmutableTreeListMap.<Integer, Integer>of());
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

    @SuppressWarnings("unchecked")
    public void testReverseOrder()
    {
        JImmutableListMap<Integer, Integer> map = verifyOperations(JImmutableTreeListMap.<Integer, Integer>of(new Comparator<Integer>()
        {
            @Override
            public int compare(Integer a,
                               Integer b)
            {
                return b.compareTo(a);
            }
        }));
        StandardCursorTest.listCursorTest(Arrays.asList(3, 2, 1), map.keysCursor());
        StandardCursorTest.listCursorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableList<Integer>>>asList(MapEntry.of(3, map.getList(3)),
                                                                                                               MapEntry.of(2, map.getList(2)),
                                                                                                               MapEntry.of(1, map.getList(1))),
                                          map.cursor());
        StandardCursorTest.listIteratorTest(Arrays.<JImmutableMap.Entry<Integer, JImmutableList<Integer>>>asList(MapEntry.of(3, map.getList(3)),
                                                                                                                 MapEntry.of(2, map.getList(2)),
                                                                                                                 MapEntry.of(1, map.getList(1))),
                                            map.iterator());
    }
}
