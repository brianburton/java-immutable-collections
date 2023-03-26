package org.javimmutable.collections;

import java.util.Comparator;
import java.util.Iterator;
import javax.annotation.Nonnull;
import org.javimmutable.collections.hash.HashSet;
import org.javimmutable.collections.inorder.OrderedSet;
import org.javimmutable.collections.tree.TreeSet;

public final class ISets
{
    private ISets()
    {
    }

    /**
     * Constructs an unsorted set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> hashed()
    {
        return HashSet.of();
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    @SafeVarargs
    public static <T> ISet<T> hashed(T... source)
    {
        return HashSet.<T>builder().add(source).build();
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> hashed(@Nonnull Iterable<? extends T> source)
    {
        return HashSet.<T>builder().add(source).build();
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> hashed(@Nonnull Iterator<? extends T> source)
    {
        return HashSet.<T>builder().add(source).build();
    }

    /**
     * Constructs an empty set that sorts values in their natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> ISet<T> sorted()
    {
        return TreeSet.of();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    @SafeVarargs
    public static <T extends Comparable<T>> ISet<T> sorted(T... source)
    {
        return TreeSet.<T>builder().add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> ISet<T> sorted(@Nonnull Iterable<? extends T> source)
    {
        return TreeSet.<T>builder().add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <T extends Comparable<T>> ISet<T> sorted(@Nonnull Iterator<? extends T> source)
    {
        return TreeSet.<T>builder().add(source).build();
    }

    /**
     * Constructs an empty set that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> ISet<T> sorted(@Nonnull Comparator<T> comparator)
    {
        return TreeSet.of(comparator);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    @SafeVarargs
    public static <T> ISet<T> sorted(@Nonnull Comparator<T> comparator,
                                     T... source)
    {
        return TreeSet.builder(comparator).add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> ISet<T> sorted(@Nonnull Comparator<T> comparator,
                                     @Nonnull Iterable<? extends T> source)
    {
        return TreeSet.builder(comparator).add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @Nonnull
    public static <T> ISet<T> sorted(@Nonnull Comparator<T> comparator,
                                     @Nonnull Iterator<? extends T> source)
    {
        return TreeSet.builder(comparator).add(source).build();
    }

    /**
     * Constructs an empty set that sorts values based on the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> ordered()
    {
        return OrderedSet.of();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    @SafeVarargs
    public static <T> ISet<T> ordered(T... source)
    {
        return OrderedSet.<T>builder().add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> ordered(@Nonnull Iterable<? extends T> source)
    {
        return OrderedSet.<T>builder().add(source).build();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @Nonnull
    public static <T> ISet<T> ordered(@Nonnull Iterator<? extends T> source)
    {
        return OrderedSet.<T>builder().add(source).build();
    }
}
