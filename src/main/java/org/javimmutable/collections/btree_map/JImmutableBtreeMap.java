package org.javimmutable.collections.btree_map;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.Conditions;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.tree.ComparableComparator;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JImmutableBtreeMap<K, V>
    extends AbstractJImmutableMap<K, V>
{
    private static final Comparator EMPTY_COMPARATOR = ComparableComparator.of();
    @SuppressWarnings("unchecked")
    private static final JImmutableBtreeMap EMPTY = new JImmutableBtreeMap(EMPTY_COMPARATOR, EmptyNode.of(), 0);

    private final Comparator<K> comparator;
    private final Node<K, V> root;
    private final int size;

    private JImmutableBtreeMap(@Nonnull Comparator<K> comparator,
                               @Nonnull Node<K, V> root,
                               int size)
    {
        this.comparator = comparator;
        this.root = root;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <K extends Comparable<K>, V> JImmutableBtreeMap<K, V> of()
    {
        return EMPTY;
    }

    @Nonnull
    public static <K, V> JImmutableBtreeMap<K, V> of(@Nonnull Comparator<K> comparator)
    {
        return new JImmutableBtreeMap<>(comparator, EmptyNode.of(), 0);
    }

    /**
     * Constructs a new map containing the same key/value pairs as map using a ComparableComparator
     * to compare the keys.
     */
    @Nonnull
    @Deprecated
    public static <K extends Comparable<K>, V> JImmutableBtreeMap<K, V> of(Map<K, V> map)
    {
        JImmutableBtreeMap<K, V> answer = of();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            answer = answer.assign(entry.getKey(), entry.getValue());
        }
        return answer;
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        Conditions.stopNull(key);
        return root.getValueOr(comparator, key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull K key)
    {
        Conditions.stopNull(key);
        return root.find(comparator, key);
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull K key)
    {
        Conditions.stopNull(key);
        return root.findEntry(comparator, key);
    }

    @Nonnull
    @Override
    public JImmutableBtreeMap<K, V> assign(@Nonnull K key,
                                           V value)
    {
        Conditions.stopNull(key);
        final UpdateResult<K, V> result = root.assign(comparator, key, value);
        switch (result.type) {
        case UNCHANGED:
            return this;
        case INPLACE:
            return new JImmutableBtreeMap<>(comparator, result.newNode, size + result.sizeDelta);
        case SPLIT:
            return new JImmutableBtreeMap<>(comparator, new BranchNode<>(result.newNode, result.extraNode), size + result.sizeDelta);
        default:
            throw new IllegalStateException("unknown UpdateResult.Type value");
        }
    }

    @Nonnull
    @Override
    public JImmutableBtreeMap<K, V> delete(@Nonnull K key)
    {
        Conditions.stopNull(key);
        final Node<K, V> newRoot = root.delete(comparator, key);
        if (newRoot == root) {
            return this;
        } else if (size == 1) {
            return deleteAll();
        } else {
            return new JImmutableBtreeMap<>(comparator, newRoot, size - 1);
        }
    }

    @Override
    public int size()
    {
        return size;
    }

    @Nonnull
    @Override
    public JImmutableBtreeMap<K, V> deleteAll()
    {
        return (comparator == EMPTY_COMPARATOR) ? EMPTY : new JImmutableBtreeMap<>(comparator, EmptyNode.of(), 0);
    }

    @Nonnull
    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return root.cursor();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @Nonnull
    @Override
    public SplitableIterator<Entry<K, V>> iterator()
    {
        return root.iterator();
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants(comparator);
    }

    @Nonnull
    public Comparator<K> getComparator()
    {
        return comparator;
    }

    @Nonnull
    List<K> getKeysList()
    {
        List<K> keys = new LinkedList<>();
        for (Entry<K, V> entry : this) {
            keys.add(entry.getKey());
        }
        return Collections.unmodifiableList(keys);
    }
}
