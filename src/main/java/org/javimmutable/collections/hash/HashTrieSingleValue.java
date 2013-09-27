///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.list.PersistentLinkedStack;
import org.javimmutable.collections.common.MutableDelta;

public class HashTrieSingleValue<K, V>
        implements HashTrieValue<K, V>,
                   Holder<V>,
                   PersistentMap.Entry<K, V>
{
    private final K key;
    private final V value;

    public HashTrieSingleValue(K key,
                               V value)
    {
        this.key = key;
        this.value = value;
    }

    @Override
    public Holder<V> getValueForKey(K key)
    {
        return key.equals(this.key) ? this : Holders.<V>of();
    }

    @Override
    public PersistentMap.Entry<K, V> getEntryForKey(K key)
    {
        return key.equals(this.key) ? this : null;
    }

    @Override
    public HashTrieValue<K, V> setValueForKey(K key,
                                              V value,
                                              MutableDelta sizeDelta)
    {
        if (key.equals(this.key)) {
            return new HashTrieSingleValue<K, V>(key, value);
        } else {
            sizeDelta.add(1);
            PersistentLinkedStack<HashTrieSingleValue<K, V>> values = PersistentLinkedStack.of();
            return new HashTrieMultiValue<K, V>(values.add(this).add(new HashTrieSingleValue<K, V>(key, value)));
        }
    }

    @Override
    public HashTrieValue<K, V> deleteValueForKey(K key,
                                                 MutableDelta sizeDelta)
    {
        if (this.key.equals(key)) {
            sizeDelta.subtract(1);
            return null;
        } else {
            return this;
        }
    }

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
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean isFilled()
    {
        return true;
    }

    @Override
    public V getValueOrNull()
    {
        return value;
    }

    @Override
    public V getValueOr(V defaultValue)
    {
        return value;
    }

    @Override
    public int size()
    {
        return 1;
    }

    @Override
    public Cursor<PersistentMap.Entry<K, V>> cursor()
    {
        return SingleValueCursor.<PersistentMap.Entry<K, V>>of(this);
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

        HashTrieSingleValue that = (HashTrieSingleValue)o;

        if (!key.equals(that.key)) {
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
        int result = key.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
