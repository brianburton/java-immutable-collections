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

import org.javimmutable.collection.Proc1;
import org.javimmutable.collection.Proc1Throws;
import org.javimmutable.collection.Sum1;
import org.javimmutable.collection.Sum1Throws;
import org.javimmutable.collection.common.CollisionSet;
import org.javimmutable.collection.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Comparator;

import static java.lang.Boolean.TRUE;

/**
 * CollisionSet implementation that stores values in Node objects (balanced trees).
 * Usable with keys that implement Comparable.  Will fail with any other
 * type of key.
 */
@Immutable
public class TreeCollisionSet<T>
    implements CollisionSet<T>
{
    @SuppressWarnings("rawtypes,unchecked")
    private static final TreeCollisionSet EMPTY = new TreeCollisionSet(ComparableComparator.of());

    private final Comparator<T> comparator;

    private TreeCollisionSet(@Nonnull Comparator<T> comparator)
    {
        this.comparator = comparator;
    }

    @SuppressWarnings("unchecked")
    public static <T> TreeCollisionSet<T> instance()
    {
        return EMPTY;
    }

    @SuppressWarnings("unchecked")
    private AbstractNode<T, Boolean> root(@Nonnull Node node)
    {
        return (AbstractNode<T, Boolean>)node;
    }

    @Nonnull
    @Override
    public Node empty()
    {
        return FringeNode.instance();
    }

    @Nonnull
    @Override
    public Node single(@Nonnull T value)
    {
        return ValueNode.instance(value, TRUE);
    }

    @Nonnull
    @Override
    public Node dual(@Nonnull T value1,
                     @Nonnull T value2)
    {
        return ValueNode.instance(comparator, value1, TRUE, value2, TRUE);
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
        return root(node).containsKey(comparator, value);
    }

    @Nonnull
    @Override
    public Node insert(@Nonnull Node node,
                       @Nonnull T value)
    {
        return root(node).assign(comparator, value, TRUE);
    }

    @Nonnull
    @Override
    public Node delete(@Nonnull Node node,
                       @Nonnull T value)
    {
        return root(node).delete(comparator, value);
    }

    @Nonnull
    @Override
    public T first(@Nonnull Node node)
    {
        return root(node).leftMost().key();
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nonnull Node node,
                                                     @Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        return GenericIterator.transformState(parent, root(node).iterateOverRange(null, offset, limit), e -> e.getKey());
    }

    @Override
    public void forEach(@Nonnull Node node,
                        @Nonnull Proc1<T> proc)
    {
        root(node).forEach((k, v) -> proc.apply(k));
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Node node,
                                                    @Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        root(node).forEachThrows((k, v) -> proc.apply(k));
    }

    @Override
    public <R> R reduce(@Nonnull Node node,
                        R sum,
                        @Nonnull Sum1<T, R> proc)
    {
        return root(node).reduce(sum, (s, k, v) -> proc.apply(s, k));
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull Node node,
                                                   R sum,
                                                   @Nonnull Sum1Throws<T, R, E> proc)
        throws E
    {
        return root(node).reduceThrows(sum, (s, k, v) -> proc.apply(s, k));
    }
}
