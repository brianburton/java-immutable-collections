///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Immutable implementation of both Map.Entry and JImmutableMap.Entry that uses the same equals() and hashCode() implementations as
 * documented in javadoc for Map.Entry.
 */
@Immutable
public class MapEntry<K, V>
    implements IMapEntry<K, V>,
               Map.Entry<K, V>
{
    @Nonnull
    protected final K key;
    protected final V value;

    public MapEntry(@Nonnull Map.Entry<K, V> entry)
    {
        this(entry.getKey(), entry.getValue());
    }

    public MapEntry(@Nonnull IMapEntry<K, V> entry)
    {
        this(entry.getKey(), entry.getValue());
    }

    public MapEntry(@Nonnull K key,
                    V value)
    {
        this.key = key;
        this.value = value;
    }

    @Nonnull
    public static <K, V> IMapEntry<K, V> entry(@Nonnull K key,
                                               V value)
    {
        return new MapEntry<K, V>(key, value);
    }

    @Nonnull
    public static <K, V> Map.Entry<K, V> javaEntry(@Nonnull K key,
                                                   V value)
    {
        return new MapEntry<K, V>(key, value);
    }

    public static <K extends Comparable<K>, V> int compareKeys(@Nonnull IMapEntry<K, V> a,
                                                               @Nonnull IMapEntry<K, V> b)
    {
        return a.getKey().compareTo(b.getKey());
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
    public V setValue(V v)
    {
        throw new UnsupportedOperationException();
    }

    public IMapEntry<K, V> asEntry()
    {
        return this;
    }

    public Map.Entry<K, V> asJavaEntry()
    {
        return this;
    }

    @Override
    public int hashCode()
    {
        return ((key == null) ? 0 : key.hashCode()) ^
               ((value == null) ? 0 : value.hashCode());
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof IMapEntry) {
            IMapEntry jentry = (IMapEntry)o;
            //noinspection ConstantConditions
            return ((key == null) ? (jentry.getKey() == null) : key.equals(jentry.getKey())) &&
                   ((value == null) ? (jentry.getValue() == null) : value.equals(jentry.getValue()));
        }

        if (!(o instanceof Map.Entry)) {
            return false;
        }

        Map.Entry entry2 = (Map.Entry)o;
        return ((key == null) ? (entry2.getKey() == null) : key.equals(entry2.getKey())) &&
               ((value == null) ? (entry2.getValue() == null) : value.equals(entry2.getValue()));
    }

    @Override
    public String toString()
    {
        return makeToString(this);
    }

    private static void addToString(StringBuilder sb,
                                    Object obj)
    {
        if (obj == null) {
            sb.append("null");
        } else {
            sb.append(obj);
        }
    }

    public static String makeToString(IMapEntry entry)
    {
        StringBuilder sb = new StringBuilder();
        addToString(sb, entry);
        return sb.toString();
    }

    public static void addToString(StringBuilder sb,
                                   IMapEntry entry)
    {
        addToString(sb, entry.getKey(), entry.getValue());
    }

    public static <K, V> void addToString(StringBuilder sb,
                                          K key,
                                          V value)
    {
        addToString(sb, key);
        sb.append("=");
        addToString(sb, value);
    }

    public static <K, V> List<Map.Entry<K, V>> toMutableEntries(@Nonnull Collection<IMapEntry<K, V>> source)
    {
        return source.stream().map(MapEntry::new).collect(Collectors.toList());
    }

    public static <K, V> List<IMapEntry<K, V>> toImmutableEntries(@Nonnull Collection<Map.Entry<K, V>> source)
    {
        return source.stream().map(MapEntry::new).collect(Collectors.toList());
    }
}
