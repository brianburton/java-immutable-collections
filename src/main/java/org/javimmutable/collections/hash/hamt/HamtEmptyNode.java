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
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECINDIRECINCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACSTRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.EmptyIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HamtEmptyNode<K, V>
    implements HamtNode<K, V>
{
    private static final HamtEmptyNode EMPTY = new HamtEmptyNode();


    @SuppressWarnings("unchecked")
    public static <K, V> HamtNode<K, V> of()
    {
        return EMPTY;
    }

    @Override
    public Holder<V> find(int hashCode,
                          @Nonnull K hashKey)
    {
        return Holders.of();
    }

    @Override
    public V getValueOr(int hashCode,
                        @Nonnull K hashKey,
                        V defaultValue)
    {
        return defaultValue;
    }

    @Nonnull
    @Override
    public HamtNode<K, V> assign(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey,
                                 @Nullable V value)
    {
        return new HamtLeafNode<>(hashCode, emptyMap.update(hashKey, value));
    }

    @Nonnull
    @Override
    public HamtNode<K, V> update(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey,
                                 @Nonnull Func1<Holder<V>, V> generator)
    {
        return new HamtLeafNode<>(hashCode, emptyMap.update(hashKey, generator));
    }

    @Nonnull
    @Override
    public HamtNode<K, V> delete(@Nonnull CollisionMap<K, V> emptyMap,
                                 int hashCode,
                                 @Nonnull K hashKey)
    {
        return this;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
    {
        return EmptyIterator.of();
    }
}
