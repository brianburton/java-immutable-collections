///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2021, Burton Computer Corporation
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

package org.javimmutable.collections.common;

import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Sum1;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for simple collection objects that manage the contents of leaf nodes in the hash table.
 * Implementations are free to use any class for their leaf nodes and manage them as needed.
 */
public interface CollisionSet<T>
{
    interface Node
    {
    }

    @Nonnull
    Node empty();

    @Nonnull
    Node single(@Nonnull T value);

    @Nonnull
    Node dual(@Nonnull T value1,
              @Nonnull T value2);

    int size(@Nonnull Node node);

    boolean contains(@Nonnull Node node,
                     @Nonnull T value);

    @Nonnull
    Node insert(@Nonnull Node node,
                @Nonnull T value);

    @Nonnull
    Node delete(@Nonnull Node node,
                @Nonnull T value);

    @Nonnull
    T first(@Nonnull Node node);

    @Nullable
    GenericIterator.State<T> iterateOverRange(@Nonnull Node node,
                                              @Nullable GenericIterator.State<T> parent,
                                              int offset,
                                              int limit);

    @Nonnull
    default GenericIterator.Iterable<T> genericIterable(@Nonnull Node node)
    {
        return new GenericIterator.Iterable<T>()
        {
            @Nullable
            @Override
            public GenericIterator.State<T> iterateOverRange(@Nullable GenericIterator.State<T> parent,
                                                             int offset,
                                                             int limit)
            {
                return CollisionSet.this.iterateOverRange(node, parent, offset, limit);
            }

            @Override
            public int iterableSize()
            {
                return size(node);
            }
        };
    }

    @Nonnull
    default SplitableIterable<T> iterable(@Nonnull CollisionSet.Node node)
    {
        return genericIterable(node);
    }

    @Nonnull
    default SplitableIterator<T> iterator(@Nonnull Node node)
    {
        return genericIterable(node).iterator();
    }

    void forEach(@Nonnull Node node,
                 @Nonnull Proc1<T> proc);

    <E extends Exception> void forEachThrows(@Nonnull Node node,
                                             @Nonnull Proc1Throws<T, E> proc)
        throws E;

    <R> R reduce(@Nonnull Node node,
                 R sum,
                 @Nonnull Sum1<T, R> proc);

    <R, E extends Exception> R reduceThrows(@Nonnull Node node,
                                            R sum,
                                            @Nonnull Sum1Throws<T, R, E> proc)
        throws E;
}
