///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.iterators.SingleValueIterator;

public class SingleKeyTransforms<K, V>
    implements Transforms<MapEntry<K, V>, K, V>
{
    @Override
    public MapEntry<K, V> update(Holder<MapEntry<K, V>> leafHolder,
                                 K key,
                                 V value,
                                 MutableDelta delta)
    {
        if (leafHolder.isEmpty()) {
            delta.add(1);
            return MapEntry.of(key, value);
        } else {
            final MapEntry<K, V> leaf = leafHolder.getValue();
            if (leaf.getKey().equals(key) && leaf.getValue() == value) {
                return leaf;
            } else {
                return MapEntry.of(key, value);
            }
        }
    }

    @Override
    public Holder<MapEntry<K, V>> delete(MapEntry<K, V> leaf,
                                         K key,
                                         MutableDelta delta)
    {
        if (leaf.getKey().equals(key)) {
            delta.subtract(1);
            return Holders.of();
        } else {
            return Holders.of(leaf);
        }
    }

    @Override
    public Holder<V> findValue(MapEntry<K, V> leaf,
                               K key)
    {
        return leaf.getKey().equals(key) ? Holders.of(leaf.getValue()) : Holders.of();
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(MapEntry<K, V> leaf,
                                                       K key)
    {
        return leaf.getKey().equals(key) ? Holders.of(leaf) : Holders.of();
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(MapEntry<K, V> leaf)
    {
        return SingleValueCursor.of(leaf);
    }

    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(MapEntry<K, V> leaf)
    {
        return SingleValueIterator.of(leaf);
    }
}
