package org.javimmutable.collections.tree;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.iterators.IteratorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

public abstract class AbstractNode<K, V>
    implements Node<K, V>
{
    @Nonnull
    @Override
    public abstract AbstractNode<K, V> assign(@Nonnull Comparator<K> comp,
                                              @Nonnull K key,
                                              @Nullable V value);

    @Nonnull
    @Override
    public abstract AbstractNode<K, V> delete(@Nonnull Comparator<K> comp,
                                              @Nonnull K key);

    @Nonnull
    @Override
    public abstract AbstractNode<K, V> update(@Nonnull Comparator<K> comp,
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

    abstract AbstractNode<K, V> leftWeighted();

    abstract AbstractNode<K, V> rightWeighted();

    public abstract void checkInvariants(@Nonnull Comparator<K> comp);

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
    }

    @Override
    public boolean equals(Object obj)
    {
        return IteratorHelper.iteratorEquals(iterator(), ((Node)obj).iterator());
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
