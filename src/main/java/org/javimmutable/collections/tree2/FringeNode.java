package org.javimmutable.collections.tree2;

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
    Node<K, V> assign(@Nonnull Comparator<K> comp,
                      @Nonnull K key,
                      @Nullable V value)
    {
        return new ValueNode<>(key, value, this, this);
    }

    @Nonnull
    @Override
    Node<K, V> update(@Nonnull Comparator<K> comp,
                      @Nonnull K key,
                      @Nonnull Func1<Holder<V>, V> generator)
    {
        return new ValueNode<>(key, generator.apply(Holders.of()), this, this);
    }

    @Nonnull
    @Override
    Node<K, V> delete(@Nonnull Comparator<K> comp,
                      @Nonnull K key)
    {
        return this;
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteLeast()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteGreatest()
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

    @Override
    V getOr(@Nonnull Comparator<K> comp,
            @Nonnull K key,
            V defaultValue)
    {
        return defaultValue;
    }

    @Nonnull
    @Override
    Holder<V> find(@Nonnull Comparator<K> comp,
                   @Nonnull K key)
    {
        return Holders.of();
    }

    @Nonnull
    @Override
    Holder<Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                  @Nonnull K key)
    {
        return Holders.of();
    }

    @Override
    boolean isEmpty()
    {
        return true;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    int size()
    {
        return 0;
    }

    @Nonnull
    @Override
    K getKey()
    {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    V getValue()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    Entry<K, V> entry()
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
