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

package org.javimmutable.collections.hash.set;

import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.SplitableIterable;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Sum1;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SetNode<T>
{
    boolean contains(@Nonnull CollisionSet<T> collisionSet,
                     int hashCode,
                     @Nonnull T value);

    @Nonnull
    SetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                      int hashCode,
                      @Nonnull T value);

    @Nonnull
    SetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                      int hashCode,
                      @Nonnull T value);

    boolean isLeaf();

    @Nonnull
    SetNode<T> liftNode(int index);

    boolean isEmpty(@Nonnull CollisionSet<T> collisionSet);

    int size(@Nonnull CollisionSet<T> collisionSet);

    default void checkInvariants(@Nonnull CollisionSet<T> collisionSet)
    {
    }

    @Nullable
    GenericIterator.State<T> iterateOverRange(@Nonnull CollisionSet<T> collisionSet,
                                              @Nullable GenericIterator.State<T> parent,
                                              int offset,
                                              int limit);

    @Nonnull
    default GenericIterator.Iterable<T> genericIterable(@Nonnull CollisionSet<T> collisionSet)
    {
        return new GenericIterator.Iterable<T>()
        {
            @Nullable
            @Override
            public GenericIterator.State<T> iterateOverRange(@Nullable GenericIterator.State<T> parent,
                                                             int offset,
                                                             int limit)
            {
                return SetNode.this.iterateOverRange(collisionSet, parent, offset, limit);
            }

            @Override
            public int iterableSize()
            {
                return size(collisionSet);
            }
        };
    }

    @Nonnull
    default SplitableIterable<T> iterable(@Nonnull CollisionSet<T> collisionSet)
    {
        return () -> iterator(collisionSet);
    }

    @Nonnull
    default SplitableIterator<T> iterator(@Nonnull CollisionSet<T> collisionSet)
    {
        return new GenericIterator<>(genericIterable(collisionSet), 0, size(collisionSet));
    }

    void forEach(@Nonnull CollisionSet<T> collisionSet,
                 @Nonnull Proc1<T> proc);

    <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                             @Nonnull Proc1Throws<T, E> proc)
        throws E;

    <R> R reduce(@Nonnull CollisionSet<T> collisionSet,
                 R sum,
                 @Nonnull Sum1<T, R> proc);

    <R, E extends Exception> R reduceThrows(@Nonnull CollisionSet<T> collisionSet,
                                            R sum,
                                            @Nonnull Sum1Throws<T, R, E> proc)
        throws E;
}
