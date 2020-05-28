package org.javimmutable.collections.array;

import javax.annotation.Nonnull;

public interface ArrayAssignMapper<K, V, T>
    extends ArraySizeMapper<T>
{
    /**
     * Called during assign operation to create a new mapping
     * for the given key and value.
     *
     * @param key   key being assigned to
     * @param value value being assigned
     * @return non-null mapping
     */
    @Nonnull
    T mappedAssign(@Nonnull K key,
                   V value);

    /**
     * Called during assign operation to replace an existing mapping
     * for the given key and value.
     *
     * @param current mapping to be replaced
     * @param key     key being assigned to
     * @param value   value being assigned
     * @return same to keep mapping or non-null to replace mapping
     */
    @Nonnull
    T mappedAssign(@Nonnull T current,
                   @Nonnull K key,
                   V value);
}
