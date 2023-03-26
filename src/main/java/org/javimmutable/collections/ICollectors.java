package org.javimmutable.collections;

import java.util.Comparator;
import java.util.stream.Collector;
import javax.annotation.Nonnull;
import org.javimmutable.collections.array.JImmutableTrieArray;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMap;
import org.javimmutable.collections.list.JImmutableTreeList;
import org.javimmutable.collections.tree.JImmutableTreeMap;

public final class ICollectors
{
    private ICollectors()
    {
    }

    /**
     * Collects values into a JImmutableArray.
     */
    @Nonnull
    public static <T> Collector<T, ?, IArray<T>> toArray()
    {
        return JImmutableTrieArray.collector();
    }

    /**
     * Efficiently collects values into a JImmutableList built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> Collector<T, ?, IList<T>> toList()
    {
        return JImmutableTreeList.createListCollector();
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toListMap()
    {
        return IListMap.<K, V>listMap().toCollector();
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toOrderedListMap()
    {
        return IListMap.<K, V>insertOrderListMap().toCollector();
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toSortedListMap()
    {
        return IListMap.<K, V>sortedListMap().toCollector();
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toSortedListMap(@Nonnull Comparator<K> comparator)
    {
        return IListMap.<K, V>sortedListMap(comparator).toCollector();
    }

    /**
     * Creates a Collector suitable for use in the stream to produce a map.
     */
    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> toMap()
    {
        return JImmutableHashMap.createMapCollector();
    }

    /**
     * Creates a Collector suitable for use in the stream to produce a sorted map.
     */
    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> toSortedMap()
    {
        return JImmutableTreeMap.createMapCollector();
    }

    /**
     * Creates a Collector suitable for use in the stream to produce a sorted map.
     */
    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> toSortedMap(@Nonnull Comparator<K> comparator)
    {
        return JImmutableTreeMap.createMapCollector(comparator);
    }

    /**
     * Creates a Collector suitable for use in the stream to produce an insert order map.
     */
    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> toOrderedMap()
    {
        return JImmutableInsertOrderMap.<K, V>of().mapCollector();
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
     * Collects values into a sorted JImmutableMultiset using natural sort order of elements.
     */
    @Nonnull
    public static <T extends Comparable<T>> Collector<T, ?, IMultiset<T>> toSortedMultiset()
    {
        return IMultisets.<T>sorted().multisetCollector();
    }

    /**
     * Collects values into a sorted JImmutableMultiset using specified Comparator.
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
     * Collects values into a sorted JImmutableSet using natural sort order of elements.
     */
    @Nonnull
    public static <T extends Comparable<T>> Collector<T, ?, ISet<T>> toSortedSet()
    {
        return ISets.<T>sorted().setCollector();
    }

    /**
     * Collects values into a sorted JImmutableSet using specified Comparator.
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
        return ISetMaps.<K, V>hashed().toCollector();
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> toOrderedSetMap()
    {
        return ISetMaps.<K, V>ordered().toCollector();
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> toSortedSetMap()
    {
        return ISetMaps.<K, V>sorted().toCollector();
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> toSortedSetMap(@Nonnull Comparator<K> comparator)
    {
        return ISetMaps.<K, V>sorted(comparator).toCollector();
    }
}
