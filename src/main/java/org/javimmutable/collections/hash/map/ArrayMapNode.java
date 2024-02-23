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

package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ArrayMapNode<K, V>
{
    int size(@Nonnull CollisionMap<K, V> collisionMap);

    V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                 @Nonnull K key,
                 V defaultValue);

    @Nonnull
    Maybe<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                  @Nonnull K key);

    @Nonnull
    Maybe<IMapEntry<K, V>> findEntry(@Nonnull CollisionMap<K, V> collisionMap,
                                     @Nonnull K key);

    @Nonnull
    ArrayMapNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                              @Nonnull K key,
                              V value);

    @Nullable
    ArrayMapNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                              @Nonnull K key);

    @Nonnull
    ArrayMapNode<K, V> update(@Nonnull CollisionMap<K, V> collisionMap,
                              @Nonnull K key,
                              @Nonnull Func1<Maybe<V>, V> generator);

    @Nonnull
    GenericIterator.Iterable<K> keys(@Nonnull CollisionMap<K, V> collisionMap);

    @Nonnull
    GenericIterator.Iterable<V> values(@Nonnull CollisionMap<K, V> collisionMap);

    @Nonnull
    GenericIterator.Iterable<IMapEntry<K, V>> entries(@Nonnull CollisionMap<K, V> collisionMap);

    void forEach(@Nonnull CollisionMap<K, V> collisionMap,
                 @Nonnull Proc2<K, V> proc);

    <E extends Exception> void forEachThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                             @Nonnull Proc2Throws<K, V, E> proc)
        throws E;
}
