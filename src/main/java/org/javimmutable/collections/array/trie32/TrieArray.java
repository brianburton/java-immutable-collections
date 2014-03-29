package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Indexed;
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
        return new TrieArray<T>(TrieNode.<T>of(), 0);
    }

    public static <T> JImmutableArray<T> of(Indexed<T> source,
                                            int offset,
                                            int limit)
    {
        final int size = limit - offset;
        if (size == 0) {
            return of();
        }

        // small lists can be directly constructed from a single leaf array
        if (size <= 32) {
            return new TrieArray<T>(TrieNode.<T>fromSource(0, source, offset, limit), size);
        }

        // first construct an array containing a single level of arrays of leaves
        final int numBranches = Math.min(32, (limit - offset + 31) / 32);
        @SuppressWarnings("unchecked") final TrieNode<T>[] branchArray = (TrieNode<T>[])new TrieNode[numBranches];
        int index = 0;
        for (int b = 0; b < numBranches; ++b) {
            int branchSize = Math.min(32, limit - offset);
            branchArray[b] = TrieNode.fromSource(index, source, offset, limit);
            offset += branchSize;
            index += branchSize;
        }

        // then add any extras left over above that size
        JImmutableArray<T> array = new TrieArray<T>(MultiBranchTrieNode.forEntries(5, branchArray), index);
        while (offset < limit) {
            array = array.assign(index++, source.get(offset++));
        }
        return array;
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
