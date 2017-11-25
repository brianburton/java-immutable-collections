///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.TransformStreamable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HamtEmptyNode<T>
    implements HamtNode<T>
{
    private static final HamtEmptyNode EMPTY = new HamtEmptyNode();


    @SuppressWarnings("unchecked")
    public static <T> HamtNode<T> of()
    {
        return EMPTY;
    }

    @Override
    public <K, V> Holder<V> find(@Nonnull Transforms<T, K, V> transforms,
                                 int hashCode,
                                 @Nonnull K hashKey)
    {
        return Holders.of();
    }

    @Override
    public <K, V> V getValueOr(@Nonnull Transforms<T, K, V> transforms,
                               int hashCode,
                               @Nonnull K hashKey,
                               V defaultValue)
    {
        return defaultValue;
    }

    @Nonnull
    @Override
    public <K, V> HamtNode<T> assign(@Nonnull Transforms<T, K, V> transforms,
                                     int hashCode,
                                     @Nonnull K hashKey,
                                     @Nullable V value,
                                     @Nonnull MutableDelta sizeDelta)
    {
        return HamtBranchNode.<T>of().assign(transforms, hashCode, hashKey, value, sizeDelta);
    }

    @Nonnull
    @Override
    public <K, V> HamtNode<T> delete(@Nonnull Transforms<T, K, V> transforms,
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
    public <K, V> IterableStreamable<JImmutableMap.Entry<K, V>> entries(@Nonnull Transforms<T, K, V> transforms)
    {
        return new IterableStreamable<JImmutableMap.Entry<K, V>>()
        {
            @Nonnull
            @Override
            public SplitableIterator<JImmutableMap.Entry<K, V>> iterator()
            {
                return EmptyIterator.of();
            }

            @Override
            public int getSpliteratorCharacteristics()
            {
                return StreamConstants.SPLITERATOR_UNORDERED;
            }
        };
    }

    @Nonnull
    @Override
    public <K, V> IterableStreamable<K> keys(@Nonnull Transforms<T, K, V> transforms)
    {
        return TransformStreamable.ofKeys(entries(transforms));
    }

    @Nonnull
    @Override
    public <K, V> IterableStreamable<V> values(@Nonnull Transforms<T, K, V> transforms)
    {
        return TransformStreamable.ofValues(entries(transforms));
    }

    @Nonnull
    @Override
    public <K, V> SplitableIterator<JImmutableMap.Entry<K, V>> iterator(Transforms<T, K, V> transforms)
    {
        return EmptyIterator.of();
    }

    @Nonnull
    @Override
    public <K, V> Cursor<JImmutableMap.Entry<K, V>> cursor(Transforms<T, K, V> transforms)
    {
        return StandardCursor.of();
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return EmptyIterator.of();
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of();
    }
}
