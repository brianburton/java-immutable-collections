package org.javimmutable.collections.array.int_trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;

public abstract class TrieNode<T>
{
    public static final int ROOT_SHIFT = 30;

    public abstract boolean isEmpty();

    public abstract T getValueOr(int shift,
                                 int index,
                                 T defaultValue);

    public abstract <K, V> V getValueOr(int shift,
                                        int index,
                                        K key,
                                        Transforms<T, K, V> transforms,
                                        V defaultValue);

    public abstract Holder<T> find(int shift,
                                   int index);

    public abstract <K, V> Holder<V> find(int shift,
                                          int index,
                                          K key,
                                          Transforms<T, K, V> transforms);

    public abstract TrieNode<T> assign(int shift,
                                       int index,
                                       T value,
                                       MutableDelta sizeDelta);

    public abstract <K, V> TrieNode<T> assign(int shift,
                                              int index,
                                              K key,
                                              V value,
                                              Transforms<T, K, V> transforms,
                                              MutableDelta sizeDelta);

    public abstract TrieNode<T> delete(int shift,
                                       int index,
                                       MutableDelta sizeDelta);

    public abstract <K, V> TrieNode<T> delete(int shift,
                                              int index,
                                              K key,
                                              Transforms<T, K, V> transforms,
                                              MutableDelta sizeDelta);

    public abstract int getShift();

    public TrieNode<T> trimmedToMinimumDepth()
    {
        return this;
    }

    public TrieNode<T> paddedToMinimumDepthForShift(int shift)
    {
        TrieNode<T> node = this;
        int nodeShift = node.getShift();
        while (nodeShift < shift) {
            nodeShift += 5;
            node = SingleBranchTrieNode.forBranchIndex(nodeShift, 0, node);
        }
        return node;
    }

    public Cursor<JImmutableMap.Entry<Integer, T>> signedOrderEntryCursor()
    {
        return anyOrderEntryCursor();
    }

    public <K, V> Cursor<JImmutableMap.Entry<K, V>> signedOrderEntryCursor(Transforms<T, K, V> transforms)
    {
        return anyOrderEntryCursor(transforms);
    }

    public abstract Cursor<JImmutableMap.Entry<Integer, T>> anyOrderEntryCursor();

    public abstract <K, V> Cursor<JImmutableMap.Entry<K, V>> anyOrderEntryCursor(Transforms<T, K, V> transforms);

    public Cursor<T> signedOrderValueCursor()
    {
        return anyOrderValueCursor();
    }

    public abstract Cursor<T> anyOrderValueCursor();

    public static int shiftForIndex(int index)
    {
        switch (Integer.numberOfLeadingZeros(index)) {
        case 0:
        case 1:
            return 30;

        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
            return 25;

        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
            return 20;

        case 12:
        case 13:
        case 14:
        case 15:
        case 16:
            return 15;

        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
            return 10;

        case 22:
        case 23:
        case 24:
        case 25:
        case 26:
            return 5;

        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
            return 0;
        }
        throw new IllegalArgumentException();
    }
}
