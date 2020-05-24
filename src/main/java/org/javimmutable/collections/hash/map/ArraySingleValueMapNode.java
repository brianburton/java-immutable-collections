package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ArraySingleValueMapNode<K, V>
    implements ArrayMapNode<K, V>
{
    private final K key;
    private final V value;

    public ArraySingleValueMapNode(K key,
                                   V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public int size(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return 1;
    }

    @Override
    public V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull K key,
                        V defaultValue)
    {
        return key.equals(this.key) ? value : defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                          @Nonnull K key)
    {
        return key.equals(this.key) ? Holders.of(value) : Holders.of();
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                                     @Nonnull K key,
                                     V value)
    {
        if (!key.equals(this.key)) {
            return new ArrayMultiValueMapNode<>(collisionMap.update(collisionMap.single(this.key, this.value), key, value));
        } else if (value == this.value) {
            return this;
        } else {
            return new ArraySingleValueMapNode<>(this.key, value);
        }
    }

    @Nullable
    @Override
    public ArrayMapNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                                     @Nonnull K key)
    {
        return key.equals(this.key) ? null : this;
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<K> keys(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return GenericIterator.valueIterable(key);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<V> values(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return GenericIterator.valueIterable(value);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<JImmutableMap.Entry<K, V>> entries(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return GenericIterator.valueIterable(MapEntry.entry(key, value));
    }
}
