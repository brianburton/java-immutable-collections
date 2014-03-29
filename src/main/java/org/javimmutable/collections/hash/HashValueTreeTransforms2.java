package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.array.int_trie.Transforms;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.TreeNode;

import java.util.Comparator;

public class HashValueTreeTransforms2<K extends Comparable<K>, V>
        implements Transforms<TreeNode<K, V>, K, V>
{
    private final Comparator<K> comparator = ComparableComparator.of();

    @Override
    public TreeNode<K, V> update(Holder<TreeNode<K, V>> leaf,
                                 K key,
                                 V value,
                                 MutableDelta delta)
    {
        if (leaf.isEmpty()) {
            return TreeNode.<K, V>of().assign(comparator, key, value, delta);
        } else {
            return leaf.getValue().assign(comparator, key, value, delta);
        }
    }

    @Override
    public Holder<TreeNode<K, V>> delete(TreeNode<K, V> leaf,
                                         K key,
                                         MutableDelta delta)
    {
        final TreeNode<K, V> newTree = leaf.delete(comparator, key, delta);
        return (newTree.isEmpty()) ? Holders.<TreeNode<K, V>>of() : Holders.of(newTree);
    }

    @Override
    public Holder<V> findValue(TreeNode<K, V> leaf,
                               K key)
    {
        return leaf.find(comparator, key);
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(TreeNode<K, V> leaf,
                                                       K key)
    {
        return leaf.findEntry(comparator, key);
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(TreeNode<K, V> leaf)
    {
        return leaf.cursor();
    }
}
