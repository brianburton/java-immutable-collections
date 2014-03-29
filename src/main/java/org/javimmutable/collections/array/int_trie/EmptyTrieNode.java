package org.javimmutable.collections.array.int_trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.StandardCursor;

public class EmptyTrieNode<T>
        extends TrieNode<T>
{
    private static final EmptyTrieNode EMPTY = new EmptyTrieNode();

    @SuppressWarnings("unchecked")
    static <T> EmptyTrieNode<T> instance()
    {
        return (EmptyTrieNode<T>)EMPTY;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    public T getValueOr(int shift,
                        int index,
                        T defaultValue)
    {
        return defaultValue;
    }

    @Override
    public <K, V> V getValueOr(int shift,
                               int index,
                               K key,
                               Transforms<T, K, V> transforms,
                               V defaultValue)
    {
        return defaultValue;
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        return Holders.of();
    }

    @Override
    public <K, V> Holder<V> find(int shift,
                                 int index,
                                 K key,
                                 Transforms<T, K, V> transforms)
    {
        return Holders.of();
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        sizeDelta.add(1);
        return LeafTrieNode.of(index, value);
    }

    @Override
    public <K, V> TrieNode<T> assign(int shift,
                                     int index,
                                     K key,
                                     V value,
                                     Transforms<T, K, V> transforms,
                                     MutableDelta sizeDelta)
    {
        return LeafTrieNode.of(index, transforms.update(Holders.<T>of(), key, value, sizeDelta));
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        return this;
    }

    @Override
    public <K, V> TrieNode<T> delete(int shift,
                                     int index,
                                     K key,
                                     Transforms<T, K, V> transforms,
                                     MutableDelta sizeDelta)
    {
        return this;
    }

    @Override
    public int getShift()
    {
        return 0;
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> anyOrderEntryCursor()
    {
        return StandardCursor.of();
    }

    @Override
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> anyOrderEntryCursor(Transforms<T, K, V> transforms)
    {
        return StandardCursor.of();
    }

    @Override
    public Cursor<T> anyOrderValueCursor()
    {
        return StandardCursor.of();
    }
}
