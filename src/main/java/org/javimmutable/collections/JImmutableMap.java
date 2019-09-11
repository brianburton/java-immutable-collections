///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

import org.javimmutable.collections.functional.Each2;
import org.javimmutable.collections.functional.Each2Throws;
import org.javimmutable.collections.functional.Sum2;
import org.javimmutable.collections.functional.Sum2Throws;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collector;

/**
 * Interface for immutable data structures that allow storage and retrieval of
 * key/value pairs.  null is always an allowed value within the map but is not
 * an allowed key.
 */
@Immutable
public interface JImmutableMap<K, V>
    extends Insertable<JImmutableMap.Entry<? extends K, ? extends V>, JImmutableMap<K, V>>,
            Mapped<K, V>,
            IterableStreamable<JImmutableMap.Entry<K, V>>,
            InvariantCheckable
{
    /**
     * An immutable entry in the map.  Contains the key and value for that entry.
     * key must not be null but value can be null.
     */
    @Immutable
    interface Entry<K, V>
    {
        @Nonnull
        K getKey();

        V getValue();
    }

    interface Builder<K, V>
    {
        @Nonnull
        JImmutableMap<K, V> build();

        @Nonnull
        Builder<K, V> add(@Nonnull K key,
                          V value);

        int size();

        @Nonnull
        default Builder<K, V> add(Entry<? extends K, ? extends V> e)
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
        default Builder<K, V> add(Iterator<? extends Entry<? extends K, ? extends V>> source)
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
        default Builder<K, V> add(Map<? extends K, ? extends V> source)
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
        default Builder<K, V> add(Iterable<? extends Entry<? extends K, ? extends V>> source)
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
        default <T extends Entry<? extends K, ? extends V>> Builder<K, V> add(T... source)
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
        default Builder<K, V> add(Indexed<? extends Entry<? extends K, ? extends V>> source,
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
        default Builder<K, V> add(Indexed<? extends Entry<? extends K, ? extends V>> source)
        {
            return add(source, 0, source.size());
        }

        @Nonnull
        default Builder<K, V> add(@Nonnull Builder<K, V> other)
        {
            add(other.build());
            return this;
        }
    }

    /**
     * Add key/value entry to the map, replacing any existing entry with same key.
     */
    @Nonnull
    @Override
    JImmutableMap<K, V> insert(@Nonnull Entry<? extends K, ? extends V> value);

    /**
     * Search for a value within the map and return a Holder indicating if the value
     * was found and, if it was found, the value itself.  Holder allows null values
     * to be returned unambiguously.
     *
     * @param key non-null key to search for
     * @return empty Holder if not found, otherwise filled Holder with value
     */
    @Nonnull
    @Override
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
     * Creates a Streamable to access all of the Map's keys.
     */
    @Nonnull
    IterableStreamable<K> keys();

    /**
     * Creates a Streamable to access all of the Map's values.
     */
    @Nonnull
    IterableStreamable<V> values();

    /**
     * Creates a Builder with the same type signature as this Map.
     */
    @Nonnull
    Builder<K, V> mapBuilder();

    /**
     * Returns a Collector that creates a set of the same type as this containing all
     * of the collected values inserted over whatever starting values this already contained.
     */
    @Nonnull
    default Collector<Entry<K, V>, ?, JImmutableMap<K, V>> mapCollector()
    {
        return GenericCollector.unordered(this, deleteAll(), a -> a.isEmpty(), (a, v) -> a.insert(v), (a, b) -> a.insertAll(b));
    }

    /**
     * Update the value at the key.  A Holder containing the value currently stored at the key,
     * or an empty Holder if the key is not currently bound, is passed to the generator function.
     *
     * @param key       non-null key
     * @param generator function to call with current value to create value if key is already bound
     * @return new map with changes applied
     */
    @Nonnull
    default JImmutableMap<K, V> update(@Nonnull K key,
                                       @Nonnull Func1<Holder<V>, V> generator)
    {
        final Holder<V> current = find(key);
        final V newValue = generator.apply(current);
        return assign(key, newValue);
    }

    /**
     * Processes every key/value pair in this map using the provided function.
     */
    default void forEach(@Nonnull Each2<K, V> proc)
    {
        for (Entry<K, V> e : this) {
            proc.accept(e.getKey(), e.getValue());
        }
    }

    /**
     * Processes every key/value pair in this map using the provided function.
     */
    default <E extends Exception> void forEachThrows(@Nonnull Each2Throws<K, V, E> proc)
        throws E
    {
        for (Entry<K, V> e : this) {
            proc.accept(e.getKey(), e.getValue());
        }
    }

    /**
     * Processes every key value pair in this map using the provided function to produce a value.
     *
     * @param sum  initial value for process (used with first key/value pair of map)
     * @param proc function to combine a sum with a key value pair to produce a new sum
     * @param <R>  type of the sum
     * @return final value (or initial value if this map is empty)
     */
    default <R> R reduce(R sum,
                         Sum2<K, V, R> proc)
    {
        for (Entry<K, V> e : this) {
            sum = proc.process(sum, e.getKey(), e.getValue());
        }
        return sum;
    }

    /**
     * Processes every key value pair in this map using the provided function to produce a value.
     *
     * @param sum  initial value for process (used with first key/value pair of map)
     * @param proc function to combine a sum with a key value pair to produce a new sum
     * @param <R>  type of the sum
     * @param <E>  type of the Exception thrown by the function
     * @return final value (or initial value if this map is empty)
     */
    default <R, E extends Exception> R reduceThrows(R sum,
                                                    Sum2Throws<K, V, R, E> proc)
        throws E
    {
        for (Entry<K, V> e : this) {
            sum = proc.process(sum, e.getKey(), e.getValue());
        }
        return sum;
    }
}
