package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.array.int_trie.EmptyTrieNode;
import org.javimmutable.collections.array.int_trie.Transforms;
import org.javimmutable.collections.array.int_trie.TrieNode;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.tree.TreeNode;

public class TrieMap<T, K, V>
        extends AbstractJImmutableMap<K, V>
{
    private final TrieNode<T> root;
    private final int size;
    private final Transforms<T, K, V> transforms;

    private TrieMap(TrieNode<T> root,
                    int size,
                    Transforms<T, K, V> transforms)
    {
        this.root = root;
        this.size = size;
        this.transforms = transforms;
    }

    public static <K extends Comparable<K>, V> JImmutableMap<K, V> of()
    {
        return new TrieMap<TreeNode<K, V>, K, V>(EmptyTrieNode.<TreeNode<K, V>>of(), 0, new HashValueTreeTransforms2<K, V>());
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        return root.getValueOr(TrieNode.ROOT_SHIFT, key.hashCode(), key, transforms, defaultValue);
    }

    @Override
    public Holder<V> find(K key)
    {
        return root.find(TrieNode.ROOT_SHIFT, key.hashCode(), key, transforms);
    }

    @Override
    public Holder<Entry<K, V>> findEntry(K key)
    {
        Holder<V> value = find(key);
        if (value.isEmpty()) {
            return Holders.of();
        } else {
            return Holders.<Entry<K, V>>of(MapEntry.of(key, value.getValue()));
        }
    }

    @Override
    public JImmutableMap<K, V> assign(K key,
                                      V value)
    {
        MutableDelta sizeDelta = new MutableDelta();
        TrieNode<T> newRoot = root.assign(TrieNode.ROOT_SHIFT, key.hashCode(), key, value, transforms, sizeDelta);
        if (newRoot == root) {
            return this;
        } else {
            return new TrieMap<T, K, V>(newRoot, size + sizeDelta.getValue(), transforms);
        }
    }

    @Override
    public JImmutableMap<K, V> delete(K key)
    {
        MutableDelta sizeDelta = new MutableDelta();
        TrieNode<T> newRoot = root.delete(TrieNode.ROOT_SHIFT, key.hashCode(), key, transforms, sizeDelta);
        if (newRoot == root) {
            return this;
        } else {
            return new TrieMap<T, K, V>(newRoot, size + sizeDelta.getValue(), transforms);
        }
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public JImmutableMap<K, V> deleteAll()
    {
        return new TrieMap<T, K, V>(EmptyTrieNode.<T>of(), 0, transforms);
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return root.anyOrderEntryCursor(transforms);
    }
}
