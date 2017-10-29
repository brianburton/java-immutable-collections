///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.setmap;


import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.Conditions;
import org.javimmutable.collections.hash.JImmutableHashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;

@Immutable
public abstract class AbstractJImmutableSetMap<K, V>
    implements JImmutableSetMap<K, V>
{
    private final JImmutableMap<K, JImmutableSet<V>> contents;

    protected AbstractJImmutableSetMap(JImmutableMap<K, JImmutableSet<V>> contents)
    {
        this.contents = contents;
    }

    @Nonnull
    @Override
    public JImmutableSet<V> getSet(@Nonnull K key)
    {
        Conditions.stopNull(key);
        return contents.getValueOr(key, emptySet());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> assign(@Nonnull K key,
                                         @Nonnull JImmutableSet<V> value)
    {
        Conditions.stopNull(key, value);
        return create(contents.assign(key, copySet(value)));
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> insert(@Nonnull K key,
                                         @Nonnull V value)
    {
        return create(contents.assign(key, insertInSet(getSet(key), value)));
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> insertAll(@Nonnull K key,
                                            @Nonnull Cursorable<? extends V> values)
    {
        return insertAll(key, values.cursor());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> insertAll(@Nonnull K key,
                                            @Nonnull Collection<? extends V> values)
    {
        return insertAll(key, values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> insertAll(@Nonnull K key,
                                            @Nonnull Cursor<? extends V> values)
    {
        return insertAll(key, values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> insertAll(@Nonnull K key,
                                            @Nonnull Iterator<? extends V> values)
    {
        return create(contents.assign(key, insertAllInSet(getSet(key), values)));
    }

    @Override
    public boolean contains(@Nonnull K key)
    {
        return !contents.find(key).isEmpty();
    }
    //TODO: add method to unit tests

    @Override
    public boolean contains(@Nonnull K key,
                            @Nullable V value)
    {
        return getSet(key).contains(value);
    }

    @Override
    public boolean containsAll(@Nonnull K key,
                               @Nonnull Cursorable<? extends V> values)
    {
        return containsAll(key, values.cursor());
    }

    @Override
    public boolean containsAll(@Nonnull K key,
                               @Nonnull Collection<? extends V> values)
    {
        return containsAll(key, values.iterator());

    }

    @Override
    public boolean containsAll(@Nonnull K key,
                               @Nonnull Cursor<? extends V> values)
    {
        return containsAll(key, values.iterator());

    }

    @Override
    public boolean containsAll(@Nonnull K key,
                               @Nonnull Iterator<? extends V> values)
    {
        return contains(key) && getSet(key).containsAll(values);
    }

    @Override
    public boolean containsAny(@Nonnull K key,
                               @Nonnull Cursorable<? extends V> values)
    {
        return containsAny(key, values.cursor());
    }

    @Override
    public boolean containsAny(@Nonnull K key,
                               @Nonnull Collection<? extends V> values)
    {
        return containsAny(key, values.iterator());
    }

    @Override
    public boolean containsAny(@Nonnull K key,
                               @Nonnull Cursor<? extends V> values)
    {
        return containsAny(key, values.iterator());
    }

    @Override
    public boolean containsAny(@Nonnull K key,
                               @Nonnull Iterator<? extends V> values)
    {
        return getSet(key).containsAny(values);
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> delete(@Nonnull K key)
    {
        return create(contents.delete(key));
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> delete(@Nonnull K key,
                                         @Nonnull V value)
    {
        JImmutableSet<V> set = getSet(key);
        return (set.contains(value)) ? create(contents.assign(key, set.delete(value))) : this;
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> deleteAll(@Nonnull K key,
                                            @Nonnull Cursorable<? extends V> other)
    {
        return deleteAll(key, other.cursor());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> deleteAll(@Nonnull K key,
                                            @Nonnull Collection<? extends V> other)
    {
        return deleteAll(key, other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> deleteAll(@Nonnull K key,
                                            @Nonnull Cursor<? extends V> other)
    {
        return deleteAll(key, other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> deleteAll(@Nonnull K key,
                                            @Nonnull Iterator<? extends V> other)
    {
        JImmutableSet<V> set = getSet(key);
        return (set.isEmpty()) ? this : create(contents.assign(key, deleteAllInSet(set, other)));
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> union(@Nonnull K key,
                                        @Nonnull Cursorable<? extends V> other)
    {
        return union(key, other.cursor());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> union(@Nonnull K key,
                                        @Nonnull Collection<? extends V> other)
    {
        return union(key, other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> union(@Nonnull K key,
                                        @Nonnull Cursor<? extends V> other)
    {
        return union(key, other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> union(@Nonnull K key,
                                        @Nonnull Iterator<? extends V> other)
    {
        return create(contents.assign(key, unionInSet(getSet(key), other)));
    }


    @Nonnull
    @Override
    public JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                               @Nonnull Cursorable<? extends V> other)
    {
        return intersection(key, other.cursor());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                               @Nonnull Collection<? extends V> other)
    {
        return intersection(key, other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                               @Nonnull Cursor<? extends V> other)
    {
        return intersection(key, other.iterator());
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                               @Nonnull Iterator<? extends V> other)
    {
        return create(contents.assign(key, intersectionInSet(getSet(key), other)));
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                               @Nonnull JImmutableSet<? extends V> other)
    {
        return create(contents.assign(key, intersectionInSet(getSet(key), other)));
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                               @Nonnull Set<? extends V> other)
    {
        return create(contents.assign(key, intersectionInSet(getSet(key), other)));
    }

    @Override
    public int size()
    {
        return contents.size();
    }

    @Override
    public boolean isEmpty()
    {
        return contents.isEmpty();
    }

    @Nonnull
    @Override
    public Cursor<K> keysCursor()
    {
        return contents.keysCursor();
    }

    @Nonnull
    @Override
    public Cursor<V> valuesCursor(@Nonnull K key)
    {
        return getSet(key).cursor();
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<K, JImmutableSet<V>>> cursor()
    {
        return contents.cursor();
    }

    @Override
    @Nonnull
    public Insertable<JImmutableMap.Entry<K, V>> insert(@Nonnull JImmutableMap.Entry<K, V> e)
    {
        return insert(e.getKey(), e.getValue());
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, JImmutableSet<V>>> iterator()
    {
        return contents.iterator();
    }

    @Nonnull
    @Override
    public Spliterator<JImmutableMap.Entry<K, JImmutableSet<V>>> spliterator()
    {
        return contents.spliterator();
    }

    @Nonnull
    @Override
    public IterableStreamable<K> keys()
    {
        return contents.keys();
    }

    @Nonnull
    @Override
    public IterableStreamable<V> values(@Nonnull K key)
    {
        return getSet(key);
    }

    @Nullable
    @Override
    public JImmutableSet<V> get(K key)
    {
        return contents.get(key);
    }

    @Override
    public JImmutableSet<V> getValueOr(K key,
                                       JImmutableSet<V> defaultValue)
    {
        return contents.getValueOr(key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<JImmutableSet<V>> find(K key)
    {
        return contents.find(key);
    }

    @Nonnull
    @Override
    public JImmutableSetMap<K, V> deleteAll()
    {
        return create(contents.deleteAll());
    }

    @Override
    public int hashCode()
    {
        return contents.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return (o instanceof AbstractJImmutableSetMap) && contents.equals(((AbstractJImmutableSetMap)o).contents);
    }

    @Override
    public String toString()
    {
        return contents.toString();
    }

    protected void checkSetMapInvariants()
    {
        contents.checkInvariants();
        for (JImmutableMap.Entry<K, JImmutableSet<V>> entry : contents) {
            entry.getValue().checkInvariants();
        }
    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     */
    protected abstract JImmutableSetMap<K, V> create(JImmutableMap<K, JImmutableSet<V>> map);

    /**
     * Overridable by derived classes to create a compatible copy of the specified set.
     * Default implementation simply returns the original.
     */
    protected JImmutableSet<V> copySet(JImmutableSet<V> original)
    {
        return original;
    }


    /**
     * Overridable by derived classes to create a new empty set.
     */
    @Nonnull
    protected JImmutableSet<V> emptySet()
    {
        return JImmutableHashSet.of();
    }

    /**
     * Overridable by derived classes to insert a value into a set in some way.
     */
    protected JImmutableSet<V> insertInSet(JImmutableSet<V> set,
                                           V value)
    {
        return set.insert(value);
    }

    /**
     * Overridable by derived classes to insert all values from an iterator into
     * a set in some way
     */
    protected JImmutableSet<V> insertAllInSet(JImmutableSet<V> set,
                                              Iterator<? extends V> values)
    {
        return set.insertAll(values);
    }

    /**
     * Overridable by derived classes to delete all values from an iterator into
     * a set in some way
     */
    protected JImmutableSet<V> deleteAllInSet(JImmutableSet<V> set,
                                              Iterator<? extends V> other)
    {
        return set.deleteAll(other);
    }

    /**
     * Overridable by derived classes to create a union from an iterator in the
     * Set for key
     */
    protected JImmutableSet<V> unionInSet(JImmutableSet<V> set,
                                          Iterator<? extends V> other)
    {
        return set.union(other);
    }

    /**
     * Overridable by derived classes to create an intersection from an iterator in
     * the Set for key
     */
    protected JImmutableSet<V> intersectionInSet(JImmutableSet<V> set,
                                                 Iterator<? extends V> other)
    {
        return set.intersection(other);
    }

    /**
     * Overridable by derived classes to create an intersection from a JImmutableSet in
     * the Set for key
     */
    protected JImmutableSet<V> intersectionInSet(JImmutableSet<V> set,
                                                 JImmutableSet<? extends V> other)
    {
        return set.intersection(other);
    }

    /**
     * Overridable by derived classes to create an intersection from a Set in
     * the JImmutableSet for key
     */
    protected JImmutableSet<V> intersectionInSet(JImmutableSet<V> set,
                                                 Set<? extends V> other)
    {
        return set.intersection(other);
    }
}
