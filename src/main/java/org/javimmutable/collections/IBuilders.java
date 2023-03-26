package org.javimmutable.collections;

import java.util.Comparator;
import javax.annotation.Nonnull;
import org.javimmutable.collections.array.TrieArray;
import org.javimmutable.collections.hash.HashMap;
import org.javimmutable.collections.hash.HashSet;
import org.javimmutable.collections.inorder.OrderedMap;
import org.javimmutable.collections.inorder.OrderedSet;
import org.javimmutable.collections.list.TreeList;
import org.javimmutable.collections.tree.TreeMap;
import org.javimmutable.collections.tree.TreeSet;

public final class IBuilders
{
    private IBuilders()
    {
    }

    /**
     * Produces a Builder for efficiently constructing a JImmutableArray
     * built atop a 32-way integer trie.  All values added by the builder are
     * assigned consecutive indices starting with zero.
     */
    @Nonnull
    public static <T> IArrayBuilder<T> array()
    {
        return TrieArray.builder();
    }

    /**
     * Produces a Builder for efficiently constructing a JImmutableList built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IListBuilder<T> list()
    {
        return TreeList.listBuilder();
    }

    /**
     * Constructs a Builder to produce unsorted maps.
     * <p>
     * Implementation note: The map will adopt a hash code collision strategy based on
     * the first key added.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    @Nonnull
    public static <K, V> IMapBuilder<K, V> map()
    {
        return HashMap.builder();
    }

    /**
     * Create a Builder to construct sorted maps using the natural order of the keys.
     */
    @Nonnull
    public static <K extends Comparable<K>, V> IMapBuilder<K, V> sortedMap()
    {
        return TreeMap.builder();
    }

    /**
     * Create a Builder to construct sorted maps using the specified Comparator for keys.
     */
    @Nonnull
    public static <K, V> IMapBuilder<K, V> sortedMap(@Nonnull Comparator<K> comparator)
    {
        return TreeMap.builder(comparator);
    }

    /**
     * Create a Builder to construct maps whose iterators visit entries in the same order they were
     * added to the map.
     */
    @Nonnull
    public static <K, V> IMapBuilder<K, V> orderedMap()
    {
        return OrderedMap.builder();
    }

    /**
     * Constructs Builder object to produce unsorted sets.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISetBuilder<T> set()
    {
        return HashSet.builder();
    }

    /**
     * Constructs a Builder object to produce sets that sort values in their natural
     * sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> ISetBuilder<T> sortedSet()
    {
        return TreeSet.builder();
    }

    /**
     * Constructs a Builder object to produce sets that sort values using specified Comparator.
     */
    @Nonnull
    public static <T> ISetBuilder<T> sortedSet(@Nonnull Comparator<T> comparator)
    {
        return TreeSet.builder(comparator);
    }

    /**
     * Constructs Builder object to produce sets that sort values based on
     * the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISetBuilder<T> orderedSet()
    {
        return OrderedSet.builder();
    }
}
