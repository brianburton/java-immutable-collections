///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.IStreamable;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.iterators.TransformStreamable;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;

/**
 * An immutable Map implementation backed by a {@link IMap}.
 */
@SuppressWarnings({"unchecked", "NullableProblems"})
@Immutable
public class MapAdaptor<K, V>
    extends AbstractMap<K, V>
{
    private final IMap<K, V> map;

    public MapAdaptor(IMap<K, V> map)
    {
        this.map = map;
    }

    public static <K, V> MapAdaptor<K, V> of(IMap<K, V> map)
    {
        return new MapAdaptor<>(map);
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o)
    {
        return map.find((K)o).isFull();
    }

    /**
     * Uses O(n) traversal of the {@link IMap} to search for a matching value.
     */
    @Override
    public boolean containsValue(Object o)
    {
        for (IMapEntry<K, V> entry : map) {
            V value = entry.getValue();
            if (o == null) {
                if (value == null) {
                    return true;
                }
            } else {
                if ((value != null) && value.equals(o)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public V get(Object o)
    {
        return map.getValueOr((K)o, null);
    }

    @Override
    public V put(K k,
                 V v)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<K> keySet()
    {
        return new AbstractSet<K>()
        {
            @Override
            public boolean isEmpty()
            {
                return map.isEmpty();
            }

            @Override
            public boolean contains(Object o)
            {
                return map.find((K)o).isFull();
            }

            @Override
            public Iterator<K> iterator()
            {
                return map.keys().iterator();
            }

            @Override
            public Spliterator<K> spliterator()
            {
                return map.keys().spliterator();
            }

            @Override
            public int size()
            {
                return map.size();
            }
        };
    }

    @Override
    public Collection<V> values()
    {
        return new AbstractCollection<V>()
        {
            @Override
            public Iterator<V> iterator()
            {
                return map.values().iterator();
            }

            @Override
            public Spliterator<V> spliterator()
            {
                return map.values().spliterator();
            }

            @Override
            public int size()
            {
                return map.size();
            }
        };
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return new AbstractSet<Entry<K, V>>()
        {
            @Override
            public boolean isEmpty()
            {
                return map.isEmpty();
            }

            @Override
            public boolean contains(Object o)
            {
                if (!(o instanceof Entry)) {
                    return false;
                }
                Entry<K, V> oEntry = (Entry<K, V>)o;
                Maybe<IMapEntry<K, V>> eMaybe = map.findEntry(oEntry.getKey());
                return eMaybe.isFull() && new MapEntry(eMaybe.unsafeGet()).equals(oEntry);
            }

            @Override
            public Iterator<Entry<K, V>> iterator()
            {
                return streamable().iterator();
            }

            @Override
            public Spliterator<Entry<K, V>> spliterator()
            {
                return streamable().spliterator();
            }

            @Override
            public int size()
            {
                return map.size();
            }

            @Nonnull
            private IStreamable<Entry<K, V>> streamable()
            {
                return TransformStreamable.of(map, MapEntry::new);
            }
        };
    }
}
