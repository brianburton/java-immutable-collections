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

package org.javimmutable.collection.tree;

import org.javimmutable.collection.Func1;
import org.javimmutable.collection.IMapEntry;
import org.javimmutable.collection.Maybe;
import org.javimmutable.collection.Proc2;
import org.javimmutable.collection.Proc2Throws;
import org.javimmutable.collection.Sum2;
import org.javimmutable.collection.Sum2Throws;
import org.javimmutable.collection.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

/**
 * Node with no value and no children.  Used as the terminating node at the bottom
 * of every tree or the root of an empty tree.  Only one instance exists and is
 * reused everywhere.
 */
@Immutable
class FringeNode<K, V>
    extends AbstractNode<K, V>
{
    @SuppressWarnings("rawtypes")
    private static final FringeNode INSTANCE = new FringeNode();

    private FringeNode()
    {
    }

    @SuppressWarnings("unchecked")
    static <K, V> AbstractNode<K, V> instance()
    {
        return INSTANCE;
    }

    @Nonnull
    @Override
    public AbstractNode<K, V> assign(@Nonnull Comparator<K> comp,
                                     @Nonnull K key,
                                     @Nullable V value)
    {
        return ValueNode.instance(key, value);
    }

    @Nonnull
    @Override
    public AbstractNode<K, V> update(@Nonnull Comparator<K> comp,
                                     @Nonnull K key,
                                     @Nonnull Func1<Maybe<V>, V> generator)
    {
        return ValueNode.instance(key, generator.apply(Maybe.empty()));
    }

    @Nonnull
    @Override
    public AbstractNode<K, V> delete(@Nonnull Comparator<K> comp,
                                     @Nonnull K key)
    {
        return this;
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteLeftmost()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    DeleteResult<K, V> deleteRightmost()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    boolean containsKey(@Nonnull Comparator<K> comp,
                        @Nonnull K key)
    {
        return false;
    }

    @Nullable
    @Override
    public V get(@Nonnull Comparator<K> comp,
                 @Nonnull K key,
                 V defaultValue)
    {
        return defaultValue;
    }

    @Nonnull
    @Override
    public Maybe<V> find(@Nonnull Comparator<K> comp,
                         @Nonnull K key)
    {
        return Maybe.empty();
    }

    @Nonnull
    @Override
    public Maybe<IMapEntry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                            @Nonnull K key)
    {
        return Maybe.empty();
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Nonnull
    @Override
    K key()
    {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    V value()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    AbstractNode<K, V> leftMost()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    AbstractNode<K, V> left()
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    AbstractNode<K, V> right()
    {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public GenericIterator.State<IMapEntry<K, V>> iterateOverRange(@Nullable GenericIterator.State<IMapEntry<K, V>> parent,
                                                                   int offset,
                                                                   int limit)
    {
        assert offset == 0 && limit == 0;
        return parent;
    }

    @Override
    public int iterableSize()
    {
        return 0;
    }

    @Override
    void forEach(@Nonnull Proc2<K, V> proc)
    {
    }

    @Override
    <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, V, E> proc)
        throws E
    {
    }

    @Override
    <R> R reduce(R sum,
                 @Nonnull Sum2<K, V, R> proc)
    {
        return sum;
    }

    @Override
    <R, E extends Exception> R reduceThrows(R sum,
                                            @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E
    {
        return sum;
    }

    @Override
    public void checkInvariants(@Nonnull Comparator<K> comp)
    {
    }
}
