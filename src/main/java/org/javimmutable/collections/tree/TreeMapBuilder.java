package org.javimmutable.collections.tree;

import org.javimmutable.collections.JImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

@ThreadSafe
class TreeMapBuilder<K, V>
    implements JImmutableMap.Builder<K, V>
{
    private final Comparator<K> comparator;
    private final Map<K, V> values;

    TreeMapBuilder(@Nonnull Comparator<K> comparator)
    {
        this.comparator = comparator;
        values = new TreeMap<>(comparator);
    }

    @Nonnull
    @Override
    public synchronized JImmutableMap<K, V> build()
    {
        if (values.isEmpty()) {
            return JImmutableTreeMap.of(comparator);
        } else {
            final List<Entry<K, V>> sorted = new ArrayList<>(values.entrySet());
            final AbstractNode<K, V> root = buildTree(sorted, 0, sorted.size());
            return new JImmutableTreeMap<>(comparator, root);
        }
    }

    @Nonnull
    @Override
    public synchronized JImmutableMap.Builder<K, V> add(@Nonnull K key,
                                                        V value)
    {
        values.put(key, value);
        return this;
    }

    public synchronized int size()
    {
        return values.size();
    }

    private AbstractNode<K, V> buildTree(@Nonnull List<Entry<K, V>> values,
                                         int offset,
                                         int limit)
    {
        assert limit > offset;
        int count = limit - offset;
        if (count == 1) {
            final Entry<K, V> e = values.get(offset);
            return ValueNode.instance(e.getKey(), e.getValue());
        } else if (count == 2) {
            final Entry<K, V> a = values.get(offset);
            final Entry<K, V> b = values.get(offset + 1);
            final AbstractNode<K, V> right = ValueNode.instance(b.getKey(), b.getValue());
            return new ValueNode<>(a.getKey(), a.getValue(), FringeNode.instance(), right);
        } else {
            final int middle = offset + count / 2;
            final Entry<K, V> e = values.get(middle);
            final AbstractNode<K, V> left = buildTree(values, offset, middle);
            final AbstractNode<K, V> right = buildTree(values, middle + 1, limit);
            return new ValueNode<>(e.getKey(), e.getValue(), left, right);
        }
    }
}
