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

package org.javimmutable.collections.listmap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListMap;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.Conditions;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.EntryIterableStreamable;

@Immutable
abstract class AbstractListMap<K, V>
    implements IListMap<K, V>
{
    protected final IList<V> emptyList;
    protected final IMap<K, IList<V>> contents;

    protected AbstractListMap(IMap<K, IList<V>> contents,
                              IList<V> emptyList)
    {
        this.emptyList = emptyList;
        this.contents = contents;
    }

    @Nonnull
    @Override
    public IList<V> getList(@Nonnull K key)
    {
        Conditions.stopNull(key);
        Holder<IList<V>> current = contents.find(key);
        return current.get(emptyList);
    }

    @Nonnull
    @Override
    public IListMap<K, V> assign(@Nonnull K key,
                                 @Nonnull IList<V> value)
    {
        Conditions.stopNull(key, value);
        return create(contents.assign(key, copyList(value)));
    }

    @Nonnull
    @Override
    public IListMap<K, V> insert(@Nonnull K key,
                                 @Nullable V value)
    {
        return create(contents.update(key, h -> h.get(emptyList).insertLast(value)));
    }

    @Nonnull
    @Override
    public IListMap<K, V> getInsertableSelf()
    {
        return this;
    }

    @Nonnull
    @Override
    public IListMap<K, V> delete(@Nonnull K key)
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
    public IterableStreamable<K> keys()
    {
        return contents.keys();
    }

    @Nonnull
    @Override
    public IterableStreamable<V> values(@Nonnull K key)
    {
        return getList(key);
    }

    @Nonnull
    @Override
    public IterableStreamable<IMapEntry<K, V>> entries()
    {
        return new EntryIterableStreamable<>(this);
    }

    @Override
    @Nonnull
    public IListMap<K, V> insert(@Nonnull IMapEntry<K, V> e)
    {
        return insert(e.getKey(), e.getValue());
    }

    @Override
    @Nonnull
    public SplitableIterator<IMapEntry<K, IList<V>>> iterator()
    {
        return contents.iterator();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @Nullable
    @Override
    public IList<V> get(K key)
    {
        return contents.get(key);
    }

    @Override
    public IList<V> getValueOr(K key,
                               IList<V> defaultValue)
    {
        return contents.getValueOr(key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<IList<V>> find(K key)
    {
        return contents.find(key);
    }

    @Nonnull
    @Override
    public IListMap<K, V> deleteAll()
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
        return (o instanceof AbstractListMap) && contents.equals(((AbstractListMap)o).contents);
    }

    @Override
    public String toString()
    {
        return contents.toString();
    }

    protected void checkListMapInvariants()
    {
        contents.checkInvariants();
        for (IMapEntry<K, IList<V>> entry : contents) {
            entry.getValue().checkInvariants();
        }
    }

    /**
     * Implemented by derived classes to create a new instance of the appropriate class.
     */
    protected abstract IListMap<K, V> create(IMap<K, IList<V>> map);

    /**
     * Overridable by derived classes to create a compatible copy of the specified list.
     * Default implementation simply returns the original.
     */
    protected IList<V> copyList(IList<V> original)
    {
        return original;
    }
}
