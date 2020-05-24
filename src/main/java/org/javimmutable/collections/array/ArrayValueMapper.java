package org.javimmutable.collections.array;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for objects that can map key/value pairs into single values
 * suitable for storage in an array.
 */
public interface ArrayValueMapper<K, V, T>
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

    /**
     * Called during delete operation to delete a key from a mapping.
     *
     * @param current mapping to be replaced
     * @param key     key being deleted
     * @return null to remove mapping, same to keep mapping, or non-null to replace mapping
     */
    @Nullable
    T mappedDelete(@Nonnull T current,
                   @Nonnull K key);

    /**
     * Called to obtain number of keys in a given mapping.
     *
     * @param mapping mapping to be sized
     * @return number of keys in the mapping
     */
    int mappedSize(@Nonnull T mapping);

    @Nonnull
    GenericIterator.Iterable<K> mappedKeys(@Nonnull T mapping);

    @Nonnull
    GenericIterator.Iterable<V> mappedValues(@Nonnull T mapping);

    @Nonnull
    GenericIterator.Iterable<JImmutableMap.Entry<K, V>> mappedEntries(@Nonnull T mapping);
}
