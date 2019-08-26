package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

@Immutable
class FringeNode<K, V>
    extends Node<K, V>
{
    private static final FringeNode INSTANCE = new FringeNode();

    private FringeNode()
    {
    }

    @SuppressWarnings("unchecked")
    static <K, V> Node<K, V> instance()
    {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public Node<K, V> assign(@Nonnull Comparator<K> comp,
                             @Nonnull K key,
                             @Nullable V value)
    {
        return new ValueNode<>(key, value, this, this);
    }

    @Nonnull
    @Override
    public Node<K, V> update(@Nonnull Comparator<K> comp,
                             @Nonnull K key,
                             @Nonnull Func1<Holder<V>, V> generator)
    {
        return new ValueNode<>(key, generator.apply(Holders.of()), this, this);
    }

    @Nonnull
    @Override
    public Node<K, V> delete(@Nonnull Comparator<K> comp,
                             @Nonnull K key)
    {
        return this;
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteLeftmost()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteRightmost()
    {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    V get(@Nonnull Comparator<K> comp,
          @Nonnull K key)
    {
        return null;
    }

    @Nullable
    @Override
    public V getOr(@Nonnull Comparator<K> comp,
                   @Nonnull K key,
                   V defaultValue)
    {
        return defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull Comparator<K> comp,
                          @Nonnull K key)
    {
        return Holders.of();
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                         @Nonnull K key)
    {
        return Holders.of();
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Nonnull
    @Override
    public K getKey()
    {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public V getValue()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    Node<K, V> left()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    Node<K, V> right()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    Node<K, V> leftWeighted()
    {
        return this;
    }

    @Override
    Node<K, V> rightWeighted()
    {
        return this;
    }

    @Override
    void checkInvariants(@Nonnull Comparator<K> comp)
    {
    }

    @Nonnull
    @Override
    public SplitableIterator<Entry<K, V>> iterator()
    {
        return EmptyIterator.of();
    }

    @Nonnull
    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return StandardCursor.of();
    }
}
