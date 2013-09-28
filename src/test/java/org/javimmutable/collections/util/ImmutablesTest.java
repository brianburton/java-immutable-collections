package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.PersistentList;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.PersistentRandomAccessList;
import org.javimmutable.collections.PersistentStack;
import org.javimmutable.collections.list.PersistentArrayList;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImmutablesTest
        extends TestCase
{
    public void testStack()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);
        List<Integer> expected = Arrays.asList(3, 2, 1);

        PersistentStack<Integer> stack = Immutables.stack();
        stack = stack.add(1).add(2).add(3);
        assertEquals(expected, Immutables.list(stack.cursor()).asList());

        PersistentList<Integer> inlist = PersistentArrayList.of();
        inlist = inlist.add(1).add(2).add(3);
        assertEquals(stack, Immutables.stack((Cursorable<Integer>)inlist));
        assertEquals(stack, Immutables.stack((inlist.cursor())));
        assertEquals(stack, Immutables.stack(input));
        assertEquals(stack, Immutables.stack(input.iterator()));
    }

    public void testList()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);

        PersistentList<Integer> list = Immutables.list(input);
        assertEquals(input, list.asList());
        assertEquals(list, Immutables.list(input.iterator()));
        assertEquals(list, Immutables.list((Cursorable<Integer>)list));
        assertEquals(list, Immutables.list(list.cursor()));
    }

    public void testRandomAccessList()
    {
        List<Integer> input = Arrays.asList(1, 2, 3);

        PersistentRandomAccessList<Integer> list = Immutables.ralist(input);
        assertEquals(input, list.asList());
        assertEquals(list, Immutables.ralist(input.iterator()));
        assertEquals(list, Immutables.ralist((Cursorable<Integer>)list));
        assertEquals(list, Immutables.ralist(list.cursor()));
    }

    public void testMap()
    {
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        PersistentMap<Integer, Integer> map = Immutables.map(input);
        assertEquals(input, map.asMap());
        assertEquals(map, Immutables.map(map));
        assertEquals(map, Immutables.map(map));
    }

    public void testSortedMap()
    {
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        PersistentMap<Integer, Integer> map = Immutables.sortedMap(input);
        assertEquals(input, map.asMap());
        assertEquals(map, Immutables.sortedMap(map));
        assertEquals(map, Immutables.map(map));
    }
}
