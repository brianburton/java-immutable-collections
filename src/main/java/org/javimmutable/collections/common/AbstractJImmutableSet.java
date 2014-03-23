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
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.TransformCursor;

import java.util.Iterator;
import java.util.Set;

public abstract class AbstractJImmutableSet<T>
        implements JImmutableSet<T>
{
    private final JImmutableMap<T, Boolean> map;

    protected AbstractJImmutableSet(JImmutableMap<T, Boolean> map)
    {
        this.map = map;
    }

    @Override
    public JImmutableSet<T> insert(T value)
    {
        JImmutableMap<T, Boolean> newMap = map.assign(value, Boolean.TRUE);
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public JImmutableSet<T> union(Cursorable<T> other)
    {
        JImmutableMap<T, Boolean> newMap = map;
        for (Cursor<T> cursor = other.cursor().start(); cursor.hasValue(); cursor = cursor.next()) {
            T value = cursor.getValue();
            newMap = newMap.assign(value, Boolean.TRUE);
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public JImmutableSet<T> delete(T value)
    {
        JImmutableMap<T, Boolean> newMap = map.delete(value);
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public JImmutableSet<T> deleteAll(Cursorable<T> other)
    {
        JImmutableMap<T, Boolean> newMap = map;
        for (Cursor<T> cursor = other.cursor().start(); cursor.hasValue(); cursor = cursor.next()) {
            T value = cursor.getValue();
            newMap = newMap.delete(value);
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public JImmutableSet<T> intersection(JImmutableSet<T> other)
    {
        JImmutableSet<T> smaller, larger;
        if (other.getClass() == getClass() && other.size() < size()) {
            smaller = other;
            larger = this;
        } else {
            smaller = this;
            larger = other;
        }
        for (T value : smaller) {
            if (!larger.contains(value)) {
                smaller = smaller.delete(value);
            }
        }
        return smaller;
    }

    @Override
    public JImmutableSet<T> intersection(Cursorable<T> other)
    {
        JImmutableMap<T, Boolean> newMap = emptyMap();
        for (Cursor<T> cursor = other.cursor().start(); cursor.hasValue(); cursor = cursor.next()) {
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
        for (Cursor<T> cursor = value.cursor().start(); cursor.hasValue(); cursor = cursor.next()) {
            if (!contains(cursor.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAny(Cursorable<T> value)
    {
        for (Cursor<T> cursor = value.cursor().start(); cursor.hasValue(); cursor = cursor.next()) {
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
    public Set<T> getSet()
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
        } else if (o instanceof JImmutableSet) {
            return getSet().equals(((JImmutableSet)o).getSet());
        } else {
            return (o instanceof Set) && getSet().equals(o);
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
    protected abstract JImmutableSet<T> create(JImmutableMap<T, Boolean> map);

    /**
     * Implemented by derived classes to create a new empty PersistentMap for use by retainAll()
     *
     * @return
     */
    protected abstract JImmutableMap<T, Boolean> emptyMap();
}
