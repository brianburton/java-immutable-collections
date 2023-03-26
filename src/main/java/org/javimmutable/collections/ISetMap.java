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

package org.javimmutable.collections;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collector;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Interface for maps that map keys to sets of values.
 */
@Immutable
public interface ISetMap<K, V>
    extends Insertable<IMapEntry<K, V>, ISetMap<K, V>>,
            Mapped<K, ISet<V>>,
            IterableStreamable<IMapEntry<K, ISet<V>>>,
            InvariantCheckable,
            Serializable
{
    /**
     * Return the set associated with key, or an empty set if no list is associated.
     */
    @Nonnull
    ISet<V> getSet(@Nonnull K key);

    /**
     * Sets the set associated with a specific key. Key and value must be non-null.
     * If the key already has a set in the map the old set is discarded and the
     * new set is stored in its place. Returns a new JImmutableSetMap reflecting
     * any changes. The original map is always left unchanged.
     */
    @Nonnull
    ISetMap<K, V> assign(@Nonnull K key,
                         @Nonnull ISet<V> value);

    /**
     * Add value to the Set for the specified key. Note that if the value has already been
     * added, it will not be added again.
     */
    @Nonnull
    @Override
    ISetMap<K, V> insert(@Nonnull IMapEntry<K, V> value);

    /**
     * Add value to the Set for the specified key. Note that if the value has already been
     * added, it will not be added again.
     */
    @Nonnull
    ISetMap<K, V> insert(@Nonnull K key,
                         @Nonnull V value);

    /**
     * Adds all of the elements of the specified Iterable to the Set for the specified key.
     */
    @Nonnull
    ISetMap<K, V> insertAll(@Nonnull K key,
                            @Nonnull Iterable<? extends V> values);

    /**
     * Adds all of the elements of the specified collection to the Set for the specified key.
     */
    @Nonnull
    ISetMap<K, V> insertAll(@Nonnull K key,
                            @Nonnull Iterator<? extends V> values);

    /**
     * Determines if the setmap contains the specified key.
     */
    boolean contains(@Nonnull K key);

    /**
     * Determines if the Set at key contains the specified value.
     *
     * @return true if the Set contains the value
     */
    boolean contains(@Nonnull K key,
                     @Nullable V value);

    /**
     * Determines if the Set at key contains all values in the specified collection.
     *
     * @return true if the Set contains the values
     */
    boolean containsAll(@Nonnull K key,
                        @Nonnull Iterable<? extends V> values);

    /**
     * Determines if the Set at key contains all values in the specified collection.
     *
     * @return true if the Set contains the values
     */
    boolean containsAll(@Nonnull K key,
                        @Nonnull Iterator<? extends V> values);

    /**
     * Determines if the Set at key contains any values in the specified collection.
     *
     * @return true if the Set contains a value
     */
    boolean containsAny(@Nonnull K key,
                        @Nonnull Iterable<? extends V> values);

    /**
     * Determines if the Set at key contains any values in the specified collection.
     *
     * @return true if the Set contains a value
     */
    boolean containsAny(@Nonnull K key,
                        @Nonnull Iterator<? extends V> values);


    /**
     * Deletes the entry for the specified key (if any). Returns a new map if the value
     * was deleted or the current map if the key was not contained in the map.
     */
    @Nonnull
    ISetMap<K, V> delete(@Nonnull K key);

    /**
     * Deletes the specified value from the specified key's set. Returns a new map if the value
     * was deleted or the current map if the key was not contained in the map.
     */
    @Nonnull
    ISetMap<K, V> delete(@Nonnull K key,
                         @Nonnull V value);

    /**
     * Deletes the elements in other at the specified key. Returns a new map if the
     * values were deleted or the current map if the key was not contained in the map.
     */
    @Nonnull
    ISetMap<K, V> deleteAll(@Nonnull K key,
                            @Nonnull Iterable<? extends V> other);

    /**
     * Deletes the elements in other at the specified key. Returns a new map if the
     * values were deleted or the current map if the key was not contained in the map.
     */
    @Nonnull
    ISetMap<K, V> deleteAll(@Nonnull K key,
                            @Nonnull Iterator<? extends V> other);

    /**
     * Adds all values from other to the Set at key
     */
    @Nonnull
    ISetMap<K, V> union(@Nonnull K key,
                        @Nonnull Iterable<? extends V> other);

    /**
     * Adds all values from other to the Set at key
     */
    @Nonnull
    ISetMap<K, V> union(@Nonnull K key,
                        @Nonnull Iterator<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     */
    @Nonnull
    ISetMap<K, V> intersection(@Nonnull K key,
                               @Nonnull Iterable<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     */
    @Nonnull
    ISetMap<K, V> intersection(@Nonnull K key,
                               @Nonnull Iterator<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     */
    @Nonnull
    ISetMap<K, V> intersection(@Nonnull K key,
                               @Nonnull ISet<? extends V> other);

    /**
     * Removes all values from the Set at key that are not contained in the other
     * collection. If the given key is not present in the map, an empty set is added
     * to the map.
     */
    @Nonnull
    ISetMap<K, V> intersection(@Nonnull K key,
                               @Nonnull Set<? extends V> other);

    /**
     * Apply the specified transform function to the Set assigned to the specified key and assign the result
     * to the key in this map.  If no Set is currently assigned to the key the transform function is called
     * with an empty set.
     *
     * @param key       key holding set to be updated
     * @param transform function to update the set
     * @return new map with update applied to set associated with key
     */
    default ISetMap<K, V> transform(@Nonnull K key,
                                    @Nonnull Func1<ISet<V>, ISet<V>> transform)
    {
        final ISet<V> current = getSet(key);
        final ISet<V> transformed = transform.apply(current);
        return (transformed == current) ? this : assign(key, transformed);
    }

    /**
     * Apply the specified transform function to the Set assigned to the specified key and assign the result
     * to the key in this map.  If no set is currently assigned to the key the transform function is never
     * called and this map is returned unchanged.
     *
     * @param key       key holding set to be updated
     * @param transform function to update the set
     * @return new map with update applied to set associated with key
     */
    default ISetMap<K, V> transformIfPresent(@Nonnull K key,
                                             @Nonnull Func1<ISet<V>, ISet<V>> transform)
    {
        final ISet<V> current = get(key);
        if (current != null) {
            final ISet<V> transformed = transform.apply(current);
            if (transformed != current) {
                return assign(key, transformed);
            }
        }
        return this;
    }

    /**
     * Return the number of keys in the map.
     */
    int size();

    /**
     * @return true only if the set contains no values
     */
    boolean isEmpty();

    /**
     * @return true only if set contains values
     */
    default boolean isNonEmpty()
    {
        return !isEmpty();
    }

    /**
     * @return an equivalent collectin with no values
     */
    @Nonnull
    ISetMap<K, V> deleteAll();

    /**
     * Creates a Streamable to access all of the Map's keys.
     */
    @Nonnull
    IterableStreamable<K> keys();

    /**
     * Creates a Streamable to access all of the specified key's set.
     * If no set exists for key an empty Streamable is returned.
     *
     * @return a (possibly empty) Streamable for traversing the values associated with key
     */
    @Nonnull
    IterableStreamable<V> values(@Nonnull K key);

    /**
     * Creates a Streamable to access all of the Map's entries.
     */
    @Nonnull
    IterableStreamable<IMapEntry<K, V>> entries();

    /**
     * Processes every key/set pair in this map using the provided function.
     */
    default void forEach(@Nonnull Proc2<K, ISet<V>> proc)
    {
        for (IMapEntry<K, ISet<V>> e : this) {
            proc.apply(e.getKey(), e.getValue());
        }
    }

    /**
     * Processes every key/set pair in this map using the provided function.
     */
    default <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, ISet<V>, E> proc)
        throws E
    {
        for (IMapEntry<K, ISet<V>> e : this) {
            proc.apply(e.getKey(), e.getValue());
        }
    }

    /**
     * Returns a Collector that creates a setMap of the same type as this containing all
     * of the collected values inserted over whatever starting values this already contained.
     */
    @Nonnull
    default Collector<IMapEntry<K, V>, ?, ISetMap<K, V>> toCollector()
    {
        return GenericCollector.unordered(this, deleteAll(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b.entries()));
    }
}
