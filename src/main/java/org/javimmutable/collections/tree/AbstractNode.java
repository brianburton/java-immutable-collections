package org.javimmutable.collections.tree;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.iterators.IteratorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

abstract class AbstractNode<K, V>
    implements SplitableIterable<Entry<K, V>>,
               GenericIterator.Iterable<Entry<K, V>>,
               CollisionMap.Node
{
    abstract V get(@Nonnull Comparator<K> comp,
                   @Nonnull K key,
                   V defaultValue);

    @Nonnull
    abstract Holder<V> find(@Nonnull Comparator<K> comp,
                            @Nonnull K key);

    @Nonnull
    abstract Holder<Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                           @Nonnull K key);

    abstract boolean isEmpty();

    abstract int size();

    @Nonnull
    abstract AbstractNode<K, V> assign(@Nonnull Comparator<K> comp,
                                       @Nonnull K key,
                                       @Nullable V value);

    @Nonnull
    abstract AbstractNode<K, V> delete(@Nonnull Comparator<K> comp,
                                       @Nonnull K key);

    @Nonnull
    abstract AbstractNode<K, V> update(@Nonnull Comparator<K> comp,
                                       @Nonnull K key,
                                       @Nonnull Func1<Holder<V>, V> generator);

    @Nonnull
    abstract DeleteResult<K, V> deleteLeftmost();

    @Nonnull
    abstract DeleteResult<K, V> deleteRightmost();

    abstract int depth();

    @Nonnull
    abstract K key();

    @Nullable
    abstract V value();

    @Nonnull
    abstract AbstractNode<K, V> left();

    @Nonnull
    abstract AbstractNode<K, V> right();

    @Nonnull
    abstract AbstractNode<K, V> leftWeighted();

    @Nonnull
    abstract AbstractNode<K, V> rightWeighted();

    abstract void checkInvariants(@Nonnull Comparator<K> comp);

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof AbstractNode) && IteratorHelper.iteratorEquals(iterator(), ((AbstractNode)obj).iterator());
    }

    @Nonnull
    public SplitableIterator<Entry<K, V>> iterator()
    {
        return new GenericIterator<>(this, 0, size());
    }

    static class DeleteResult<K, V>
    {
        final K key;
        final V value;
        final AbstractNode<K, V> remainder;

        DeleteResult(@Nonnull K key,
                     V value,
                     @Nonnull AbstractNode<K, V> remainder)
        {
            this.key = key;
            this.value = value;
            this.remainder = remainder;
        }

        @Nonnull
        DeleteResult<K, V> withRemainder(@Nonnull AbstractNode<K, V> remainder)
        {
            return new DeleteResult<>(key, value, remainder);
        }
    }
}
