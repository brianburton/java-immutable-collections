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

package org.javimmutable.collections.setmap;


import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.common.AbstractJImmutableSet;
import org.javimmutable.collections.common.Conditions;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.hash.JImmutableHashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Iterator;

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
        Holder<JImmutableSet<V>> current = contents.find(key);
        return current.isFilled() ? current.getValue() : emptySet();
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
    public JImmutableSetMap<K, V> delete(@Nonnull K key)
    {
        return create(contents.delete(key));
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

    @Override
    public Iterator<JImmutableMap.Entry<K, JImmutableSet<V>>> iterator()
    {
        return IteratorAdaptor.of(cursor());
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

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     *
     * @param map
     * @return
     */
    protected abstract JImmutableSetMap<K, V> create(JImmutableMap<K, JImmutableSet<V>> map);

    /**
     * Overridable by derived classes to create a compatible copy of the specified set.
     * Default implementatin simply returns the original.
     *
     * @param original
     * @return
     */
    protected JImmutableSet<V> copySet(JImmutableSet<V> original)
    {
        return original;
    }


    /**
     * Overridable by derived classes to create a new empty set.
     *
     * @return
     */
    @Nonnull
    protected JImmutableSet<V> emptySet()
    {
        return JImmutableHashSet.of();
    }

    /**
     * Overridable by derived classes to insert a value into a set in some way.
     *
     * @param set
     * @param value
     * @return
     */
    protected JImmutableSet<V> insertInSet(JImmutableSet<V> set,
                                           V value)
    {
        return set.insert(value);
    }
}
