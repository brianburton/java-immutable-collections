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

package org.javimmutable.collections.hash.hamt;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.hash.collision_map.CollisionMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface HamtNode<T, K, V>
    extends SplitableIterable<T>,
            Cursorable<T>,
            InvariantCheckable

{
    Holder<V> find(@Nonnull CollisionMap<T, K, V> collisionMap,
                   int hashCode,
                   @Nonnull K hashKey);

    V getValueOr(@Nonnull CollisionMap<T, K, V> collisionMap,
                 int hashCode,
                 @Nonnull K hashKey,
                 V defaultValue);

    @Nonnull
    HamtNode<T, K, V> assign(@Nonnull CollisionMap<T, K, V> collisionMap,
                             int hashCode,
                             @Nonnull K hashKey,
                             @Nullable V value,
                             @Nonnull MutableDelta sizeDelta);

    @Nonnull
    HamtNode<T, K, V> delete(@Nonnull CollisionMap<T, K, V> collisionMap,
                             int hashCode,
                             @Nonnull K hashKey,
                             @Nonnull MutableDelta sizeDelta);

    boolean isEmpty();

    @Nonnull
    SplitableIterator<JImmutableMap.Entry<K, V>> iterator(CollisionMap<T, K, V> collisionMap);

    @Nonnull
    Cursor<JImmutableMap.Entry<K, V>> cursor(CollisionMap<T, K, V> collisionMap);

    @Override
    default void checkInvariants()
    {
    }
}
