///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.cursors.TransformCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.Map;

@Immutable
public abstract class AbstractJImmutableArray<T>
        implements JImmutableArray<T>
{
    @Override
    @Nullable
    public T get(int index)
    {
        return find(index).getValueOrNull();
    }

    @Nonnull
    @Override
    public Holder<JImmutableMap.Entry<Integer, T>> findEntry(int key)
    {
        Holder<T> value = find(key);
        return value.isFilled() ? Holders.<JImmutableMap.Entry<Integer, T>>of(MapEntry.of(key, value.getValue())) : Holders.<JImmutableMap.Entry<Integer, T>>of();
    }

    @Override
    public boolean isEmpty()
    {
        return size() == 0;
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
    public Insertable<JImmutableMap.Entry<Integer, T>> insert(@Nullable JImmutableMap.Entry<Integer, T> e)
    {
        return (e == null) ? this : assign(e.getKey(), e.getValue());
    }

    @Nonnull
    @Override
    public Cursor<Integer> keysCursor()
    {
        return TransformCursor.ofKeys(cursor());
    }

    @Nonnull
    @Override
    public Cursor<T> valuesCursor()
    {
        return TransformCursor.ofValues(cursor());
    }

    @Override
    public Iterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Nonnull
    @Override
    public Map<Integer, T> getMap()
    {
        return ArrayToMapAdaptor.of(this);
    }
}
