///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.util;

import org.javimmutable.collections.*;
import org.javimmutable.collections.array.trie32.TrieArray;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMap;
import org.javimmutable.collections.inorder.JImmutableInsertOrderSet;
import org.javimmutable.collections.list.JImmutableArrayList;
import org.javimmutable.collections.list.JImmutableLinkedStack;
import org.javimmutable.collections.listmap.JImmutableHashListMap;
import org.javimmutable.collections.listmap.JImmutableInsertOrderListMap;
import org.javimmutable.collections.listmap.JImmutableTreeListMap;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.JImmutableTreeMap;
import org.javimmutable.collections.tree.JImmutableTreeSet;
import org.javimmutable.collections.tree_list.JImmutableTreeList;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class contains static factory methods to create instances of each of the collection interfaces.
 * Overloaded variants are provided for each to pre-populate the created collection with existing values.
 * Where possible the empty collection methods return a common singleton instance to save memory.  The
 * factory methods always return the fastest implementation of each interface (i.e. hash when sort not
 * required, trie when random access not required, etc).
 */
public final class JImmutables
{
    /**
     * Produces an empty JImmutableStack.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableStack<T> stack()
    {
        return JImmutableLinkedStack.of();
    }

    /**
     * Produces a JImmutableStack containing all of the specified values.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableStack<T> stack(T... source)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), source);
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableStack<T> stack(Cursor<? extends T> source)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), source);
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableStack<T> stack(Cursorable<? extends T> source)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), source.cursor());
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableStack<T> stack(Iterator<? extends T> source)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), source);
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableStack<T> stack(Collection<? extends T> source)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), source.iterator());
    }

    /**
     * Produces an empty JImmutableList built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list()
    {
        return JImmutableArrayList.of();
    }

    /**
     * Produces a JImmutableList containing all of the specified values built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(T... values)
    {
        return JImmutableArrayList.<T>builder().add(values).build();
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(Cursor<? extends T> source)
    {
        return JImmutableArrayList.<T>builder().add(source).build();
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(Indexed<? extends T> source)
    {
        return JImmutableArrayList.<T>builder().add(source).build();
    }

    /**
     * Produces a JImmutableList containing all of the values in the specified range from source
     * built atop a sparse array.  The values copied from source are those whose index are in the
     * range offset to (limit - 1).
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(Indexed<? extends T> source,
                                             int offset,
                                             int limit)
    {
        return JImmutableArrayList.<T>builder().add(source, offset, limit).build();
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(JImmutableSet<? extends T> source)
    {
        return list(source.cursor());
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(JImmutableArray<? extends T> source)
    {
        return list(source.valuesCursor());
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(Iterator<? extends T> source)
    {
        return JImmutableArrayList.<T>builder().add(source).build();
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(List<? extends T> source)
    {
        return JImmutableArrayList.<T>builder().add(source).build();
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a sparse array.
     * <p/>
     * Implementation note: Using a sparse array internally provides excellent performance
     * but also imposes a small limitation.  Making any combination of calls to insert(),
     * insertLast(), insertFirst() etc over 2 billion times could lead to the list exhausting
     * the range of valid array indexes and trigger an ArrayIndexOutOfBoundsException.
     * If your program might run into this limitation (wow!) use ralist() instead since
     * tree based lists do not have this limitation.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableList<T> list(Collection<? extends T> source)
    {
        return JImmutableArrayList.<T>builder().add(source).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList built atop a 2-3 tree.
     * <p/>
     * Implementation note: Using a 2-3 tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the array based lists.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableRandomAccessList<T> ralist()
    {
        return JImmutableTreeList.of();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a 2-3 tree.
     * <p/>
     * Implementation note: Using a 2-3 tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the array based lists.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableRandomAccessList<T> ralist(T... source)
    {
        return JImmutableTreeList.<T>builder().add(source).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a 2-3 tree.
     * <p/>
     * Implementation note: Using a 2-3 tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the array based lists.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableRandomAccessList<T> ralist(Cursor<? extends T> source)
    {
        return JImmutableTreeList.<T>builder().add(source).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a 2-3 tree.
     * <p/>
     * Implementation note: Using a 2-3 tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the array based lists.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableRandomAccessList<T> ralist(Cursorable<? extends T> source)
    {
        return JImmutableTreeList.<T>builder().add(source.cursor()).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a 2-3 tree.
     * <p/>
     * Implementation note: Using a 2-3 tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the array based lists.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableRandomAccessList<T> ralist(Iterator<? extends T> source)
    {
        return JImmutableTreeList.<T>builder().add(source).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a 2-3 tree.
     * <p/>
     * Implementation note: Using a 2-3 tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the array based lists.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableRandomAccessList<T> ralist(Collection<? extends T> source)
    {
        return JImmutableTreeList.<T>builder().add(source).build();
    }

    /**
     * Constructs an empty unsorted map.
     * <p/>
     * Implementation note: The map will adopt a hash code collision strategy based on
     * the first key assigned to the map.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableMap<K, V> map()
    {
        return JImmutableHashMap.of();
    }

    /**
     * Constructs an unsorted map.
     * All key/value pairs from source are copied into the newly created map.
     * <p/>
     * Implementation note: The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     *
     * @param source
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableMap<K, V> map(Map<K, V> source)
    {
        return Functions.assignAll(JImmutableHashMap.<K, V>of(), source);
    }

    /**
     * Constructs an unsorted map.
     * If source is already an unsorted map it is returned directly, otherwise a new map
     * is created and all key/value pairs from source are copied into the newly created map.
     * <p/>
     * Implementation note: The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     *
     * @param source
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableMap<K, V> map(JImmutableMap<K, V> source)
    {
        if (source instanceof JImmutableHashMap) {
            return source;
        } else {
            return Functions.assignAll(JImmutableHashMap.<K, V>of(), source);
        }
    }

    /**
     * Constructs an empty map that sorts keys in their natural sort order (using ComparableComparator).
     */
    public static <K extends Comparable<K>, V> JImmutableMap<K, V> sortedMap()
    {
        return JImmutableTreeMap.of();
    }

    /**
     * Constructs a map that sorts keys in their natural sort order (using ComparableComparator).
     * All key/value pairs from source are copied into the newly created map.
     *
     * @param source java.util.Map containing starting key/value pairs
     */
    public static <K extends Comparable<K>, V> JImmutableMap<K, V> sortedMap(Map<K, V> source)
    {
        return Functions.assignAll(JImmutableTreeMap.<K, V>of(), source);
    }

    /**
     * Constructs a map that sorts keys in their natural sort order (using ComparableComparator).
     * All key/value pairs from source are copied into the newly created map.
     * If source is already a sorted map using the natural sort order it will be returned directly
     * (effectively performing a simple cast).
     *
     * @param source JImmutableMap containing starting key/value pairs
     */
    public static <K extends Comparable<K>, V> JImmutableMap<K, V> sortedMap(JImmutableMap<K, V> source)
    {
        return sortedMap(ComparableComparator.<K>of(), source);
    }

    /**
     * Constructs a map that sorts keys using the specified Comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     * <p/>
     * All key/value pairs from map are copied into the newly created map.
     */
    public static <K, V> JImmutableMap<K, V> sortedMap(Comparator<K> comparator)
    {
        return JImmutableTreeMap.of(comparator);
    }

    /**
     * Constructs a map that sorts keys using the specified Comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     * <p/>
     * All key/value pairs from source are copied into the newly created map.
     *
     * @param source java.util.Map containing starting key/value pairs
     */
    public static <K, V> JImmutableMap<K, V> sortedMap(Comparator<K> comparator,
                                                       Map<K, V> source)
    {
        return Functions.assignAll(JImmutableTreeMap.<K, V>of(comparator), source);
    }

    /**
     * Constructs a map that sorts keys using the specified Comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     * <p/>
     * If source is already a sorted map that uses the same comparator (as indicated by comparator.equals())
     * then source will be returned directly.  Otherwise all key/value pairs from source are copied into
     * the newly created map.
     *
     * @param source JImmutableMap containing starting key/value pairs
     */
    public static <K, V> JImmutableMap<K, V> sortedMap(Comparator<K> comparator,
                                                       JImmutableMap<K, V> source)
    {
        if (source instanceof JImmutableTreeMap) {
            JImmutableTreeMap treemap = (JImmutableTreeMap)source;
            if (treemap.getComparator().equals(comparator)) {
                return source;
            }
        }
        return Functions.assignAll(JImmutableTreeMap.<K, V>of(comparator), source);
    }

    /**
     * Constructs an empty map whose cursors traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * <p/>
     * The map will adopt a hash code collision strategy based on
     * the first key assigned to the map.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableMap<K, V> insertOrderMap()
    {
        return JImmutableInsertOrderMap.of();
    }

    /**
     * Constructs a map whose cursors traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * All key/value pairs from source are copied into the newly created map.
     * <p/>
     * The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     *
     * @param source
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableMap<K, V> insertOrderMap(Map<K, V> source)
    {
        return Functions.assignAll(JImmutableInsertOrderMap.<K, V>of(), source);
    }

    /**
     * Constructs a map whose cursors traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * If source is already an in order map it is returned directly, otherwise a new map
     * is created and all key/value pairs from source are copied into the newly created map.
     * In this case the iteration order for those entries would be based on the order of elements
     * returned by source's cursor.
     * <p/>
     * The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     *
     * @param source
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableMap<K, V> insertOrderMap(JImmutableMap<K, V> source)
    {
        if (source instanceof JImmutableInsertOrderMap) {
            return source;
        } else {
            return Functions.assignAll(JImmutableInsertOrderMap.<K, V>of(), source);
        }
    }

    /**
     * Constructs an unsorted set.
     * <p/>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> set()
    {
        return JImmutableHashSet.of();
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p/>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> set(Cursor<? extends T> source)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), source);
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p/>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> set(T... source)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), source);
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p/>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> set(Cursorable<? extends T> source)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), source.cursor());
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p/>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> set(Iterator<? extends T> source)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), source);
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p/>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> set(Collection<? extends T> source)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), source.iterator());
    }

    /**
     * Constructs an empty set that sorts values in their natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet()
    {
        return JImmutableTreeSet.of();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(T... source)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Cursor<? extends T> source)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Cursorable<? extends T> source)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), source.cursor());
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Iterator<? extends T> source)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Collection<? extends T> source)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), source.iterator());
    }

    /**
     * Constructs an empty set that sorts values using comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator)
    {
        return JImmutableTreeSet.of(comparator);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Cursor<? extends T> source)
    {
        return Functions.insertAll(JImmutableTreeSet.of(comparator), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 T... source)
    {
        return Functions.insertAll(JImmutableTreeSet.of(comparator), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Cursorable<? extends T> source)
    {
        return Functions.insertAll(JImmutableTreeSet.of(comparator), source.cursor());
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Iterator<? extends T> source)
    {
        return Functions.insertAll(JImmutableTreeSet.of(comparator), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p/>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Collection<? extends T> source)
    {
        return Functions.insertAll(JImmutableTreeSet.of(comparator), source.iterator());
    }

    /**
     * Constructs an empty set that sorts values based on the order they were originally added to the set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> insertOrderSet()
    {
        return JImmutableInsertOrderSet.of();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> insertOrderSet(Cursor<? extends T> source)
    {
        return Functions.insertAll(JImmutableInsertOrderSet.<T>of(), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> insertOrderSet(T... source)
    {
        return Functions.insertAll(JImmutableInsertOrderSet.<T>of(), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> insertOrderSet(Cursorable<? extends T> source)
    {
        return Functions.insertAll(JImmutableInsertOrderSet.<T>of(), source.cursor());
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> insertOrderSet(Iterator<? extends T> source)
    {
        return Functions.insertAll(JImmutableInsertOrderSet.<T>of(), source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableSet<T> insertOrderSet(Collection<? extends T> source)
    {
        return Functions.insertAll(JImmutableInsertOrderSet.<T>of(), source.iterator());
    }

    /**
     * Creates a list map with higher performance but no specific ordering of keys.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableListMap<K, V> listMap()
    {
        return JImmutableHashListMap.of();
    }

    /**
     * Creates a list map with keys sorted by order they are inserted.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableListMap<K, V> insertOrderListMap()
    {
        return JImmutableInsertOrderListMap.of();
    }

    /**
     * Creates a list map with keys sorted by their natural ordering.
     *
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K extends Comparable<K>, V> JImmutableListMap<K, V> sortedListMap()
    {
        return JImmutableTreeListMap.of();
    }

    /**
     * Creates a list map with keys sorted by the specified Comparator.  The Comparator MUST BE IMMUTABLE.
     *
     * @param comparator
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> JImmutableListMap<K, V> sortedListMap(Comparator<K> comparator)
    {
        return JImmutableTreeListMap.of(comparator);
    }

    /**
     * Creates an empty sparse array that supports any integer (positive or negative) as an index.
     * Indexes do not need to be consecutive there can be gaps of any size between indexes.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableArray<T> array()
    {
        return TrieArray.of();
    }

    /**
     * Creates an empty sparse array that supports any integer (positive or negative) as an index.
     * Indexes do not need to be consecutive there can be gaps of any size between indexes.
     * Copies all values into the array starting at index zero.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableArray<T> array(T... source)
    {
        return TrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableArray<T> array(Cursor<JImmutableMap.Entry<Integer, T>> source)
    {
        return Functions.insertAll(TrieArray.<T>of(), source);
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableArray<T> array(Indexed<? extends T> source)
    {
        return TrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates a sparse array containing all of the values in the specified range from source that
     * supports any integer (positive or negative) as an index.  Indexes do not need to be
     * consecutive there can be gaps of any size between indexes. Copies all entries into the
     * array using each key as an index for storing the corresponding value.  The values copied
     * from source are those whose index are in the range offset to (limit - 1).
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableArray<T> array(Indexed<? extends T> source,
                                               int offset,
                                               int limit)
    {
        return TrieArray.<T>builder().add(source, offset, limit).build();
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     *
     * @param <T>
     * @return
     */
    public static <T> JImmutableArray<T> array(List<? extends T> source)
    {
        return TrieArray.<T>builder().add(source).build();
    }
}
