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
import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * Interface for immutable data structures that allow storage and retrieval of
 * key/value pairs.  null is always an allowed value within the map but is not
 * an allowed key.
 *
 * @param <K>
 * @param <V>
 */
@Immutable
public interface JImmutableMap<K, V>
        extends Insertable<JImmutableMap.Entry<K, V>>,
                Mapped<K, V>,
                Iterable<JImmutableMap.Entry<K, V>>,
                Cursorable<JImmutableMap.Entry<K, V>>,
                InvariantCheckable
{
    /**
     * An immutable entry in the map.  Contains the key and value for that entry.
     * key must not be null but value can be null.
     *
     * @param <K>
     * @param <V>
     */
    @Immutable
    interface Entry<K, V>
    {
        @Nonnull
        K getKey();

        V getValue();
    }

    /**
     * Add key/value entry to the map, replacing any existing entry with same key.
     *
     * @param value
     * @return
     */
    @Nonnull
    @Override
    Insertable<Entry<K, V>> insert(@Nonnull Entry<K, V> value);

    /**
     * Search for a value within the map and return a Holder indicating if the value
     * was found and, if it was found, the value itself.  Holder allows null values
     * to be returned unambiguously.
     *
     * @param key non-null key to search for
     * @return empty Holder if not found, otherwise filled Holder with value
     */
    @Nonnull
    Holder<V> find(@Nonnull K key);

    /**
     * Search for an Entry within the map and return a Holder indicating if the Entry
     * was found and, if it was found, the Entry itself.
     *
     * @param key non-null key to search for
     * @return empty Holder if not found, otherwise filled Holder with Entry
     */
    @Nonnull
    Holder<Entry<K, V>> findEntry(@Nonnull K key);

    /**
     * Sets the value associated with a specific key.  Key must be non-null but value
     * can be null.  If the key already has a value in the map the old value is discarded
     * and the new value is stored in its place.  Returns a new JImmutableMap reflecting
     * any changes.  The original map is always left unchanged.
     *
     * @param key   non-null key
     * @param value possibly null value
     * @return new map reflecting the change
     */
    @Nonnull
    JImmutableMap<K, V> assign(@Nonnull K key,
                               V value);

    /**
     * Copies all key-value pairs from the given map. The map itself and its keys must be
     * nonnull, but values can be null.  If a key already has a value in the map, the old
     * value is replaced with the new value. Returns a new JImmutableMap with the changes.
     *
     * @param map JImmutableMap to take values from
     * @return new map reflecting the change
     */
    @Nonnull
    JImmutableMap<K, V> assignAll(@Nonnull JImmutableMap<? extends K, ? extends V> map);


    /**
     * Copies all key-value pairs from the given map. The map itself and its keys must be
     * nonnull, but values can be null.  If a key already has a value in the map, the old
     * value is replaced with the new value. Returns a new JImmutableMap with the changes.
     *
     * @param map Map to take values from
     * @return new map reflecting the change
     */
    @Nonnull
    JImmutableMap<K, V> assignAll(@Nonnull Map<? extends K, ? extends V> map);

    /**
     * Deletes the entry for the specified key (if any).  Returns a new map if the value
     * was deleted or the current map if the key was not contained in the map.
     *
     * @param key non-null key
     * @return same or different map depending on whether key was removed
     */
    @Nonnull
    JImmutableMap<K, V> delete(@Nonnull K key);

    /**
     * Return the number of entries in the map.
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
    JImmutableMap<K, V> deleteAll();

    /**
     * Creates an unmodifiable java.util.Map reflecting the values of this JImmutableMap.
     *
     * @return Map view of this JImmutableMap
     */
    @Nonnull
    Map<K, V> getMap();

    /**
     * Creates a Cursor to access all of the Map's keys.
     *
     * @return
     */
    @Nonnull
    Cursor<K> keysCursor();

    /**
     * Creates a Cursor to access all of the Map's values.
     *
     * @return
     */
    @Nonnull
    Cursor<V> valuesCursor();
}
