package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IterableStreamable.Partitions;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.MapEntry;

import static org.javimmutable.collections.util.JImmutables.list;

public class IterableStreamableTest
    extends TestCase
{
    public void testCount()
    {
        assertEquals(0, list().count());
        assertEquals(1, list(1).count());
        assertEquals(5, list(1, 3, 5, 7, 9).count());

        assertEquals(0, list().count(x -> false));
        assertEquals(0, list(1).count(x -> false));
        assertEquals(1, list(1).count(x -> true));
        assertEquals(2, list(1, 3, 5, 7, 9).count(x -> x % 3 == 0));
    }

    public void testAllMatch()
    {
        assertEquals(true, list().allMatch(x -> false));
        assertEquals(true, list().allMatch(x -> true));

        assertEquals(false, list(1).allMatch(x -> false));
        assertEquals(true, list(1).allMatch(x -> true));
    }

    public void testAnyMatch()
    {
        assertEquals(false, list().anyMatch(x -> false));
        assertEquals(false, list().anyMatch(x -> true));

        assertEquals(false, list(1).anyMatch(x -> false));
        assertEquals(true, list(1).anyMatch(x -> true));

        assertEquals(false, list(1, 3, 5).anyMatch(x -> x > 5));
        assertEquals(true, list(1, 3, 5).anyMatch(x -> x < 5));
    }

    public void testFirst()
    {
        assertEquals(Holders.of(), list().first(x -> true));
        assertEquals(Holders.of(), list(1, 3).first(x -> x >= 5));
        assertEquals(Holders.of(3), list(1, 3, 5).first(x -> x >= 3));
    }

    public void testCollectAllMatching()
    {
        assertEquals(list(), list().collectAllMatching(list(), x -> true));
        assertEquals(list(1, 3, 5), list(1, 3, 5).collectAllMatching(list(), x -> true));
        assertEquals(list(1, 5), list(1, 3, 5).collectAllMatching(list(), x -> x != 3));
    }

    public void testCollectAtMostMatching()
    {
        assertEquals(list(), list().collectAtMostMatching(3, list(), x -> true));
        assertEquals(list(), list(1, 3, 5).collectAtMostMatching(0, list(), x -> true));
        assertEquals(list(), list(1, 3, 5).collectAtMostMatching(-10, list(), x -> true));
        assertEquals(list(1), list(1, 3, 5).collectAtMostMatching(1, list(), x -> true));
        assertEquals(list(1, 3), list(1, 3, 5).collectAtMostMatching(2, list(), x -> true));
        assertEquals(list(1, 5), list(1, 3, 5, 7).collectAtMostMatching(2, list(), x -> x != 3));
    }

    public void testCollectAll()
    {
        assertEquals(list(), list().collectAll(list(), x -> x));
        assertEquals(list(-1), list(1).collectAll(list(), x -> -x));
        assertEquals(list(-1, -3, -5), list(1, 3, 5).collectAll(list(), x -> -x));
    }

    public void testCollectAtMost()
    {
        assertEquals(list(), list().collectAtMost(10, list(), x -> x));
        assertEquals(list(-1, -3), list(1, 3, 5).collectAtMost(2, list(), x -> -x));
    }

    public void testCollectSome()
    {
        assertEquals(list(), list().collectSome(list(), x -> Holders.of(x)));
        assertEquals(list(9, -1, -5), list(1, 3, 5).collectSome(list(9), x -> x == 3 ? Holders.of() : Holders.of(-x)));
    }

    public void testCollectAtMostSome()
    {
        assertEquals(list(), list().collectAtMostSome(10, list(), x -> Holders.of(x)));
        assertEquals(list(9, -1, -5), list(1, 3, 5).collectAtMostSome(10, list(9), x -> x == 3 ? Holders.of() : Holders.of(-x)));
        assertEquals(list(9, -1), list(1, 3, 5).collectAtMostSome(1, list(9), x -> x == 3 ? Holders.of() : Holders.of(-x)));
    }

    public void testPartition()
    {
        assertEquals(new Partitions<>(list(), list()), list().partition(list(), list(), x -> true));
        assertEquals(new Partitions<>(list(1, 2, 3, 4, 5), list()), list(1, 2, 3, 4, 5).partition(list(), list(), x -> true));
        assertEquals(new Partitions<>(list(), list(1, 2, 3, 4, 5)), list(1, 2, 3, 4, 5).partition(list(), list(), x -> false));
        assertEquals(new Partitions<>(list(999, 1, 3, 5), list(888, 2, 4)), list(1, 2, 3, 4, 5).partition(list(999), list(888), x -> x % 2 == 1));
        Partitions<JImmutableList<Integer>> p = list(1, 2, 3, 4, 5).partition(list(999), list(888), x -> x % 2 == 1);
        assertEquals(list(999, 1, 3, 5), p.getMatched());
        assertEquals(list(888, 2, 4), p.getUnmatched());
    }

    public void testReduce()
    {
        assertEquals(Holders.of(), list().reduce((s, x) -> s));
        assertEquals(Holders.of(1), list(1).reduce((s, x) -> s + x));
        assertEquals(Holders.of(3), list(1, 2).reduce((s, x) -> s + x));
        assertEquals(Holders.of(6), list(1, 2, 3).reduce((s, x) -> s + x));
    }

    public void testInject()
    {
        final Func2<String, Integer, String> accumulator = (s, x) -> String.valueOf(Integer.parseInt(s) + x);
        assertEquals("0", JImmutables.<Integer>list().inject("0", accumulator));
        assertEquals("1", JImmutables.list(1).inject("0", accumulator));
        assertEquals("9", JImmutables.list(1, 3, 5).inject("0", accumulator));
        assertEquals(Integer.valueOf(-9), JImmutables.list(1, 3, 5).inject(-18, (s, x) -> s + x));
    }

    public void testConversions()
    {
        assertEquals(JImmutables.set(3, 5), list(5, 3, 3, 5).collectAll(JImmutables.<Integer>sortedSet(), x -> x));
        assertEquals(JImmutables.multiset(3, 3, 5, 5), list(5, 3, 3, 5).collectAll(JImmutables.<Integer>sortedMultiset(), x -> x));
        assertEquals(JImmutables.map().assign(3, 6).assign(5, 10), list(5, 3, 3, 5).collectAll(JImmutables.sortedMap(), x -> MapEntry.of(x, 2 * x)));
        assertEquals(JImmutables.listMap().assign(3, list(3, 3)).assign(5, list(5)), list(3, 5, 3).collectAll(JImmutables.sortedListMap(), x -> MapEntry.of(x, x)));
    }
}
