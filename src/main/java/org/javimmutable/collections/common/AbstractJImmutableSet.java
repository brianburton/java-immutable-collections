///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.iterators.IteratorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;
import java.util.Set;

@Immutable
public abstract class AbstractJImmutableSet<T>
    implements JImmutableSet<T>
{
    protected final JImmutableMap<T, Boolean> map;

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

    @Nonnull
    @Override
    public JImmutableSet<T> getInsertableSelf()
    {
        return this;
    }

    @Override
    @Nonnull
    public JImmutableSet<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return union(values.iterator());
    }

    @Override
    @Nonnull
    public JImmutableSet<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return union(values);
    }

    @Override
    public boolean contains(@Nullable T value)
    {
        return (value != null) && map.getValueOr(value, Boolean.FALSE);
    }

    @Override
    public boolean containsAll(@Nonnull Iterable<? extends T> values)
    {
        return containsAll(values.iterator());
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
    public boolean containsAny(@Nonnull Iterable<? extends T> values)
    {
        return containsAny(values.iterator());
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
    public JImmutableSet<T> deleteAll(@Nonnull Iterable<? extends T> other)
    {
        return deleteAll(other.iterator());
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
    public JImmutableSet<T> union(@Nonnull Iterable<? extends T> other)
    {
        return union(other.iterator());
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
    public JImmutableSet<T> intersection(@Nonnull Iterable<? extends T> other)
    {
        return intersection(other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Iterator<? extends T> values)
    {
        if (isEmpty()) {
            return this;
        }

        if (!values.hasNext()) {
            return deleteAll();
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

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull JImmutableSet<? extends T> other)
    {
        return intersection(other.getSet());
    }

    @Nonnull
    @Override
    public JImmutableSet<T> intersection(@Nonnull Set<? extends T> other)
    {
        if (isEmpty()) {
            return this;
        } else if (other.isEmpty()) {
            return deleteAll();
        } else {
            JImmutableMap<T, Boolean> newMap = map;
            for (T value : map.keys()) {
                if (!other.contains(value)) {
                    newMap = newMap.delete(value);
                }
            }
            return (newMap != map) ? create(newMap) : this;
        }
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

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return map.keys().iterator();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return map.keys().getSpliteratorCharacteristics();
    }

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
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
        return IteratorHelper.iteratorToString(iterator());
    }

    @Override
    public void checkInvariants()
    {
        checkSetInvariants();
    }

    protected void checkSetInvariants()
    {
        map.checkInvariants();
        for (JImmutableMap.Entry<T, Boolean> entry : map) {
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
     * Implemented by derived classes to create a new empty Set
     */
    protected abstract Set<T> emptyMutableSet();
}
