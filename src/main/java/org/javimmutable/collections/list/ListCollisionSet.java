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

package org.javimmutable.collections.list;

import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.Sum1;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ListCollisionSet<T>
    implements CollisionSet<T>
{
    @SuppressWarnings("rawtypes")
    private static final ListCollisionSet INSTANCE = new ListCollisionSet();

    private ListCollisionSet()
    {
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> ListCollisionSet<T> instance()
    {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private AbstractNode<T> root(@Nonnull Node node)
    {
        return (AbstractNode<T>)node;
    }

    @Nonnull
    @Override
    public Node empty()
    {
        return EmptyNode.instance();
    }

    @Nonnull
    @Override
    public Node single(@Nonnull T value)
    {
        return new OneValueNode<>(value);
    }

    @Nonnull
    @Override
    public Node dual(@Nonnull T value1,
                     @Nonnull T value2)
    {
        return new MultiValueNode<>(value1, value2);
    }

    @Override
    public int size(@Nonnull Node node)
    {
        return root(node).size();
    }

    @Override
    public boolean contains(@Nonnull Node node,
                            @Nonnull T value)
    {
        for (T v : root(node)) {
            if (v.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    @Override
    public Node insert(@Nonnull Node node,
                       @Nonnull T value)
    {
        final AbstractNode<T> root = root(node);
        int i = 0;
        for (T v : root) {
            if (v.equals(value)) {
                return root;
            }
            i += 1;
        }
        return root.append(value);
    }

    @Nonnull
    @Override
    public Node delete(@Nonnull Node node,
                       @Nonnull T value)
    {
        final AbstractNode<T> root = root(node);
        int i = 0;
        for (T v : root) {
            if (v.equals(value)) {
                return root.delete(i);
            }
            i += 1;
        }
        return root;
    }

    @Nonnull
    @Override
    public T first(@Nonnull Node node)
    {
        return root(node).get(0);
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nonnull Node node,
                                                     @Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        return root(node).iterateOverRange(parent, offset, limit);
    }

    @Override
    public void forEach(@Nonnull Node node,
                        @Nonnull Proc1<T> proc)
    {
        root(node).forEach(proc::apply);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Node node,
                                                    @Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        root(node).forEachThrows(proc);
    }

    @Override
    public <R> R reduce(@Nonnull Node node,
                        R sum,
                        @Nonnull Sum1<T, R> proc)
    {
        return root(node).reduce(sum, proc::apply);
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull Node node,
                                                   R sum,
                                                   @Nonnull Sum1Throws<T, R, E> proc)
        throws E
    {
        return root(node).reduceThrows(sum, proc);
    }
}
