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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Interface for maps that map keys to sets of values.
 *
 * @param <K>
 * @param <V>
 */
@Immutable
public interface JImmutableSetMap<K, V>
        extends Insertable<JImmutableMap.Entry<K, V>>,
                Mapped<K, JImmutableSet<V>>,
                Iterable<JImmutableMap.Entry<K, JImmutableSet<V>>>,
                Cursorable<JImmutableMap.Entry<K, JImmutableSet<V>>>,
                InvariantCheckable
{
    /**
     * Return the set associated with key, or an empty set if no list is associated.
     *
     * @param key
     * @return
     */
    @Nonnull
    JImmutableSet<V> getSet(@Nonnull K key);

    /**
     * Sets the set associated with a specific key. Key and value must be non-null.
     * If the key already has a set in the map the old set is discarded and the
     * new set is stored in its place. Returns a new JImmutableSetMap reflecting
     * any changes. The original map is always left unchanged.
     *
     * @param key
     * @param value
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> assign(@Nonnull K key,
                                  @Nonnull JImmutableSet<V> value);

    /**
     * Add value to the Set for the specified key. Note that if the value has already been
     * added, it will not be added again.
     *
     * @param value
     * @return
     */
    @Nonnull
    @Override
    Insertable<JImmutableMap.Entry<K, V>> insert(@Nonnull JImmutableMap.Entry<K, V> value);

    /**
     * Add value to the Set for the specified key. Note that if the value has already been
     * added, it will not be added again.
     *
     * @param key
     * @param value
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> insert(@Nonnull K key,
                                  @Nonnull V value);

    /**
     * Adds all of the elements of the specified collection to the Set for the specified key.
     *
     * @param key
     * @param values
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> insertAll(@Nonnull K key,
                                     @Nonnull Cursorable<? extends V> values);

    /**
     * Adds all of the elements of the specified collection to the Set for the specified key.
     *
     * @param key
     * @param values
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> insertAll(@Nonnull K key,
                                     @Nonnull Collection<? extends V> values);

    /**
     * Adds all of the elements of the specified collection to the Set for the specified key.
     *
     * @param key
     * @param values
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> insertAll(@Nonnull K key,
                                     @Nonnull Cursor<? extends V> values);

    /**
     * Adds all of the elements of the specified collection to the Set for the specified key.
     *
     * @param key
     * @param values
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> insertAll(@Nonnull K key,
                                     @Nonnull Iterator<? extends V> values);

    /**
     * Determines if the setmap contains the specified key.
     *
     * @param key
     * @return
     */
    boolean contains(@Nonnull K key);

    /**
     * Determines if the Set at key contains the specified value.
     *
     * @param key
     * @param value
     * @return true if the Set contains the value
     */
    boolean contains(@Nonnull K key,
                     @Nullable V value);

    /**
     * Determines if the Set at key contains all values in the specified collection.
     *
     * @param key
     * @param values
     * @return true if the Set contains the values
     */
    boolean containsAll(@Nonnull K key,
                        @Nonnull Cursorable<? extends V> values);

    /**
     * Determines if the Set at key contains all values in the specified collection.
     *
     * @param key
     * @param values
     * @return true if the Set contains the values
     */
    boolean containsAll(@Nonnull K key,
                        @Nonnull Collection<? extends V> values);

    /**
     * Determines if the Set at key contains all values in the specified collection.
     *
     * @param key
     * @param values
     * @return true if the Set contains the values
     */
    boolean containsAll(@Nonnull K key,
                        @Nonnull Cursor<? extends V> values);

    /**
     * Determines if the Set at key contains all values in the specified collection.
     *
     * @param key
     * @param values
     * @return true if the Set contains the values
     */
    boolean containsAll(@Nonnull K key,
                        @Nonnull Iterator<? extends V> values);


    /**
     * Determines if the Set at key conains any values in the specified collection.
     *
     * @param key
     * @param values
     * @return true if the Set contains a value
     */
    boolean containsAny(@Nonnull K key,
                        @Nonnull Cursorable<? extends V> values);

    /**
     * Determines if the Set at key contains any values in the specified collection.
     *
     * @param key
     * @param values
     * @return true if the Set contains a value
     */
    boolean containsAny(@Nonnull K key,
                        @Nonnull Collection<? extends V> values);

    /**
     * Determines if the Set at key contains any values in the specified collection.
     *
     * @param key
     * @param values
     * @return true if the Set contains a value
     */
    boolean containsAny(@Nonnull K key,
                        @Nonnull Cursor<? extends V> values);

    /**
     * Determines if the Set at key contains any values in the specified collection.
     *
     * @param key
     * @param values
     * @return true if the Set contains a value
     */
    boolean containsAny(@Nonnull K key,
                        @Nonnull Iterator<? extends V> values);


    /**
     * Deletes the entry for the specified key (if any). Returns a new map if the value
     * was deleted or the current map if the key was not contained in the map.
     *
     * @param key
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> delete(@Nonnull K key);

    /**
     * Deletes the specified value from the specified key's set. Returns a new map if the value
     * was deleted or the current map if the key was not contained in the map.
     *
     * @param key
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> delete(@Nonnull K key,
                                  @Nonnull V value);

    /**
     * Deletes the elements in other at the specified key. Returns a new map if the
     * values were deleted or the current map if the key was not contained in the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> deleteAll(@Nonnull K key,
                                     @Nonnull Cursorable<? extends V> other);

    /**
     * Deletes the elements in other at the specified key. Returns a new map if the
     * values were deleted or the current map if the key was not contained in the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> deleteAll(@Nonnull K key,
                                     @Nonnull Collection<? extends V> other);

    /**
     * Deletes the elements in other at the specified key. Returns a new map if the
     * values were deleted or the current map if the key was not contained in the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> deleteAll(@Nonnull K key,
                                     @Nonnull Cursor<? extends V> other);

    /**
     * Deletes the elements in other at the specified key. Returns a new map if the
     * values were deleted or the current map if the key was not contained in the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> deleteAll(@Nonnull K key,
                                     @Nonnull Iterator<? extends V> other);

    /**
     * Adds all values from other to the Set at key
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> union(@Nonnull K key,
                                 @Nonnull Cursorable<? extends V> other);

    /**
     * Adds all values from other to the Set at key
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> union(@Nonnull K key,
                                 @Nonnull Collection<? extends V> other);

    /**
     * Adds all values from other to the Set at key
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> union(@Nonnull K key,
                                 @Nonnull Cursor<? extends V> other);

    /**
     * Adds all values from other to the Set at key
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> union(@Nonnull K key,
                                 @Nonnull Iterator<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                        @Nonnull Cursorable<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                        @Nonnull Collection<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                        @Nonnull Cursor<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                        @Nonnull Iterator<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                        @Nonnull JImmutableSet<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     *
     * @param key
     * @param other
     * @return
     */
    @Nonnull
    JImmutableSetMap<K, V> intersection(@Nonnull K key,
                                        @Nonnull Set<? extends V> other);


    /**
     * Return the number of keys in the map.
     *
     * @return
     */
    int size();

    /**
     * @return true only if the set contains no values
     */
    boolean isEmpty();

    /**
     * @return an equivalent collectin with no values
     */
    @Nonnull
    JImmutableSetMap<K, V> deleteAll();

    /**
     * Creates a Cursor to access all of the Map's keys
     *
     * @return
     */
    @Nonnull
    Cursor<K> keysCursor();

    /**
     * Creates a Cursor to access all of the specified key's set.
     * If no set exists for key, an empty Cursor is returned.
     *
     * @param key
     * @return a (possibly empty) cursor for traversing the values associated with key
     */
    @Nonnull
    Cursor<V> valuesCursor(@Nonnull K key);
}
