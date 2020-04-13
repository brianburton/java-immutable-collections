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
import org.javimmutable.collections.Sum1;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class SetEmptyNode<T>
    implements SetNode<T>
{
    @SuppressWarnings("rawtypes")
    private static final SetEmptyNode EMPTY = new SetEmptyNode();

    @SuppressWarnings("unchecked")
    public static <T> SetNode<T> of()
    {
        return EMPTY;
    }

    @Override
    public boolean contains(@Nonnull CollisionSet<T> collisionSet,
                            int hashCode,
                            @Nonnull T value)
    {
        return false;
    }

    @Nonnull
    @Override
    public SetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T value)
    {
        return new SetSingleValueLeafNode<>(hashCode, value);
    }

    @Nonnull
    @Override
    public SetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T value)
    {
        return this;
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public boolean isEmpty(@Nonnull CollisionSet<T> collisionSet)
    {
        return true;
    }

    @Override
    public int size(@Nonnull CollisionSet<T> collisionSet)
    {
        return 0;
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nonnull CollisionSet<T> collisionSet,
                                                     @Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        assert offset == 0 && limit == 0;
        return parent;
    }

    @Override
    public void forEach(@Nonnull CollisionSet<T> collisionSet,
                        @Nonnull Proc1<T> proc)
    {
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                                    @Nonnull Proc1Throws<T, E> proc)
        throws E
    {
    }

    @Override
    public <R> R reduce(@Nonnull CollisionSet<T> collisionSet,
                        R sum,
                        @Nonnull Sum1<T, R> proc)
    {
        return sum;
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull CollisionSet<T> collisionSet,
                                                   R sum,
                                                   @Nonnull Sum1Throws<T, R, E> proc)
        throws E
    {
        return sum;
    }

    @Nonnull
    @Override
    public SetNode<T> liftNode(int index)
    {
        throw new UnsupportedOperationException();
    }
}
