package org.javimmutable.collections.tree2;

import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.SplitableIterable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

abstract class Node<K, V>
    implements SplitableIterable<Entry<K, V>>,
               Cursorable<Entry<K, V>>
{
    @Nonnull
    abstract Node<K, V> assign(@Nonnull Comparator<K> comp,
                               @Nonnull K key,
                               @Nullable V value);

    @Nonnull
    abstract Node<K, V> delete(@Nonnull Comparator<K> comp,
                               @Nonnull K key);

    @Nonnull
    abstract Node<K, V> update(@Nonnull Comparator<K> comp,
                               @Nonnull K key,
                               @Nonnull Func1<Holder<V>, V> generator);

    @Nonnull
    abstract DeleteResult<K, V> deleteLeftmost();

    @Nonnull
    abstract DeleteResult<K, V> deleteRightmost();

    @Nullable
    abstract V get(@Nonnull Comparator<K> comp,
                   @Nonnull K key);

    abstract V getOr(@Nonnull Comparator<K> comp,
                     @Nonnull K key,
                     V defaultValue);

    @Nonnull
    abstract Holder<V> find(@Nonnull Comparator<K> comp,
                            @Nonnull K key);

    @Nonnull
    abstract Holder<Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                           @Nonnull K key);

    abstract boolean isEmpty();

    abstract int depth();

    abstract int size();

    @Nonnull
    abstract K getKey();

    @Nullable
    abstract V getValue();

    @Nonnull
    abstract Node<K, V> left();

    @Nonnull
    abstract Node<K, V> right();

    abstract Node<K, V> leftWeighted();

    abstract Node<K, V> rightWeighted();

    abstract void checkInvariants(@Nonnull Comparator<K> comp);

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
