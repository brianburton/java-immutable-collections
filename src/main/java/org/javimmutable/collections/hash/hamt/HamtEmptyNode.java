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

package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.hash.collision_map.CollisionMap;
import org.javimmutable.collections.iterators.EmptyIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HamtEmptyNode<T, K, V>
    implements HamtNode<T, K, V>
{
    private static final HamtEmptyNode EMPTY = new HamtEmptyNode();


    @SuppressWarnings("unchecked")
    public static <T, K, V> HamtNode<T, K, V> of()
    {
        return EMPTY;
    }

    @Override
    public Holder<V> find(@Nonnull CollisionMap<T, K, V> collisionMap,
                          int hashCode,
                          @Nonnull K hashKey)
    {
        return Holders.of();
    }

    @Override
    public V getValueOr(@Nonnull CollisionMap<T, K, V> collisionMap,
                        int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        return defaultValue;
    }

    @Nonnull
    @Override
    public HamtNode<T, K, V> assign(@Nonnull CollisionMap<T, K, V> collisionMap,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nullable V value,
                                    @Nonnull MutableDelta sizeDelta)
    {
        return new HamtLeafNode<>(hashCode, collisionMap.update(null, hashKey, value, sizeDelta));
    }

    @Nonnull
    @Override
    public HamtNode<T, K, V> update(@Nonnull CollisionMap<T, K, V> collisionMap,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nonnull Func1<Holder<V>, V> generator,
                                    @Nonnull MutableDelta sizeDelta)
    {
        return new HamtLeafNode<>(hashCode, collisionMap.update(null, hashKey, generator, sizeDelta));
    }

    @Nonnull
    @Override
    public HamtNode<T, K, V> delete(@Nonnull CollisionMap<T, K, V> collisionMap,
                                    int hashCode,
                                    @Nonnull K hashKey,
                                    @Nonnull MutableDelta sizeDelta)
    {
        return this;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(CollisionMap<T, K, V> collisionMap)
    {
        return EmptyIterator.of();
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return EmptyIterator.of();
    }
}
