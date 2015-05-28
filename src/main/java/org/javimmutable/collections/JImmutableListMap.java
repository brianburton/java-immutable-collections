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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Interface for maps that map keys to lists of values.
 *
 * @param <K>
 * @param <V>
 */
@Immutable
public interface JImmutableListMap<K, V>
        extends Insertable<JImmutableMap.Entry<K, V>>,
                Mapped<K, JImmutableList<V>>,
                Iterable<JImmutableMap.Entry<K, JImmutableList<V>>>,
                Cursorable<JImmutableMap.Entry<K, JImmutableList<V>>>
{
    /**
     * Return the list associated with key or an empty list if no list is associated.
     *
     * @param key identifies the value to retrieve
     * @return list associated with key or an empty list if no value is associated
     */
    @Nonnull
    JImmutableList<V> getList(@Nonnull K key);

    /**
     * Sets the list associated with a specific key.  Key and value must be non-null.
     * If the key already has a list in the map the old list is discarded
     * and the new list is stored in its place.  Returns a new JImmutableListMap reflecting
     * any changes.  The original map is always left unchanged.
     *
     * @param key   non-null key
     * @param value list of possibly null values to use for this key
     * @return new map reflecting the change
     */
    @Nonnull
    JImmutableListMap<K, V> assign(@Nonnull K key,
                                   @Nonnull JImmutableList<V> value);

    /**
     * Add key/value entry to the map, replacing any existing entry with same key.
     *
     * @param value
     * @return
     */
    @Nonnull
    @Override
    Insertable<JImmutableMap.Entry<K, V>> insert(@Nonnull JImmutableMap.Entry<K, V> value);

    /**
     * Add value to the list for the specified key.  Note that this can create duplicate values
     * in the list.
     *
     * @param value
     * @param value
     * @return
     */
    @Nonnull
    JImmutableListMap<K, V> insert(@Nonnull K key,
                                   @Nullable V value);

    /**
     * Deletes the entry for the specified key (if any).  Returns a new map if the value
     * was deleted or the current map if the key was not contained in the map.
     *
     * @param key non-null key
     * @return same or different map depending on whether key was removed
     */
    @Nonnull
    JImmutableListMap<K, V> delete(@Nonnull K key);

    /**
     * Return the number of keys in the map.
     *
     * @return
     */
    int size();

    /**
     * @return true only if list contains no values
     */
    boolean isEmpty();

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    JImmutableListMap<K, V> deleteAll();

    /**
     * Creates a Cursor to access all of the Map's keys.
     *
     * @return
     */
    @Nonnull
    Cursor<K> keysCursor();

    /**
     * Creates a Cursor to access all of the specified key's list.
     * If no list exists for key an empty cursor is returned.
     *
     * @return a (possibly empty) cursor for traversing the values associated with key
     */
    @Nonnull
    Cursor<V> valuesCursor(@Nonnull K key);
}
