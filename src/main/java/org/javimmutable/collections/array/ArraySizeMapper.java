package org.javimmutable.collections.array;

import javax.annotation.Nonnull;

public interface ArraySizeMapper<T>
{
    /**
     * Called to obtain number of keys in a given mapping.
     *
     * @param mapping mapping to be sized
     * @return number of keys in the mapping
     */
    int mappedSize(@Nonnull T mapping);
}
