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

package org.javimmutable.collections.hash.map;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ArrayMultiValueMapNode<K, V>
    implements ArrayMapNode<K, V>
{
    @Nonnull
    private final CollisionMap.Node node;

    public ArrayMultiValueMapNode(@Nonnull CollisionMap.Node node)
    {
        this.node = node;
    }

    @Override
    public int size(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return collisionMap.size(node);
    }

    @Override
    public V getValueOr(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull K key,
                        V defaultValue)
    {
        return collisionMap.getValueOr(node, key, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<V> find(@Nonnull CollisionMap<K, V> collisionMap,
                          @Nonnull K key)
    {
        return collisionMap.findValue(node, key);
    }

    @Nonnull
    @Override
    public Holder<IMapEntry<K, V>> findEntry(@Nonnull CollisionMap<K, V> collisionMap,
                                             @Nonnull K key)
    {
        return collisionMap.findEntry(node, key);
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> assign(@Nonnull CollisionMap<K, V> collisionMap,
                                     @Nonnull K key,
                                     V value)
    {
        final CollisionMap.Node thisNode = this.node;
        final CollisionMap.Node newNode = collisionMap.update(thisNode, key, value);
        if (newNode == thisNode) {
            return this;
        } else {
            return new ArrayMultiValueMapNode<>(newNode);
        }
    }

    @Nonnull
    @Override
    public ArrayMapNode<K, V> update(@Nonnull CollisionMap<K, V> collisionMap,
                                     @Nonnull K key,
                                     @Nonnull Func1<Holder<V>, V> generator)
    {
        final CollisionMap.Node thisNode = this.node;
        final CollisionMap.Node newNode = collisionMap.update(thisNode, key, generator);
        if (newNode == thisNode) {
            return this;
        } else {
            return new ArrayMultiValueMapNode<>(newNode);
        }
    }

    @Nullable
    @Override
    public ArrayMapNode<K, V> delete(@Nonnull CollisionMap<K, V> collisionMap,
                                     @Nonnull K key)
    {
        final CollisionMap.Node thisNode = this.node;
        final CollisionMap.Node newNode = collisionMap.delete(thisNode, key);
        if (newNode == thisNode) {
            return this;
        } else {
            final int newSize = collisionMap.size(newNode);
            switch (newSize) {
                case 0:
                    return null;
                case 1:
                    return new ArraySingleValueMapNode<>(collisionMap.first(newNode));
                default:
                    return new ArrayMultiValueMapNode<>(newNode);
            }
        }
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<K> keys(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return GenericIterator.transformIterable(collisionMap.genericIterable(node), IMapEntry::getKey);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<V> values(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return GenericIterator.transformIterable(collisionMap.genericIterable(node), IMapEntry::getValue);
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<IMapEntry<K, V>> entries(@Nonnull CollisionMap<K, V> collisionMap)
    {
        return collisionMap.genericIterable(node);
    }

    @Override
    public void forEach(@Nonnull CollisionMap<K, V> collisionMap,
                        @Nonnull Proc2<K, V> proc)
    {
        collisionMap.forEach(node, proc);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionMap<K, V> collisionMap,
                                                    @Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        collisionMap.forEachThrows(node, proc);
    }
}
