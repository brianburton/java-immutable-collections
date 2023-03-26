package org.javimmutable.collections;

import java.util.Iterator;
import javax.annotation.Nonnull;
import org.javimmutable.collections.array.JImmutableTrieArray;

public final class IArrays
{
    private IArrays()
    {
    }

    /**
     * Creates an empty sparse array that supports any integer (positive or negative) as an index.
     * Indexes do not need to be consecutive there can be gaps of any size between indexes.
     */
    @Nonnull
    public static <T> IArray<T> of()
    {
        return JImmutableTrieArray.of();
    }

    /**
     * Creates an empty sparse array that supports any integer (positive or negative) as an index.
     * Indexes do not need to be consecutive there can be gaps of any size between indexes.
     * Copies all values into the array starting at index zero.
     */
    @Nonnull
    @SafeVarargs
    public static <T> IArray<T> of(T... source)
    {
        return JImmutableTrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    @Nonnull
    public static <T> IArray<T> allOf(@Nonnull Iterator<IMapEntry<Integer, T>> source)
    {
        return JImmutableTrieArray.<T>of().insertAll(source);
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    @Nonnull
    public static <T> IArray<T> allOf(@Nonnull Indexed<? extends T> source)
    {
        return JImmutableTrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates a sparse array containing all of the values in the specified range from source that
     * supports any integer (positive or negative) as an index.  Indexes do not need to be
     * consecutive there can be gaps of any size between indexes. Copies all entries into the
     * array using each key as an index for storing the corresponding value.  The values copied
     * from source are those whose index are in the range offset to (limit - 1).
     */
    @Nonnull
    public static <T> IArray<T> allOf(@Nonnull Indexed<? extends T> source,
                                      int offset,
                                      int limit)
    {
        return JImmutableTrieArray.<T>builder().add(source, offset, limit).build();
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    @Nonnull
    public static <T> IArray<T> allOf(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableTrieArray.<T>builder().add(source).build();
    }
}
