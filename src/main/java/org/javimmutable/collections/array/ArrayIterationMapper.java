package org.javimmutable.collections.array;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;

public interface ArrayIterationMapper<K, V, T>
{
    @Nonnull
    GenericIterator.Iterable<K> mappedKeys(@Nonnull T mapping);

    @Nonnull
    GenericIterator.Iterable<V> mappedValues(@Nonnull T mapping);

    @Nonnull
    GenericIterator.Iterable<JImmutableMap.Entry<K, V>> mappedEntries(@Nonnull T mapping);
}
