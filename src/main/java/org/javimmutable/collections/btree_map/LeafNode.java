package org.javimmutable.collections.btree_map;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;
import java.util.Objects;

@Immutable
public class LeafNode<K, V>
    implements Node<K, V>,
               JImmutableMap.Entry<K, V>,
               Holder<V>
{
    private final K key;
    private final V value;

    public LeafNode(@Nonnull K key,
                    V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public K baseKey()
    {
        return key;
    }

    @Override
    public int childCount()
    {
        return 1;
    }

    @Override
    public int valueCount()
    {
        return 1;
    }

    @Override
    public V getValueOr(@Nonnull Comparator<K> comparator,
                        @Nonnull K key,
                        V defaultValue)
    {
        return comparator.compare(this.key, key) == 0 ? value : defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull Comparator<K> comparator,
                          @Nonnull K key)
    {
        return comparator.compare(this.key, key) == 0 ? this : Holders.of();
    }

    @Nonnull
    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull Comparator<K> comparator,
                                                       @Nonnull K key)
    {
        return comparator.compare(this.key, key) == 0 ? Holders.of(this) : Holders.of();
    }

    @Nonnull
    @Override
    public UpdateResult<K, V> assign(@Nonnull Comparator<K> comparator,
                                     @Nonnull K key,
                                     V value)
    {
        final LeafNode<K, V> newLeaf = new LeafNode<>(key, value);
        final int diff = comparator.compare(this.key, key);
        if (diff == 0) {
            return (this.value == value) ? UpdateResult.createUnchanged() : UpdateResult.createInPlace(newLeaf, 0);
        } else {
            return (diff < 0) ? UpdateResult.createSplit(this, newLeaf, 1) : UpdateResult.createSplit(newLeaf, this, 1);
        }
    }

    @Nonnull
    @Override
    public Node<K, V> delete(@Nonnull Comparator<K> comparator,
                             @Nonnull K key)
    {
        final int diff = comparator.compare(this.key, key);
        return (diff == 0) ? EmptyNode.of() : this;
    }

    @Nonnull
    @Override
    public Node<K, V> mergeChildren(@Nonnull Node<K, V> sibling)
    {
        return new BranchNode<>(this, sibling);
    }

    @Nonnull
    @Override
    public Tuple2<Node<K, V>, Node<K, V>> distributeChildren(@Nonnull Node<K, V> sibling)
    {
        return Tuple2.of(this, sibling);
    }

    @Nonnull
    @Override
    public Node<K, V> compress()
    {
        return this;
    }

    @Override
    public int depth()
    {
        return 0;
    }

    @Nonnull
    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return SingleValueCursor.of(this);
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
    {
        return SingleValueIterator.of(this);
    }

    @Override
    public void checkInvariants(@Nonnull Comparator<K> comparator)
    {
    }

    @Nonnull
    @Override
    public K getKey()
    {
        return key;
    }

    @Override
    public V getValue()
    {
        return value;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean isFilled()
    {
        return true;
    }

    @Override
    public V getValueOrNull()
    {
        return value;
    }

    @Override
    public V getValueOr(V defaultValue)
    {
        return value;
    }

    @Override
    public String toString()
    {
        return "<" + key + "," + value + ">";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LeafNode<?, ?> leafNode = (LeafNode<?, ?>)o;
        return Objects.equals(key, leafNode.key) &&
               Objects.equals(value, leafNode.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(key, value);
    }
}
