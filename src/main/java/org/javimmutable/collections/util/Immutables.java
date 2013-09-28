///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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
import org.javimmutable.collections.PersistentList;
import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.PersistentRandomAccessList;
import org.javimmutable.collections.PersistentSet;
import org.javimmutable.collections.PersistentStack;
import org.javimmutable.collections.hash.PersistentHashMap;
import org.javimmutable.collections.hash.PersistentHashSet;
import org.javimmutable.collections.list.PersistentArrayList;
import org.javimmutable.collections.list.PersistentLinkedStack;
import org.javimmutable.collections.tree.PersistentTreeMap;
import org.javimmutable.collections.tree.PersistentTreeSet;
import org.javimmutable.collections.tree_list.PersistentTreeList;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * This class contains static factory methods to create instances of each of the collection interfaces.
 * Overloaded variants are provided for each to pre-populate the created collection with existing values.
 * Where possible the empty collection methods return a common singleton instance to save memory.  The
 * factory methods always return the fastest implementation of each interface (i.e. hash when sort not
 * required, trie when random access not required, etc).
 */
public final class Immutables
{
    public static <T> PersistentStack<T> stack()
    {
        return PersistentLinkedStack.of();
    }

    public static <T> PersistentStack<T> stack(T... values)
    {
        return Functions.insertAll(PersistentLinkedStack.<T>of(), values);
    }

    public static <T> PersistentStack<T> stack(Cursor<T> cursor)
    {
        return Functions.insertAll(PersistentLinkedStack.<T>of(), cursor);
    }

    public static <T> PersistentStack<T> stack(Cursorable<T> cursorable)
    {
        return Functions.insertAll(PersistentLinkedStack.<T>of(), cursorable.cursor());
    }

    public static <T> PersistentStack<T> stack(Iterator<T> iterator)
    {
        return Functions.insertAll(PersistentLinkedStack.<T>of(), iterator);
    }

    public static <T> PersistentStack<T> stack(Collection<T> collection)
    {
        return Functions.insertAll(PersistentLinkedStack.<T>of(), collection.iterator());
    }

    public static <T> PersistentList<T> list()
    {
        return PersistentArrayList.of();
    }

    public static <T> PersistentList<T> list(T... values)
    {
        return Functions.insertAll(PersistentArrayList.<T>of(), values);
    }

    public static <T> PersistentList<T> list(Cursor<T> cursor)
    {
        return Functions.insertAll(PersistentArrayList.<T>of(), cursor);
    }

    public static <T> PersistentList<T> list(Cursorable<T> cursorable)
    {
        return Functions.insertAll(PersistentArrayList.<T>of(), cursorable.cursor());
    }

    public static <T> PersistentList<T> list(Iterator<T> iterator)
    {
        return Functions.insertAll(PersistentArrayList.<T>of(), iterator);
    }

    public static <T> PersistentList<T> list(Collection<T> collection)
    {
        return Functions.insertAll(PersistentArrayList.<T>of(), collection.iterator());
    }

    public static <T> PersistentRandomAccessList<T> ralist()
    {
        return PersistentTreeList.of();
    }

    public static <T> PersistentRandomAccessList ralist(Cursor<T> cursor)
    {
        return Functions.insertAll(PersistentTreeList.<T>of(), cursor);
    }

    public static <T> PersistentRandomAccessList ralist(T... values)
    {
        return Functions.insertAll(PersistentTreeList.<T>of(), values);
    }

    public static <T> PersistentRandomAccessList ralist(Cursorable<T> cursorable)
    {
        return Functions.insertAll(PersistentTreeList.<T>of(), cursorable.cursor());
    }

    public static <T> PersistentRandomAccessList<T> ralist(Iterator<T> iterator)
    {
        return Functions.insertAll(PersistentTreeList.<T>of(), iterator);
    }

    public static <T> PersistentRandomAccessList<T> ralist(Collection<T> collection)
    {
        return Functions.insertAll(PersistentTreeList.<T>of(), collection.iterator());
    }

    public static <K, V> PersistentMap<K, V> map()
    {
        return PersistentHashMap.of();
    }

    public static <K, V> PersistentMap<K, V> map(Map<K, V> map)
    {
        return Functions.assignAll(PersistentHashMap.<K, V>of(), map);
    }

    public static <K, V> PersistentMap<K, V> map(PersistentMap<K, V> map)
    {
        return Functions.assignAll(PersistentHashMap.<K, V>of(), map);
    }

    public static <K extends Comparable<K>, V> PersistentMap<K, V> sortedMap()
    {
        return PersistentTreeMap.of();
    }

    public static <K extends Comparable<K>, V> PersistentMap<K, V> sortedMap(Map<K, V> map)
    {
        return Functions.assignAll(PersistentTreeMap.<K, V>of(), map);
    }

    public static <K extends Comparable<K>, V> PersistentMap<K, V> sortedMap(PersistentMap<K, V> map)
    {
        return Functions.assignAll(PersistentTreeMap.<K, V>of(), map);
    }

    public static <K, V> PersistentMap<K, V> sortedMap(Comparator<K> comparator)
    {
        return PersistentTreeMap.of(comparator);
    }

    public static <K, V> PersistentMap<K, V> sortedMap(Comparator<K> comparator,
                                                       Map<K, V> map)
    {
        return Functions.assignAll(PersistentTreeMap.<K, V>of(comparator), map);
    }

    public static <K, V> PersistentMap<K, V> sortedMap(Comparator<K> comparator,
                                                       PersistentMap<K, V> map)
    {
        return Functions.assignAll(PersistentTreeMap.<K, V>of(comparator), map);
    }

    public static <T> PersistentSet<T> set()
    {
        return PersistentHashSet.of();
    }

    public static <T> PersistentSet<T> set(Cursor<T> cursor)
    {
        return Functions.insertAll(PersistentHashSet.<T>of(), cursor);
    }

    public static <T> PersistentSet<T> set(T... values)
    {
        return Functions.insertAll(PersistentHashSet.<T>of(), values);
    }

    public static <T> PersistentSet<T> set(Cursorable<T> cursorable)
    {
        return Functions.insertAll(PersistentHashSet.<T>of(), cursorable.cursor());
    }

    public static <T> PersistentSet<T> set(Iterator<T> iterator)
    {
        return Functions.insertAll(PersistentHashSet.<T>of(), iterator);
    }

    public static <T> PersistentSet<T> set(Collection<T> collection)
    {
        return Functions.insertAll(PersistentHashSet.<T>of(), collection.iterator());
    }

    public static <T extends Comparable<T>> PersistentSet<T> sortedSet()
    {
        return PersistentTreeSet.of();
    }

    public static <T extends Comparable<T>> PersistentSet<T> sortedSet(T... values)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(), values);
    }

    public static <T extends Comparable<T>> PersistentSet<T> sortedSet(Cursor<T> cursor)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(), cursor);
    }

    public static <T extends Comparable<T>> PersistentSet<T> sortedSet(Cursorable<T> cursorable)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(), cursorable.cursor());
    }

    public static <T extends Comparable<T>> PersistentSet<T> sortedSet(Iterator<T> iterator)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(), iterator);
    }

    public static <T extends Comparable<T>> PersistentSet<T> sortedSet(Collection<T> collection)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(), collection.iterator());
    }

    public static <T> PersistentSet<T> sortedSet(Comparator<T> comparator)
    {
        return PersistentTreeSet.of(comparator);
    }

    public static <T> PersistentSet<T> sortedSet(Comparator<T> comparator,
                                                 Cursor<T> cursor)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(comparator), cursor);
    }

    public static <T> PersistentSet<T> sortedSet(Comparator<T> comparator,
                                                 T... values)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(comparator), values);
    }

    public static <T> PersistentSet<T> sortedSet(Comparator<T> comparator,
                                                 Cursorable<T> cursorable)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(comparator), cursorable.cursor());
    }

    public static <T> PersistentSet<T> sortedSet(Comparator<T> comparator,
                                                 Iterator<T> iterator)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(comparator), iterator);
    }

    public static <T> PersistentSet<T> sortedSet(Comparator<T> comparator,
                                                 Collection<T> collection)
    {
        return Functions.insertAll(PersistentTreeSet.<T>of(comparator), collection.iterator());
    }
}
