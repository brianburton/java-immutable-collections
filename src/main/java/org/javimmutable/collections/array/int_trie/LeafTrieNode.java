package org.javimmutable.collections.array.int_trie;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;

public class LeafTrieNode<T>
        extends TrieNode<T>
        implements Holder<T>
{
    private final int shift;
    private final int index;
    private final T value;

    public LeafTrieNode(int shift,
                        int index,
                        T value)
    {
        assert shift >= -5;
        this.shift = shift;
        this.index = index;
        this.value = value;
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
        return (this.index == index) ? value : defaultValue;
    }

    @Override
    public <K, V> V getValueOr(int shift,
                               int index,
                               K key,
                               Transforms<T, K, V> transforms,
                               V defaultValue)
    {
        assert this.shift == shift;
        return (this.index == index) ? transforms.findValue(value, key).getValueOr(defaultValue) : defaultValue;
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        assert this.shift == shift;
        return (this.index == index) ? this : Holders.<T>of();
    }

    @Override
    public <K, V> Holder<V> find(int shift,
                                 int index,
                                 K key,
                                 Transforms<T, K, V> transforms)
    {
        assert this.shift == shift;
        return (this.index == index) ? transforms.findValue(value, key) : Holders.<V>of();
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        if (this.index == index) {
            if (this.value == value) {
                return this;
            } else {
                return new LeafTrieNode<T>(shift, index, value);
            }
        } else {
            assert shift >= 0;
            return SingleBranchTrieNode.<T>forIndex(shift, this.index, withShift(shift - 5)).assign(shift, index, value, sizeDelta);
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
        if (this.index == index) {
            final T newValue = transforms.update(Holders.of(this.value), key, value, sizeDelta);
            if (this.value == newValue) {
                return this;
            } else {
                return new LeafTrieNode<T>(shift, index, newValue);
            }
        } else {
            assert shift >= 0;
            return SingleBranchTrieNode.<T>forIndex(shift, this.index, withShift(shift - 5)).assign(shift, index, key, value, transforms, sizeDelta);
        }
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        assert this.shift == shift;
        if (this.index == index) {
            sizeDelta.subtract(1);
            return new EmptyTrieNode<T>(shift);
        } else {
            assert shift > 0;
            return this;
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
        if (this.index == index) {
            final Holder<T> newValue = transforms.delete(value, key, sizeDelta);
            if (newValue.isEmpty()) {
                return new EmptyTrieNode<T>(shift);
            } else if (newValue.getValue() == value) {
                return this;
            } else {
                return withValue(newValue.getValue());
            }
        } else {
            assert shift > 0;
            return this;
        }
    }

    @Override
    public Cursor<JImmutableMap.Entry<Integer, T>> anyOrderEntryCursor()
    {
        return SingleValueCursor.<JImmutableMap.Entry<Integer, T>>of(MapEntry.<Integer, T>of(index, value));
    }

    @Override
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> anyOrderEntryCursor(Transforms<T, K, V> transforms)
    {
        return transforms.cursor(value);
    }

    @Override
    public Cursor<T> anyOrderValueCursor()
    {
        return SingleValueCursor.of(value);
    }

    @Override
    public boolean isFilled()
    {
        return true;
    }

    @Override
    public T getValue()
    {
        return value;
    }

    @Override
    public T getValueOrNull()
    {
        return value;
    }

    @Override
    public T getValueOr(T defaultValue)
    {
        return value;
    }

    private TrieNode<T> withShift(int newShift)
    {
        return new LeafTrieNode<T>(newShift, index, value);
    }

    private TrieNode<T> withValue(T newValue)
    {
        return new LeafTrieNode<T>(shift, index, newValue);
    }
}
