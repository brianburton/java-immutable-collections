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

package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MapEmptyNode<K, V>
    implements MapNode<K, V>
{
    private static final MapEmptyNode EMPTY = new MapEmptyNode();


    @SuppressWarnings("unchecked")
    public static <K, V> MapNode<K, V> of()
    {
        return EMPTY;
    }

    @Override
    public Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                          int hashCode,
                          @Nonnull K hashKey)
    {
        return Holders.of();
    }

    @Override
    public V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                        int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        return defaultValue;
    }

    @Nonnull
    @Override
    public MapNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey,
                                @Nullable V value)
    {
        return new MapSingleKeyLeafNode<>(hashCode, hashKey, value);
    }

    @Nonnull
    @Override
    public MapNode<K, V> update(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey,
                                @Nonnull Func1<Holder<V>, V> generator)
    {
        return new MapSingleKeyLeafNode<>(hashCode, hashKey, generator.apply(Holders.of()));
    }

    @Nonnull
    @Override
    public MapNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                                int hashCode,
                                @Nonnull K hashKey)
    {
        return this;
    }

    @Override
    public int size(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return 0;
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public boolean isEmpty(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return true;
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<K, V>> iterateOverRange(@Nonnull CollisionMap<K, V> collisionMap,
                                                                             @Nullable GenericIterator.State<JImmutableMap.Entry<K, V>> parent,
                                                                             int offset,
                                                                             int limit)
    {
        assert offset == 0 && limit == 0;
        return parent;
    }

    @Override
    public void forEach(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull Proc2<K, V> proc)
    {
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                    @Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
    }

    @Override
    public <R> R reduce(@Nonnull CollisionMap<K, V> collisionMap,
                        R sum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        return sum;
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                   R sum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return sum;
    }

    @Nonnull
    @Override
    public MapNode<K, V> liftNode(int index)
    {
        throw new UnsupportedOperationException();
    }
}
