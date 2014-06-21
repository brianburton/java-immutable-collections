///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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

package org.javimmutable.collections.hash;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.AbstractJImmutableMap;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Singleton implementation of JImmutableMap that contains no elements.
 * When a value is assigned to the map a JImmutableHashMap is created that
 * manages hash collisions using a tree if key is Comparable or a list otherwise.
 *
 * @param <K>
 * @param <V>
 */
@Immutable
public class EmptyHashMap<K, V>
        extends AbstractJImmutableMap<K, V>
{
    static final EmptyHashMap INSTANCE = new EmptyHashMap();

    private EmptyHashMap()
    {
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull K key)
    {
        return Holders.of();
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull K key)
    {
        return Holders.of();
    }

    @Nonnull
    @Override
    public JImmutableMap<K, V> assign(@Nonnull K key,
                                      V value)
    {
        return JImmutableHashMap.<K, V>forKey(key).assign(key, value);
    }

    @Nonnull
    @Override
    public JImmutableMap<K, V> delete(@Nonnull K key)
    {
        return this;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Nonnull
    @Override
    public JImmutableMap<K, V> deleteAll()
    {
        return this;
    }

    @Override
    @Nonnull
    public Cursor<Entry<K, V>> cursor()
    {
        return StandardCursor.of();
    }

    @Override
    public V getValueOr(K key,
                        V defaultValue)
    {
        return defaultValue;
    }
}
