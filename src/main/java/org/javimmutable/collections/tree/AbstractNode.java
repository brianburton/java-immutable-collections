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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.JImmutableMap.Entry;
import org.javimmutable.collections.Proc2;
import org.javimmutable.collections.Proc2Throws;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.Sum2;
import org.javimmutable.collections.Sum2Throws;
import org.javimmutable.collections.common.CollisionMap;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.iterators.IteratorHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Comparator;

abstract class AbstractNode<K, V>
    implements SplitableIterable<Entry<K, V>>,
               GenericIterator.Iterable<Entry<K, V>>,
               CollisionMap.Node
{
    abstract V get(@Nonnull Comparator<K> comp,
                   @Nonnull K key,
                   V defaultValue);

    @Nonnull
    abstract Holder<V> find(@Nonnull Comparator<K> comp,
                            @Nonnull K key);

    @Nonnull
    abstract Holder<Entry<K, V>> findEntry(@Nonnull Comparator<K> comp,
                                           @Nonnull K key);

    abstract boolean isEmpty();

    abstract int size();

    @Nonnull
    abstract AbstractNode<K, V> assign(@Nonnull Comparator<K> comp,
                                       @Nonnull K key,
                                       @Nullable V value);

    @Nonnull
    abstract AbstractNode<K, V> delete(@Nonnull Comparator<K> comp,
                                       @Nonnull K key);

    @Nonnull
    abstract AbstractNode<K, V> update(@Nonnull Comparator<K> comp,
                                       @Nonnull K key,
                                       @Nonnull Func1<Holder<V>, V> generator);

    @Nonnull
    abstract DeleteResult<K, V> deleteLeftmost();

    @Nonnull
    abstract DeleteResult<K, V> deleteRightmost();

    abstract int depth();

    @Nonnull
    abstract K key();

    @Nullable
    abstract V value();

    @Nonnull
    abstract AbstractNode<K, V> left();

    @Nonnull
    abstract AbstractNode<K, V> right();

    abstract void checkInvariants(@Nonnull Comparator<K> comp);

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj instanceof AbstractNode) && IteratorHelper.iteratorEquals(iterator(), ((AbstractNode)obj).iterator());
    }

    abstract void forEach(@Nonnull Proc2<K, V> proc);

    abstract <E extends Exception> void forEachThrows(@Nonnull Proc2Throws<K, V, E> proc)
        throws E;

    abstract <R> R reduce(R sum,
                          @Nonnull Sum2<K, V, R> proc);

    abstract <R, E extends Exception> R reduceThrows(R sum,
                                                     @Nonnull Sum2Throws<K, V, R, E> proc)
        throws E;

    static class DeleteResult<K, V>
    {
        final K key;
        final V value;
        final AbstractNode<K, V> remainder;

        DeleteResult(@Nonnull K key,
                     V value,
                     @Nonnull AbstractNode<K, V> remainder)
        {
            this.key = key;
            this.value = value;
            this.remainder = remainder;
        }

        @Nonnull
        DeleteResult<K, V> withRemainder(@Nonnull AbstractNode<K, V> remainder)
        {
            return new DeleteResult<>(key, value, remainder);
        }
    }
}
