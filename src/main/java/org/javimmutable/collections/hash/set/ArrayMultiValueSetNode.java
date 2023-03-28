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

package org.javimmutable.collections.hash.set;

import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ArrayMultiValueSetNode<T>
    implements ArraySetNode<T>
{
    private final CollisionSet.Node node;

    public ArrayMultiValueSetNode(@Nonnull CollisionSet.Node node)
    {
        this.node = node;
    }

    @Override
    public int size(@Nonnull CollisionSet<T> collisionSet)
    {
        return collisionSet.size(node);
    }

    @Override
    public boolean contains(@Nonnull CollisionSet<T> collisionSet,
                            @Nonnull T value)
    {
        return collisionSet.contains(node, value);
    }

    @Nonnull
    @Override
    public ArraySetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                                  @Nonnull T value)
    {
        final CollisionSet.Node oldNode = this.node;
        final CollisionSet.Node newNode = collisionSet.insert(oldNode, value);
        if (newNode == oldNode) {
            return this;
        } else {
            return new ArrayMultiValueSetNode<>(newNode);
        }
    }

    @Nullable
    @Override
    public ArraySetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                                  @Nonnull T value)
    {
        final CollisionSet.Node oldNode = this.node;
        final CollisionSet.Node newNode = collisionSet.delete(oldNode, value);
        if (newNode == oldNode) {
            return this;
        } else {
            final int newSize = collisionSet.size(newNode);
            switch (newSize) {
                case 0:
                    return null;
                case 1:
                    return new ArraySingleValueSetNode<>(collisionSet.first(newNode));
                default:
                    return new ArrayMultiValueSetNode<>(newNode);
            }
        }
    }

    @Nonnull
    @Override
    public GenericIterator.Iterable<T> values(@Nonnull CollisionSet<T> collisionSet)
    {
        return collisionSet.genericIterable(node);
    }

    @Override
    public void forEach(@Nonnull CollisionSet<T> collisionSet,
                        @Nonnull Proc1<T> proc)
    {
        collisionSet.forEach(node, proc);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                                    @Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        collisionSet.forEachThrows(node, proc);
    }
}
