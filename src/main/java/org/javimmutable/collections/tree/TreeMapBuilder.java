package org.javimmutable.collections.tree;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.MapEntry;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@ThreadSafe
class TreeMapBuilder<K, V>
    implements JImmutableMap.Builder<K, V>
{
    private final List<Entry<K, V>> values = new ArrayList<>();
    private final Comparator<K> comparator;

    TreeMapBuilder(@Nonnull Comparator<K> comparator)
    {
        this.comparator = comparator;
    }

    @Nonnull
    @Override
    public synchronized JImmutableMap.Builder<K, V> add(@Nonnull K key,
                                                        V value)
    {
        values.add(MapEntry.of(key, value));
        return this;
    }

    @Nonnull
    @Override
    public synchronized JImmutableMap<K, V> build()
    {
        if (values.isEmpty()) {
            return JImmutableTreeMap.of(comparator);
        } else {
            values.sort((a, b) -> comparator.compare(a.getKey(), b.getKey()));
            final AbstractNode<K, V> root = buildTree(values, 0, values.size());
            return new JImmutableTreeMap<>(comparator, root);
        }
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
