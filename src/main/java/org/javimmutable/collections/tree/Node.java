package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.iterators.IteratorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

public abstract class Node<K, V>
    implements SplitableIterable<Entry<K, V>>,
               Cursorable<Entry<K, V>>
{
    public static <K, V> Node<K, V> empty()
    {
        return FringeNode.instance();
    }

    public static <K, V> Node<K, V> single(K key,
                                           V value)
    {
        return new ValueNode<>(key, value, FringeNode.instance(), FringeNode.instance());
    }

    @Nonnull
    public abstract Node<K, V> assign(@Nonnull Comparator<K> comp,
                                      @Nonnull K key,
                                      @Nullable V value);

    @Nonnull
    public abstract Node<K, V> delete(@Nonnull Comparator<K> comp,
                                      @Nonnull K key);

    @Nonnull
    public abstract Node<K, V> update(@Nonnull Comparator<K> comp,
                                      @Nonnull K key,
                                      @Nonnull Func1<Holder<V>, V> generator);

    @Nonnull
    abstract DeleteResult<K, V> deleteLeftmost();

    @Nonnull
    abstract DeleteResult<K, V> deleteRightmost();

    public abstract V get(@Nonnull Comparator<K> comp,
                          @Nonnull K key,
                          V defaultValue);

    @Nonnull
    public abstract Holder<V> find(@Nonnull Comparator<K> comp,
                                   @Nonnull K key);

    @Nonnull
    public abstract Holder<Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                                  @Nonnull K key);

    public abstract boolean isEmpty();

    abstract int depth();

    public abstract int size();

    @Nonnull
    abstract K key();

    @Nullable
    abstract V value();

    @Nonnull
    abstract Node<K, V> left();

    @Nonnull
    abstract Node<K, V> right();

    abstract Node<K, V> leftWeighted();

    abstract Node<K, V> rightWeighted();

    abstract void checkInvariants(@Nonnull Comparator<K> comp);

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
        final Node<K, V> remainder;

        DeleteResult(@Nonnull K key,
                     V value,
                     @Nonnull Node<K, V> remainder)
        {
            this.key = key;
            this.value = value;
            this.remainder = remainder;
        }

        @Nonnull
        DeleteResult<K, V> withRemainder(@Nonnull Node<K, V> remainder)
        {
            return new DeleteResult<>(key, value, remainder);
        }
    }
}
