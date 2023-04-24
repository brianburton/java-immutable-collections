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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.IArray;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.IStreamable;
import org.javimmutable.collections.MapEntry;
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

@SuppressWarnings({"unchecked", "NullableProblems"})
@Immutable
public class ArrayToMapAdaptor<T>
    extends AbstractMap<Integer, T>
{
    private final IArray<T> map;

    public ArrayToMapAdaptor(IArray<T> map)
    {
        this.map = map;
    }

    public static <V> ArrayToMapAdaptor<V> of(IArray<V> map)
    {
        return new ArrayToMapAdaptor<V>(map);
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
        return map.find((Integer)o).isSome();
    }

    /**
     * Uses O(n) traversal of the JImmutableMap to search for a matching value.
     */
    @Override
    public boolean containsValue(Object o)
    {
        for (IMapEntry<Integer, T> entry : map) {
            T value = entry.getValue();
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
    public T get(Object o)
    {
        return map.get((Integer)o);
    }

    @Override
    public T put(Integer k,
                 T t)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends T> map)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> keySet()
    {
        return new AbstractSet<Integer>()
        {
            @Override
            public boolean isEmpty()
            {
                return map.isEmpty();
            }

            @Override
            public boolean contains(Object o)
            {
                return map.find((Integer)o).isSome();
            }

            @Override
            public Iterator<Integer> iterator()
            {
                return map.keys().iterator();
            }

            @Override
            public Spliterator<Integer> spliterator()
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
    public Collection<T> values()
    {
        return new AbstractCollection<T>()
        {
            @Override
            public Iterator<T> iterator()
            {
                return map.values().iterator();
            }

            @Override
            public Spliterator<T> spliterator()
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
    public Set<Entry<Integer, T>> entrySet()
    {
        return new AbstractSet<Entry<Integer, T>>()
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
                Entry<Integer, T> oEntry = (Entry<Integer, T>)o;
                Holder<IMapEntry<Integer, T>> eHolder = map.findEntry(oEntry.getKey());
                return eHolder.isSome() && new MapEntry(eHolder.unsafeGet()).equals(oEntry);
            }

            @Override
            public Iterator<Entry<Integer, T>> iterator()
            {
                return streamable().iterator();
            }

            @Override
            public Spliterator<Entry<Integer, T>> spliterator()
            {
                return streamable().spliterator();
            }

            @Override
            public int size()
            {
                return map.size();
            }

            @Nonnull
            private IStreamable<Entry<Integer, T>> streamable()
            {
                return TransformStreamable.of(map, MapEntry::new);
            }
        };
    }
}
