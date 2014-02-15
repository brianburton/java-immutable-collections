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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.common.IndexedList;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMap;
import org.javimmutable.collections.list.JImmutableArrayList;
import org.javimmutable.collections.list.JImmutableLinkedStack;
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
    public static <T> JImmutableStack<T> stack()
    {
        return JImmutableLinkedStack.of();
    }

    public static <T> JImmutableStack<T> stack(T... values)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), values);
    }

    public static <T> JImmutableStack<T> stack(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), cursor);
    }

    public static <T> JImmutableStack<T> stack(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), cursorable.cursor());
    }

    public static <T> JImmutableStack<T> stack(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), iterator);
    }

    public static <T> JImmutableStack<T> stack(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), collection.iterator());
    }

    public static <T> JImmutableList<T> list()
    {
        return JImmutableArrayList.of();
    }

    public static <T> JImmutableList<T> list(T... values)
    {
        return JImmutableArrayList.of(IndexedArray.retained(values));
    }

    public static <T> JImmutableList<T> list(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableArrayList.<T>of(), cursor);
    }

    public static <T> JImmutableList<T> list(Indexed<T> cursorable)
    {
        return list(cursorable, 0, cursorable.size());
    }

    public static <T> JImmutableList<T> list(Indexed<T> cursorable,
                                             int offset,
                                             int limit)
    {
        return JImmutableArrayList.of(cursorable, offset, limit);
    }

    public static <T> JImmutableList<T> list(JImmutableSet<T> cursorable)
    {
        return list(cursorable.cursor());
    }

    public static <T> JImmutableList<T> list(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableArrayList.<T>of(), iterator);
    }

    public static <T> JImmutableList<T> list(List<T> collection)
    {
        return JImmutableArrayList.of(IndexedList.retained(collection));
    }

    public static <T> JImmutableList<T> list(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableArrayList.<T>of(), collection.iterator());
    }

    public static <T> JImmutableRandomAccessList<T> ralist()
    {
        return JImmutableTreeList.of();
    }

    public static <T> JImmutableRandomAccessList ralist(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), cursor);
    }

    public static <T> JImmutableRandomAccessList ralist(T... values)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), values);
    }

    public static <T> JImmutableRandomAccessList ralist(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), cursorable.cursor());
    }

    public static <T> JImmutableRandomAccessList<T> ralist(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), iterator);
    }

    public static <T> JImmutableRandomAccessList<T> ralist(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), collection.iterator());
    }

    /**
     * Constructs an empty unsorted map.
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

    public static <T> JImmutableSet<T> set()
    {
        return JImmutableHashSet.of();
    }

    public static <T> JImmutableSet<T> set(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), cursor);
    }

    public static <T> JImmutableSet<T> set(T... values)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), values);
    }

    public static <T> JImmutableSet<T> set(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), cursorable.cursor());
    }

    public static <T> JImmutableSet<T> set(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), iterator);
    }

    public static <T> JImmutableSet<T> set(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), collection.iterator());
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet()
    {
        return JImmutableTreeSet.of();
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(T... values)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), values);
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), cursor);
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), cursorable.cursor());
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), iterator);
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), collection.iterator());
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator)
    {
        return JImmutableTreeSet.of(comparator);
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), cursor);
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 T... values)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), values);
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), cursorable.cursor());
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), iterator);
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Collection<T> collection)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), collection.iterator());
    }
}
