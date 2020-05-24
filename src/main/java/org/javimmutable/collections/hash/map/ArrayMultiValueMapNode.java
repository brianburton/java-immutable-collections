package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ArrayMultiValueMapNode<K, V>
    implements ArrayMapNode<K, V>
{
    @Nonnull
    private final CollisionMap.Node node;

    public ArrayMultiValueMapNode(@Nonnull CollisionMap.Node node)
    {
        this.node = node;
    }

    @Override
    public int size(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return collisionMap.size(node);
    }

    @Override
    public V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull K key,
                        V defaultValue)
    {
        return collisionMap.getValueOr(node, key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                          @Nonnull K key)
    {
        return collisionMap.findValue(node, key);
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                                     @Nonnull K key,
                                     V value)
    {
        final CollisionMap.Node newNode = collisionMap.update(node, key, value);
        if (newNode == value) {
            return this;
        } else {
            return new ArrayMultiValueMapNode<>(newNode);
        }
    }

    @Nullable
    @Override
    public ArrayMapNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                                     @Nonnull K key)
    {
        final CollisionMap.Node newNode = collisionMap.delete(node, key);
        if (newNode == node) {
            return this;
        } else if (collisionMap.size(newNode) == 0) {
            return null;
        } else {
            return new ArrayMultiValueMapNode<>(newNode);
        }
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<K> keys(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return GenericIterator.transformIterable(collisionMap.genericIterable(node), JImmutableMap.Entry::getKey);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<V> values(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return GenericIterator.transformIterable(collisionMap.genericIterable(node), JImmutableMap.Entry::getValue);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<JImmutableMap.Entry<K, V>> entries(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return collisionMap.genericIterable(node);
    }
}
