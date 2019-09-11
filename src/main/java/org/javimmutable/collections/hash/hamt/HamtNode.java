///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface HamtNode<K, V>
{
    Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                   int hashCode,
                   @Nonnull K hashKey);

    V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                 int hashCode,
                 @Nonnull K hashKey,
                 V defaultValue);

    @Nonnull
    HamtNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                          int hashCode,
                          @Nonnull K hashKey,
                          @Nullable V value);

    @Nonnull
    HamtNode<K, V> update(@Nonnull CollisionMap<K, V> collisionMap,
                          int hashCode,
                          @Nonnull K hashKey,
                          @Nonnull Func1<Holder<V>, V> generator);

    @Nonnull
    HamtNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                          int hashCode,
                          @Nonnull K hashKey);

    boolean isEmpty(@Nonnull CollisionMap<K, V> collisionMap);

    int size(@Nonnull CollisionMap<K, V> collisionMap);

    default void checkInvariants(@Nonnull CollisionMap<K, V> collisionMap)
    {
    }

    @Nullable
    GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nonnull CollisionMap<K, V> collisionMap,
                                                                      @Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                      int offset,
                                                                      int limit);

    @Nonnull
    default GenericIterator.Iterable<JImmutableMap.Entry<K, V>> genericIterable(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return new GenericIterator.Iterable<JImmutableMap.Entry<K, V>>()
        {
            @Nullable
            @Override
            public GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                                     int offset,
                                                                                     int limit)
            {
                return HamtNode.this.iterateOverRange(collisionMap, parent, offset, limit);
            }

            @Override
            public int iterableSize()
            {
                return size(collisionMap);
            }
        };
    }

    @Nonnull
    default SplitableIterable<JImmutableMap.Entry<K, V>> iterable(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return () -> iterator(collisionMap);
    }

    @Nonnull
    default SplitableIterator<JImmutableMap.Entry<K, V>> iterator(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return new GenericIterator<>(genericIterable(collisionMap), 0, size(collisionMap));
    }

    void forEach(@Nonnull CollisionMap<K, V> collisionMap,
                 @Nonnull Proc2<K, V> proc);

    <E extends Exception> void forEachThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                             @Nonnull Proc2Throws<K, V, E> proc)
        throws E;

    <R> R reduce(@Nonnull CollisionMap<K, V> collisionMap,
                 R sum,
                 @Nonnull Sum2<K, V, R> proc);

    <R, E extends Exception> R reduceThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                            R sum,
                                            @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E;
}
