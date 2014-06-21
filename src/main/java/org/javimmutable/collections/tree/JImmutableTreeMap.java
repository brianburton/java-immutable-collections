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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.MutableDelta;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Immutable
public class JImmutableTreeMap<K, V>
        extends AbstractJImmutableMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableTreeMap EMPTY = new JImmutableTreeMap(new ComparableComparator());

    private final Comparator<K> comparator;
    private final TreeNode<K, V> root;
    private final int size;

    /**
     * Constructs an empty map using the specified Comparator.  Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     *
     * @param comparator
     */
    private JImmutableTreeMap(Comparator<K> comparator)
    {
        this(comparator, EmptyNode.<K, V>of(), 0);
    }

    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> JImmutableTreeMap<K, V> of()
    {
        return (JImmutableTreeMap<K, V>)EMPTY;
    }

    /**
     * Constructs an empty map using the specified Comparator.  Note that the Comparator MUST BE IMMUTABLE.
     * The Comparator will be retained and used throughout the life of the map and its offspring and will
     * be aggressively shared so it is imperative that the Comparator be completely immutable.
     *
     * @param comparator
     */
    public static <K, V> JImmutableTreeMap<K, V> of(Comparator<K> comparator)
    {
        return new JImmutableTreeMap<K, V>(comparator);
    }

    /**
     * Constructs a new map containing the same key/value pairs as map using a ComparableComparator
     * to compare the keys.
     *
     * @param map
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K extends Comparable<K>, V> JImmutableTreeMap<K, V> of(Map<K, V> map)
    {
        JImmutableTreeMap<K, V> answer = of();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            answer = answer.assign(entry.getKey(), entry.getValue());
        }
        return answer;
    }

    private JImmutableTreeMap(Comparator<K> comparator,
                              TreeNode<K, V> root,
                              int size)
    {
        this.comparator = comparator;
        this.root = root;
        this.size = size;
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        return root.getValueOr(comparator, key, defaultValue);
    }

    @Override
    public Holder<V> find(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        return root.find(comparator, key);
    }

    @Override
    public Holder<Entry<K, V>> findEntry(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        return root.findEntry(comparator, key);
    }

    @Override
    public JImmutableTreeMap<K, V> assign(K key,
                                          V value)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        MutableDelta sizeDelta = new MutableDelta();
        TreeNode<K, V> newRoot = root.assign(comparator, key, value, sizeDelta);
        if (newRoot == root) {
            return this;
        } else {
            return create(newRoot, sizeDelta.getValue());
        }
    }

    @Override
    public JImmutableTreeMap<K, V> delete(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        MutableDelta sizeDelta = new MutableDelta();
        TreeNode<K, V> newRoot = root.delete(comparator, key, sizeDelta);
        if (newRoot == root) {
            return this;
        } else {
            return create(newRoot, sizeDelta.getValue());
        }
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public JImmutableTreeMap<K, V> deleteAll()
    {
        return of(comparator);
    }

    @Override
    @Nonnull
    public Cursor<Entry<K, V>> cursor()
    {
        return root.cursor();
    }

    public List<K> getKeysList()
    {
        List<K> keys = new LinkedList<K>();
        for (Entry<K, V> entry : this) {
            keys.add(entry.getKey());
        }
        return Collections.unmodifiableList(keys);
    }

    public void verifyDepthsMatch()
    {
        root.verifyDepthsMatch();
    }

    public Comparator<K> getComparator()
    {
        return comparator;
    }

    private JImmutableTreeMap<K, V> create(TreeNode<K, V> root,
                                           int sizeDelta)
    {
        return new JImmutableTreeMap<K, V>(comparator, root, size + sizeDelta);
    }
}
