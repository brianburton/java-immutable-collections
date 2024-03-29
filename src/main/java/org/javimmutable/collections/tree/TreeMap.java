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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMap;
import org.javimmutable.collections.IMapBuilder;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.AbstractMap;
import org.javimmutable.collections.common.Conditions;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.serialization.TreeMapProxy;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Immutable
public class TreeMap<K, V>
    extends AbstractMap<K, V>
    implements Serializable
{
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final TreeMap EMPTY = new TreeMap(ComparableComparator.of(), FringeNode.instance());

    private static final long serialVersionUID = -121805;

    private final Comparator<K> comparator;
    private final AbstractNode<K, V> root;

    TreeMap(@Nonnull Comparator<K> comparator,
            @Nonnull AbstractNode<K, V> root)
    {
        this.comparator = comparator;
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <K extends Comparable<K>, V> TreeMap<K, V> of()
    {
        return EMPTY;
    }

    @Nonnull
    public static <K, V> TreeMap<K, V> of(@Nonnull Comparator<K> comparator)
    {
        return new TreeMap<>(comparator, FringeNode.instance());
    }

    @Nonnull
    public static <K extends Comparable<K>, V> IMapBuilder<K, V> builder()
    {
        return new TreeMapBuilder<>(ComparableComparator.<K>of());
    }

    @Nonnull
    public static <K, V> IMapBuilder<K, V> builder(@Nonnull Comparator<K> comparator)
    {
        return new TreeMapBuilder<>(comparator);
    }

    @Nonnull
    @Override
    public IMapBuilder<K, V> mapBuilder()
    {
        return new TreeMapBuilder<>(comparator);
    }

    @Nonnull
    public static <K extends Comparable<K>, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> createMapCollector()
    {
        return createMapCollector(ComparableComparator.<K>of());
    }

    @Nonnull
    public static <K, V> Collector<IMapEntry<K, V>, ?, IMap<K, V>> createMapCollector(@Nonnull Comparator<K> comparator)
    {
        return Collector.<IMapEntry<K, V>, IMapBuilder<K, V>, IMap<K, V>>of(() -> new TreeMapBuilder<>(comparator),
                                                                            (b, v) -> b.add(v),
                                                                            (b1, b2) -> b1.add(b2),
                                                                            b -> b.build(),
                                                                            Collector.Characteristics.CONCURRENT);
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        Conditions.stopNull(key);
        return root.get(comparator, key, defaultValue);
    }

    @Nonnull
    @Override
    public Maybe<V> find(@Nonnull K key)
    {
        Conditions.stopNull(key);
        return root.find(comparator, key);
    }

    @Nonnull
    @Override
    public Maybe<IMapEntry<K, V>> findEntry(@Nonnull K key)
    {
        Conditions.stopNull(key);
        return root.findEntry(comparator, key);
    }

    @Nonnull
    @Override
    public TreeMap<K, V> assign(@Nonnull K key,
                                V value)
    {
        Conditions.stopNull(key);
        return create(root.assign(comparator, key, value));
    }

    @Nonnull
    @Override
    public TreeMap<K, V> update(@Nonnull K key,
                                @Nonnull Func1<Maybe<V>, V> generator)
    {
        Conditions.stopNull(key);
        return create(root.update(comparator, key, generator));
    }

    @Nonnull
    @Override
    public TreeMap<K, V> delete(@Nonnull K key)
    {
        Conditions.stopNull(key);
        final AbstractNode<K, V> newRoot = root.delete(comparator, key);
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
    public TreeMap<K, V> deleteAll()
    {
        return (comparator == ComparableComparator.of()) ? EMPTY : new TreeMap<>(comparator, FringeNode.instance());
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @Nonnull
    @Override
    public SplitableIterator<IMapEntry<K, V>> iterator()
    {
        return root.iterator();
    }

    @Override
    public void forEach(@Nonnull Proc2<K, V> proc)
    {
        root.forEach(proc);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        root.forEachThrows(proc);
    }

    @Override
    public <R> R reduce(R sum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        return root.reduce(sum, proc);
    }

    @Override
    public <R, E extends Exception> R reduceThrows(R sum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return root.reduceThrows(sum, proc);
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
        return keys().stream().collect(Collectors.toList());
    }

    @Nonnull
    private TreeMap<K, V> create(AbstractNode<K, V> newRoot)
    {
        if (newRoot == root) {
            return this;
        } else {
            return new TreeMap<>(comparator, newRoot);
        }
    }

    private Object writeReplace()
    {
        return new TreeMapProxy(this);
    }
}
