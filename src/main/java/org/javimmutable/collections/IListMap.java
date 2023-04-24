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

package org.javimmutable.collections;

import org.javimmutable.collections.listmap.HashListMap;
import org.javimmutable.collections.listmap.OrderedListMap;
import org.javimmutable.collections.listmap.TreeListMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collector;

/**
 * Interface for maps that map keys to lists of values.
 */
@Immutable
public interface IListMap<K, V>
    extends ICollection<IMapEntry<K, IList<V>>>,
            Mapped<K, IList<V>>,
            InvariantCheckable
{
    @Nonnull
    @Override
    IListMap<K, V> insertAll(@Nonnull Iterator<? extends IMapEntry<K, IList<V>>> iterator);

    @Nonnull
    @Override
    default IListMap<K, V> insertAll(@Nonnull Iterable<? extends IMapEntry<K, IList<V>>> iterable)
    {
        return insertAll(iterable.iterator());
    }

    /**
     * Creates a list map with higher performance but no specific ordering of keys.
     */
    @Nonnull
    static <K, V> IListMap<K, V> listMap()
    {
        return HashListMap.of();
    }

    /**
     * Creates a list map with keys sorted by order they are inserted.
     */
    @Nonnull
    static <K, V> IListMap<K, V> insertOrderListMap()
    {
        return OrderedListMap.of();
    }

    /**
     * Creates a list map with keys sorted by their natural ordering.
     */
    @Nonnull
    static <K extends Comparable<K>, V> IListMap<K, V> sortedListMap()
    {
        return TreeListMap.of();
    }

    /**
     * Creates a list map with keys sorted by the specified Comparator.  The Comparator MUST BE IMMUTABLE.
     */
    @Nonnull
    static <K, V> IListMap<K, V> sortedListMap(@Nonnull Comparator<K> comparator)
    {
        return TreeListMap.of(comparator);
    }

    /**
     * Return the list associated with key or an empty list if no list is associated.
     *
     * @param key identifies the value to retrieve
     * @return list associated with key or an empty list if no value is associated
     */
    @Nonnull
    IList<V> getList(@Nonnull K key);

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
    IListMap<K, V> assign(@Nonnull K key,
                          @Nonnull IList<V> value);

    /**
     * Add key/value entry to the map, replacing any existing entry with same key.
     */
    @Nonnull
    @Override
    IListMap<K, V> insert(@Nonnull IMapEntry<K, IList<V>> value);

    /**
     * Add value to the list for the specified key.  Note that this can create duplicate values
     * in the list.
     */
    @Nonnull
    IListMap<K, V> insert(@Nonnull K key,
                          @Nullable V value);

    /**
     * Adds all of the elements of the specified collection to the List for the specified key.
     */
    @Nonnull
    default IListMap<K, V> insertAll(@Nonnull K key,
                                     @Nonnull Iterable<? extends V> values)
    {
        return assign(key, getList(key).insertAll(values));
    }

    /**
     * Adds all of the elements of the specified collection to the List for the specified key.
     */
    @Nonnull
    default IListMap<K, V> insertAll(@Nonnull K key,
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
    IListMap<K, V> delete(@Nonnull K key);

    /**
     * Deletes the list for every key in keys. Returns a new map if the keys were deleted or the
     * current map if the keys were contained in the map.
     */
    @Nonnull
    default IListMap<K, V> deleteAll(@Nonnull Iterable<? extends K> keys)
    {
        return deleteAll(keys.iterator());
    }

    /**
     * Deletes the list for every key in keys. Returns a new map if the keys were deleted or the
     * current map if the keys were contained in the map.
     */
    @Nonnull
    default IListMap<K, V> deleteAll(@Nonnull Iterator<? extends K> keys)
    {
        IListMap<K, V> map = this;
        while (keys.hasNext()) {
            map = map.delete(keys.next());
        }
        return map;
    }

    /**
     * Apply the specified transform function to the List assigned to the specified key and assign the result
     * to the key in this map.  If no List is currently assigned to the key the transform function is called
     * with an empty list.
     *
     * @param key       key holding list to be updated
     * @param transform function to update the list
     * @return new map with update applied to list associated with key
     */
    default IListMap<K, V> transform(@Nonnull K key,
                                     @Nonnull Func1<IList<V>, IList<V>> transform)
    {
        final IList<V> current = getList(key);
        final IList<V> transformed = transform.apply(current);
        return (transformed == current) ? this : assign(key, transformed);
    }

    /**
     * Apply the specified transform function to the List assigned to the specified key and assign the result
     * to the key in this map.  If no list is currently assigned to the key the transform function is never
     * called and this map is returned unchanged.
     *
     * @param key       key holding list to be updated
     * @param transform function to update the list
     * @return new map with update applied to list associated with key
     */
    default IListMap<K, V> transformIfPresent(@Nonnull K key,
                                              @Nonnull Func1<IList<V>, IList<V>> transform)
    {
        final IList<V> current = get(key);
        if (current != null) {
            final IList<V> transformed = transform.apply(current);
            if (transformed != current) {
                return assign(key, transformed);
            }
        }
        return this;
    }

    /**
     * @return an equivalent collection with no values
     */
    @Nonnull
    @Override
    IListMap<K, V> deleteAll();

    /**
     * Creates a Streamable to access all of the Map's keys.
     */
    @Nonnull
    IStreamable<K> keys();

    /**
     * Creates a Streamable to access all of the specified key's list.
     * If no list exists for key an empty Streamable is returned.
     *
     * @return a (possibly empty) Streamable for traversing the values associated with key
     */
    @Nonnull
    IStreamable<V> values(@Nonnull K key);

    /**
     * Creates a Streamable to access all of the Map's entries.
     */
    @Nonnull
    IStreamable<IMapEntry<K, IList<V>>> entries();

    /**
     * Processes every key/list pair in this map using the provided function.
     */
    default void forEach(@Nonnull Proc2<K, IList<V>> proc)
    {
        for (IMapEntry<K, IList<V>> e : this) {
            proc.apply(e.getKey(), e.getValue());
        }
    }

    /**
     * Processes every key/list pair in this map using the provided function.
     */
    default <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, IList<V>, E> proc)
        throws E
    {
        for (IMapEntry<K, IList<V>> e : this) {
            proc.apply(e.getKey(), e.getValue());
        }
    }

    /**
     * Returns a Collector that creates a listMap of the same type as this containing all
     * of the collected values inserted over whatever starting values this already contained.
     */
    @Nonnull
    default Collector<IMapEntry<K, V>, ?, IListMap<K, V>> toCollector()
    {
        return GenericCollector.ordered(this,
                                        deleteAll(),
                                        a -> a.isEmpty(),
                                        (a, e) -> a.insert(e.getKey(), e.getValue()),
                                        (a, b) -> a.insertAll(b.entries()));
    }
}
