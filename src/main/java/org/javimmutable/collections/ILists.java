package org.javimmutable.collections;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.list.TreeList;

public final class ILists
{
    private ILists()
    {
    }

    /**
     * Produces an empty JImmutableList built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> of()
    {
        return TreeList.of();
    }

    /**
     * Efficiently produces a JImmutableList containing all of the specified values built atop a balanced binary tree.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IList<T> of(T... values)
    {
        return TreeList.of(IndexedArray.retained(values));
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull Indexed<? extends T> source)
    {
        return TreeList.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in the specified range from source
     * built atop a balanced binary tree.  The values copied from source are those whose index are in the
     * range offset to (limit - 1).
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull Indexed<? extends T> source,
                                     int offset,
                                     int limit)
    {
        return TreeList.of(source, offset, limit);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull ISet<? extends T> source)
    {
        return TreeList.of(source.iterator());
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull List<? extends T> source)
    {
        return TreeList.of(IndexedList.retained(source));
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull Iterator<? extends T> source)
    {
        return TreeList.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull IList<? extends T> source)
    {
        return TreeList.of(source);
    }

    /**
     * Efficiently produces a JImmutableList containing all of the values in source built atop a balanced binary tree.
     */
    @Nonnull
    public static <T> IList<T> allOf(@Nonnull Iterable<? extends T> source)
    {
        return TreeList.of(source.iterator());
    }
}
