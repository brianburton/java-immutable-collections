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
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.trie32.Transforms;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.iterators.SingleValueIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SingleKeyTransforms<K, V>
    implements Transforms<MapEntry<K, V>, K, V>
{
    @Nonnull
    @Override
    public MapEntry<K, V> update(@Nullable MapEntry<K, V> leaf,
                                 @Nonnull K key,
                                 @Nullable V value,
                                 @Nonnull MutableDelta delta)
    {
        if (leaf == null) {
            delta.add(1);
            return MapEntry.of(key, value);
        } else {
            if (leaf.getKey().equals(key) && leaf.getValue() == value) {
                return leaf;
            } else {
                return MapEntry.of(key, value);
            }
        }
    }

    @Override
    public MapEntry<K, V> delete(@Nonnull MapEntry<K, V> leaf,
                                 @Nonnull K key,
                                 @Nonnull MutableDelta delta)
    {
        if (leaf.getKey().equals(key)) {
            delta.subtract(1);
            return null;
        } else {
            return leaf;
        }
    }

    @Override
    public V getValueOr(@Nonnull MapEntry<K, V> leaf,
                        @Nonnull K key,
                        V defaultValue)
    {
        return leaf.getKey().equals(key) ? leaf.getValue() : defaultValue;
    }

    @Override
    public Holder<V> findValue(@Nonnull MapEntry<K, V> leaf,
                               @Nonnull K key)
    {
        return leaf.getKey().equals(key) ? Holders.of(leaf.getValue()) : Holders.of();
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(@Nonnull MapEntry<K, V> leaf,
                                                       @Nonnull K key)
    {
        return leaf.getKey().equals(key) ? Holders.of(leaf) : Holders.of();
    }

    @Override
    public Cursor<JImmutableMap.Entry<K, V>> cursor(@Nonnull MapEntry<K, V> leaf)
    {
        return SingleValueCursor.of(leaf);
    }

    @Override
    public SplitableIterator<JImmutableMap.Entry<K, V>> iterator(@Nonnull MapEntry<K, V> leaf)
    {
        return SingleValueIterator.of(leaf);
    }
}
