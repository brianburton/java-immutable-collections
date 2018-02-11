///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

package org.javimmutable.collections.hash.collision_map;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class SingleValueListNode<K, V>
    implements ListNode<K, V>,
               JImmutableMap.Entry<K, V>,
               Holders.Filled<V>
{
    private final K key;
    private final V value;

    private SingleValueListNode(K key,
                                V value)
    {
        this.key = key;
        this.value = value;
    }

    static <K, V> SingleValueListNode<K, V> of(K key,
                                               V value)
    {
        return new SingleValueListNode<>(key, value);
    }

    @Override
    public V getValueForKey(K key,
                            V defaultValue)
    {
        return this.key.equals(key) ? value : defaultValue;
    }

    @Override
    public Holder<V> findValueForKey(K key)
    {
        return this.key.equals(key) ? this : Holders.of();
    }

    @Override
    public JImmutableMap.Entry<K, V> getEntryForKey(K key)
    {
        return this.key.equals(key) ? this : null;
    }

    @Override
    public ListNode<K, V> setValueForKey(K key,
                                         V value,
                                         MutableDelta sizeDelta)
    {
        if (key.equals(this.key)) {
            return (this.value == value) ? this : new SingleValueListNode<>(key, value);
        } else {
            sizeDelta.add(1);
            return MultiValueListNode.of(this, new SingleValueListNode<>(key, value));
        }
    }

    @Override
    public ListNode<K, V> setValueForKey(K key,
                                         Func1<Holder<V>, V> generator,
                                         MutableDelta sizeDelta)
    {
        if (key.equals(this.key)) {
            final V value = generator.apply(this);
            return (this.value == value) ? this : new SingleValueListNode<>(key, value);
        } else {
            sizeDelta.add(1);
            return MultiValueListNode.of(this, new SingleValueListNode<>(key, generator.apply(Holders.of())));
        }
    }

    @Override
    public ListNode<K, V> deleteValueForKey(K key,
                                            MutableDelta sizeDelta)
    {
        if (this.key.equals(key)) {
            sizeDelta.subtract(1);
            return null;
        } else {
            return this;
        }
    }

    @Nonnull
    @Override
    public K getKey()
    {
        return key;
    }

    @Override
    public V getValue()
    {
        return value;
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return SingleValueCursor.of(this);
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
    {
        return SingleValueIterator.of(this);
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

        SingleValueListNode that = (SingleValueListNode)o;

        if (key != null ? !key.equals(that.key) : that.key != null) {
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
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "SingleValueLeafNode{" +
               "key=" + key +
               ", value=" + value +
               '}';
    }
}
