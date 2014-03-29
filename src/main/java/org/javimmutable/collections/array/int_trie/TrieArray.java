package org.javimmutable.collections.array.int_trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.AbstractJImmutableArray;
import org.javimmutable.collections.common.MutableDelta;

public class TrieArray<T>
        extends AbstractJImmutableArray<T>
{
    private final TrieNode<T> root;
    private final int size;

    private TrieArray(TrieNode<T> root,
                      int size)
    {
        this.root = root;
        this.size = size;
    }

    public static <T> TrieArray<T> of()
    {
        return new TrieArray<T>(EmptyTrieNode.<T>of(), 0);
    }

    @Override
    public T getValueOr(int index,
                        T defaultValue)
    {
        return root.getValueOr(TrieNode.ROOT_SHIFT, index, defaultValue);
    }

    @Override
    public Holder<T> find(int index)
    {
        return Holders.fromNullable(getValueOr(index, null));
    }

    @Override
    public JImmutableArray<T> assign(int index,
                                     T value)
    {
        MutableDelta sizeDelta = new MutableDelta();
        final TrieNode<T> newRoot = root.assign(TrieNode.ROOT_SHIFT, index, value, sizeDelta);
        return (newRoot == root) ? this : new TrieArray<T>(newRoot, size + sizeDelta.getValue());
    }

    @Override
    public JImmutableArray<T> delete(int index)
    {
        MutableDelta sizeDelta = new MutableDelta();
        final TrieNode<T> newRoot = root.delete(TrieNode.ROOT_SHIFT, index, sizeDelta);
        return (newRoot == root) ? this : new TrieArray<T>(newRoot, size + sizeDelta.getValue());
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public JImmutableArray<T> deleteAll()
    {
        return of();
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return root.signedOrderEntryCursor();
    }
}
