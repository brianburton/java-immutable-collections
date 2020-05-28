package org.javimmutable.collections.array;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ArrayDeleteMapper<K, T>
    extends ArraySizeMapper<T>
{
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
}
