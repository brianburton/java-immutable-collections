package org.javimmutable.collections.tree;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

import static org.javimmutable.collections.MapEntry.entry;

/**
 * ¬ * A Node containing one value and no children.
 */
public class LeafNode<K, V>
    extends AbstractNode<K, V>
{
    private final K key;
    private final V value;

    LeafNode(@Nonnull K key,
             @Nullable V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    boolean containsKey(@Nonnull Comparator<K> comp,
                        @Nonnull K key)
    {
        return isMatch(comp, key);
    }

    @Override
    V get(@Nonnull Comparator<K> comp,
          @Nonnull K key,
          V defaultValue)
    {
        return isMatch(comp, key) ? value : defaultValue;
    }

    @Nonnull
    @Override
    Holder<V> find(@Nonnull Comparator<K> comp,
                   @Nonnull K key)
    {
        if (isMatch(comp, key)) {
            return Holders.of(value);
        } else {
            return Holders.of();
        }
    }

    @Nonnull
    @Override
    Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                                @Nonnull K key)
    {
        if (isMatch(comp, key)) {
            return Holders.of(asEntry());
        } else {
            return Holders.of();
        }
    }

    @Override
    boolean isEmpty()
    {
        return false;
    }

    @Override
    int size()
    {
        return 1;
    }

    @Nonnull
    @Override
    AbstractNode<K, V> assign(@Nonnull Comparator<K> comp,
                              @Nonnull K key,
                              @Nullable V value)
    {
        if (isMatch(comp, key)) {
            if (this.value == value) {
                return this;
            } else {
                return new LeafNode<>(this.key, value);
            }
        } else {
            return ValueNode.instance(comp, this.key, this.value, key, value);
        }
    }

    @Nonnull
    @Override
    AbstractNode<K, V> delete(@Nonnull Comparator<K> comp,
                              @Nonnull K key)
    {
        if (isMatch(comp, key)) {
            return FringeNode.instance();
        } else {
            return this;
        }
    }

    @Nonnull
    @Override
    AbstractNode<K, V> update(@Nonnull Comparator<K> comp,
                              @Nonnull K key,
                              @Nonnull Func1<Holder<V>, V> generator)
    {
        if (isMatch(comp, key)) {
            final V value = generator.apply(Holders.of(this.value));
            if (this.value == value) {
                return this;
            } else {
                return new LeafNode<>(this.key, value);
            }
        } else {
            final V value = generator.apply(Holders.of());
            return ValueNode.instance(comp, this.key, this.value, key, value);
        }
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteLeftmost()
    {
        return new DeleteResult<>(key, value, FringeNode.instance());
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteRightmost()
    {
        return new DeleteResult<>(key, value, FringeNode.instance());
    }

    @Override
    int depth()
    {
        return 1;
    }

    @Nonnull
    @Override
    K key()
    {
        return key;
    }

    @Nullable
    @Override
    V value()
    {
        return value;
    }

    @Nonnull
    @Override
    AbstractNode<K, V> left()
    {
        return FringeNode.instance();
    }

    @Nonnull
    @Override
    AbstractNode<K, V> right()
    {
        return FringeNode.instance();
    }

    @Override
    void checkInvariants(@Nonnull Comparator<K> comp)
    {
    }

    @Override
    void forEach(@Nonnull Proc2<K, V> proc)
    {
        proc.apply(key, value);
    }

    @Override
    <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        proc.apply(key, value);
    }

    @Override
    <R> R reduce(R sum,
                 @Nonnull Sum2<K, V, R> proc)
    {
        return proc.apply(sum, key, value);
    }

    @Override
    <R, E extends Exception> R reduceThrows(R sum,
                                            @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return proc.apply(sum, key, value);
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                             int offset,
                                                                             int limit)
    {
        return GenericIterator.valueState(parent, asEntry());
    }

    @Override
    public int iterableSize()
    {
        return 1;
    }

    private boolean isMatch(@Nonnull Comparator<K> comp,
                            @Nonnull K key)
    {
        return comp.compare(key, this.key) == 0;
    }

    private JImmutableMap.Entry<K, V> asEntry()
    {
        return entry(key, value);
    }
}
