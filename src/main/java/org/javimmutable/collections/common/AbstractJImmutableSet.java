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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@Immutable
public abstract class AbstractJImmutableSet<T>
        implements JImmutableSet<T>
{
    private final JImmutableMap<T, Boolean> map;

    protected AbstractJImmutableSet(JImmutableMap<T, Boolean> map)
    {
        this.map = map;
    }

    @Override
    @Nonnull
    public JImmutableSet<T> insert(@Nonnull T value)
    {
        JImmutableMap<T, Boolean> newMap = map.assign(value, Boolean.TRUE);
        return (newMap != map) ? create(newMap) : this;
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return (value != null) && map.getValueOr(value, Boolean.FALSE);
    }

    @Override
    public boolean containsAll(@Nonnull Cursorable<? extends T> values)
    {
        return containsAll(values.cursor());
    }

    @Override
    public boolean containsAll(@Nonnull Collection<? extends T> values)
    {
        return containsAll(values.iterator());
    }

    @Override
    public boolean containsAll(@Nonnull Cursor<? extends T> values)
    {
        for (Cursor<? extends T> c = values.start(); c.hasValue(); c = c.next()) {
            if (!contains(c.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAll(@Nonnull Iterator<? extends T> values)
    {
        while (values.hasNext()) {
            if (!contains(values.next())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean containsAny(@Nonnull Cursorable<? extends T> values)
    {
        return containsAny(values.cursor());
    }

    @Override
    public boolean containsAny(@Nonnull Collection<? extends T> values)
    {
        return containsAny(values.iterator());
    }

    @Override
    public boolean containsAny(@Nonnull Cursor<? extends T> values)
    {
        for (Cursor<? extends T> c = values.start(); c.hasValue(); c = c.next()) {
            if (contains(c.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(@Nonnull Iterator<? extends T> values)
    {
        while (values.hasNext()) {
            if (contains(values.next())) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> delete(T value)
    {
        JImmutableMap<T, Boolean> newMap = map.delete(value);
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll(@Nonnull Cursorable<? extends T> other)
    {
        return deleteAll(other.cursor());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll(@Nonnull Collection<? extends T> other)
    {
        return deleteAll(other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll(@Nonnull Cursor<? extends T> values)
    {
        JImmutableMap<T, Boolean> newMap = map;
        for (Cursor<? extends T> c = values.start(); c.hasValue(); c = c.next()) {
            final T value = c.getValue();
            if (value != null) {
                newMap = newMap.delete(value);
            }
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> deleteAll(@Nonnull Iterator<? extends T> values)
    {
        JImmutableMap<T, Boolean> newMap = map;
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                newMap = newMap.delete(value);
            }
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> union(@Nonnull Cursorable<? extends T> other)
    {
        return union(other.cursor());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> union(@Nonnull Collection<? extends T> other)
    {
        return union(other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> union(@Nonnull Cursor<? extends T> values)
    {
        JImmutableMap<T, Boolean> newMap = map;
        for (Cursor<? extends T> c = values.start(); c.hasValue(); c = c.next()) {
            final T value = c.getValue();
            if (value != null) {
                newMap = newMap.assign(value, Boolean.TRUE);
            }
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> union(@Nonnull Iterator<? extends T> values)
    {
        JImmutableMap<T, Boolean> newMap = map;
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                newMap = newMap.assign(value, Boolean.TRUE);
            }
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Cursorable<? extends T> other)
    {
        return intersection(other.cursor());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Collection<? extends T> other)
    {
        return intersection(other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Cursor<? extends T> values)
    {
        if (isEmpty()) {
            return this;
        }

        JImmutableMap<T, Boolean> newMap = emptyMap();
        for (Cursor<? extends T> c = values.start(); c.hasValue(); c = c.next()) {
            final T value = c.getValue();
            if ((value != null) && map.getValueOr(value, Boolean.FALSE)) {
                newMap = newMap.assign(value, Boolean.TRUE);
            }
        }
        return create(newMap);
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Iterator<? extends T> values)
    {
        if (isEmpty()) {
            return this;
        }

        JImmutableMap<T, Boolean> newMap = emptyMap();
        while (values.hasNext()) {
            final T value = values.next();
            if ((value != null) && map.getValueOr(value, Boolean.FALSE)) {
                newMap = newMap.assign(value, Boolean.TRUE);
            }
        }
        return create(newMap);
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull JImmutableSet<T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        }

        JImmutableMap<T, Boolean> newMap = map;
        for (Cursor<JImmutableMap.Entry<T, Boolean>> c = map.cursor().start(); c.hasValue(); c = c.next()) {
            final T value = c.getValue().getKey();
            if (!other.contains(value)) {
                newMap = newMap.delete(value);
            }
        }
        return (newMap != map) ? create(newMap) : this;
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Set<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        }

        JImmutableMap<T, Boolean> newMap = map;
        for (Cursor<JImmutableMap.Entry<T, Boolean>> c = map.cursor().start(); c.hasValue(); c = c.next()) {
            final T value = c.getValue().getKey();
            if (!other.contains(value)) {
                newMap = newMap.delete(value);
            }
        }
        return (newMap != map) ? create(newMap) : this;
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

    @Nonnull
    @Override
    public Set<T> getSet()
    {
        return SetAdaptor.of(this);
    }

    @Override
    @Nonnull
    public Cursor<T> cursor()
    {
        return map.keysCursor();
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
