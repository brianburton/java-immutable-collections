package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.array.ArrayValueMapper;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ArrayMapper<K, V>
    implements ArrayValueMapper<K, V, ArrayMapNode<K, V>>
{
    private final CollisionMap<K, V> collisionMap;

    public ArrayMapper(@Nonnull CollisionMap<K, V> collisionMap)
    {
        this.collisionMap = collisionMap;
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> mappedAssign(@Nonnull K key,
                                           V value)
    {
        return new ArraySingleValueMapNode<>(key, value);
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> mappedAssign(@Nonnull ArrayMapNode<K, V> current,
                                           @Nonnull K key,
                                           V value)
    {
        return current.assign(collisionMap, key, value);
    }

    @Nullable
    @Override
    public ArrayMapNode<K, V> mappedDelete(@Nonnull ArrayMapNode<K, V> current,
                                           @Nonnull K key)
    {
        return current.delete(collisionMap, key);
    }

    @Override
    public int mappedSize(@Nonnull ArrayMapNode<K, V> mapping)
    {
        return mapping.size(collisionMap);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<K> mappedKeys(@Nonnull ArrayMapNode<K, V> mapping)
    {
        return mapping.keys(collisionMap);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<V> mappedValues(@Nonnull ArrayMapNode<K, V> mapping)
    {
        return mapping.values(collisionMap);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<JImmutableMap.Entry<K, V>> mappedEntries(@Nonnull ArrayMapNode<K, V> mapping)
    {
        return mapping.entries(collisionMap);
    }
}
