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
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return defaultValue;
        } else {
            return root.getValueOr(root.getShift(), index, defaultValue);
        }
    }

    @Override
    public Holder<T> find(int index)
    {
        if (root.getShift() < TrieNode.shiftForIndex(index)) {
            return Holders.of();
        } else {
            return root.find(root.getShift(), index);
        }
    }

    @Override
    public JImmutableArray<T> assign(int index,
                                     T value)
    {
        MutableDelta sizeDelta = new MutableDelta();
        TrieNode<T> newRoot = root.paddedToMinimumDepthForShift(TrieNode.shiftForIndex(index));
        newRoot = newRoot.assign(newRoot.getShift(), index, value, sizeDelta);
        return (newRoot == root) ? this : new TrieArray<T>(newRoot, size + sizeDelta.getValue());
    }

    @Override
    public JImmutableArray<T> delete(int index)
    {
        MutableDelta sizeDelta = new MutableDelta();
        final TrieNode<T> newRoot = root.delete(root.getShift(), index, sizeDelta).trimmedToMinimumDepth();
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
