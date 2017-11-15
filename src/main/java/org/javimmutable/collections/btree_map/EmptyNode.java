package org.javimmutable.collections.btree_map;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

@Immutable
public class EmptyNode<K, V>
    implements Node<K, V>
{
    @SuppressWarnings("unchecked")
    static final EmptyNode INSTANCE = new EmptyNode();

    @SuppressWarnings("unchecked")
    public static <K, V> Node<K, V> of()
    {
        return INSTANCE;
    }

    @Nullable
    @Override
    public K baseKey()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int childCount()
    {
        return 0;
    }

    @Override
    public int valueCount()
    {
        return 0;
    }

    @Override
    public V getValueOr(@Nonnull Comparator<K> comparator,
                        @Nonnull K key,
                        V defaultValue)
    {
        return defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull Comparator<K> comparator,
                          @Nonnull K key)
    {
        return Holders.of();
    }

    @Nonnull
    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull Comparator<K> comparator,
                                                       @Nonnull K key)
    {
        return Holders.of();
    }

    @Nonnull
    @Override
    public UpdateResult<K, V> assign(@Nonnull Comparator<K> comparator,
                                     @Nonnull K key,
                                     V value)
    {
        return UpdateResult.createInPlace(new LeafNode<>(key, value), 1);
    }

    @Nonnull
    @Override
    public Node<K, V> delete(@Nonnull Comparator<K> comparator,
                             @Nonnull K key)
    {
        return this;
    }

    @Nonnull
    @Override
    public Node<K, V> mergeChildren(@Nonnull Node<K, V> sibling)
    {
        return sibling;
    }

    @Nonnull
    @Override
    public Tuple2<Node<K, V>, Node<K, V>> distributeChildren(@Nonnull Node<K, V> sibling)
    {
        throw new UnsupportedOperationException();
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

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Nonnull
    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return StandardCursor.of();
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
    {
        return EmptyIterator.of();
    }

    @Override
    public void checkInvariants(@Nonnull Comparator<K> comparator)
    {
    }
}
