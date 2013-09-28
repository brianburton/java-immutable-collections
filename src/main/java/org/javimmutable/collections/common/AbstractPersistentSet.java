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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.PersistentSet;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.TransformCursor;

import java.util.Iterator;
import java.util.Set;

public abstract class AbstractPersistentSet<T>
        implements PersistentSet<T>
{
    private final PersistentMap<T, Boolean> map;

    protected AbstractPersistentSet(PersistentMap<T, Boolean> map)
    {
        this.map = map;
    }

    @Override
    public PersistentSet<T> insert(T value)
    {
        PersistentMap<T, Boolean> newMap = map.assign(value, Boolean.TRUE);
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public PersistentSet<T> insertAll(Cursorable<T> other)
    {
        PersistentMap<T, Boolean> newMap = map;
        for (Cursor<T> cursor = other.cursor().next(); cursor.hasValue(); cursor = cursor.next()) {
            T value = cursor.getValue();
            newMap = newMap.assign(value, Boolean.TRUE);
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public PersistentSet<T> delete(T value)
    {
        PersistentMap<T, Boolean> newMap = map.delete(value);
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public PersistentSet<T> deleteAll(Cursorable<T> other)
    {
        PersistentMap<T, Boolean> newMap = map;
        for (Cursor<T> cursor = other.cursor().next(); cursor.hasValue(); cursor = cursor.next()) {
            T value = cursor.getValue();
            newMap = newMap.delete(value);
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public PersistentSet<T> intersection(Cursorable<T> other)
    {
        PersistentMap<T, Boolean> newMap = emptyMap();
        for (Cursor<T> cursor = other.cursor().next(); cursor.hasValue(); cursor = cursor.next()) {
            T value = cursor.getValue();
            if (map.find(value).isFilled()) {
                newMap = newMap.assign(value, Boolean.TRUE);
            }
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public boolean contains(T value)
    {
        return value != null && map.find(value).isFilled();
    }

    @Override
    public boolean containsAll(Cursorable<T> value)
    {
        for (Cursor<T> cursor = value.cursor().next(); cursor.hasValue(); cursor = cursor.next()) {
            if (!contains(cursor.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAny(Cursorable<T> value)
    {
        for (Cursor<T> cursor = value.cursor().next(); cursor.hasValue(); cursor = cursor.next()) {
            if (contains(cursor.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size()
    {
        return map.size();
    }

    @Override
    public boolean isEmpty()
    {
        return map.size() == 0;
    }

    @Override
    public Set<T> asSet()
    {
        return SetAdaptor.of(this);
    }

    @Override
    public Cursor<T> cursor()
    {
        return TransformCursor.ofKeys(map.cursor());
    }

    @Override
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public int hashCode()
    {
        return Cursors.computeHashCode(cursor());
    }

    @Override
    public boolean equals(Object o)
    {
        if (o == this) {
            return true;
        } else if (o == null) {
            return false;
        } else if (o instanceof PersistentSet) {
            return asSet().equals(((PersistentSet)o).asSet());
        } else {
            return (o instanceof Set) && asSet().equals(o);
        }
    }

    @Override
    public String toString()
    {
        return Cursors.makeString(cursor());
    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map
     * @return
     */
    protected abstract PersistentSet<T> create(PersistentMap<T, Boolean> map);

    /**
     * Implemented by derived classes to create a new empty PersistentMap for use by retainAll()
     *
     * @return
     */
    protected abstract PersistentMap<T, Boolean> emptyMap();
}
