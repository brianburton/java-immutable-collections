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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.TransformCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.Map;

@Immutable
public abstract class AbstractJImmutableMap<K, V>
        implements JImmutableMap<K, V>
{
    @Override
    public V get(K key)
    {
        return getValueOr(key, null);
    }

    /**
     * Adds the key/value pair to this map.  Any value already existing for the specified key
     * is replaced with the new value.
     *
     * @param e
     * @return
     */
    @Override
    @Nonnull
    public Insertable<Entry<K, V>> insert(@Nullable Entry<K, V> e)
    {
        return (e == null) ? this : assign(e.getKey(), e.getValue());
    }

    @Override
    public boolean isEmpty()
    {
        return size() == 0;
    }

    @Override
    public Cursor<K> keysCursor()
    {
        return TransformCursor.ofKeys(cursor());
    }

    @Override
    public Cursor<V> valuesCursor()
    {
        return TransformCursor.ofValues(cursor());
    }

    @Override
    public Map<K, V> getMap()
    {
        return MapAdaptor.of(this);
    }

    @Override
    public Iterator<Entry<K, V>> iterator()
    {
        return IteratorAdaptor.of(cursor());
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
        } else if (o instanceof JImmutableMap) {
            return getMap().equals(((JImmutableMap)o).getMap());
        } else {
            return (o instanceof Map) && getMap().equals(o);
        }
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Cursor<Entry<K, V>> cursor = cursor().start(); cursor.hasValue(); cursor = cursor.next()) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            MapEntry.addToString(sb, cursor.getValue());
        }
        sb.append("}");
        return sb.toString();
    }
}
