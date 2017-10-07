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

package org.javimmutable.collections.array.trie32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.IterableCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A Transforms implementation intended solely for unit tests because it uses a mutable
 * map to store its values and makes frequent complete copies of the map.
 */
class TestOnlyTransforms<K, V>
        implements Transforms<Map<K, V>, K, V>
{
    @Override
    public Map<K, V> update(Holder<Map<K, V>> leaf,
                            K key,
                            V value,
                            MutableDelta delta)
    {
        if (leaf.isEmpty()) {
            delta.add(1);
            Map<K, V> map = new TreeMap<K, V>();
            map.put(key, value);
            return map;
        } else {
            Map<K, V> map = new TreeMap<K, V>(leaf.getValue());
            if (!map.containsKey(key)) {
                delta.add(1);
                map.put(key, value);
                return map;
            } else if (map.get(key) != value) {
                map.put(key, value);
                return map;
            } else {
                return leaf.getValue();
            }
        }
    }

    @Override
    public Holder<Map<K, V>> delete(Map<K, V> leaf,
                                    K key,
                                    MutableDelta delta)
    {
        if (leaf.containsKey(key)) {
            Map<K, V> map = new TreeMap<K, V>(leaf);
            delta.subtract(1);
            map.remove(key);
            if (map.isEmpty()) {
                return Holders.of();
            } else {
                return Holders.of(map);
            }
        } else {
            return Holders.of(leaf);
        }
    }

    @Override
    public Holder<V> findValue(Map<K, V> leaf,
                               K key)
    {
        return Holders.fromNullable(leaf.get(key));
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(Map<K, V> leaf,
                                                       K key)
    {
        V value = leaf.get(key);
        if (value == null) {
            return Holders.of();
        } else {
            return Holders.<JImmutableMap.Entry<K, V>>of(MapEntry.<K, V>of(key, value));
        }
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(Map<K, V> leaf)
    {
        List<JImmutableMap.Entry<K, V>> entries = new ArrayList<JImmutableMap.Entry<K, V>>();
        for (Map.Entry<K, V> entry : leaf.entrySet()) {
            entries.add(MapEntry.of(entry));
        }
        return IterableCursor.of(entries);
    }
}
