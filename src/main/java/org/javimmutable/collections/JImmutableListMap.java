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

package org.javimmutable.collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Iterator;

/**
 * Interface for maps that map keys to lists of values.
 */
@Immutable
public interface JImmutableListMap<K, V>
    extends Insertable<JImmutableMap.Entry<K, V>, JImmutableListMap<K, V>>,
            Mapped<K, JImmutableList<V>>,
            IterableStreamable<JImmutableMap.Entry<K, JImmutableList<V>>>,
            Cursorable<JImmutableMap.Entry<K, JImmutableList<V>>>,
            InvariantCheckable
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
     */
    @Nonnull
    @Override
    JImmutableListMap<K, V> insert(@Nonnull JImmutableMap.Entry<K, V> value);

    /**
     * Add value to the list for the specified key.  Note that this can create duplicate values
     * in the list.
     */
    @Nonnull
    JImmutableListMap<K, V> insert(@Nonnull K key,
                                   @Nullable V value);

    /**
     * Adds all of the elements of the specified collection to the List for the specified key.
     */
    @Nonnull
    default JImmutableListMap<K, V> insertAll(@Nonnull K key,
                                              @Nonnull Cursorable<? extends V> values)
    {
        return assign(key, getList(key).insertAll(values));
    }

    /**
     * Adds all of the elements of the specified collection to the List for the specified key.
     */
    @Nonnull
    default JImmutableListMap<K, V> insertAll(@Nonnull K key,
                                              @Nonnull Collection<? extends V> values)
    {
        return assign(key, getList(key).insertAll(values));
    }

    /**
     * Adds all of the elements of the specified collection to the List for the specified key.
     */
    @Nonnull
    default JImmutableListMap<K, V> insertAll(@Nonnull K key,
                                              @Nonnull Cursor<? extends V> values)
    {
        return assign(key, getList(key).insertAll(values));
    }

    /**
     * Adds all of the elements of the specified collection to the List for the specified key.
     */
    @Nonnull
    default JImmutableListMap<K, V> insertAll(@Nonnull K key,
                                              @Nonnull Iterator<? extends V> values)
    {
        return assign(key, getList(key).insertAll(values));
    }

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
     * Deletes the list for every key in keys. Returns a new map if the keys were deleted or the
     * current map if the keys were contained in the map.
     */
    @Nonnull
    default JImmutableListMap<K, V> delete(@Nonnull Cursorable<? extends K> keys)
    {
        return delete(keys.cursor());
    }

    /**
     * Deletes the list for every key in keys. Returns a new map if the keys were deleted or the
     * current map if the keys were contained in the map.
     */
    @Nonnull
    default JImmutableListMap<K, V> delete(@Nonnull Collection<? extends K> keys)
    {
        return delete(keys.iterator());
    }

    /**
     * Deletes the list for every key in keys. Returns a new map if the keys were deleted or the
     * current map if the keys were contained in the map.
     */
    @Nonnull
    default JImmutableListMap<K, V> delete(@Nonnull Cursor<? extends K> keys)
    {
        JImmutableListMap<K, V> map = this;
        for (Cursor<? extends K> cursor = keys.start(); cursor.hasValue(); cursor = cursor.next()) {
            map = map.delete(cursor.getValue());
        }
        return map;
    }

    /**
     * Deletes the list for every key in keys. Returns a new map if the keys were deleted or the
     * current map if the keys were contained in the map.
     */
    @Nonnull
    default JImmutableListMap<K, V> delete(@Nonnull Iterator<? extends K> keys)
    {
        JImmutableListMap<K, V> map = this;
        while (keys.hasNext()) {
            map = map.delete(keys.next());
        }
        return map;
    }

    /**
     * Return the number of keys in the map.
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

    /**
     * Creates a Streamable to access all of the Map's keys.
     */
    @Nonnull
    IterableStreamable<K> keys();

    /**
     * Creates a Streamable to access all of the specified key's list.
     * If no list exists for key an empty Streamable is returned.
     *
     * @return a (possibly empty) Streamable for traversing the values associated with key
     */
    @Nonnull
    IterableStreamable<V> values(@Nonnull K key);
}
