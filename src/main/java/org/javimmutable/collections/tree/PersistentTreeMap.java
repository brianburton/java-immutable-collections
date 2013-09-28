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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Addable;
import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.common.AbstractPersistentMap;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.MapAdaptor;
import org.javimmutable.collections.cursors.EmptyCursor;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PersistentTreeMap<K, V>
        extends AbstractPersistentMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final PersistentTreeMap EMPTY = new PersistentTreeMap(new ComparableComparator());

    private final Comparator<K> properties;
    private final TreeNode<K, V> root;
    private final int size;

    public PersistentTreeMap(Comparator<K> comparator)
    {
        this(comparator, null, 0);
    }

    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>, V> PersistentTreeMap<K, V> of()
    {
        return (PersistentTreeMap<K, V>)EMPTY;
    }

    public static <K, V> PersistentTreeMap<K, V> of(Comparator<K> comparator)
    {
        return new PersistentTreeMap<K, V>(comparator);
    }

    public static <K extends Comparable<K>, V> PersistentTreeMap<K, V> of(Map<K, V> map)
    {
        PersistentTreeMap<K, V> answer = of();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            answer = answer.set(entry.getKey(), entry.getValue());
        }
        return answer;
    }

    private PersistentTreeMap(Comparator<K> properties,
                              TreeNode<K, V> root,
                              int size)
    {
        this.properties = properties;
        this.root = root;
        this.size = size;
    }

    @Override
    public V get(K key)
    {
        return find(key).getValueOrNull();
    }

    @Override
    public Holder<V> find(K key)
    {
        if (root == null) {
            return Holders.of();
        } else {
            return root.find(properties, key);
        }
    }

    @Override
    public Holder<Entry<K, V>> findEntry(K key)
    {
        if (root == null) {
            return Holders.of();
        } else {
            return root.findEntry(properties, key);
        }
    }

    @Override
    public PersistentTreeMap<K, V> set(K key,
                                       V value)
    {
        if (root == null) {
            return create(new LeafNode<K, V>(key, value), 1);
        } else {
            UpdateResult<K, V> result = root.update(properties, key, value);
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
    public PersistentTreeMap<K, V> remove(K key)
    {
        if (root == null) {
            return this;
        }

        DeleteResult<K, V> result = root.delete(properties, key);
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
    public Map<K, V> asMap()
    {
        return MapAdaptor.of(this);
    }

    /**
     * Adds the key/value pair to this map.  Any value already existing for the specified key
     * is replaced with the new value.
     *
     * @param e
     * @return
     */
    @Override
    public Addable<Entry<K, V>> add(Entry<K, V> e)
    {
        return set(e.getKey(), e.getValue());
    }

    @Override
    public Iterator<Entry<K, V>> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return (root == null) ? EmptyCursor.<Entry<K, V>>of() : root.cursor();
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

    private PersistentTreeMap<K, V> create(TreeNode<K, V> root,
                                           int sizeDelta)
    {
        return new PersistentTreeMap<K, V>(properties, root, size + sizeDelta);
    }
}
