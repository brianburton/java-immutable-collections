///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

import java.util.Map;
import java.util.function.BiPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.iterators.TransformStreamable;

@Immutable
public abstract class AbstractJImmutableMap<K, V>
    implements IMap<K, V>
{
    @Nullable
    @Override
    public V get(K key)
    {
        return getValueOr(key, null);
    }

    /**
     * Adds the key/value pair to this map.  Any value already existing for the specified key
     * is replaced with the new value.
     */
    @Override
    @Nonnull
    public IMap<K, V> insert(@Nonnull IMapEntry<? extends K, ? extends V> e)
    {
        return assign(e.getKey(), e.getValue());
    }

    @Nonnull
    @Override
    public IMap<K, V> getInsertableSelf()
    {
        return this;
    }

    @Nonnull
    @Override
    public IMap<K, V> assignAll(@Nonnull IMap<? extends K, ? extends V> map)
    {
        return map.reduce((IMap<K, V>)this, (m, k, v) -> m = m.assign(k, v));
    }

    @Nonnull
    @Override
    public IMap<K, V> assignAll(@Nonnull Map<? extends K, ? extends V> map)
    {
        IMap<K, V> answer = this;
        for (Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            answer = answer.assign(e.getKey(), e.getValue());
        }
        return answer;
    }

    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    @Override
    public boolean isNonEmpty()
    {
        return size() != 0;
    }

    @Nonnull
    @Override
    public Map<K, V> getMap()
    {
        return MapAdaptor.of(this);
    }

    @Nonnull
    @Override
    public IterableStreamable<K> keys()
    {
        return TransformStreamable.ofKeys(this);
    }

    @Nonnull
    @Override
    public IterableStreamable<V> values()
    {
        return TransformStreamable.ofValues(this);
    }

    @Override
    public int hashCode()
    {
        return getMap().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        } else if (o instanceof IMap) {
            return getMap().equals(((IMap)o).getMap());
        } else {
            return (o instanceof Map) && getMap().equals(o);
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        forEach((k, v) -> {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            MapEntry.addToString(sb, k, v);
        });
        sb.append("}");
        return sb.toString();
    }

    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_UNORDERED;
    }

    @Nonnull
    @Override
    public IMap<K, V> select(@Nonnull BiPredicate<K, V> predicate)
    {
        return reduce(mapBuilder(), (b, k, v) -> predicate.test(k, v) ? b.add(k, v) : b).build();
    }

    @Nonnull
    @Override
    public IMap<K, V> reject(@Nonnull BiPredicate<K, V> predicate)
    {
        return reduce((IMap<K, V>)this, (m, k, v) -> predicate.test(k, v) ? m.delete(k) : m);
    }
}
