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

package org.javimmutable.collections.list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.javimmutable.collections.MapEntry.mapEntry;

public class ListCollisionMap<K, V>
    implements CollisionMap<K, V>
{
    private static final ListCollisionMap INSTANCE = new ListCollisionMap();

    private ListCollisionMap()
    {
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <K, V> ListCollisionMap<K, V> instance()
    {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private AbstractNode<Entry<K, V>> root(@Nonnull Node node)
    {
        return (AbstractNode<Entry<K, V>>)node;
    }

    @Nonnull
    @Override
    public Node emptyNode()
    {
        return EmptyNode.instance();
    }

    @Override
    public int size(@Nonnull Node node)
    {
        return root(node).size();
    }

    @Nonnull
    @Override
    public Node update(@Nonnull Node node,
                       @Nonnull K key,
                       @Nullable V value)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                if (e.getValue() == value) {
                    return root;
                } else {
                    return root.assign(i, mapEntry(key, value));
                }
            }
            i += 1;
        }
        return root.append(mapEntry(key, value));
    }

    @Nonnull
    @Override
    public Node update(@Nonnull Node node,
                       @Nonnull K key,
                       @Nonnull Func1<Holder<V>, V> generator)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                V value = generator.apply(Holders.of(e.getValue()));
                if (e.getValue() == value) {
                    return root;
                } else {
                    return root.assign(i, mapEntry(key, value));
                }
            }
            i += 1;
        }
        V value = generator.apply(Holders.of());
        return root.append(mapEntry(key, value));
    }

    @Nonnull
    @Override
    public Node delete(@Nonnull Node node,
                       @Nonnull K key)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        int i = 0;
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return root.delete(i);
            }
            i += 1;
        }
        return root;
    }

    @Override
    public V getValueOr(@Nonnull Node node,
                        @Nonnull K key,
                        V defaultValue)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return e.getValue();
            }
        }
        return defaultValue;
    }

    @Nonnull
    @Override
    public Holder<V> findValue(@Nonnull Node node,
                               @Nonnull K key)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return Holders.of(e.getValue());
            }
        }
        return Holders.of();
    }

    @Nonnull
    @Override
    public Holder<Entry<K, V>> findEntry(@Nonnull Node node,
                                         @Nonnull K key)
    {
        final AbstractNode<Entry<K, V>> root = root(node);
        for (Entry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return Holders.of(e);
            }
        }
        return Holders.of();
    }

    @Nullable
    @Override
    public GenericIterator.State<Entry<K, V>> iterateOverRange(@Nonnull Node node,
                                                               @Nullable GenericIterator.State<Entry<K, V>> parent,
                                                               int offset,
                                                               int limit)
    {
        return root(node).iterateOverRange(parent, offset, limit);
    }

    @Nonnull
    public SplitableIterator<Entry<K, V>> iterator(@Nonnull Node node)
    {
        return new GenericIterator<>((p, o, l) -> iterateOverRange(node, p, o, l), 0, size(node));
    }

    @Nonnull
    public SplitableIterable<Entry<K, V>> iterable(@Nonnull Node node)
    {
        return () -> iterator(node);
    }
}
