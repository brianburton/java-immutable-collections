package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ArrayMapNode<K, V>
{
    int size(@Nonnull CollisionMap<K, V> collisionMap);

    V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                 @Nonnull K key,
                 V defaultValue);

    @Nonnull
    Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                   @Nonnull K key);

    @Nonnull
    ArrayMapNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                              @Nonnull K key,
                              V value);

    @Nullable
    ArrayMapNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                              @Nonnull K key);

    @Nonnull
    GenericIterator.Iterable<K> keys(@Nonnull CollisionMap<K, V> collisionMap);

    @Nonnull
    GenericIterator.Iterable<V> values(@Nonnull CollisionMap<K, V> collisionMap);

    @Nonnull
    GenericIterator.Iterable<JImmutableMap.Entry<K, V>> entries(@Nonnull CollisionMap<K, V> collisionMap);
}
