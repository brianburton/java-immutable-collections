///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections;

import org.javimmutable.collections.array.TrieArray;
import org.javimmutable.collections.deque.ArrayDeque;
import org.javimmutable.collections.hash.HashMap;
import org.javimmutable.collections.inorder.OrderedMap;
import org.javimmutable.collections.list.TreeList;
import org.javimmutable.collections.tree.TreeMap;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collector;

public final class ICollectors
{
    private ICollectors()
    {
    }

    /**
     * Collects values into a {@link IArray}.
     */
    @Nonnull
    public static <T> Collector<T, ?, IArray<T>> toArray()
    {
        return TrieArray.collector();
    }

    /**
     * Collects values into a IDeque.
     */
    @Nonnull
    public static <T> Collector<T, ?, IDeque<T>> toDeque()
    {
        return ArrayDeque.collector();
    }

    /**
     * Efficiently collects values into a {@link IList} built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> Collector<T, ?, IList<T>> toList()
    {
        return TreeList.createListCollector();
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toListMap()
    {
        return IListMaps.<K, V>hashed().listMapCollector();
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toOrderedListMap()
    {
        return IListMaps.<K, V>ordered().listMapCollector();
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toSortedListMap()
    {
        return IListMaps.<K, V>sorted().listMapCollector();
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toSortedListMap(@Nonnull Comparator<K> comparator)
    {
        return IListMaps.<K, V>sorted(comparator).listMapCollector();
    }

    /**
     * Creates a Collector suitable for use in the stream to produce a map.
     */
    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> toMap()
    {
        return HashMap.createMapCollector();
    }

    /**
     * Creates a Collector suitable for use in the stream to produce a sorted map.
     */
    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> toSortedMap()
    {
        return TreeMap.createMapCollector();
    }

    /**
     * Creates a Collector suitable for use in the stream to produce a sorted map.
     */
    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> toSortedMap(@Nonnull Comparator<K> comparator)
    {
        return TreeMap.createMapCollector(comparator);
    }

    /**
     * Creates a Collector suitable for use in the stream to produce an insert order map.
     */
    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> toOrderedMap()
    {
        return OrderedMap.<K, V>of().mapCollector();
    }

    /**
     * Collects into a multiset that sorts values based on the order they were originally added to the set.
     */
    @Nonnull
    public static <T> Collector<T, ?, IMultiset<T>> toMultiset()
    {
        return IMultisets.<T>hashed().multisetCollector();
    }

    /**
     * Collects values into a sorted {@link IMultiset} using natural sort order of elements.
     */
    @Nonnull
    public static <T extends Comparable<T>> Collector<T, ?, IMultiset<T>> toSortedMultiset()
    {
        return IMultisets.<T>sorted().multisetCollector();
    }

    /**
     * Collects values into a sorted {@link IMultiset} using specified Comparator.
     */
    @Nonnull
    public static <T> Collector<T, ?, IMultiset<T>> toSortedMultiset(@Nonnull Comparator<T> comparator)
    {
        return IMultisets.sorted(comparator).multisetCollector();
    }

    /**
     * Collects into a multiset that sorts values based on the order they were originally added to the set.
     */
    @Nonnull
    public static <T> Collector<T, ?, IMultiset<T>> toOrderedMultiset()
    {
        return IMultisets.<T>ordered().multisetCollector();
    }

    /**
     * Collects into an unsorted set to the set.
     */
    @Nonnull
    public static <T> Collector<T, ?, ISet<T>> toSet()
    {
        return ISets.<T>hashed().setCollector();
    }

    /**
     * Collects values into a sorted {@link ISet} using natural sort order of elements.
     */
    @Nonnull
    public static <T extends Comparable<T>> Collector<T, ?, ISet<T>> toSortedSet()
    {
        return ISets.<T>sorted().setCollector();
    }

    /**
     * Collects values into a sorted {@link ISet} using specified Comparator.
     */
    @Nonnull
    public static <T> Collector<T, ?, ISet<T>> toSortedSet(@Nonnull Comparator<T> comparator)
    {
        return ISets.sorted(comparator).setCollector();
    }

    /**
     * Collects into a set that sorts values based on the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> Collector<T, ?, ISet<T>> toOrderedSet()
    {
        return ISets.<T>ordered().setCollector();
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> toSetMap()
    {
        return ISetMaps.<K, V>hashed().setMapCollector();
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> toOrderedSetMap()
    {
        return ISetMaps.<K, V>ordered().setMapCollector();
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> toSortedSetMap()
    {
        return ISetMaps.<K, V>sorted().setMapCollector();
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> toSortedSetMap(@Nonnull Comparator<K> comparator)
    {
        return ISetMaps.<K, V>sorted(comparator).setMapCollector();
    }

    /**
     * Collects values into a hashed {@link IListMap} using the specified classifier function
     * to generate keys from the encountered elements.
     */
    @Nonnull
    public static <T, K> Collector<T, ?, IListMap<K, T>> groupingBy(@Nonnull Function<? super T, ? extends K> classifier)
    {
        return GenericCollector.ordered(IListMaps.hashed(),
                                        IListMaps.hashed(),
                                        IListMap::isEmpty,
                                        (a, v) -> a.insert(classifier.apply(v), v),
                                        (a, b) -> a.insertAll(b.entries()));
    }
}
