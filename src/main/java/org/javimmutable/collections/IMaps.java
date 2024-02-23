///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

import org.javimmutable.collections.hash.HashMap;
import org.javimmutable.collections.inorder.OrderedMap;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.TreeMap;
import org.javimmutable.collections.util.Functions;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Map;

public final class IMaps
{
    private IMaps()
    {
    }

    /**
     * Constructs an empty unsorted map.
     * <p>
     * Implementation note: The map will adopt a hash code collision strategy based on
     * the first key assigned to the map.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    @Nonnull
    public static <K, V> IMap<K, V> hashed()
    {
        return HashMap.of();
    }

    /**
     * Constructs an unsorted map.
     * All key/value pairs from source are copied into the newly created map.
     * <p>
     * Implementation note: The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    @Nonnull
    public static <K, V> IMap<K, V> hashed(@Nonnull Map<K, V> source)
    {
        return HashMap.<K, V>builder().add(source).build();
    }

    /**
     * Constructs an unsorted map.
     * If source is already an unsorted map it is returned directly, otherwise a new map
     * is created and all key/value pairs from source are copied into the newly created map.
     * <p>
     * Implementation note: The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    @Nonnull
    public static <K, V> IMap<K, V> hashed(@Nonnull IMap<K, V> source)
    {
        if (source instanceof HashMap) {
            return source;
        } else {
            return HashMap.<K, V>builder().add(source).build();
        }
    }

    /**
     * Constructs an empty map that sorts keys in their natural sort order (using ComparableComparator).
     */
    @Nonnull
    public static <K extends Comparable<K>, V> IMap<K, V> sorted()
    {
        return TreeMap.of();
    }

    /**
     * Constructs a map that sorts keys in their natural sort order (using ComparableComparator).
     * All key/value pairs from source are copied into the newly created map.
     *
     * @param source java.util.Map containing starting key/value pairs
     */
    @Nonnull
    public static <K extends Comparable<K>, V> IMap<K, V> sorted(@Nonnull Map<K, V> source)
    {
        return sorted(ComparableComparator.of(), source);
    }

    /**
     * Constructs a map that sorts keys in their natural sort order (using ComparableComparator).
     * All key/value pairs from source are copied into the newly created map.
     * If source is already a sorted map using the natural sort order it will be returned directly
     * (effectively performing a simple cast).
     *
     * @param source {@link IMap} containing starting key/value pairs
     */
    @Nonnull
    public static <K extends Comparable<K>, V> IMap<K, V> sorted(@Nonnull IMap<K, V> source)
    {
        return sorted(ComparableComparator.of(), source);
    }

    /**
     * Constructs a map that sorts keys using the specified Comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     * <p>
     * All key/value pairs from map are copied into the newly created map.
     */
    @Nonnull
    public static <K, V> IMap<K, V> sorted(@Nonnull Comparator<K> comparator)
    {
        return TreeMap.of(comparator);
    }

    /**
     * Constructs a map that sorts keys using the specified Comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     * <p>
     * All key/value pairs from source are copied into the newly created map.
     *
     * @param source java.util.Map containing starting key/value pairs
     */
    @Nonnull
    public static <K, V> IMap<K, V> sorted(@Nonnull Comparator<K> comparator,
                                           @Nonnull Map<K, V> source)
    {
        return TreeMap.<K, V>builder(comparator).add(source).build();
    }

    /**
     * Constructs a map that sorts keys using the specified Comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     * <p>
     * If source is already a sorted map that uses the same comparator (as indicated by comparator.equals())
     * then source will be returned directly.  Otherwise all key/value pairs from source are copied into
     * the newly created map.
     *
     * @param source {@link IMap} containing starting key/value pairs
     */
    @Nonnull
    public static <K, V> IMap<K, V> sorted(@Nonnull Comparator<K> comparator,
                                           @Nonnull IMap<K, V> source)
    {
        if (source instanceof TreeMap) {
            final TreeMap treemap = (TreeMap)source;
            if (treemap.getComparator().equals(comparator)) {
                return source;
            }
        }
        return TreeMap.<K, V>builder(comparator).add(source).build();
    }

    /**
     * Constructs an empty map whose iterators traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * <p>
     * The map will adopt a hash code collision strategy based on
     * the first key assigned to the map.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    @Nonnull
    public static <K, V> IMap<K, V> ordered()
    {
        return OrderedMap.of();
    }

    /**
     * Constructs a map whose iterators traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * All key/value pairs from source are copied into the newly created map.
     * <p>
     * The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    @Nonnull
    public static <K, V> IMap<K, V> ordered(@Nonnull Map<K, V> source)
    {
        return Functions.assignAll(OrderedMap.of(), source);
    }

    /**
     * Constructs a map whose iterators traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * If source is already an in order map it is returned directly, otherwise a new map
     * is created and all key/value pairs from source are copied into the newly created map.
     * In this case the iteration order for those entries would be based on the order of elements
     * returned by source's iterator.
     * <p>
     * The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    @Nonnull
    public static <K, V> IMap<K, V> ordered(@Nonnull IMap<K, V> source)
    {
        if (source instanceof OrderedMap) {
            return source;
        } else {
            return Functions.assignAll(OrderedMap.of(), source);
        }
    }

    /**
     * Constructs a Builder to produce unsorted maps.
     * <p>
     * Implementation note: The map will adopt a hash code collision strategy based on
     * the first key added.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    @Nonnull
    public static <K, V> IMapBuilder<K, V> hashedBuilder()
    {
        return HashMap.builder();
    }

    /**
     * Create a Builder to construct sorted maps using the natural order of the keys.
     */
    @Nonnull
    public static <K extends Comparable<K>, V> IMapBuilder<K, V> sortedBuilder()
    {
        return TreeMap.builder();
    }

    /**
     * Create a Builder to construct sorted maps using the specified Comparator for keys.
     */
    @Nonnull
    public static <K, V> IMapBuilder<K, V> sortedBuilder(@Nonnull Comparator<K> comparator)
    {
        return TreeMap.builder(comparator);
    }

    /**
     * Create a Builder to construct maps whose iterators visit entries in the same order they were
     * added to the map.
     */
    @Nonnull
    public static <K, V> IMapBuilder<K, V> orderedBuilder()
    {
        return OrderedMap.builder();
    }
}
