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

package org.javimmutable.collections.list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    private AbstractNode<IMapEntry<K, V>> root(@Nonnull Node node)
    {
        return (AbstractNode<IMapEntry<K, V>>)node;
    }

    @Nonnull
    @Override
    public Node empty()
    {
        return EmptyNode.instance();
    }

    @Nonnull
    @Override
    public Node single(@Nonnull K key,
                       @Nullable V value)
    {
        return new OneValueNode<>(IMapEntry.of(key, value));
    }

    @Nonnull
    @Override
    public Node dual(@Nonnull K key1,
                     @Nullable V value1,
                     @Nonnull K key2,
                     @Nullable V value2)
    {
        return new MultiValueNode<>(IMapEntry.of(key1, value1), IMapEntry.of(key2, value2));
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
        final AbstractNode<IMapEntry<K, V>> root = root(node);
        int i = 0;
        for (IMapEntry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                if (e.getValue() == value) {
                    return root;
                } else {
                    return root.assign(i, IMapEntry.of(key, value));
                }
            }
            i += 1;
        }
        return root.append(IMapEntry.of(key, value));
    }

    @Nonnull
    @Override
    public Node update(@Nonnull Node node,
                       @Nonnull K key,
                       @Nonnull Func1<Maybe<V>, V> generator)
    {
        final AbstractNode<IMapEntry<K, V>> root = root(node);
        int i = 0;
        for (IMapEntry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                V value1 = e.getValue();
                V value = generator.apply(Maybe.of(value1));
                if (e.getValue() == value) {
                    return root;
                } else {
                    return root.assign(i, IMapEntry.of(key, value));
                }
            }
            i += 1;
        }
        V value = generator.apply(Maybe.empty());
        return root.append(IMapEntry.of(key, value));
    }

    @Nonnull
    @Override
    public Node delete(@Nonnull Node node,
                       @Nonnull K key)
    {
        final AbstractNode<IMapEntry<K, V>> root = root(node);
        int i = 0;
        for (IMapEntry<K, V> e : root) {
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
        final AbstractNode<IMapEntry<K, V>> root = root(node);
        for (IMapEntry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return e.getValue();
            }
        }
        return defaultValue;
    }

    @Nonnull
    @Override
    public Maybe<V> findValue(@Nonnull Node node,
                              @Nonnull K key)
    {
        final AbstractNode<IMapEntry<K, V>> root = root(node);
        for (IMapEntry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                V value = e.getValue();
                return Maybe.of(value);
            }
        }
        return Maybe.empty();
    }

    @Nonnull
    @Override
    public Maybe<IMapEntry<K, V>> findEntry(@Nonnull Node node,
                                            @Nonnull K key)
    {
        final AbstractNode<IMapEntry<K, V>> root = root(node);
        for (IMapEntry<K, V> e : root) {
            if (e.getKey().equals(key)) {
                return Maybe.of(e);
            }
        }
        return Maybe.empty();
    }

    @Nonnull
    @Override
    public IMapEntry<K, V> first(@Nonnull Node node)
    {
        return root(node).get(0);
    }

    @Nullable
    @Override
    public GenericIterator.State<IMapEntry<K, V>> iterateOverRange(@Nonnull Node node,
                                                                   @Nullable GenericIterator.State<IMapEntry<K, V>> parent,
                                                                   int offset,
                                                                   int limit)
    {
        return root(node).iterateOverRange(parent, offset, limit);
    }

    @Override
    public void forEach(@Nonnull Node node,
                        @Nonnull Proc2<K, V> proc)
    {
        root(node).forEach(e -> proc.apply(e.getKey(), e.getValue()));
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Node node,
                                                    @Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
        root(node).forEachThrows(e -> proc.apply(e.getKey(), e.getValue()));
    }

    @Override
    public <R> R reduce(@Nonnull Node node,
                        R sum,
                        @Nonnull Sum2<K, V, R> proc)
    {
        return root(node).reduce(sum, (s, e) -> proc.apply(s, e.getKey(), e.getValue()));
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull Node node,
                                                   R sum,
                                                   @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return root(node).reduceThrows(sum, (s, e) -> proc.apply(s, e.getKey(), e.getValue()));
    }
}
