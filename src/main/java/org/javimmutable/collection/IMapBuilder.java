///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collection;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public interface IMapBuilder<K, V>
{
    @Nonnull
    IMap<K, V> build();

    @Nonnull
    IMapBuilder<K, V> add(@Nonnull K key,
                          V value);

    int size();

    @Nonnull
    default IMapBuilder<K, V> add(IMapEntry<? extends K, ? extends V> e)
    {
        return add(e.getKey(), e.getValue());
    }

    /**
     * Adds all values in the Iterator to the values included in the collection when build() is called.
     *
     * @param source Iterator containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default IMapBuilder<K, V> add(Iterator<? extends IMapEntry<? extends K, ? extends V>> source)
    {
        while (source.hasNext()) {
            add(source.next());
        }
        return this;
    }

    /**
     * Adds all values in the Map to the values included in the collection when build() is called.
     *
     * @param source Iterator containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default IMapBuilder<K, V> add(Map<? extends K, ? extends V> source)
    {
        for (Map.Entry<? extends K, ? extends V> e : source.entrySet()) {
            add(e.getKey(), e.getValue());
        }
        return this;
    }

    /**
     * Adds all values in the Collection to the values included in the collection when build() is called.
     *
     * @param source Collection containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default IMapBuilder<K, V> add(Iterable<? extends IMapEntry<? extends K, ? extends V>> source)
    {
        return add(source.iterator());
    }

    /**
     * Adds all values in the array to the values included in the collection when build() is called.
     *
     * @param source array containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default <T extends IMapEntry<? extends K, ? extends V>> IMapBuilder<K, V> add(T... source)
    {
        return add(Arrays.asList(source));
    }

    /**
     * Adds all values in the specified range of Indexed to the values included in the collection when build() is called.
     *
     * @param source Indexed containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default IMapBuilder<K, V> add(Indexed<? extends IMapEntry<? extends K, ? extends V>> source,
                                  int offset,
                                  int limit)
    {
        for (int i = offset; i < limit; ++i) {
            add(source.get(i));
        }
        return this;
    }

    /**
     * Adds all values in the Indexed to the values included in the collection when build() is called.
     *
     * @param source Indexed containing values to add
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    default IMapBuilder<K, V> add(Indexed<? extends IMapEntry<? extends K, ? extends V>> source)
    {
        return add(source, 0, source.size());
    }

    @Nonnull
    default IMapBuilder<K, V> add(@Nonnull IMapBuilder<K, V> other)
    {
        add(other.build());
        return this;
    }

    /**
     * Deletes all values.  This is useful to reset to build a new map with the same builder.
     *
     * @return the builder (convenience for chaining multiple calls)
     */
    @Nonnull
    IMapBuilder<K, V> clear();
}
