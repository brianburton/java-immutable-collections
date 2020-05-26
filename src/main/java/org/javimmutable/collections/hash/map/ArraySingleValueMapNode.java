package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
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
        final K thisKey = this.key;
        final V thisValue = this.value;
        if (!key.equals(thisKey)) {
            return new ArrayMultiValueMapNode<>(collisionMap.update(collisionMap.single(thisKey, thisValue), key, value));
        } else if (value == thisValue) {
            return this;
        } else {
            return new ArraySingleValueMapNode<>(thisKey, value);
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

    @Override
    public void forEach(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull Proc2<K, V> proc)
    {
        proc.apply(key, value);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                    @Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        proc.apply(key, value);
    }
}
