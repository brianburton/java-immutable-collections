package org.javimmutable.collections.array.int_trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;

public class SingleBranchTrieNode<T>
        extends TrieNode<T>
{
    private final int shift;
    private final int branchIndex;
    private final TrieNode<T> child;

    private SingleBranchTrieNode(int shift,
                                 int branchIndex,
                                 TrieNode<T> child)
    {
        assert shift >= 0;
        this.shift = shift;
        this.branchIndex = branchIndex;
        this.child = child;
    }

    public static <T> SingleBranchTrieNode<T> forIndex(int shift,
                                                       int index,
                                                       TrieNode<T> child)
    {
        final int childIndex = (index >>> shift) & 0x1f;
        return new SingleBranchTrieNode<T>(shift, childIndex, child);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public T getValueOr(int shift,
                        int index,
                        T defaultValue)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        return (this.branchIndex == branchIndex) ? child.getValueOr(shift - 5, index, defaultValue) : defaultValue;
    }

    @Override
    public <K, V> V getValueOr(int shift,
                               int index,
                               K key,
                               Transforms<T, K, V> transforms,
                               V defaultValue)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        return (this.branchIndex == branchIndex) ? child.getValueOr(shift - 5, index, key, transforms, defaultValue) : defaultValue;
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        return (this.branchIndex == branchIndex) ? child.find(shift - 5, index) : Holders.<T>of();
    }

    @Override
    public <K, V> Holder<V> find(int shift,
                                 int index,
                                 K key,
                                 Transforms<T, K, V> transforms)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        return (this.branchIndex == branchIndex) ? child.find(shift - 5, index, key, transforms) : Holders.<V>of();
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        if (this.branchIndex == branchIndex) {
            TrieNode<T> newChild = child.assign(shift - 5, index, value, sizeDelta);
            return (newChild == child) ? this : new SingleBranchTrieNode<T>(shift, branchIndex, newChild);
        } else {
            return MultiBranchTrieNode.forBranchIndex(shift, this.branchIndex, child).assign(shift, index, value, sizeDelta);
        }
    }

    @Override
    public <K, V> TrieNode<T> assign(int shift,
                                     int index,
                                     K key,
                                     V value,
                                     Transforms<T, K, V> transforms,
                                     MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        if (this.branchIndex == branchIndex) {
            TrieNode<T> newChild = child.assign(shift - 5, index, key, value, transforms, sizeDelta);
            return (newChild == child) ? this : new SingleBranchTrieNode<T>(shift, branchIndex, newChild);
        } else {
            return MultiBranchTrieNode.forBranchIndex(shift, this.branchIndex, child).assign(shift, index, key, value, transforms, sizeDelta);
        }
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        if (this.branchIndex != branchIndex) {
            return this;
        } else {
            final TrieNode<T> newChild = child.delete(shift - 5, index, sizeDelta);
            if (newChild == child) {
                return this;
            } else if (newChild.isEmpty()) {
                return EmptyTrieNode.of();
            } else {
                return new SingleBranchTrieNode<T>(shift, branchIndex, newChild);
            }
        }
    }

    @Override
    public <K, V> TrieNode<T> delete(int shift,
                                     int index,
                                     K key,
                                     Transforms<T, K, V> transforms,
                                     MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        if (this.branchIndex != branchIndex) {
            return this;
        } else {
            final TrieNode<T> newChild = child.delete(shift - 5, index, key, transforms, sizeDelta);
            if (newChild == child) {
                return this;
            } else if (newChild.isEmpty()) {
                return EmptyTrieNode.of();
            } else {
                return new SingleBranchTrieNode<T>(shift, branchIndex, newChild);
            }
        }
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> anyOrderEntryCursor()
    {
        return child.anyOrderEntryCursor();
    }

    @Override
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> anyOrderEntryCursor(Transforms<T, K, V> transforms)
    {
        return child.anyOrderEntryCursor(transforms);
    }

    @Override
    public Cursor<T> anyOrderValueCursor()
    {
        return child.anyOrderValueCursor();
    }
}
