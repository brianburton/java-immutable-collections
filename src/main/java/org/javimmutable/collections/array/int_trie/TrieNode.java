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

}
