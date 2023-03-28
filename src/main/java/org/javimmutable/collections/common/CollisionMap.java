///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2023, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.iterators.GenericIterator;

/**
 * Interface for simple collection objects that manage the contents of leaf nodes in the hash table.
 * Implementations are free to use any class for their leaf nodes and manage them as needed.
 */
public interface CollisionMap<K, V>
{
    interface Node
    {
    }

    @Nonnull
    Node empty();

    @Nonnull
    Node single(@Nonnull K key,
                @Nullable V value);

    @Nonnull
    Node dual(@Nonnull K key1,
              @Nullable V value1,
              @Nonnull K key2,
              @Nullable V value2);

    int size(@Nonnull Node node);

    @Nonnull
    Node update(@Nonnull Node node,
                @Nonnull K key,
                @Nullable V value);

    @Nonnull
    Node update(@Nonnull Node node,
                @Nonnull K key,
                @Nonnull Func1<Holder<V>, V> generator);

    @Nonnull
    Node delete(@Nonnull Node node,
                @Nonnull K key);

    V getValueOr(@Nonnull Node node,
                 @Nonnull K key,
                 V defaultValue);

    @Nonnull
    Holder<V> findValue(@Nonnull Node node,
                        @Nonnull K key);

    @Nonnull
    Holder<IMapEntry<K, V>> findEntry(@Nonnull Node node,
                                      @Nonnull K key);

    @Nonnull
    IMapEntry<K, V> first(@Nonnull Node node);

    @Nullable
    GenericIterator.State<IMapEntry<K, V>> iterateOverRange(@Nonnull Node node,
                                                            @Nullable GenericIterator.State<IMapEntry<K, V>> parent,
                                                            int offset,
                                                            int limit);

    @Nonnull
    default GenericIterator.Iterable<IMapEntry<K, V>> genericIterable(@Nonnull Node node)
    {
        return new GenericIterator.Iterable<IMapEntry<K, V>>()
        {
            @Nullable
            @Override
            public GenericIterator.State<IMapEntry<K, V>> iterateOverRange(@Nullable GenericIterator.State<IMapEntry<K, V>> parent,
                                                                           int offset,
                                                                           int limit)
            {
                return CollisionMap.this.iterateOverRange(node, parent, offset, limit);
            }

            @Override
            public int iterableSize()
            {
                return size(node);
            }
        };
    }

    @Nonnull
    default SplitableIterable<IMapEntry<K, V>> iterable(@Nonnull CollisionMap.Node node)
    {
        return genericIterable(node);
    }

    @Nonnull
    default SplitableIterator<IMapEntry<K, V>> iterator(@Nonnull Node node)
    {
        return genericIterable(node).iterator();
    }

    void forEach(@Nonnull Node node,
                 @Nonnull Proc2<K, V> proc);

    <E extends Exception> void forEachThrows(@Nonnull Node node,
                                             @Nonnull Proc2Throws<K, V, E> proc)
        throws E;

    <R> R reduce(@Nonnull Node node,
                 R sum,
                 @Nonnull Sum2<K, V, R> proc);

    <R, E extends Exception> R reduceThrows(@Nonnull Node node,
                                            R sum,
                                            @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E;
}
