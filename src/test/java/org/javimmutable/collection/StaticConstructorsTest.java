///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection;

import junit.framework.TestCase;
import org.javimmutable.collection.array.TrieArray;
import org.javimmutable.collection.hash.EmptyHashMap;
import org.javimmutable.collection.hash.EmptyHashSet;
import org.javimmutable.collection.hash.HashMap;
import org.javimmutable.collection.hash.HashMultiset;
import org.javimmutable.collection.hash.HashSet;
import org.javimmutable.collection.indexed.IndexedArray;
import org.javimmutable.collection.inorder.OrderedMap;
import org.javimmutable.collection.inorder.OrderedMultiset;
import org.javimmutable.collection.inorder.OrderedSet;
import org.javimmutable.collection.iterators.IndexedIterator;
import org.javimmutable.collection.iterators.IteratorHelper;
import org.javimmutable.collection.list.TreeList;
import org.javimmutable.collection.listmap.HashListMap;
import org.javimmutable.collection.listmap.OrderedListMap;
import org.javimmutable.collection.listmap.TreeListMap;
import org.javimmutable.collection.setmap.HashSetMap;
import org.javimmutable.collection.setmap.OrderedSetMap;
import org.javimmutable.collection.setmap.SetMapFactory;
import org.javimmutable.collection.setmap.TemplateSetMap;
import org.javimmutable.collection.setmap.TreeSetMap;
import org.javimmutable.collection.tree.ComparableComparator;
import org.javimmutable.collection.tree.TreeMap;
import org.javimmutable.collection.tree.TreeMultiset;
import org.javimmutable.collection.tree.TreeSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@SuppressWarnings("unchecked")
public class StaticConstructorsTest
    extends TestCase
{
    private final Predicate<IArray> isArray = x -> x instanceof TrieArray;
    private final Predicate<IList> isList = x -> x instanceof TreeList;
    private final Predicate<IMap> isEmptyMap = x -> x instanceof EmptyHashMap;
    private final Predicate<IMap> isMap = x -> x instanceof HashMap;
    private final Predicate<IMap> isSortedMap = x -> x instanceof TreeMap;
    private final Predicate<IMap> isInsertOrderMap = x -> x instanceof OrderedMap;
    private final Predicate<ISet> isSet = x -> x instanceof HashSet;
    private final Predicate<ISet> isEmptySet = x -> x instanceof EmptyHashSet;
    private final Predicate<ISet> isSortedSet = x -> x instanceof TreeSet;
    private final Predicate<ISet> isInsertOrderSet = x -> x instanceof OrderedSet;
    private final Predicate<IMultiset> isMultiset = x -> x instanceof HashMultiset;
    private final Predicate<IMultiset> isSortedMultiset = x -> x instanceof TreeMultiset;
    private final Predicate<IMultiset> isInsertOrderMultiset = x -> x instanceof OrderedMultiset;
    private final Predicate<IListMap> isListMap = x -> x instanceof HashListMap;
    private final Predicate<IListMap> isSortedListMap = x -> x instanceof TreeListMap;
    private final Predicate<IListMap> isInsertOrderListMap = x -> x instanceof OrderedListMap;
    private final Predicate<ISetMap> isSetMap = x -> x instanceof HashSetMap;
    private final Predicate<ISetMap> isSortedSetMap = x -> x instanceof TreeSetMap;
    private final Predicate<ISetMap> isInsertOrderSetMap = x -> x instanceof OrderedSetMap;
    private final Predicate<ISetMap> isTemplateSetMap = x -> x instanceof TemplateSetMap;

    public void testArray()
    {
        verifyOrdered(isArray,
                      Arrays.asList(),
                      () -> IArrays.of());
        verifyOrdered(isArray,
                      asList(entry(-20, "a"), entry(-7, "c"), entry(17, "d"), entry(100, "a")),
                      () -> IArrays.allOf(iterator(entry(-7, "c"), entry(-20, "a"), entry(17, "d"), entry(100, "a"))));
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> IArrays.of("c", "a", "d", "a"));
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> IArrays.allOf(indexed("c", "a", "d", "a")));
        verifyOrdered(isArray,
                      asList(entry(0, "d")),
                      () -> IArrays.allOf(indexed("c", "a", "d", "a"), 2, 3));
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> IArrays.allOf(asList("c", "a", "d", "a")));
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> IBuilders.array().add(asList("c", "a", "d", "a")).build());
        verifyOrdered(isArray,
                      asList(entry(0, "c"), entry(1, "a"), entry(2, "d"), entry(3, "a")),
                      () -> Stream.of("c", "a", "d", "a").collect(ICollectors.toArray()));
    }

    public void testList()
    {
        List<Integer> input = asList(1, 2, 3);

        IList<Integer> list = ILists.allOf(input);
        assertEquals(input, list.getList());
        assertEquals(list, ILists.allOf(input.iterator()));
        assertEquals(list, ILists.allOf(list));
        assertEquals(list, ILists.of(1, 2, 3));
        assertEquals(list, IBuilders.<Integer>list().addAll(input).build());
        assertEquals(list, ILists.allOf(IteratorHelper.plainIterable(asList(1, 2, 3))));

        verifyOrdered(isList, asList(), () -> ILists.of());
        verifyOrdered(isList, asList("a", "b", "c"), () -> ILists.of("a", "b", "c"));
        verifyOrdered(isList, asList("a", "b", "c"), () -> ILists.allOf(asList("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c"), () -> ILists.allOf(ISets.ordered("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c"), () -> ILists.allOf(iterator("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c"), () -> ILists.allOf(asList("a", "b", "c")));
        verifyOrdered(isList, asList("a", "b", "c"), () -> IBuilders.list().addAll("a", "b", "c").build());
        verifyOrdered(isList, asList("a", "b", "c"), () -> Stream.of("a", "b", "c").collect(ICollectors.toList()));
    }

    public void testLargeList()
    {
        List<Integer> input = new ArrayList<>();
        for (int i = 0; i < 2000; ++i) {
            input.add(i);
        }
        IList<Integer> list = ILists.allOf(input);
        assertEquals(input, list.getList());
    }

    public void testMap()
    {
        Map<Integer, Integer> input = new java.util.HashMap<>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        IMap<Integer, Integer> map = IMaps.hashed(input);
        assertEquals(input, map.getMap());
        assertSame(map, IMaps.hashed(map));
        assertEquals(map, IMaps.hashed(map));
        assertNotSame(map, IMaps.sorted(input));
        assertEquals(map, IMaps.sorted(input));

        verifyUnordered(isEmptyMap, asList(), () -> IMaps.hashed());
        final List<IMapEntry<String, String>> entries = asList(entry("z", "1"), entry("x", "2"), entry("w", "3"), entry("y", "4"));
        verifyUnordered(isMap, entries, () -> IMaps.hashed(map(entries)));
        verifyUnordered(isMap, entries, () -> IBuilders.<String, String>map().add(entries).build());
    }

    public void testSortedMap()
    {
        Map<Integer, Integer> input = new java.util.HashMap<>();
        input.put(1, 3);
        input.put(2, 4);
        input.put(3, 5);

        IMap<Integer, Integer> map = IMaps.sorted(input);
        assertEquals(input, map.getMap());
        assertSame(map, IMaps.sorted(map));
        assertEquals(map, IMaps.hashed(map));

        final Comparator<Integer> comparator = (a, b) -> -b.compareTo(a);
        IMap<Integer, Integer> map2 = IMaps.sorted(comparator, input);
        assertEquals(input, map2.getMap());
        assertEquals(map, IMaps.sorted(map2));
        assertNotSame(map, IMaps.sorted(map2));
        assertSame(map2, IMaps.sorted(comparator, map2));
        assertEquals(map, IMaps.hashed(map2));
        assertNotSame(map, IMaps.hashed(map2));

        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        final List<IMapEntry<String, String>> entries = asList(entry("z", "1"), entry("x", "2"), entry("w", "3"), entry("y", "4"));
        final List<IMapEntry<String, String>> sorted = asList(entry("w", "3"), entry("x", "2"), entry("y", "4"), entry("z", "1"));
        final List<IMapEntry<String, String>> reversed = asList(entry("z", "1"), entry("y", "4"), entry("x", "2"), entry("w", "3"));

        verifyOrdered(isSortedMap, asList(), () -> IMaps.sorted());
        verifyOrdered(isSortedMap, sorted, () -> IMaps.sorted(map(entries)));
        verifyOrdered(isSortedMap, sorted, () -> IBuilders.<String, String>sortedMap().add(entries).build());
        verifyOrdered(isSortedMap, sorted, () -> entries.stream().collect(ICollectors.toSortedMap()));

        verifyOrdered(isSortedMap, asList(), () -> IMaps.sorted());
        verifyOrdered(isSortedMap, reversed, () -> IMaps.sorted(reverse, map(entries)));
        verifyOrdered(isSortedMap, reversed, () -> IBuilders.<String, String>sortedMap(reverse).add(entries).build());
        verifyOrdered(isSortedMap, reversed, () -> entries.stream().collect(ICollectors.toSortedMap(reverse)));
    }

    public void testInsertOrderMap()
    {
        final List<IMapEntry<String, String>> entries = asList(entry("z", "1"), entry("x", "2"), entry("w", "3"), entry("y", "4"));
        final List<IMapEntry<String, String>> sorted = asList(entry("w", "3"), entry("x", "2"), entry("y", "4"), entry("z", "1"));
        final List<IMapEntry<String, String>> reversed = asList(entry("z", "1"), entry("y", "4"), entry("x", "2"), entry("w", "3"));

        verifyOrdered(isInsertOrderMap, asList(), () -> IMaps.ordered());
        verifyOrdered(isInsertOrderMap, entries, () -> IMaps.ordered(map(entries)));
        verifyOrdered(isInsertOrderMap, sorted, () -> IMaps.ordered(IMaps.sorted(map(reversed))));

        IMap<String, String> iomap = IMaps.ordered(map(entries));
        assertSame(iomap, IMaps.ordered(iomap));

        assertEquals(IMaps.<String, String>ordered().insertAll(entries), IBuilders.orderedMap().add(entries).build());
    }

    public void testSet()
    {
        List<Integer> input = asList(1, 87, 100, 1, 45);

        ISet<Integer> set = ISets.hashed(input);
        assertEquals(new java.util.HashSet<>(input), set.getSet());
        assertEquals(set, ISets.hashed(input.iterator()));
        assertEquals(set, ISets.hashed(set));
        assertEquals(set, ISets.hashed(1, 100, 45, 87, 1));
        assertEquals(set, IBuilders.set().add(1, 100, 45, 87, 1).build());

        verifyUnordered(isEmptySet, asList(), () -> ISets.hashed());
        verifyUnordered(isSet, asList("a", "b", "c"), () -> ISets.hashed("a", "b", "c"));
        verifyUnordered(isSet, asList("a", "b", "c", "d", "e"), () -> ISets.hashed(iterable("a", "b", "c", "d", "e")));
        verifyUnordered(isSet, asList("a", "b", "c"), () -> ISets.hashed(iterator("a", "b", "c")));
        verifyUnordered(isSet, asList("a", "b", "c"), () -> Stream.of("a", "b", "c").collect(ICollectors.toSet()));
    }

    public void testSortedSet()
    {
        List<Integer> input = asList(1, 87, 100, 1, 45);

        ISet<Integer> set = ISets.sorted(input);
        assertEquals(new java.util.HashSet<>(input), set.getSet());
        assertEquals(set, ISets.sorted(input.iterator()));
        assertEquals(set, ISets.sorted(set));
        assertEquals(set, ISets.sorted(1, 100, 45, 87, 1));
        assertEquals(set, IBuilders.<Integer>sortedSet().add(1, 100, 45, 87, 1).build());
        assertEquals(set, IBuilders.sortedSet(ComparableComparator.<Integer>of()).add(1, 100, 45, 87, 1).build());
        IteratorHelper.iteratorEquals(set.iterator(), asList(1, 45, 87, 100).iterator());

        Comparator<Integer> reverser = (a, b) -> -a.compareTo(b);
        set = ISets.sorted(reverser, input);
        assertEquals(new java.util.HashSet<>(input), set.getSet());
        assertEquals(set, ISets.sorted(reverser, input.iterator()));
        assertEquals(set, ISets.sorted(reverser, set));
        assertEquals(set, ISets.sorted(reverser, 1, 100, 45, 87, 1));
        assertEquals(set, IBuilders.sortedSet(reverser).add(1, 100, 45, 87, 1).build());
        IteratorHelper.iteratorEquals(set.iterator(), asList(100, 87, 45, 1).iterator());

        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        final List<String> sorted = asList("w", "x", "y", "z");
        final List<String> reversed = asList("z", "y", "x", "w");

        verifyOrdered(isSortedSet, asList(), () -> ISets.<String>sorted());
        verifyOrdered(isSortedSet, sorted, () -> ISets.sorted("z", "w", "y", "x"));
        verifyOrdered(isSortedSet, sorted, () -> ISets.sorted(iterable("z", "w", "y", "x")));
        verifyOrdered(isSortedSet, sorted, () -> ISets.sorted(iterator("z", "w", "y", "x")));

        verifyOrdered(isSortedSet, asList(), () -> ISets.sorted(reverse));
        verifyOrdered(isSortedSet, reversed, () -> ISets.sorted(reverse, "x", "w", "z", "y"));
        verifyOrdered(isSortedSet, reversed, () -> ISets.sorted(reverse, iterable("x", "w", "z", "y")));
        verifyOrdered(isSortedSet, reversed, () -> ISets.sorted(reverse, iterator("x", "w", "z", "y")));
        verifyOrdered(isSortedSet, sorted, () -> Stream.of("x", "w", "z", "y").collect(ICollectors.toSortedSet()));
        verifyOrdered(isSortedSet, reversed, () -> Stream.of("x", "w", "z", "y").collect(ICollectors.toSortedSet(reverse)));
    }

    public void testInsertOrderSet()
    {
        final List<String> entries = asList("x", "w", "z", "y");

        verifyOrdered(isInsertOrderSet, asList(), () -> ISets.<String>ordered());
        verifyOrdered(isInsertOrderSet, entries, () -> ISets.ordered("x", "w", "z", "y"));
        verifyOrdered(isInsertOrderSet, entries, () -> ISets.ordered(iterable("x", "w", "z", "y")));
        verifyOrdered(isInsertOrderSet, entries, () -> ISets.ordered(iterator("x", "w", "z", "y")));
        verifyOrdered(isInsertOrderSet, entries, () -> IBuilders.<String>orderedSet().add("x", "w", "z", "y").build());
        verifyOrdered(isInsertOrderSet, entries, () -> Stream.of("x", "w", "z", "y").collect(ICollectors.toOrderedSet()));
    }

    public void testMultiset()
    {
        final Func1<IMultiset, Iterator<IMapEntry<String, Integer>>> occurrences = x -> x.entries().iterator();
        verifyUnordered(isMultiset, entryList(), () -> IMultisets.<String>hashed(), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> IMultisets.hashed("y", "x", "y", "x", "y"), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> IMultisets.hashed(iterable("y", "x", "y", "x", "y")), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> IMultisets.hashed(iterator("y", "x", "y", "x", "y")), occurrences);
        verifyUnordered(isMultiset, entryList(entry("x", 2), entry("y", 3)), () -> Stream.of("y", "x", "y", "x", "y").collect(ICollectors.toMultiset()), occurrences);
    }

    public void testSortedMultiset()
    {
        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        final List<IMapEntry<String, Integer>> sorted = asList(entry("x", 2), entry("y", 3));
        final List<IMapEntry<String, Integer>> reversed = asList(entry("y", 3), entry("x", 2));
        final Func1<IMultiset, Iterator<IMapEntry<String, Integer>>> occurrences = x -> x.entries().iterator();

        verifyOrdered(isSortedMultiset, entryList(), () -> IMultisets.<String>sorted(), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> IMultisets.sorted("y", "x", "y", "x", "y"), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> IMultisets.sorted(iterable("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> IMultisets.sorted(iterator("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, sorted, () -> Stream.of("y", "x", "y", "x", "y").collect(ICollectors.toSortedMultiset()), occurrences);

        verifyOrdered(isSortedMultiset, entryList(), () -> IMultisets.sorted(reverse), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> IMultisets.sorted(reverse, "y", "x", "y", "x", "y"), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> IMultisets.sorted(reverse, iterable("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> IMultisets.sorted(reverse, iterator("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isSortedMultiset, reversed, () -> Stream.of("y", "x", "y", "x", "y").collect(ICollectors.toSortedMultiset(reverse)), occurrences);
    }

    public void testInsertOrderMultiset()
    {
        final List<IMapEntry<String, Integer>> entered = asList(entry("y", 3), entry("x", 2));
        final Func1<IMultiset, Iterator<IMapEntry<String, Integer>>> occurrences = x -> x.entries().iterator();

        verifyOrdered(isInsertOrderMultiset, entryList(), () -> IMultisets.<String>ordered(), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> IMultisets.ordered("y", "x", "y", "x", "y"), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> IMultisets.ordered(iterable("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> IMultisets.ordered(iterator("y", "x", "y", "x", "y")), occurrences);
        verifyOrdered(isInsertOrderMultiset, entered, () -> Stream.of("y", "x", "y", "x", "y").collect(ICollectors.toOrderedMultiset()), occurrences);
    }

    public void testListMap()
    {
        verifyUnordered(isListMap, entryList(entry("y", ILists.of(1)), entry("z", ILists.of(2)), entry("x", ILists.of(3))), () -> IListMaps.<String, Integer>listMap().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testSortedListMap()
    {
        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        verifyOrdered(isSortedListMap, entryList(entry("x", ILists.of(3)), entry("y", ILists.of(1)), entry("z", ILists.of(2))), () -> IListMaps.<String, Integer>sortedListMap().insert("y", 1).insert("z", 2).insert("x", 3));
        verifyOrdered(isSortedListMap, entryList(entry("z", ILists.of(2)), entry("y", ILists.of(1)), entry("x", ILists.of(3))), () -> IListMaps.<String, Integer>sortedListMap(reverse).insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testInsertOrderListMap()
    {
        verifyOrdered(isInsertOrderListMap, entryList(entry("y", ILists.of(1)), entry("z", ILists.of(2)), entry("x", ILists.of(3))), () -> IListMaps.<String, Integer>insertOrderListMap().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testSetMap()
    {
        verifyUnordered(isSetMap, entryList(entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2)), entry("x", ISets.hashed(3))), () -> ISetMaps.<String, Integer>hashed().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testSortedSetMap()
    {
        final Comparator<String> reverse = ComparableComparator.<String>of().reversed();
        verifyOrdered(isSortedSetMap, entryList(entry("x", ISets.hashed(3)), entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2))), () -> ISetMaps.<String, Integer>sorted().insert("y", 1).insert("z", 2).insert("x", 3));
        verifyOrdered(isSortedSetMap, entryList(entry("z", ISets.hashed(2)), entry("y", ISets.hashed(1)), entry("x", ISets.hashed(3))), () -> ISetMaps.<String, Integer>sorted(reverse).insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testInsertOrderSetMap()
    {
        verifyOrdered(isInsertOrderSetMap, entryList(entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2)), entry("x", ISets.hashed(3))), () -> ISetMaps.<String, Integer>ordered().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testTemplateSetMap()
    {
        final ISetMap<String, Integer> setmap = ISetMaps.templated(IMaps.hashed(), ISets.hashed());
        verifyUnordered(isTemplateSetMap, entryList(entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2)), entry("x", ISets.hashed(3))), () -> setmap.insert("y", 1).insert("z", 2).insert("x", 3));
        final SetMapFactory<String, Integer> factory1 = ISetMaps.<String, Integer>factory().withMap(IMaps.hashed()).withSet(ISets.hashed());
        verifyUnordered(isTemplateSetMap, entryList(entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2)), entry("x", ISets.hashed(3))), () -> factory1.create().insert("y", 1).insert("z", 2).insert("x", 3));
        final SetMapFactory<String, Integer> factory2 = ISetMaps.factory(String.class, Integer.class).withMap(IMaps.hashed()).withSet(ISets.hashed());
        verifyUnordered(isTemplateSetMap, entryList(entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2)), entry("x", ISets.hashed(3))), () -> factory2.create().insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testTemplateSortedSetMap()
    {
        final Comparator<String> reversedComparator = ComparableComparator.<String>of().reversed();
        final ISetMap<String, Integer> forward = ISetMaps.templated(IMaps.<String, ISet<Integer>>sorted(), ISets.hashed());
        final ISetMap<String, Integer> reverse = ISetMaps.templated(IMaps.sorted(reversedComparator), ISets.hashed());
        verifyOrdered(isTemplateSetMap, entryList(entry("x", ISets.hashed(3)), entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2))), () -> forward.insert("y", 1).insert("z", 2).insert("x", 3));
        verifyOrdered(isTemplateSetMap, entryList(entry("z", ISets.hashed(2)), entry("y", ISets.hashed(1)), entry("x", ISets.hashed(3))), () -> reverse.insert("y", 1).insert("z", 2).insert("x", 3));
    }

    public void testTemplateInsertOrderSetMap()
    {
        final ISetMap<String, Integer> setmap = ISetMaps.templated(IMaps.ordered(), ISets.hashed());
        verifyOrdered(isTemplateSetMap, entryList(entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2)), entry("x", ISets.hashed(3))), () -> setmap.insert("y", 1).insert("z", 2).insert("x", 3));
        final ISetMap<String, Integer> setmap2 = ISetMaps.<String, Integer>factory().withMap(IMaps.ordered()).withSet(ISets.hashed()).create();
        verifyOrdered(isTemplateSetMap, entryList(entry("y", ISets.hashed(1)), entry("z", ISets.hashed(2)), entry("x", ISets.hashed(3))), () -> setmap2.insert("y", 1).insert("z", 2).insert("x", 3));
    }

    private <K, V> List<IMapEntry<K, V>> entryList(IMapEntry<K, V>... values)
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
        IList<Integer> list = ILists.of();
        list = list.insert(10).insert(20).insert(30);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));

        IList<Integer> changed = list.deleteLast().insert(45);
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
        assertEquals(10, changed.get(0));
        assertEquals(20, changed.get(1));
        assertEquals(45, changed.get(2));

        assertEquals(asList(10, 20, 30), list.getList());
        assertEquals(asList(10, 20, 45), changed.getList());

        IList<Integer> ralist = ILists.of();
        ralist = ralist.insert(30).insert(0, 20).insert(0, 10);
        assertEquals(10, ralist.get(0));
        assertEquals(20, ralist.get(1));
        assertEquals(30, ralist.get(2));
        IList<Integer> ralist2 = ralist;
        ralist2 = ralist2.delete(1).insert(1, 87);
        assertEquals(10, ralist.get(0));
        assertEquals(20, ralist.get(1));
        assertEquals(30, ralist.get(2));
        assertEquals(10, ralist2.get(0));
        assertEquals(87, ralist2.get(1));
        assertEquals(30, ralist2.get(2));
        assertEquals(asList(10, 20, 30), ralist.getList());
        assertEquals(asList(10, 87, 30), ralist2.getList());

        IList<String> source = ILists.of("able", "baker", "charlie", "delta", "echo");
        assertEquals(ILists.of("baker", "charlie"), source.select(str -> str.contains("r")));
        assertEquals(ILists.of("able", "baker", "delta"), source.reject(str -> str.contains("h")));
        assertEquals("ablebakercharliedeltaecho", source.reduce("", (answer, str) -> answer + str));
        assertEquals(ILists.of("baker", "charlie"),
                     source.stream()
                         .filter(str -> str.contains("r"))
                         .collect(ICollectors.toList()));
    }

    public void testMapTutorialCode()
    {
        IMap<Integer, Integer> hmap = IMaps.hashed();
        hmap = hmap.assign(10, 11).assign(20, 21).assign(30, 31).assign(20, 19);

        IMap<Integer, Integer> hmap2 = hmap.delete(20).assign(18, 19);

        assertEquals(11, hmap.get(10));
        assertEquals(19, hmap.get(20));
        assertEquals(31, hmap.get(30));

        assertEquals(11, hmap2.get(10));
        assertEquals(19, hmap2.get(18));
        assertEquals(null, hmap2.get(20));
        assertEquals(31, hmap2.get(30));

        hmap2 = hmap2.assign(80, null);
        assertEquals(null, hmap2.get(20));
        Maybe<Integer> integers1 = hmap2.find(20);
        assertEquals(true, integers1.isEmpty());
        // hmap2.find(20).getValue() would throw since the Holder is empty

        assertEquals(null, hmap2.get(80));
        Maybe<Integer> integers = hmap2.find(80);
        assertEquals(false, integers.isEmpty());
        Maybe<Integer> integers2 = hmap2.find(80);
        assertEquals(null, integers2.unsafeGet());

        IMap<Integer, Integer> smap = IMaps.sorted();
        smap = smap.assign(10, 80).assign(20, 21).assign(30, 31).assign(20, 19);
        assertEquals(asList(10, 20, 30), new ArrayList<>(smap.getMap().keySet()));
        assertEquals(asList(80, 19, 31), new ArrayList<>(smap.getMap().values()));
    }

    public void testEntry()
    {
        IMap<Integer, Integer> m = IMaps.<Integer, Integer>hashed().assign(1, 1).assign(2, 2);
        IMap<Number, Number> n = m.stream().map(e -> IMapEntry.<Number, Number>of(e.getKey(), e.getValue())).collect(ICollectors.toMap());
        assertEquals(IMaps.<Number, Number>hashed().assign(1, 1).assign(2, 2), n);

        IList<Integer> il = ILists.of(4, 2, 3, 1);
        final Function<Integer, IMapEntry<String, Number>> mapper = i -> IMapEntry.of(String.valueOf(i), i);
        IMap<String, Number> im = il.stream().map(mapper).collect(ICollectors.toSortedMap());
        assertEquals("{1=1, 2=2, 3=3, 4=4}", im.toString());
        im = il.stream().map(mapper).collect(ICollectors.toOrderedMap());
        assertEquals("{4=4, 2=2, 3=3, 1=1}", im.toString());

        Map<Integer, Integer> m1 = new LinkedHashMap<>();
        m1.put(4, 4);
        m1.put(2, 2);
        m1.put(3, 3);
        m1.put(1, 1);
        IMap<Integer, Integer> m2 = m1.entrySet().stream().map(IMapEntry::of).collect(ICollectors.toOrderedMap());
        assertEquals("{4=4, 2=2, 3=3, 1=1}", m2.toString());
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
        Set actualSet = new java.util.HashSet<>();
        Iterator<T> actualIter = iterator.apply(collection);
        while (actualIter.hasNext()) {
            T x = actualIter.next();
            if (x instanceof IMapEntry) {
                actualSet.add(IMapEntry.of((IMapEntry)x));
            } else {
                actualSet.add(x);
            }
        }
        assertEquals(expectedValues.size(), actualSet.size());
        for (T value : expectedValues) {
            assertTrue("expected value is missing: " + value, actualSet.contains(value));
        }
    }

    private <K, V> Map<K, V> map(Iterable<IMapEntry<K, V>> values)
    {
        Map<K, V> answer = new LinkedHashMap<>();
        for (IMapEntry<K, V> e : values) {
            answer.put(e.getKey(), e.getValue());
        }
        return answer;
    }

    private <K, V> IMapEntry<K, V> entry(K key,
                                         V value)
    {
        return IMapEntry.of(key, value);
    }

    private <T> Indexed<T> indexed(T... values)
    {
        return IndexedArray.retained(values);
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
