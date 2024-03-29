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
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

import static org.javimmutable.collections.MapEntry.entry;

/**
 * CollisionMap implementation that stores values in Node objects (balanced trees).
 * Usable with keys that implement Comparable.  Will fail with any other
 * type of key.
 */
@Immutable
public class TreeCollisionMap<K, V>
    implements CollisionMap<K, V>
{
    @SuppressWarnings("unchecked")
    private static final TreeCollisionMap EMPTY = new TreeCollisionMap(ComparableComparator.of());

    private final Comparator<K> comparator;

    private TreeCollisionMap(@Nonnull Comparator<K> comparator)
    {
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> TreeCollisionMap<K, V> instance()
    {
        return EMPTY;
    }

    @SuppressWarnings("unchecked")
    private AbstractNode<K, V> root(@Nonnull CollisionMap.Node node)
    {
        return (AbstractNode<K, V>)node;
    }

    @Nonnull
    @Override
    public Node empty()
    {
        return FringeNode.instance();
    }

    @Nonnull
    @Override
    public Node single(@Nonnull K key,
                       @Nullable V value)
    {
        return ValueNode.instance(key, value);
    }

    @Nonnull
    @Override
    public Node dual(@Nonnull K key1,
                     @Nullable V value1,
                     @Nonnull K key2,
                     @Nullable V value2)
    {
        return ValueNode.instance(comparator, key1, value1, key2, value2);
    }

    @Override
    public int size(@Nonnull Node node)
    {
        return root(node).size();
    }

    @Nonnull
    @Override
    public Node update(@Nonnull Node node,
                       @Nonnull K key,
                       @Nullable V value)
    {
        return root(node).assign(comparator, key, value);
    }

    @Nonnull
    @Override
    public Node update(@Nonnull Node node,
                       @Nonnull K key,
                       @Nonnull Func1<Maybe<V>, V> generator)
    {
        return root(node).update(comparator, key, generator);
    }

    @Nonnull
    @Override
    public Node delete(@Nonnull Node node,
                       @Nonnull K key)
    {
        return root(node).delete(comparator, key);
    }

    @Override
    public V getValueOr(@Nonnull Node node,
                        @Nonnull K key,
                        V defaultValue)
    {
        return root(node).get(comparator, key, defaultValue);
    }

    @Nonnull
    @Override
    public Maybe<V> findValue(@Nonnull Node node,
                              @Nonnull K key)
    {
        return root(node).find(comparator, key);
    }

    @Nonnull
    @Override
    public Maybe<IMapEntry<K, V>> findEntry(@Nonnull Node node,
                                            @Nonnull K key)
    {
        return root(node).findEntry(comparator, key);
    }

    @Nonnull
    @Override
    public IMapEntry<K, V> first(@Nonnull Node node)
    {
        final AbstractNode<K, V> first = root(node).leftMost();
        return entry(first.key(), first.value());
    }

    @Nullable
    @Override
    public GenericIterator.State<IMapEntry<K, V>> iterateOverRange(@Nonnull Node node,
                                                                   @Nullable GenericIterator.State<IMapEntry<K, V>> parent,
                                                                   int offset,
                                                                   int limit)
    {
        return root(node).iterateOverRange(parent, offset, limit);
    }

    @Override
    public void forEach(@Nonnull Node node,
                        @Nonnull Proc2<K, V> proc)
    {
        root(node).forEach(proc);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Node node,
                                                    @Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        root(node).forEachThrows(proc);
    }

    @Override
    public <R> R reduce(@Nonnull Node node,
                        R sum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        return root(node).reduce(sum, proc);
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull Node node,
                                                   R sum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return root(node).reduceThrows(sum, proc);
    }
}
