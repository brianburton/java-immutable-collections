package org.javimmutable.collections;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import org.javimmutable.collections.deque.ArrayDeque;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedList;

public final class IDeques
{
    private IDeques()
    {
    }

    /**
     * Produces an empty JImmutableList built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> of()
    {
        return ArrayDeque.of();
    }

    /**
     * Efficiently produces a JImmutableList containing all of the specified values built atop a balanced binary tree.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IDeque<T> of(T... values)
    {
        return ArrayDeque.of(IndexedArray.retained(values));
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull Indexed<? extends T> source)
    {
        return ArrayDeque.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in the specified range from source
     * built atop a balanced binary tree.  The values copied from source are those whose index are in the
     * range offset to (limit - 1).
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull Indexed<? extends T> source,
                                      int offset,
                                      int limit)
    {
        return ArrayDeque.of(source, offset, limit);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull ISet<? extends T> source)
    {
        return ArrayDeque.of(source.iterator());
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull List<? extends T> source)
    {
        return ArrayDeque.of(IndexedList.retained(source));
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull Iterator<? extends T> source)
    {
        return ArrayDeque.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull IDeque<? extends T> source)
    {
        return ArrayDeque.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IDeque<T> allOf(@Nonnull Iterable<? extends T> source)
    {
        return ArrayDeque.of(source.iterator());
    }

    public static <T> IDequeBuilder<T> builder()
    {
        return ArrayDeque.builder();
    }
}
