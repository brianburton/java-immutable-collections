///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.array.trie32;

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
    private final int index;
    private final T value;
    private final int shift;

    private LeafTrieNode(int index,
                         T value,
                         int shift)
    {
        this.index = index;
        this.value = value;
        this.shift = shift;
    }

    static <T> LeafTrieNode<T> of(int index,
                                  T value)
    {
        return new LeafTrieNode<T>(index, value, shiftForIndex(index));
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
        assert shift >= -5;
        return (this.index == index) ? value : defaultValue;
    }

    @Override
    public <K, V> V getValueOr(int shift,
                               int index,
                               K key,
                               Transforms<T, K, V> transforms,
                               V defaultValue)
    {
        assert shift >= -5;
        return (this.index == index) ? transforms.findValue(value, key).getValueOr(defaultValue) : defaultValue;
    }

    @Override
    public Holder<T> find(int shift,
                          int index)
    {
        assert shift >= -5;
        return (this.index == index) ? this : Holders.<T>of();
    }

    @Override
    public <K, V> Holder<V> find(int shift,
                                 int index,
                                 K key,
                                 Transforms<T, K, V> transforms)
    {
        assert shift >= -5;
        return (this.index == index) ? transforms.findValue(value, key) : Holders.<V>of();
    }

    @Override
    public TrieNode<T> assign(int shift,
                              int index,
                              T value,
                              MutableDelta sizeDelta)
    {
        assert shift >= -5;
        if (this.index == index) {
            if (this.value == value) {
                return this;
            } else {
                return withValue(value);
            }
        } else {
            assert shift >= 0;
            return SingleBranchTrieNode.<T>forIndex(shift, this.index, this).assign(shift, index, value, sizeDelta);
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
        assert shift >= -5;
        if (this.index == index) {
            final T newValue = transforms.update(Holders.of(this.value), key, value, sizeDelta);
            if (this.value == newValue) {
                return this;
            } else {
                return withValue(newValue);
            }
        } else {
            assert shift >= 0;
            return SingleBranchTrieNode.<T>forIndex(shift, this.index, this).assign(shift, index, key, value, transforms, sizeDelta);
        }
    }

    @Override
    public TrieNode<T> delete(int shift,
                              int index,
                              MutableDelta sizeDelta)
    {
        assert shift >= -5;
        if (this.index == index) {
            sizeDelta.subtract(1);
            return of();
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
        assert shift >= -5;
        if (this.index == index) {
            final Holder<T> newValue = transforms.delete(value, key, sizeDelta);
            if (newValue.isEmpty()) {
                return of();
            } else if (newValue.getValue() == value) {
                return this;
            } else {
                return withValue(newValue.getValue());
            }
        } else {
            assert shift >= 0;
            return this;
        }
    }

    @Override
    public int getShift()
    {
        return shift;
    }

    @Override
    public boolean isLeaf()
    {
        return true;
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

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LeafTrieNode that = (LeafTrieNode)o;

        if (index != that.index) {
            return false;
        }
        if (shift != that.shift) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = index;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + shift;
        return result;
    }

    private TrieNode<T> withValue(T newValue)
    {
        return new LeafTrieNode<T>(index, newValue, shift);
    }
}
