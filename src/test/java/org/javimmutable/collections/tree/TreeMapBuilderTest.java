package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MapBuilderTestAdaptor;
import org.javimmutable.collections.common.StandardBuilderTests;
import org.javimmutable.collections.common.TestUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TreeMapBuilderTest
    extends TestCase
{
    public void testBuilding()
    {
        TreeMapBuilder<Integer, String> builder = new TreeMapBuilder<>(ComparableComparator.<Integer>of());
        JImmutableMap<Integer, String> map = builder.build();
        map.checkInvariants();
        assertEquals(0, map.size());

        final List<Integer> keys = new ArrayList<>();
        final Map<Integer, String> expected = new TreeMap<>();
        for (int i = 1; i <= 512; ++i) {
            expected.put(i, String.valueOf(i));
            keys.add(i);
            Collections.shuffle(keys);
            builder = new TreeMapBuilder<>(ComparableComparator.<Integer>of());
            for (Integer key : keys) {
                builder.add(key, String.valueOf(key));
            }
            map = builder.build();
            map.checkInvariants();
            assertEquals(map, builder.build());
            assertEquals(TestUtil.makeList(expected.keySet()), TestUtil.makeList(map.keys()));
            assertEquals(TestUtil.makeList(expected.values()), TestUtil.makeList(map.values()));
        }
    }

    public void testStandard()
        throws InterruptedException
    {
        final List<Entry<Integer, Integer>> values = new ArrayList<>();
        for (int i = 1; i <= 5000; ++i) {
            values.add(MapEntry.of(i, 5001 - i));
        }
        StandardBuilderTests.verifyBuilder(values, this::adaptor, this::stdComparator, new Entry[0]);
        StandardBuilderTests.verifyThreadSafety(values, this::compare, this::adaptor, a -> a);
    }

    private MapBuilderTestAdaptor<Integer, Integer> adaptor()
    {
        return new MapBuilderTestAdaptor<>(new TreeMapBuilder<>(ComparableComparator.<Integer>of()));
    }

    private Boolean stdComparator(List<Entry<Integer, Integer>> expected,
                                  JImmutableMap<Integer, Integer> actual)
    {
        assertEquals(expected, actual.stream().collect(Collectors.toList()));
        return true;
    }

    private int compare(Entry<Integer, Integer> a,
                        Entry<Integer, Integer> b)
    {
        return Integer.compare(a.getKey(), b.getKey());
    }
}
