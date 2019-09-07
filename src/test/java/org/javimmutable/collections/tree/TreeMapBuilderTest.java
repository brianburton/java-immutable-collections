package org.javimmutable.collections.tree;

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
            assertEquals(list(expected.keySet()), list(map.keys()));
            assertEquals(list(expected.values()), list(map.values()));
        }
    }

    private <T> List<T> list(@Nonnull Iterable<T> src)
    {
        final List<T> dst = new ArrayList<>();
        for (T value : src) {
            dst.add(value);
        }
        return dst;
    }
}
