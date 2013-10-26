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

package org.javimmutable.collections;

import java.util.Map;

/**
 * Immutable implementation of both Map.Entry and PersistentMap.Entry that uses the same equals() and hashCode() implementations as
 * documented in javadoc for Map.Entry.
 *
 * @param <K>
 * @param <V>
 */
public class MapEntry<K, V>
        implements JImmutableMap.Entry<K, V>,
                   Map.Entry<K, V>
{
    private final K key;
    private final V value;

    public MapEntry(Map.Entry<K, V> entry)
    {
        this(entry.getKey(), entry.getValue());
    }

    public MapEntry(JImmutableMap.Entry<K, V> entry)
    {
        this(entry.getKey(), entry.getValue());
    }

    public MapEntry(K key,
                    V value)
    {
        this.key = key;
        this.value = value;
    }

    public static <K, V> MapEntry<K, V> of(Map.Entry<K, V> entry)
    {
        return new MapEntry<K, V>(entry);
    }

    public static <K, V> MapEntry<K, V> of(JImmutableMap.Entry<K, V> entry)
    {
        return new MapEntry<K, V>(entry);
    }

    public static <K, V> MapEntry<K, V> of(K key,
                                           V value)
    {
        return new MapEntry<K, V>(key, value);
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
    public V setValue(V v)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode()
    {
        return (key == null ? 0 : key.hashCode()) ^
               (value == null ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof JImmutableMap.Entry) {
            JImmutableMap.Entry jentry = (JImmutableMap.Entry)o;
            return (key == null ?
                    jentry.getKey() == null : key.equals(jentry.getKey())) &&
                   (value == null ?
                    jentry.getValue() == null : value.equals(jentry.getValue()));
        }

        if (!(o instanceof Map.Entry)) {
            return false;
        }

        Map.Entry entry2 = (Map.Entry)o;
        return (key == null ?
                entry2.getKey() == null : key.equals(entry2.getKey())) &&
               (value == null ?
                entry2.getValue() == null : value.equals(entry2.getValue()));
    }
}
