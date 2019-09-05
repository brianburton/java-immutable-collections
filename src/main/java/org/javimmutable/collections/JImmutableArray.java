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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Immutable sparse array implementation using integers as keys.  Keys are traversed in signed integer
 * order by Iterators so negative values are visited before positive values.  Implementations
 * are allowed to restrict the range of allowable indexes for performance or other reasons.
 * Implementations should throw IndexOutOfBounds exceptions if presented with an invalid index.
 * <p>
 * Arrays are sparse meaning that they can contain elements at any valid index with no need
 * to keep them consecutive (like a List).  Memory is managed to use no more than necessary
 * for the number of elements currently in the array.
 */
@Immutable
public interface JImmutableArray<T>
    extends Indexed<T>,
            Insertable<JImmutableMap.Entry<Integer, T>, JImmutableArray<T>>,
            IterableStreamable<JImmutableMap.Entry<Integer, T>>,
            InvariantCheckable
{
    interface Builder<T>
    {
        /**
         * Builds and returns a collection containing all of the added values.  May be called
         * as often as desired and is safe to call and then continue adding more elements to build
         * another collection with those additional elements.
         *
         * @return the collection
         */
        @Nonnull
        JImmutableArray<T> build();

        /**
         * Determines how many values will be in the collection if build() is called now.
         */
        int size();

        /**
         * Adds the specified value to the values included in the collection when build() is called.
         *
         * @return the builder (convenience for chaining multiple calls)
         */
        @Nonnull
        Builder<T> add(T value);

        /**
         * Adds all values in the Iterator to the values included in the collection when build() is called.
         *
         * @param source Iterator containing values to add
         * @return the builder (convenience for chaining multiple calls)
         */
        @Nonnull
        default Builder<T> add(Iterator<? extends T> source)
        {
            while (source.hasNext()) {
                add(source.next());
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
        default Builder<T> add(Iterable<? extends T> source)
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
        default <K extends T> Builder<T> add(K... source)
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
        default Builder<T> add(Indexed<? extends T> source,
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
        default Builder<T> add(Indexed<? extends T> source)
        {
            return add(source, 0, source.size());
        }
    }

    /**
     * Return the value associated with index or null if no value is associated.
     * Note that if null is an acceptable value to the container then this method
     * will be ambiguous and find() should be used instead.
     *
     * @param index identifies the value to retrieve
     * @return value associated with index or null if no value is associated
     */
    @Nullable
    @Override
    T get(int index);


    /**
     * Return the value associated with index or defaultValue if no value is associated.
     * Note that if defaultValue is an acceptable value to the container then this method
     * will be ambiguous and find() should be used instead.
     *
     * @param index        identifies the value to retrieve
     * @param defaultValue value to return if no entry exists for index
     * @return value associated with index or defaultValue if no value is associated
     */
    @Nullable
    T getValueOr(int index,
                 @Nullable T defaultValue);

    /**
     * Return a Holder containing the value associated wth the index or an empty
     * Holder if no value is associated with the index.
     *
     * @param index identifies the value to retrieve
     * @return possibly empty Holder containing any value associated with the index
     */
    @Nonnull
    Holder<T> find(int index);

    /**
     * Search for an Entry within the map and return a Holder indicating if the Entry
     * was found and, if it was found, the Entry itself.
     *
     * @param index index to search for
     * @return empty Holder if not found, otherwise filled Holder with Entry
     */
    @Nonnull
    Holder<JImmutableMap.Entry<Integer, T>> findEntry(int index);

    /**
     * Sets the value associated with a specific index.  Index must be non-null but value
     * can be null.  If the index already has a value in the map the old value is discarded
     * and the new value is stored in its place.  Returns a new JImmutableMap reflecting
     * any changes.  The original map is always left unchanged.
     *
     * @param index index
     * @param value possibly null value
     * @return new map reflecting the change
     */
    @Nonnull
    JImmutableArray<T> assign(int index,
                              @Nullable T value);

    /**
     * Deletes the entry for the specified index (if any).  Returns a new map if the value
     * was deleted or the current map if the index was not contained in the map.
     *
     * @param index index
     * @return same or different map depending on whether index was removed
     */
    @Nonnull
    JImmutableArray<T> delete(int index);

    /**
     * Return the number of entries in the map.
     */
    @Override
    int size();

    /**
     * @return true only if list contains no values
     */
    boolean isEmpty();

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    JImmutableArray<T> deleteAll();

    /**
     * Creates an unmodifiable java.util.Map reflecting the values of the JImmutableMap backing the array.
     *
     * @return Map view of this JImmutableMap
     */
    @Nonnull
    Map<Integer, T> getMap();

    /**
     * Creates a Streamable to access all of the array's keys.
     */
    @Nonnull
    IterableStreamable<Integer> keys();

    /**
     * Creates a Streamable to access all of the array's values.
     */
    @Nonnull
    IterableStreamable<T> values();
}
