package org.javimmutable.collections;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import javax.annotation.Nonnull;
import org.javimmutable.collections.hash.HashMultiset;
import org.javimmutable.collections.inorder.OrderedMultiset;
import org.javimmutable.collections.tree.TreeMultiset;

public final class IMultisets
{
    private IMultisets()
    {
    }

    /**
     * Constructs an unsorted multiset.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value assigned to the multiset.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> IMultiset<T> hashed()
    {
        return HashMultiset.of();
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IMultiset<T> hashed(T... source)
    {
        return HashMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> IMultiset<T> hashed(@Nonnull Iterable<? extends T> source)
    {
        return HashMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> IMultiset<T> hashed(@Nonnull Iterator<? extends T> source)
    {
        return HashMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an empty set that sorts values in their natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> IMultiset<T> sorted()
    {
        return TreeMultiset.of();
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    @SafeVarargs
    public static <T extends Comparable<T>> IMultiset<T> sorted(T... source)
    {
        return TreeMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> IMultiset<T> sorted(@Nonnull Iterable<? extends T> source)
    {
        return TreeMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> IMultiset<T> sorted(@Nonnull Iterator<? extends T> source)
    {
        return TreeMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an empty multiset that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> IMultiset<T> sorted(@Nonnull Comparator<T> comparator)
    {
        return TreeMultiset.of(comparator);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IMultiset<T> sorted(@Nonnull Comparator<T> comparator,
                                          T... source)
    {
        return TreeMultiset.of(comparator).insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> IMultiset<T> sorted(@Nonnull Comparator<T> comparator,
                                          @Nonnull Iterable<? extends T> source)
    {
        return TreeMultiset.of(comparator).insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> IMultiset<T> sorted(@Nonnull Comparator<T> comparator,
                                          @Nonnull Iterator<? extends T> source)
    {
        return TreeMultiset.of(comparator).insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @Nonnull
    public static <T> IMultiset<T> ordered()
    {
        return OrderedMultiset.of();
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IMultiset<T> ordered(T... source)
    {
        return OrderedMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @Nonnull
    public static <T> IMultiset<T> ordered(@Nonnull Iterable<? extends T> source)
    {
        return OrderedMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @Nonnull
    public static <T> IMultiset<T> ordered(@Nonnull Iterator<? extends T> source)
    {
        return OrderedMultiset.<T>of().insertAll(source);
    }
}
