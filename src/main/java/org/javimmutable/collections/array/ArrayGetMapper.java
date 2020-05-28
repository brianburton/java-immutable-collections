package org.javimmutable.collections.array;

import org.javimmutable.collections.Holder;

import javax.annotation.Nonnull;

public interface ArrayGetMapper<K, V, T>
{
    V mappedGetValueOr(@Nonnull T mapping,
                       @Nonnull K key,
                       V defaultValue);

    @Nonnull
    Holder<V> mappedFind(@Nonnull T mapping,
                         @Nonnull K key);
}
