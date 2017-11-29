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

package org.javimmutable.collections.util;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.InsertableSequence;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableListMap;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableMultiset;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableSetMap;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.array.trie32.JImmutableTrieArray;
import org.javimmutable.collections.btree_list.JImmutableBtreeList;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.hash.JImmutableHashMultiset;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMap;
import org.javimmutable.collections.inorder.JImmutableInsertOrderMultiset;
import org.javimmutable.collections.inorder.JImmutableInsertOrderSet;
import org.javimmutable.collections.list.JImmutableArrayList;
import org.javimmutable.collections.list.JImmutableLinkedStack;
import org.javimmutable.collections.listmap.JImmutableHashListMap;
import org.javimmutable.collections.listmap.JImmutableInsertOrderListMap;
import org.javimmutable.collections.listmap.JImmutableTreeListMap;
import org.javimmutable.collections.sequence.EmptySequenceNode;
import org.javimmutable.collections.sequence.FilledSequenceNode;
import org.javimmutable.collections.setmap.JImmutableHashSetMap;
import org.javimmutable.collections.setmap.JImmutableInsertOrderSetMap;
import org.javimmutable.collections.setmap.JImmutableTemplateSetMap;
import org.javimmutable.collections.setmap.JImmutableTreeSetMap;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.JImmutableTreeMap;
import org.javimmutable.collections.tree.JImmutableTreeMultiset;
import org.javimmutable.collections.tree.JImmutableTreeSet;

import javax.annotation.Nonnull;
import java.util.Arrays;
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
@SuppressWarnings("ClassWithTooManyMethods")
public final class JImmutables
{
    private JImmutables()
    {
    }

    /**
     * Produces an empty JImmutableStack.
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
     */
    @SafeVarargs
    public static <T> JImmutableStack<T> stack(T... source)
    {
        return JImmutableLinkedStack.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     */
    public static <T> JImmutableStack<T> stack(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableLinkedStack.<T>of().insertAll(source);
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     */
    public static <T> JImmutableStack<T> stack(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableLinkedStack.<T>of().insertAll(source);
    }

    /**
     * Produces a JImmutableStack containing all of the values in source.  Note that values
     * are added to the stack in the order they appear in source which means they will be
     * retrieved in the opposite order from the stack (i.e. the last value in source will
     * be the first value retrieved from the stack).
     */
    public static <T> JImmutableStack<T> stack(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableLinkedStack.<T>of().insertAll(source);
    }

    /**
     * Produces an empty JImmutableList built atop a 32-way tree.
     */
    public static <T> JImmutableList<T> list()
    {
        return JImmutableArrayList.of();
    }

    /**
     * Produces a MutableBuilder for efficiently constructing a JImmutableList built atop a 32-way tree.
     */
    public static <T> JImmutableList.Builder<T> listBuilder()
    {
        return JImmutableArrayList.builder();
    }

    /**
     * Produces a JImmutableList containing all of the specified values built atop a 32-way tree.
     */
    @SafeVarargs
    public static <T> JImmutableList<T> list(T... values)
    {
        return JImmutableArrayList.of(IndexedArray.retained(values));
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a 32-way tree.
     */
    public static <T> JImmutableList<T> list(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableArrayList.<T>builder().add(source).build();
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a 32-way tree.
     */
    public static <T> JImmutableList<T> list(@Nonnull Indexed<? extends T> source)
    {
        return JImmutableArrayList.of(source);
    }

    /**
     * Produces a JImmutableList containing all of the values in the specified range from source
     * built atop a 32-way tree.  The values copied from source are those whose index are in the
     * range offset to (limit - 1).
     */
    public static <T> JImmutableList<T> list(@Nonnull Indexed<? extends T> source,
                                             int offset,
                                             int limit)
    {
        return JImmutableArrayList.of(source, offset, limit);
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a 32-way tree.
     */
    public static <T> JImmutableList<T> list(@Nonnull JImmutableSet<? extends T> source)
    {
        return list(source.cursor());
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a 32-way tree.
     */
    public static <T> JImmutableList<T> list(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableArrayList.<T>builder().add(source).build();
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a 32-way tree.
     */
    public static <T> JImmutableList<T> list(@Nonnull JImmutableList<? extends T> source)
    {
        return JImmutableArrayList.of(source);
    }

    /**
     * Produces a JImmutableList containing all of the values in source built atop a 32-way tree.
     */
    public static <T> JImmutableList<T> list(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableArrayList.<T>builder().add(source.iterator()).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList built atop a B-Tree.
     * <p>
     * Implementation note: Using a B-Tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the 32-way tree lists.
     */
    public static <T> JImmutableRandomAccessList<T> ralist()
    {
        return JImmutableBtreeList.of();
    }

    /**
     * Produces a MutableBuilder to efficiently construct a JImmutableRandomAccessList built atop a B-Tree.
     * <p>
     * Implementation note: Using a B-Tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the 32-way tree lists.
     */
    public static <T> JImmutableRandomAccessList.Builder<T> ralistBuilder()
    {
        return JImmutableBtreeList.builder();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a B-Tree.
     * <p>
     * Implementation note: Using a B-Tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the 32-way tree lists.
     */
    @SafeVarargs
    public static <T> JImmutableRandomAccessList<T> ralist(T... source)
    {
        return JImmutableBtreeList.of(IndexedArray.retained(source));
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a B-Tree.
     * <p>
     * Implementation note: Using a B-Tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the 32-way tree lists.
     */
    public static <T> JImmutableRandomAccessList<T> ralist(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableBtreeList.<T>builder().add(source).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a B-Tree.
     * <p>
     * Implementation note: Using a B-Tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the 32-way tree lists.
     */
    public static <T> JImmutableRandomAccessList<T> ralist(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableBtreeList.<T>builder().add(source.iterator()).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a B-Tree.
     * <p>
     * Implementation note: Using a B-Tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the 32-way tree lists.
     */
    public static <T> JImmutableRandomAccessList<T> ralist(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableBtreeList.<T>builder().add(source).build();
    }

    /**
     * Produces an empty JImmutableRandomAccessList containing all of the values in source built atop a B-Tree.
     * <p>
     * Implementation note: Using a B-Tree provides maximum flexibility and good performance
     * for insertion and deletion anywhere in the list but is slower than the 32-way tree lists.
     */
    public static <T> JImmutableRandomAccessList<T> ralist(@Nonnull List<? extends T> source)
    {
        return JImmutableBtreeList.of(IndexedList.retained(source));
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
    public static <K, V> JImmutableMap<K, V> map()
    {
        return JImmutableHashMap.of();
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
    public static <K, V> JImmutableMap<K, V> map(@Nonnull Map<K, V> source)
    {
        return Functions.assignAll(JImmutableHashMap.of(), source);
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
    public static <K, V> JImmutableMap<K, V> map(@Nonnull JImmutableMap<K, V> source)
    {
        if (source instanceof JImmutableHashMap) {
            return source;
        } else {
            return Functions.assignAll(JImmutableHashMap.of(), source);
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
    public static <K extends Comparable<K>, V> JImmutableMap<K, V> sortedMap(@Nonnull Map<K, V> source)
    {
        return Functions.assignAll(JImmutableTreeMap.of(), source);
    }

    /**
     * Constructs a map that sorts keys in their natural sort order (using ComparableComparator).
     * All key/value pairs from source are copied into the newly created map.
     * If source is already a sorted map using the natural sort order it will be returned directly
     * (effectively performing a simple cast).
     *
     * @param source JImmutableMap containing starting key/value pairs
     */
    public static <K extends Comparable<K>, V> JImmutableMap<K, V> sortedMap(@Nonnull JImmutableMap<K, V> source)
    {
        return sortedMap(ComparableComparator.of(), source);
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
    public static <K, V> JImmutableMap<K, V> sortedMap(@Nonnull Comparator<K> comparator)
    {
        return JImmutableTreeMap.of(comparator);
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
    public static <K, V> JImmutableMap<K, V> sortedMap(@Nonnull Comparator<K> comparator,
                                                       @Nonnull Map<K, V> source)
    {
        return Functions.assignAll(JImmutableTreeMap.of(comparator), source);
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
     * @param source JImmutableMap containing starting key/value pairs
     */
    public static <K, V> JImmutableMap<K, V> sortedMap(@Nonnull Comparator<K> comparator,
                                                       @Nonnull JImmutableMap<K, V> source)
    {
        if (source instanceof JImmutableTreeMap) {
            final JImmutableTreeMap treemap = (JImmutableTreeMap)source;
            if (treemap.getComparator().equals(comparator)) {
                return source;
            }
        }
        return Functions.assignAll(JImmutableTreeMap.of(comparator), source);
    }

    /**
     * Constructs an empty map whose cursors traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * <p>
     * The map will adopt a hash code collision strategy based on
     * the first key assigned to the map.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    public static <K, V> JImmutableMap<K, V> insertOrderMap()
    {
        return JImmutableInsertOrderMap.of();
    }

    /**
     * Constructs a map whose cursors traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * All key/value pairs from source are copied into the newly created map.
     * <p>
     * The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    public static <K, V> JImmutableMap<K, V> insertOrderMap(@Nonnull Map<K, V> source)
    {
        return Functions.assignAll(JImmutableInsertOrderMap.of(), source);
    }

    /**
     * Constructs a map whose cursors traverse elements in the same order that they
     * were originally added to the map.  Similar to LinkedHapMap.
     * If source is already an in order map it is returned directly, otherwise a new map
     * is created and all key/value pairs from source are copied into the newly created map.
     * In this case the iteration order for those entries would be based on the order of elements
     * returned by source's cursor.
     * <p>
     * The map will adopt a hash code collision strategy based on
     * the first key in source.  All keys in the map must either implement Comparable (and
     * be comparable to all other keys in the map) or not implement Comparable.  Attempting to use keys
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous keys in any map.
     */
    public static <K, V> JImmutableMap<K, V> insertOrderMap(@Nonnull JImmutableMap<K, V> source)
    {
        if (source instanceof JImmutableInsertOrderMap) {
            return source;
        } else {
            return Functions.assignAll(JImmutableInsertOrderMap.of(), source);
        }
    }

    /**
     * Constructs an unsorted set.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value assigned to the set.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    public static <T> JImmutableSet<T> set()
    {
        return JImmutableHashSet.of();
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    public static <T> JImmutableSet<T> set(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableHashSet.<T>of().insertAll(source);
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @SafeVarargs
    public static <T> JImmutableSet<T> set(T... source)
    {
        return JImmutableHashSet.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    public static <T> JImmutableSet<T> set(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableHashSet.<T>of().insertAll(source);
    }

    /**
     * Constructs an unsorted set containing the values from source.
     * <p>
     * Implementation note: The set will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    public static <T> JImmutableSet<T> set(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableHashSet.<T>of().insertAll(source);
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
    @SafeVarargs
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(T... source)
    {
        return JImmutableTreeSet.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableTreeSet.<T>of().insertAll(source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableTreeSet.<T>of().insertAll(source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableTreeSet.<T>of().insertAll(source);
    }

    /**
     * Constructs an empty set that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(@Nonnull Comparator<T> comparator)
    {
        return JImmutableTreeSet.of(comparator);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(@Nonnull Comparator<T> comparator,
                                                 @Nonnull Cursor<? extends T> source)
    {
        return JImmutableTreeSet.of(comparator).insertAll(source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @SafeVarargs
    public static <T> JImmutableSet<T> sortedSet(@Nonnull Comparator<T> comparator,
                                                 T... source)
    {
        return JImmutableTreeSet.of(comparator).insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(@Nonnull Comparator<T> comparator,
                                                 @Nonnull Iterable<? extends T> source)
    {
        return JImmutableTreeSet.of(comparator).insertAll(source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableSet<T> sortedSet(@Nonnull Comparator<T> comparator,
                                                 @Nonnull Iterator<? extends T> source)
    {
        return JImmutableTreeSet.of(comparator).insertAll(source);
    }

    /**
     * Constructs an empty set that sorts values based on the order they were originally added to the set.
     */
    public static <T> JImmutableSet<T> insertOrderSet()
    {
        return JImmutableInsertOrderSet.of();
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     */
    public static <T> JImmutableSet<T> insertOrderSet(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableInsertOrderSet.<T>of().insertAll(source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     */
    @SafeVarargs
    public static <T> JImmutableSet<T> insertOrderSet(T... source)
    {
        return JImmutableInsertOrderSet.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     */
    public static <T> JImmutableSet<T> insertOrderSet(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableInsertOrderSet.<T>of().insertAll(source);
    }

    /**
     * Constructs a set containing all of the values in source that sorts values based on
     * the order they were originally added to the set.
     */
    public static <T> JImmutableSet<T> insertOrderSet(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableInsertOrderSet.<T>of().insertAll(source);
    }

    /**
     * Constructs an unsorted multiset.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value assigned to the multiset.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    public static <T> JImmutableMultiset<T> multiset()
    {
        return JImmutableHashMultiset.of();
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    public static <T> JImmutableMultiset<T> multiset(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableHashMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    @SafeVarargs
    public static <T> JImmutableMultiset<T> multiset(T... source)
    {
        return JImmutableHashMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    public static <T> JImmutableMultiset<T> multiset(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableHashMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an unsorted multiset containing the values from source.
     * <p>
     * Implementation note: The multiset will adopt a hash code collision strategy based on
     * the first value in source.  All values in the map must either implement Comparable (and
     * be comparable to all other values in the set) or not implement Comparable.  Attempting to use values
     * some of which implement Comparable and some of which do not will lead to runtime errors.  It is
     * always safest to use homogeneous values in any set.
     */
    public static <T> JImmutableMultiset<T> multiset(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableHashMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an empty set that sorts values in their natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableMultiset<T> sortedMultiset()
    {
        return JImmutableTreeMultiset.of();
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    @SafeVarargs
    public static <T extends Comparable<T>> JImmutableMultiset<T> sortedMultiset(T... source)
    {
        return JImmutableTreeMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableMultiset<T> sortedMultiset(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableTreeMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableMultiset<T> sortedMultiset(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableTreeMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values in their
     * natural sort order (using ComparableComparator).
     */
    public static <T extends Comparable<T>> JImmutableMultiset<T> sortedMultiset(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableTreeMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs an empty multiset that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableMultiset<T> sortedMultiset(@Nonnull Comparator<T> comparator)
    {
        return JImmutableTreeMultiset.of(comparator);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableMultiset<T> sortedMultiset(@Nonnull Comparator<T> comparator,
                                                           @Nonnull Cursor<? extends T> source)
    {
        return JImmutableTreeMultiset.of(comparator).insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    @SafeVarargs
    public static <T> JImmutableMultiset<T> sortedMultiset(@Nonnull Comparator<T> comparator,
                                                           T... source)
    {
        return JImmutableTreeMultiset.of(comparator).insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableMultiset<T> sortedMultiset(@Nonnull Comparator<T> comparator,
                                                           @Nonnull Iterable<? extends T> source)
    {
        return JImmutableTreeMultiset.of(comparator).insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values using comparator.
     * <p>
     * Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     */
    public static <T> JImmutableMultiset<T> sortedMultiset(@Nonnull Comparator<T> comparator,
                                                           @Nonnull Iterator<? extends T> source)
    {
        return JImmutableTreeMultiset.of(comparator).insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    public static <T> JImmutableMultiset<T> insertOrderMultiset()
    {
        return JImmutableInsertOrderMultiset.of();
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    public static <T> JImmutableMultiset<T> insertOrderMultiset(@Nonnull Cursor<? extends T> source)
    {
        return JImmutableInsertOrderMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    @SafeVarargs
    public static <T> JImmutableMultiset<T> insertOrderMultiset(T... source)
    {
        return JImmutableInsertOrderMultiset.<T>of().insertAll(Arrays.asList(source));
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    public static <T> JImmutableMultiset<T> insertOrderMultiset(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableInsertOrderMultiset.<T>of().insertAll(source);
    }

    /**
     * Constructs a multiset containing all of the values in source that sorts values based on
     * the order they were originally added to the multiset.
     */
    public static <T> JImmutableMultiset<T> insertOrderMultiset(@Nonnull Iterator<? extends T> source)
    {
        return JImmutableInsertOrderMultiset.<T>of().insertAll(source);
    }

    /**
     * Creates a list map with higher performance but no specific ordering of keys.
     */
    public static <K, V> JImmutableListMap<K, V> listMap()
    {
        return JImmutableHashListMap.of();
    }

    /**
     * Creates a list map with keys sorted by order they are inserted.
     */
    public static <K, V> JImmutableListMap<K, V> insertOrderListMap()
    {
        return JImmutableInsertOrderListMap.of();
    }

    /**
     * Creates a list map with keys sorted by their natural ordering.
     */
    public static <K extends Comparable<K>, V> JImmutableListMap<K, V> sortedListMap()
    {
        return JImmutableTreeListMap.of();
    }

    /**
     * Creates a list map with keys sorted by the specified Comparator.  The Comparator MUST BE IMMUTABLE.
     */
    public static <K, V> JImmutableListMap<K, V> sortedListMap(@Nonnull Comparator<K> comparator)
    {
        return JImmutableTreeListMap.of(comparator);
    }

    /**
     * Creates a set map with higher performance but no specific ordering of keys.
     * Sets for each key are equivalent to one created by JImmutables.set().
     */
    public static <K, V> JImmutableSetMap<K, V> setMap()
    {
        return JImmutableHashSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by order they are inserted.
     * Sets for each value are equivalent to one created by JImmutables.set().
     */
    public static <K, V> JImmutableSetMap<K, V> insertOrderSetMap()
    {
        return JImmutableInsertOrderSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by their natural ordering.
     * Sets for each key are equivalent to one created by JImmutables.set().
     */
    public static <K extends Comparable<K>, V> JImmutableSetMap<K, V> sortedSetMap()
    {
        return JImmutableTreeSetMap.of();
    }

    /**
     * Creates a set map with keys sorted by the specified Comparator.  The Comparator MUST BE IMMUTABLE.
     * Sets for each value are equivalent to one created by JImmutables.set().
     */
    public static <K, V> JImmutableSetMap<K, V> sortedSetMap(@Nonnull Comparator<K> comparator)
    {
        return JImmutableTreeSetMap.of(comparator);
    }

    /**
     * Creates a set map using the provided templates for map and set.  The templates do not have to be
     * empty but the set map will always use empty versions of them internally.  This factory method
     * provided complete flexibility in the choice of map and set types by caller.
     *
     * @param templateMap instance of the type of map to use
     * @param templateSet instance of the type of set to use
     */
    public static <K, V> JImmutableSetMap<K, V> setMap(@Nonnull JImmutableMap<K, JImmutableSet<V>> templateMap,
                                                       @Nonnull JImmutableSet<V> templateSet)
    {
        return JImmutableTemplateSetMap.of(templateMap, templateSet);
    }

    /**
     * Creates an empty sparse array that supports any integer (positive or negative) as an index.
     * Indexes do not need to be consecutive there can be gaps of any size between indexes.
     */
    public static <T> JImmutableArray<T> array()
    {
        return JImmutableTrieArray.of();
    }

    /**
     * Creates an empty sparse array that supports any integer (positive or negative) as an index.
     * Indexes do not need to be consecutive there can be gaps of any size between indexes.
     * Copies all values into the array starting at index zero.
     */
    @SafeVarargs
    public static <T> JImmutableArray<T> array(T... source)
    {
        return JImmutableTrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    public static <T> JImmutableArray<T> array(@Nonnull Cursor<JImmutableMap.Entry<Integer, T>> source)
    {
        return JImmutableTrieArray.<T>of().insertAll(source);
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    public static <T> JImmutableArray<T> array(@Nonnull Indexed<? extends T> source)
    {
        return JImmutableTrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates a sparse array containing all of the values in the specified range from source that
     * supports any integer (positive or negative) as an index.  Indexes do not need to be
     * consecutive there can be gaps of any size between indexes. Copies all entries into the
     * array using each key as an index for storing the corresponding value.  The values copied
     * from source are those whose index are in the range offset to (limit - 1).
     */
    public static <T> JImmutableArray<T> array(@Nonnull Indexed<? extends T> source,
                                               int offset,
                                               int limit)
    {
        return JImmutableTrieArray.<T>builder().add(source, offset, limit).build();
    }

    /**
     * Creates a sparse array containing all of the values from source that supports any integer
     * (positive or negative) as an index.  Indexes do not need to be consecutive there can be gaps
     * of any size between indexes. Copies all entries into the array using each key as an index
     * for storing the corresponding value.
     */
    public static <T> JImmutableArray<T> array(@Nonnull Iterable<? extends T> source)
    {
        return JImmutableTrieArray.<T>builder().add(source).build();
    }

    /**
     * Creates an empty InsertableSequence.
     */
    public static <T> InsertableSequence<T> sequence()
    {
        return EmptySequenceNode.of();
    }

    /**
     * Creates an InsertableSequence with a single value.
     */
    public static <T> InsertableSequence<T> sequence(T value)
    {
        return FilledSequenceNode.of(value);
    }
}
