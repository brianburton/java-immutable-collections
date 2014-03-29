package org.javimmutable.collections.array.int_trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;

public abstract class TrieNode<T>
{
    static final int ROOT_SHIFT = 30;

    public abstract boolean isEmpty();

    public abstract T getValueOr(int shift,
                                 int index,
                                 T defaultValue);

    public abstract Holder<T> find(int shift,
                                   int index);

    public abstract TrieNode<T> assign(int shift,
                                       int index,
                                       T value,
                                       MutableDelta sizeDelta);

    public abstract TrieNode<T> delete(int shift,
                                       int index,
                                       MutableDelta sizeDelta);

    public Cursor<JImmutableMap.Entry<Integer, T>> signedOrderEntryCursor()
    {
        return anyOrderEntryCursor();
    }

    public abstract Cursor<JImmutableMap.Entry<Integer, T>> anyOrderEntryCursor();

    public Cursor<T> signedOrderValueCursor()
    {
        return anyOrderValueCursor();
    }

    public abstract Cursor<T> anyOrderValueCursor();

}
