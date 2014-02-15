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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.LazyCursor;
import org.javimmutable.collections.cursors.MultiTransformCursor;
import org.javimmutable.collections.list.JImmutableLinkedStack;

public class HashTrieMultiValue<K, V>
        implements HashTrieValue<K, V>
{
    private final JImmutableLinkedStack<HashTrieSingleValue<K, V>> values;

    public HashTrieMultiValue(JImmutableLinkedStack<HashTrieSingleValue<K, V>> values)
    {
        this.values = values;
    }

    @Override
    public Holder<V> getValueForKey(K key)
    {
        final JImmutableLinkedStack<HashTrieSingleValue<K, V>> values = this.values;
        for (JImmutableLinkedStack<HashTrieSingleValue<K, V>> list = values; !list.isEmpty(); list = list.getTail()) {
            if (list.getHead().getKey().equals(key)) {
                return Holders.of(list.getHead().getValue());
            }
        }
        return Holders.of();
    }

    @Override
    public JImmutableMap.Entry<K, V> getEntryForKey(K key)
    {
        final JImmutableLinkedStack<HashTrieSingleValue<K, V>> values = this.values;
        for (JImmutableLinkedStack<HashTrieSingleValue<K, V>> list = values; !list.isEmpty(); list = list.getTail()) {
            if (list.getHead().getKey().equals(key)) {
                return list.getHead();
            }
        }
        return null;
    }

    @Override
    public HashTrieValue<K, V> setValueForKey(K key,
                                              V value,
                                              MutableDelta sizeDelta)
    {
        final JImmutableLinkedStack<HashTrieSingleValue<K, V>> values = this.values;
        JImmutableLinkedStack<HashTrieSingleValue<K, V>> newList = JImmutableLinkedStack.of();
        boolean found = false;
        for (JImmutableLinkedStack<HashTrieSingleValue<K, V>> list = values; !list.isEmpty(); list = list.getTail()) {
            final HashTrieSingleValue<K, V> head = list.getHead();
            if (head.getKey().equals(key)) {
                if (head.getValue() == value) {
                    return this;
                }
                found = true;
            } else {
                newList = newList.insert(head);
            }
        }
        newList = newList.insert(new HashTrieSingleValue<K, V>(key, value));
        if (!found) {
            sizeDelta.add(1);
        }
        return new HashTrieMultiValue<K, V>(newList);
    }

    @Override
    public HashTrieValue<K, V> deleteValueForKey(K key,
                                                 MutableDelta sizeDelta)
    {
        boolean found = false;
        JImmutableLinkedStack<HashTrieSingleValue<K, V>> newList = JImmutableLinkedStack.of();
        for (JImmutableLinkedStack<HashTrieSingleValue<K, V>> list = values; !list.isEmpty(); list = list.getTail()) {
            final HashTrieSingleValue<K, V> entry = list.getHead();
            if (entry.getKey().equals(key)) {
                found = true;
            } else {
                newList = newList.insert(entry);
            }
        }
        if (found) {
            sizeDelta.subtract(1);
            if (newList.isEmpty()) {
                return null;
            } else if (newList.getTail().isEmpty()) {
                return newList.getHead();
            } else {
                return new HashTrieMultiValue<K, V>(newList);
            }
        } else {
            return this;
        }
    }

    @Override
    public int size()
    {
        int total = 0;
        JImmutableLinkedStack<HashTrieSingleValue<K, V>> values = this.values;
        while (!values.isEmpty()) {
            total += 1;
            values = values.getTail();
        }
        return total;
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return MultiTransformCursor.of(LazyCursor.of(values), HashTrieValueToEntryCursorFunc.<K, V>of());
    }
}
