package org.javimmutable.collections.array;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;

import javax.annotation.Nonnull;

public interface ArrayFindEntryMapper<K, V, T>
    extends ArrayGetMapper<K, V, T>
{
    @Nonnull
    Holder<JImmutableMap.Entry<K, V>> mappedFindEntry(@Nonnull T mapping,
                                                      @Nonnull K key);
}
