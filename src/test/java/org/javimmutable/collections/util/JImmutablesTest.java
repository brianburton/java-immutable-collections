///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.util;

import junit.framework.TestCase;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.InsertableSequence;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.hash.EmptyHashMap;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.hash.JImmutableHashMultiset;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMap;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMultiset;
import org.javimmutable.collections.inorder.JImmutableInsertOrderSet;
import org.javimmutable.collections.iterators.IndexedIterator;
import org.javimmutable.collections.iterators.SequenceIterator;
import org.javimmutable.collections.list.JImmutableTreeList;
import org.javimmutable.collections.listmap.JImmutableHashListMap;
import org.javimmutable.collections.listmap.JImmutableInsertOrderListMap;
import org.javimmutable.collections.listmap.JImmutableTreeListMap;
import org.javimmutable.collections.sequence.EmptySequenceNode;
import org.javimmutable.collections.sequence.FilledSequenceNode;
import org.javimmutable.collections.setmap.JImmutableHashSetMap;
import org.javimmutable.collections.setmap.JImmutableInsertOrderSetMap;
import org.javimmutable.collections.setmap.JImmutableTemplateSetMap;
import org.javimmutable.collections.setmap.JImmutableTreeSetMap;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.JImmutableTreeMap;
import org.javimmutable.collections.tree.JImmutableTreeMultiset;
import org.javimmutable.collections.tree.JImmutableTreeSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.javimmutable.collections.util.JImmutables.*;

@SuppressWarnings("unchecked")
public class JImmutablesTest
    extends TestCase
{
    private final Predicate<JImmutableArray> isArray = x -> x instanceof JImmutableArray;
    private final Predicate<JImmutableStack> isStack = x -> x instanceof JImmutableStack;
    private final Predicate<JImmutableList> isList = x -> x instanceof JImmutableTreeList;
    private final Predicate<JImmutableList> isRalist = x -> x instanceof JImmutableTreeList;
    private final Predicate<JImmutableMap> isEmptyMap = x -> x instanceof EmptyHashMap;
    private final Predicate<JImmutableMap> isMap = x -> x instanceof JImmutableHashMap;
    private final Predicate<JImmutableMap> isSortedMap = x -> x instanceof JImmutableTreeMap;
    private final Predicate<JImmutableMap> isInsertOrderMap = x -> x instanceof JImmutableInsertOrderMap;
    private final Predicate<JImmutableSet> isSet = x -> x instanceof JImmutableHashSet;
    private final Predicate<JImmutableSet> isSortedSet = x -> x instanceof JImmutableTreeSet;
    private final Predicate<JImmutableSet> isInsertOrderSet = x -> x instanceof JImmutableInsertOrderSet;
    private final Predicate<JImmutableMultiset> isMultiset = x -> x instanceof JImmutableHashMultiset;
    private final Predicate<JImmutableMultiset> isSortedMultiset = x -> x instanceof JImmutableTreeMultiset;
    private final Predicate<JImmutableMultiset> isInsertOrderMultiset = x -> x instanceof JImmutableInsertOrderMultiset;
    private final Predicate<JImmutableListMap> isListMap = x -> x instanceof JImmutableHashListMap;
    private final Predicate<JImmutableListMap> isSortedListMap = x -> x instanceof JImmutableTreeListMap;
    private final Predicate<JImmutableListMap> isInsertOrderListMap = x -> x instanceof JImmutableInsertOrderListMap;
    private final Predicate<JImmutableSetMap> isSetMap = x -> x instanceof JImmutableHashSetMap;
    private final Predicate<JImmutableSetMap> isSortedSetMap = x -> x instanceof JImmutableTreeSetMap;
    private final Predicate<JImmutableSetMap> isInsertOrderSetMap = x -> x instanceof JImmutableInsertOrderSetMap;
    private final Predicate<JImmutableSetMap> isTemplateSetMap = x -> x instanceof JImmutableTemplateSetMap;
    private final Predicate<InsertableSequence> isEmptyInsertableSequence = x -> x instanceof EmptySequenceNode;
    private final Predicate<InsertableSequence> isInsertableSequence = x -> x instanceof FilledSequenceNode;

    public void testArray()
    {
        verifyOrdered(isArray,
                      Arrays.asList(),
                      () -> array());
        verifyOrdered(isArray,
                      asList(entry(-20, "a"), entry(-7, "c"), entry(17, "d"), entry(100, "a")),
                      () -> array(cursor(entry(-7, "c"), entry(-20, "a"), entry(17, "d"), entry(100, "a"))));
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> array("c", "a", "d", "a"));
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> array(indexed("c", "a", "d", "a")));
        verifyOrdered(isArray,
                      asList(entry(0, "d")),
                      () -> array(indexed("c", "a", "d", "a"), 2, 3));
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> array(asList("c", "a", "d", "a")));
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> arrayBuilder().add(asList("c", "a", "d", "a")).build());
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> Stream.of("c", "a", "d", "a").collect(arrayCollector()));
    }

    public void testStack()
    {
        List<Integer> input = asList(1, 2, 3);
        List<Integer> expected = asList(3, 2, 1);

        JImmutableStack<Integer> stack = JImmutables.stack();
        stack = stack.insert(1).insert(2).insert(3);
        assertEquals(expected, list(stack.cursor()).getList());

        JImmutableList<Integer> inlist = JImmutableTreeList.of();
        inlist = inlist.insert(1).insert(2).insert(3);
        assertEquals(stack, JImmutables.stack(inlist));
        assertEquals(stack, JImmutables.stack((inlist.cursor())));
        assertEquals(stack, JImmutables.stack(input));
        assertEquals(stack, JImmutables.stack(input.iterator()));
        assertEquals(stack, JImmutables.stack(1, 2, 3));

        verifyOrdered(isStack, asList(), () -> JImmutables.stack());
        verifyOrdered(isStack, asList("z", "y", "x", "w"), () -> JImmutables.stack("w", "x", "y", "z"));
        verifyOrdered(isStack, asList("z", "y", "x", "w"), () -> JImmutables.stack(cursor("w", "x", "y", "z")));
        verifyOrdered(isStack, asList("z", "y", "x", "w"), () -> JImmutables.stack(iterable("w", "x", "y", "z")));
        verifyOrdered(isStack, asList("z", "y", "x", "w"), () -> JImmutables.stack(iterator("w", "x", "y", "z")));
    }

    public void testList()
    {
        List<Integer> input = asList(1, 2, 3);

        JImmutableList<Integer> list = list(input);
        assertEquals(input, list.getList());
        assertEquals(list, list(input.iterator()));
        assertEquals(list, list(list));
        assertEquals(list, list(list.cursor()));
        assertEquals(list, list(1, 2, 3));
        assertEquals(list, JImmutables.<Integer>listBuilder().add(input).build());

        verifyOrdered(isList, asList(), () -> list());
        verifyOrdered(isList, asList("a", "b", "c"), () -> list("a", "b", "c"));
        verifyOrdered(isList, asList("a", "b", "c"), () -> list(cursor("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c", "d", "e"), () -> list(indexed("a", "b", "c", "d", "e")));
        verifyOrdered(isList, asList("b", "c", "d"), () -> list(indexed("a", "b", "c", "d", "e"), 1, 4));
        verifyOrdered(isList, asList("a", "b", "c"), () -> list(insertOrderSet("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c"), () -> list(iterator("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c"), () -> list(list("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c"), () -> list(asList("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c"), () -> listBuilder().add("a", "b", "c").build());
        verifyOrdered(isList, asList("a", "b", "c"), () -> Stream.of("a", "b", "c").collect(listCollector()));
    }

    public void testLargeList()
    {
        List<Integer> input = new ArrayList<Integer>();
        for (int i = 0; i < 2000; ++i) {
            input.add(i);
        }
        JImmutableList<Integer> list = list(input);
        assertEquals(input, list.getList());
    }

    public void testRandomAccessList()
    {
        List<Integer> input = asList(1, 2, 3);

        JImmutableRandomAccessList<Integer> list = ralist(input);
        assertEquals(input, list.getList());
        assertEquals(list, ralist(input.iterator()));
        assertEquals(list, ralist(list));
        assertEquals(list, ralist(list.cursor()));
        assertEquals(list, ralist(1, 2, 3));
        assertEquals(list, JImmutables.<Integer>ralistBuilder().add(input).build());

        verifyOrdered(isRalist, asList(), () -> ralist());
        verifyOrdered(isRalist, asList("a", "b", "c"), () -> ralist("a", "b", "c"));
        verifyOrdered(isRalist, asList("a", "b", "c"), () -> ralist(cursor("a", "b", "c")));
        verifyOrdered(isRalist, asList("a", "b", "c", "d", "e"), () -> ralist(iterable("a", "b", "c", "d", "e")));
        verifyOrdered(isRalist, asList("a", "b", "c"), () -> ralist(iterator("a", "b", "c")));
        verifyOrdered(isRalist, asList("a", "b", "c"), () -> ralist(asList("a", "b", "c")));
        verifyOrdered(isRalist, asList("a", "b", "c"), () -> ralistBuilder().add("a", "b", "c").build());
        verifyOrdered(isRalist, asList("a", "b", "c"), () -> Stream.of("a", "b", "c").collect(ralistCollector()));
    }

    @SuppressWarnings("AssertEqualsBetweenInconvertibleTypes")
    public void testLargeRandomAccessList()
    {
        List<Integer> input = new ArrayList<Integer>();
        for (int i = 0; i < 2000; ++i) {
            input.add(i);
        }
        JImmutableRandomAccessList<Integer> list = ralist(input);
        assertEquals(input, list.getList());
        assertEquals(list, ralist(input.iterator()));
        assertEquals(list, ralist(list));
        assertEquals(list, ralist(list.cursor()));
        assertEquals(list, ralist(input.toArray()));
        assertEquals(list, JImmutables.<Integer>ralistBuilder().add(input).build());
    }

    public void testMap()
    {
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        JImmutableMap<Integer, Integer> map = JImmutables.map(input);
        assertEquals(input, map.getMap());
        assertSame(map, JImmutables.map(map));
        assertEquals(map, JImmutables.map(map));
        assertNotSame(map, JImmutables.sortedMap(input));
        assertEquals(map, JImmutables.sortedMap(input));

        verifyUnordered(isEmptyMap, asList(), () -> JImmutables.map());
        final List<Entry<String, String>> entries = asList(entry("z", "1"), entry("x", "2"), entry("w", "3"), entry("y", "4"));
        verifyUnordered(isMap, entries, () -> JImmutables.map(map(entries)));
    }

    public void testSortedMap()
    {
        Map<Integer, Integer> input = new HashMap<Integer, Integer>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        JImmutableMap<Integer, Integer> map = JImmutables.sortedMap(input);
        assertEquals(input, map.getMap());
        assertSame(map, JImmutables.sortedMap(map));
        assertEquals(map, JImmutables.map(map));

        final Comparator<Integer> comparator = new Comparator<Integer>()
        {
            @Override
            public int compare(Integer a,
                               Integer b)
            {
                return -b.compareTo(a);
            }
        };
        JImmutableMap<Integer, Integer> map2 = JImmutables.sortedMap(comparator, input);
        assertEquals(input, map2.getMap());
        assertEquals(map, JImmutables.sortedMap(map2));
        assertNotSame(map, JImmutables.sortedMap(map2));
        assertSame(map2, JImmutables.sortedMap(comparator, map2));
        assertEquals(map, JImmutables.map(map2));
        assertNotSame(map, JImmutables.map(map2));

        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        final List<Entry<String, String>> entries = asList(entry("z", "1"), entry("x", "2"), entry("w", "3"), entry("y", "4"));
        final List<Entry<String, String>> sorted = asList(entry("w", "3"), entry("x", "2"), entry("y", "4"), entry("z", "1"));
        final List<Entry<String, String>> reversed = asList(entry("z", "1"), entry("y", "4"), entry("x", "2"), entry("w", "3"));

        verifyOrdered(isSortedMap, asList(), () -> JImmutables.sortedMap());
        verifyOrdered(isSortedMap, sorted, () -> JImmutables.sortedMap(map(entries)));

        verifyOrdered(isSortedMap, asList(), () -> JImmutables.sortedMap());
        verifyOrdered(isSortedMap, reversed, () -> JImmutables.sortedMap(reverse, map(entries)));
    }

    public void testInsertOrderMap()
    {
        final List<Entry<String, String>> entries = asList(entry("z", "1"), entry("x", "2"), entry("w", "3"), entry("y", "4"));
        final List<Entry<String, String>> sorted = asList(entry("w", "3"), entry("x", "2"), entry("y", "4"), entry("z", "1"));
        final List<Entry<String, String>> reversed = asList(entry("z", "1"), entry("y", "4"), entry("x", "2"), entry("w", "3"));

        verifyOrdered(isInsertOrderMap, asList(), () -> JImmutables.insertOrderMap());
        verifyOrdered(isInsertOrderMap, entries, () -> JImmutables.insertOrderMap(map(entries)));
        verifyOrdered(isInsertOrderMap, sorted, () -> JImmutables.insertOrderMap(JImmutables.sortedMap(map(reversed))));

        JImmutableMap<String, String> iomap = JImmutables.insertOrderMap(map(entries));
        assertSame(iomap, JImmutables.insertOrderMap(iomap));
    }

    public void testSet()
    {
        List<Integer> input = asList(1, 87, 100, 1, 45);

        JImmutableSet<Integer> set = set(input);
        assertEquals(new HashSet<Integer>(input), set.getSet());
        assertEquals(set, set(input.iterator()));
        assertEquals(set, set(set));
        assertEquals(set, set(set.cursor()));
        assertEquals(set, set(1, 100, 45, 87, 1));

        verifyUnordered(isSet, asList(), () -> set());
        verifyUnordered(isSet, asList("a", "b", "c"), () -> set(cursor("a", "b", "c")));
        verifyUnordered(isSet, asList("a", "b", "c"), () -> set("a", "b", "c"));
        verifyUnordered(isSet, asList("a", "b", "c", "d", "e"), () -> set(iterable("a", "b", "c", "d", "e")));
        verifyUnordered(isSet, asList("a", "b", "c"), () -> set(iterator("a", "b", "c")));
        verifyUnordered(isSet, asList("a", "b", "c"), () -> Stream.of("a", "b", "c").collect(setCollector()));
    }

    public void testSortedSet()
    {
        List<Integer> input = asList(1, 87, 100, 1, 45);

        JImmutableSet<Integer> set = JImmutables.sortedSet(input);
        assertEquals(new HashSet<Integer>(input), set.getSet());
        assertEquals(set, JImmutables.sortedSet(input.iterator()));
        assertEquals(set, JImmutables.sortedSet(set));
        assertEquals(set, JImmutables.sortedSet(set.cursor()));
        assertEquals(set, JImmutables.sortedSet(1, 100, 45, 87, 1));
        Cursors.areEqual(set.cursor(), list(asList(1, 45, 87, 100).iterator()).cursor());

        Comparator<Integer> reverser = (a, b) -> -a.compareTo(b);
        set = JImmutables.sortedSet(reverser, input);
        assertEquals(new HashSet<Integer>(input), set.getSet());
        assertEquals(set, JImmutables.sortedSet(reverser, input.iterator()));
        assertEquals(set, JImmutables.sortedSet(reverser, set));
        assertEquals(set, JImmutables.sortedSet(reverser, set.cursor()));
        assertEquals(set, JImmutables.sortedSet(reverser, 1, 100, 45, 87, 1));
        Cursors.areEqual(set.cursor(), list(asList(100, 87, 45, 1).iterator()).cursor());

        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        final List<String> sorted = asList("w", "x", "y", "z");
        final List<String> reversed = asList("z", "y", "x", "w");

        verifyOrdered(isSortedSet, asList(), () -> JImmutables.<String>sortedSet());
        verifyOrdered(isSortedSet, sorted, () -> JImmutables.sortedSet("z", "w", "y", "x"));
        verifyOrdered(isSortedSet, sorted, () -> JImmutables.sortedSet(cursor("z", "w", "y", "x")));
        verifyOrdered(isSortedSet, sorted, () -> JImmutables.sortedSet(iterable("z", "w", "y", "x")));
        verifyOrdered(isSortedSet, sorted, () -> JImmutables.sortedSet(iterator("z", "w", "y", "x")));

        verifyOrdered(isSortedSet, asList(), () -> JImmutables.sortedSet(reverse));
        verifyOrdered(isSortedSet, reversed, () -> JImmutables.sortedSet(reverse, "x", "w", "z", "y"));
        verifyOrdered(isSortedSet, reversed, () -> JImmutables.sortedSet(reverse, cursor("x", "w", "z", "y")));
        verifyOrdered(isSortedSet, reversed, () -> JImmutables.sortedSet(reverse, iterable("x", "w", "z", "y")));
        verifyOrdered(isSortedSet, reversed, () -> JImmutables.sortedSet(reverse, iterator("x", "w", "z", "y")));
        verifyOrdered(isSortedSet, sorted, () -> Stream.of("x", "w", "z", "y").collect(sortedSetCollector()));
        verifyOrdered(isSortedSet, reversed, () -> Stream.of("x", "w", "z", "y").collect(sortedSetCollector(reverse)));
    }

    public void testInsertOrderSet()
    {
        final List<String> entries = asList("x", "w", "z", "y");

        verifyOrdered(isInsertOrderSet, asList(), () -> JImmutables.<String>insertOrderSet());
        verifyOrdered(isInsertOrderSet, entries, () -> insertOrderSet("x", "w", "z", "y"));
        verifyOrdered(isInsertOrderSet, entries, () -> insertOrderSet(cursor("x", "w", "z", "y")));
        verifyOrdered(isInsertOrderSet, entries, () -> insertOrderSet(iterable("x", "w", "z", "y")));
        verifyOrdered(isInsertOrderSet, entries, () -> insertOrderSet(iterator("x", "w", "z", "y")));
        verifyOrdered(isInsertOrderSet, entries, () -> Stream.of("x", "w", "z", "y").collect(insertOrderSetCollector()));
    }

    public void testMultiset()
    {
        final Func1<JImmutableMultiset, Iterator<Entry<String, Integer>>> occurrences = x -> x.entries().iterator();
        verifyUnordered(isMultiset, entryList(), () -> JImmutables.<String>multiset(), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> JImmutables.multiset("y", "x", "y", "x", "y"), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> JImmutables.multiset(cursor("y", "x", "y", "x", "y")), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> JImmutables.multiset(iterable("y", "x", "y", "x", "y")), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> JImmutables.multiset(iterator("y", "x", "y", "x", "y")), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> Stream.of("y", "x", "y", "x", "y").collect(multisetCollector()), occurrences);
    }

    public void testSortedMultiset()
    {
        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        final List<Entry<String, Integer>> sorted = asList(entry("x", 2), entry("y", 3));
        final List<Entry<String, Integer>> reversed = asList(entry("y", 3), entry("x", 2));
        final Func1<JImmutableMultiset, Iterator<Entry<String, Integer>>> occurrences = x -> x.entries().iterator();

        verifyOrdered(isSortedMultiset, entryList(), () -> JImmutables.<String>sortedMultiset(), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> JImmutables.sortedMultiset("y", "x", "y", "x", "y"), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> JImmutables.sortedMultiset(cursor("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> JImmutables.sortedMultiset(iterable("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> JImmutables.sortedMultiset(iterator("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> Stream.of("y", "x", "y", "x", "y").collect(sortedMultisetCollector()), occurrences);

        verifyOrdered(isSortedMultiset, entryList(), () -> JImmutables.sortedMultiset(reverse), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> JImmutables.sortedMultiset(reverse, "y", "x", "y", "x", "y"), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> JImmutables.sortedMultiset(reverse, cursor("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> JImmutables.sortedMultiset(reverse, iterable("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> JImmutables.sortedMultiset(reverse, iterator("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> Stream.of("y", "x", "y", "x", "y").collect(sortedMultisetCollector(reverse)), occurrences);
    }

    public void testInsertOrderMultiset()
    {
        final List<Entry<String, Integer>> entered = asList(entry("y", 3), entry("x", 2));
        final Func1<JImmutableMultiset, Iterator<Entry<String, Integer>>> occurrences = x -> x.entries().iterator();

        verifyOrdered(isInsertOrderMultiset, entryList(), () -> JImmutables.<String>insertOrderMultiset(), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> JImmutables.insertOrderMultiset("y", "x", "y", "x", "y"), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> JImmutables.insertOrderMultiset(cursor("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> JImmutables.insertOrderMultiset(iterable("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> JImmutables.insertOrderMultiset(iterator("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> Stream.of("y", "x", "y", "x", "y").collect(insertOrderMultisetCollector()), occurrences);
    }

    public void testListMap()
    {
        verifyUnordered(isListMap, entryList(entry("y", list(1)), entry("z", list(2)), entry("x", list(3))), () -> JImmutables.<String, Integer>listMap().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testSortedListMap()
    {
        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        verifyOrdered(isSortedListMap, entryList(entry("x", list(3)), entry("y", list(1)), entry("z", list(2))), () -> JImmutables.<String, Integer>sortedListMap().insert("y", 1).insert("z", 2).insert("x", 3));
        verifyOrdered(isSortedListMap, entryList(entry("z", list(2)), entry("y", list(1)), entry("x", list(3))), () -> JImmutables.<String, Integer>sortedListMap(reverse).insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testInsertOrderListMap()
    {
        verifyOrdered(isInsertOrderListMap, entryList(entry("y", list(1)), entry("z", list(2)), entry("x", list(3))), () -> JImmutables.<String, Integer>insertOrderListMap().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testSetMap()
    {
        verifyUnordered(isSetMap, entryList(entry("y", set(1)), entry("z", set(2)), entry("x", set(3))), () -> JImmutables.<String, Integer>setMap().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testSortedSetMap()
    {
        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        verifyOrdered(isSortedSetMap, entryList(entry("x", set(3)), entry("y", set(1)), entry("z", set(2))), () -> JImmutables.<String, Integer>sortedSetMap().insert("y", 1).insert("z", 2).insert("x", 3));
        verifyOrdered(isSortedSetMap, entryList(entry("z", set(2)), entry("y", set(1)), entry("x", set(3))), () -> JImmutables.<String, Integer>sortedSetMap(reverse).insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testInsertOrderSetMap()
    {
        verifyOrdered(isInsertOrderSetMap, entryList(entry("y", set(1)), entry("z", set(2)), entry("x", set(3))), () -> JImmutables.<String, Integer>insertOrderSetMap().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testTemplateSetMap()
    {
        final JImmutableSetMap<String, Integer> setmap = JImmutables.setMap(JImmutables.map(), JImmutables.set());
        verifyUnordered(isTemplateSetMap, entryList(entry("y", set(1)), entry("z", set(2)), entry("x", set(3))), () -> setmap.insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testTemplateSortedSetMap()
    {
        final Comparator<String> reversedComparator = ComparableComparator.<String>of().reversed();
        final JImmutableSetMap<String, Integer> forward = JImmutables.setMap(JImmutables.<String, JImmutableSet<Integer>>sortedMap(), JImmutables.set());
        final JImmutableSetMap<String, Integer> reverse = JImmutables.setMap(JImmutables.sortedMap(reversedComparator), JImmutables.set());
        verifyOrdered(isTemplateSetMap, entryList(entry("x", set(3)), entry("y", set(1)), entry("z", set(2))), () -> forward.insert("y", 1).insert("z", 2).insert("x", 3));
        verifyOrdered(isTemplateSetMap, entryList(entry("z", set(2)), entry("y", set(1)), entry("x", set(3))), () -> reverse.insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testTemplateInsertOrderSetMap()
    {
        final JImmutableSetMap<String, Integer> setmap = JImmutables.setMap(JImmutables.insertOrderMap(), JImmutables.set());
        verifyOrdered(isTemplateSetMap, entryList(entry("y", set(1)), entry("z", set(2)), entry("x", set(3))), () -> setmap.insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testSequence()
    {
        verifyOrdered(isEmptyInsertableSequence, asList(), () -> JImmutables.<String>sequence(), x -> SequenceIterator.iterator(x));
        verifyOrdered(isInsertableSequence, asList("x"), () -> JImmutables.<String>sequence("x"), x -> SequenceIterator.iterator(x));
    }

    private <K, V> List<Entry<K, V>> entryList(Entry<K, V>... values)
    {
        return asList(values);
    }

    private void assertEquals(int expected,
                              Integer actual)
    {
        assertEquals(expected, (int)actual);
    }

    public void testListTutorialCode()
    {
        JImmutableList<Integer> list = list();
        list = list.insert(10).insert(20).insert(30);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));

        JImmutableList<Integer> changed = list.deleteLast().insert(45);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
        assertEquals(10, changed.get(0));
        assertEquals(20, changed.get(1));
        assertEquals(45, changed.get(2));

        assertEquals(asList(10, 20, 30), list.getList());
        assertEquals(asList(10, 20, 45), changed.getList());

        JImmutableRandomAccessList<Integer> ralist = ralist();
        ralist = ralist.insert(30).insert(0, 20).insert(0, 10);
        assertEquals(10, ralist.get(0));
        assertEquals(20, ralist.get(1));
        assertEquals(30, ralist.get(2));
        JImmutableRandomAccessList<Integer> ralist2 = ralist;
        ralist2 = ralist2.delete(1).insert(1, 87);
        assertEquals(10, ralist.get(0));
        assertEquals(20, ralist.get(1));
        assertEquals(30, ralist.get(2));
        assertEquals(10, ralist2.get(0));
        assertEquals(87, ralist2.get(1));
        assertEquals(30, ralist2.get(2));
        assertEquals(asList(10, 20, 30), ralist.getList());
        assertEquals(asList(10, 87, 30), ralist2.getList());
    }

    public void testMapTutorialCode()
    {
        JImmutableMap<Integer, Integer> hmap = JImmutables.map();
        hmap = hmap.assign(10, 11).assign(20, 21).assign(30, 31).assign(20, 19);

        JImmutableMap<Integer, Integer> hmap2 = hmap.delete(20).assign(18, 19);

        assertEquals(11, hmap.get(10));
        assertEquals(19, hmap.get(20));
        assertEquals(31, hmap.get(30));

        assertEquals(11, hmap2.get(10));
        assertEquals(19, hmap2.get(18));
        assertEquals(null, hmap2.get(20));
        assertEquals(31, hmap2.get(30));

        hmap2 = hmap2.assign(80, null);
        assertEquals(null, hmap2.get(20));
        assertEquals(true, hmap2.find(20).isEmpty());
        // hmap2.find(20).getValue() would throw since the Holder is empty

        assertEquals(null, hmap2.get(80));
        assertEquals(false, hmap2.find(80).isEmpty());
        assertEquals(null, hmap2.find(80).getValue());

        JImmutableMap<Integer, Integer> smap = JImmutables.sortedMap();
        smap = smap.assign(10, 80).assign(20, 21).assign(30, 31).assign(20, 19);
        assertEquals(asList(10, 20, 30), new ArrayList<Integer>(smap.getMap().keySet()));
        assertEquals(asList(80, 19, 31), new ArrayList<Integer>(smap.getMap().values()));
    }

    private <T, C extends Iterable<T>> void verifyOrdered(@Nonnull Predicate<C> classTest,
                                                          @Nonnull Collection<T> expectedValues,
                                                          @Nonnull Func0<C> create)
    {
        verifyOrdered(classTest, expectedValues, create, Iterable::iterator);
    }

    private <T, C> void verifyOrdered(@Nonnull Predicate<C> classTest,
                                      @Nonnull Collection<T> expectedValues,
                                      @Nonnull Func0<C> create,
                                      @Nonnull Func1<C, Iterator<T>> iterator)
    {
        C collection = create.apply();
        assertTrue("collection class is invalid: " + collection.getClass().getName(), classTest.test(collection));
        Iterator<T> expectedIter = expectedValues.iterator();
        Iterator<T> actualIter = iterator.apply(collection);
        for (; ; ) {
            assertEquals(expectedIter.hasNext(), actualIter.hasNext());
            if (!expectedIter.hasNext()) {
                break;
            }
            assertEquals(expectedIter.next(), actualIter.next());
        }
    }

    private <T, C extends Iterable<T>> void verifyUnordered(@Nonnull Predicate<C> classTest,
                                                            @Nonnull Collection<T> expectedValues,
                                                            @Nonnull Func0<C> create)
    {
        verifyUnordered(classTest, expectedValues, create, Iterable::iterator);
    }

    private <T, C> void verifyUnordered(@Nonnull Predicate<C> classTest,
                                        @Nonnull Collection<T> expectedValues,
                                        @Nonnull Func0<C> create,
                                        @Nonnull Func1<C, Iterator<T>> iterator)
    {
        C collection = create.apply();
        assertTrue("collection class is invalid: " + collection.getClass().getName(), classTest.test(collection));
        Set actualSet = new HashSet<>();
        Iterator<T> actualIter = iterator.apply(collection);
        while (actualIter.hasNext()) {
            T x = actualIter.next();
            if (x instanceof Entry) {
                actualSet.add(MapEntry.of((Entry)x));
            } else {
                actualSet.add(x);
            }
        }
        assertEquals(expectedValues.size(), actualSet.size());
        for (T value : expectedValues) {
            assertTrue("expected value is missing: " + value, actualSet.contains(value));
        }
    }

    private <K, V> Map<K, V> map(Iterable<Entry<K, V>> values)
    {
        Map<K, V> answer = new LinkedHashMap<>();
        for (Entry<K, V> e : values) {
            answer.put(e.getKey(), e.getValue());
        }
        return answer;
    }

    private <K, V> Entry<K, V> entry(K key,
                                     V value)
    {
        return MapEntry.of(key, value);
    }

    private <T> Indexed<T> indexed(T... values)
    {
        return IndexedArray.retained(values);
    }

    private <T> Cursor<T> cursor(T... values)
    {
        return StandardCursor.of(indexed(values));
    }

    private <T> Iterable<T> iterable(T... values)
    {
        return () -> IndexedIterator.iterator(indexed(values));
    }

    private <T> Iterator<T> iterator(T... values)
    {
        return asList(values).iterator();
    }
}
