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
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
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
    @Nonnull
    public JImmutableSet<T> insertAll(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(values.cursor());
    }

    @Override
    @Nonnull
    public JImmutableSet<T> insertAll(@Nonnull Collection<? extends T> values)
    {
        return insertAll(values.iterator());
    }

    @Override
    @Nonnull
    public JImmutableSet<T> insertAll(@Nonnull Cursor<? extends T> values)
    {
        JImmutableSet<T> answer = this;
        for (Cursor<? extends T> c = values.start(); c.hasValue(); c = c.next()) {
            answer = answer.insert(c.getValue());
        }
        return answer;
    }

    @Override
    @Nonnull
    public JImmutableSet<T> insertAll(@Nonnull Iterator<? extends T> values)
    {

        JImmutableSet<T> answer = this;
        while (values.hasNext()) {
            answer = answer.insert(values.next());
        }
        return answer;
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
        return deleteAll(values.iterator());
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
        return union(values.iterator());
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
        return intersection(other.cursor().iterator());
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
        return intersection(values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Iterator<? extends T> values)
    {
        if (isEmpty()) {
            return this;
        }

        Set<T> otherSet = emptyMutableSet();
        while (values.hasNext()) {
            final T value = values.next();
            if (value != null) {
                otherSet.add(value);
            }
        }

        JImmutableMap<T, Boolean> newMap = map;
        for (JImmutableMap.Entry<T, Boolean> entry : map) {
            if (!otherSet.contains(entry.getKey())) {
                newMap = newMap.delete(entry.getKey());
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
        } else if (o instanceof JImmutableMultiset) {
            return o.equals(this);
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

    @Override
    public void checkInvariants()
    {
        checkSetInvariants();
    }

    protected void checkSetInvariants()
    {
        map.checkInvariants();
        for (JImmutableMap.Entry<T, Boolean> entry : map.cursor()) {
            if (!entry.getValue()) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     */
    protected abstract JImmutableSet<T> create(JImmutableMap<T, Boolean> map);

    /**
     * Implemented by derived classes to create a new empty JImmutableMap
     */
    protected abstract JImmutableMap<T, Boolean> emptyMap();

    /**
     * Implemented by derived classes to create a new empty Set
     */
    protected abstract Set<T> emptyMutableSet();
}
