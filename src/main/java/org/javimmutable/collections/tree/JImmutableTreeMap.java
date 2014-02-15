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
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.cursors.StandardCursor;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public JImmutableTreeMap(Comparator<K> comparator)
    {
        this(comparator, null, 0);
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
    public Holder<V> find(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        if (root == null) {
            return Holders.of();
        } else {
            return root.find(comparator, key);
        }
    }

    @Override
    public Holder<Entry<K, V>> findEntry(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        if (root == null) {
            return Holders.of();
        } else {
            return root.findEntry(comparator, key);
        }
    }

    @Override
    public JImmutableTreeMap<K, V> assign(K key,
                                          V value)
    {
        if (key == null) {
            throw new NullPointerException();
        }
        if (root == null) {
            return create(new LeafNode<K, V>(key, value), 1);
        } else {
            UpdateResult<K, V> result = root.update(comparator, key, value);
            switch (result.type) {
            case UNCHANGED:
                return this;

            case INPLACE:
                return create(result.newNode, result.sizeDelta);

            case SPLIT:
                return create(new TwoNode<K, V>(result.newNode,
                                                result.extraNode,
                                                result.newNode.getMaxKey(),
                                                result.extraNode.getMaxKey()),
                              result.sizeDelta);
            }
            throw new RuntimeException();
        }
    }

    @Override
    public JImmutableTreeMap<K, V> delete(K key)
    {
        if (key == null) {
            throw new NullPointerException();
        }

        if (root == null) {
            return this;
        }

        DeleteResult<K, V> result = root.delete(comparator, key);
        if (result.type == DeleteResult.Type.UNCHANGED) {
            return this;
        } else {
            return create(result.node, -1);
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
    public Cursor<Entry<K, V>> cursor()
    {
        return (root == null) ? StandardCursor.<Entry<K, V>>of() : root.cursor();
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
        if (root != null) {
            root.verifyDepthsMatch();
        }
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
