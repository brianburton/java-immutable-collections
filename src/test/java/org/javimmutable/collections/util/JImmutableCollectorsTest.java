package org.javimmutable.collections.util;

import junit.framework.TestCase;

import static java.util.Arrays.asList;

@SuppressWarnings("SimplifyStreamApiCallChains")
public class JImmutableCollectorsTest
    extends TestCase
{
    public void test()
    {
        assertEquals(JImmutables.list(1, 2, 3), asList(1, 2, 3).stream().collect(JImmutableCollectors.list()));
        assertEquals(JImmutables.ralist(1, 2, 3), asList(1, 2, 3).stream().collect(JImmutableCollectors.ralist()));
        assertEquals(JImmutables.set(1, 2, 3), asList(1, 2, 3, 3, 1, 2).stream().collect(JImmutableCollectors.set()));
        assertEquals(JImmutables.sortedSet(1, 2, 3), asList(1, 2, 3, 3, 1, 2).stream().collect(JImmutableCollectors.sortedSet()));
        assertEquals(JImmutables.sortedSet(String.CASE_INSENSITIVE_ORDER, "a", "B", "c"), asList("a", "B", "c", "A", "b", "C").stream().collect(JImmutableCollectors.sortedSet(String.CASE_INSENSITIVE_ORDER)));
        assertEquals(JImmutables.listMap().insert(0, 1).insert(0, 5).insert(1, 14).insert(3, 37),
                     asList(1, 14, 37, 5).stream().collect(JImmutableCollectors.groupingBy(x -> x / 10)));
    }
}
