///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2020, Burton Computer Corporation
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
import java.util.Objects;

import static org.javimmutable.collections.common.IntArrayMappedTrieMath.liftedHashCode;

/**
 * SetNode that stores only one value.  Any assign that would progress down the tree
 * below this node replaces it with a branch node instead.  These exist to shorten the
 * height of the overall tree structure when hashCodes are dispersed.
 */
@Immutable
public class SetSingleValueLeafNode<T>
    implements SetNode<T>
{
    private final int hashCode;
    @Nonnull
    private final T value;

    SetSingleValueLeafNode(int hashCode,
                           @Nonnull T value)
    {
        this.hashCode = hashCode;
        this.value = value;
    }

    SetSingleValueLeafNode(@Nonnull CollisionSet<T> collisionSet,
                           int hashCode,
                           @Nonnull CollisionSet.Node node)
    {
        assert collisionSet.size(node) == 1;
        this.hashCode = hashCode;
        this.value = collisionSet.iterator(node).next();
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public int size(@Nonnull CollisionSet<T> collisionSet)
    {
        return 1;
    }

    @Override
    public boolean contains(@Nonnull CollisionSet<T> collisionSet,
                            int hashCode,
                            @Nonnull T value)
    {
        return this.hashCode == hashCode && this.value.equals(value);
    }

    @Nonnull
    @Override
    public SetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T value)
    {
        final int thisHashCode = this.hashCode;
        final T thisValue = this.value;
        if (thisHashCode == hashCode) {
            if (thisValue.equals(value)) {
                return this;
            } else {
                final CollisionSet.Node thisNode = collisionSet.single(thisValue);
                return new SetMultiValueLeafNode<>(hashCode, collisionSet.insert(thisNode, value));
            }
        } else {
            final SetNode<T> expanded = SetBranchNode.forLeafExpansion(collisionSet, thisHashCode, thisValue);
            return expanded.insert(collisionSet, hashCode, value);
        }
    }

    @Nonnull
    @Override
    public SetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T value)
    {
        if (this.hashCode == hashCode && this.value.equals(value)) {
            return SetEmptyNode.of();
        } else {
            return this;
        }
    }

    @Nonnull
    public SetNode<T> liftNode(int index)
    {
        return new SetSingleValueLeafNode<>(liftedHashCode(hashCode, index), value);
    }

    @Override
    public boolean isEmpty(@Nonnull CollisionSet<T> collisionSet)
    {
        return false;
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nonnull CollisionSet<T> collisionSet,
                                                     @Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        return GenericIterator.valueState(parent, value);
    }

    @Override
    public void forEach(@Nonnull CollisionSet<T> collisionSet,
                        @Nonnull Proc1<T> proc)
    {
        proc.apply(value);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                                    @Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        proc.apply(value);
    }

    @Override
    public <R> R reduce(@Nonnull CollisionSet<T> collisionSet,
                        R sum,
                        @Nonnull Sum1<T, R> proc)
    {
        return proc.apply(sum, value);
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull CollisionSet<T> collisionSet,
                                                   R sum,
                                                   @Nonnull Sum1Throws<T, R, E> proc)
        throws E
    {
        return proc.apply(sum, value);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SetSingleValueLeafNode<?> that = (SetSingleValueLeafNode<?>)o;
        return hashCode == that.hashCode &&
               value.equals(that.value);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(hashCode, value);
    }

    @Override
    public String toString()
    {
        return "(0x" + Integer.toHexString(hashCode) + "," + value + ")";
    }
}
