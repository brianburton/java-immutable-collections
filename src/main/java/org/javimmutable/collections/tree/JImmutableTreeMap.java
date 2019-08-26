///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.common.Conditions;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.serialization.JImmutableTreeMapProxy;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Immutable
public class JImmutableTreeMap<K, V>
    extends AbstractJImmutableMap<K, V>
    implements Serializable
{
    @SuppressWarnings("unchecked")
    private static final JImmutableTreeMap EMPTY = new JImmutableTreeMap(ComparableComparator.of(), FringeNode.instance());

    private static final long serialVersionUID = -121805;

    private final Comparator<K> comparator;
    private final Node<K, V> root;

    private JImmutableTreeMap(@Nonnull Comparator<K> comparator,
                              @Nonnull Node<K, V> root)
    {
        this.comparator = comparator;
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <K extends Comparable<K>, V> JImmutableTreeMap<K, V> of()
    {
        return EMPTY;
    }

    @Nonnull
    public static <K, V> JImmutableTreeMap<K, V> of(@Nonnull Comparator<K> comparator)
    {
        return new JImmutableTreeMap<>(comparator, FringeNode.instance());
    }

    /**
     * Constructs a new map containing the same key/value pairs as map using a ComparableComparator
     * to compare the keys.
     */
    @Nonnull
    public static <K extends Comparable<K>, V> JImmutableTreeMap<K, V> of(@Nonnull Iterable<Map.Entry<K, V>> map)
    {
        final Comparator<K> comp = ComparableComparator.of();
        Node<K, V> root = FringeNode.instance();
        for (Map.Entry<K, V> entry : map) {
            root = root.assign(comp, entry.getKey(), entry.getValue());
        }
        return root.isEmpty() ? of() : new JImmutableTreeMap<>(comp, root);
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        Conditions.stopNull(key);
        return root.getOr(comparator, key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull K key)
    {
        Conditions.stopNull(key);
        return root.find(comparator, key);
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull K key)
    {
        Conditions.stopNull(key);
        return root.findEntry(comparator, key);
    }

    @Nonnull
    @Override
    public JImmutableTreeMap<K, V> assign(@Nonnull K key,
                                          V value)
    {
        Conditions.stopNull(key);
        return create(root.assign(comparator, key, value));
    }

    @Nonnull
    @Override
    public JImmutableTreeMap<K, V> update(@Nonnull K key,
                                          @Nonnull Func1<Holder<V>, V> generator)
    {
        Conditions.stopNull(key);
        return create(root.update(comparator, key, generator));
    }

    @Nonnull
    @Override
    public JImmutableTreeMap<K, V> delete(@Nonnull K key)
    {
        Conditions.stopNull(key);
        final Node<K, V> newRoot = root.delete(comparator, key);
        if (newRoot.isEmpty()) {
            return deleteAll();
        } else {
            return create(newRoot);
        }
    }

    @Override
    public int size()
    {
        return root.size();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public JImmutableTreeMap<K, V> deleteAll()
    {
        return (comparator == ComparableComparator.of()) ? EMPTY : new JImmutableTreeMap<>(comparator, FringeNode.instance());
    }

    @Nonnull
    @Override
    public Cursor<Entry<K, V>> cursor()
    {
        return root.cursor();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @Nonnull
    @Override
    public SplitableIterator<Entry<K, V>> iterator()
    {
        return root.iterator();
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants(comparator);
    }

    @Nonnull
    public Comparator<K> getComparator()
    {
        return comparator;
    }

    @Nonnull
    List<K> getKeysList()
    {
        List<K> keys = new LinkedList<>();
        for (Entry<K, V> entry : this) {
            keys.add(entry.getKey());
        }
        return Collections.unmodifiableList(keys);
    }

    @Nonnull
    private JImmutableTreeMap<K, V> create(Node<K, V> newRoot)
    {
        if (newRoot == root) {
            return this;
        } else {
            return new JImmutableTreeMap<>(comparator, newRoot);
        }
    }

    private Object writeReplace()
    {
        return new JImmutableTreeMapProxy(this);
    }
}
