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

/**
 * HamtNode that stores only one value.  Any assign that would progress down the tree
 * below this node replaces it with a normal node instead.  These exist to shorten the
 * height of the overall tree structure when hashCodes are dispersed.
 */
public class SetMultiValueLeafNode<T>
    implements SetNode<T>
{
    private final int hashCode;
    @Nonnull
    private final CollisionSet.Node values;

    SetMultiValueLeafNode(int hashCode,
                          @Nonnull CollisionSet.Node values)
    {
        this.hashCode = hashCode;
        this.values = values;
    }

    static <T> SetNode<T> createLeaf(@Nonnull CollisionSet<T> collisionSet,
                                     int hashCode,
                                     @Nonnull CollisionSet.Node values)
    {
        if (collisionSet.size(values) == 1) {
            return new SetSingleValueLeafNode<>(collisionSet, hashCode, values);
        } else {
            assert collisionSet.size(values) > 1;
            return new SetMultiValueLeafNode<>(hashCode, values);
        }
    }

    @Override
    public boolean isLeaf()
    {
        return true;
    }

    @Override
    public int size(@Nonnull CollisionSet<T> collisionSet)
    {
        return collisionSet.size(values);
    }

    @Override
    public boolean contains(@Nonnull CollisionSet<T> collisionSet,
                            int hashCode,
                            @Nonnull T value)
    {
        return collisionSet.contains(values, value);
    }

    @Nonnull
    @Override
    public SetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T value)
    {
        final int thisHashCode = this.hashCode;
        final CollisionSet.Node currentValues = this.values;
        if (hashCode == thisHashCode) {
            final CollisionSet.Node newValues = collisionSet.insert(currentValues, value);
            if (newValues == currentValues) {
                return this;
            } else {
                return new SetMultiValueLeafNode<>(hashCode, newValues);
            }
        } else {
            final SetNode<T> expanded = SetBranchNode.forLeafExpansion(collisionSet, thisHashCode, currentValues);
            return expanded.insert(collisionSet, hashCode, value);
        }
    }

    @Nonnull
    @Override
    public SetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T value)
    {
        final int thisHashCode = this.hashCode;
        final CollisionSet.Node currentValues = this.values;
        if (hashCode == thisHashCode) {
            final CollisionSet.Node newValues = collisionSet.delete(currentValues, value);
            if (newValues != currentValues) {
                final int newSize = collisionSet.size(newValues);
                if (newSize == 0) {
                    return SetEmptyNode.of();
                } else if (newSize == 1) {
                    return new SetSingleValueLeafNode<>(collisionSet, hashCode, newValues);
                } else {
                    return new SetMultiValueLeafNode<>(hashCode, newValues);
                }
            }
        }
        return this;
    }

    @Override
    @Nonnull
    public SetNode<T> liftNode(int index)
    {
        return new SetMultiValueLeafNode<>(hashCode << SetBranchNode.SHIFT | index, values);
    }

    @Override
    public boolean isEmpty(@Nonnull CollisionSet<T> collisionSet)
    {
        return collisionSet.size(values) == 0;
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nonnull CollisionSet<T> collisionSet,
                                                     @Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        return collisionSet.iterateOverRange(values, parent, offset, limit);
    }

    @Override
    public void forEach(@Nonnull CollisionSet<T> collisionSet,
                        @Nonnull Proc1<T> proc)
    {
        collisionSet.forEach(values, proc);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                                    @Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        collisionSet.forEachThrows(values, proc);
    }

    @Override
    public <R> R reduce(@Nonnull CollisionSet<T> collisionSet,
                        R sum,
                        @Nonnull Sum1<T, R> proc)
    {
        return collisionSet.reduce(values, sum, proc);
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull CollisionSet<T> collisionSet,
                                                   R sum,
                                                   @Nonnull Sum1Throws<T, R, E> proc)
        throws E
    {
        return collisionSet.reduceThrows(values, sum, proc);
    }

    @Override
    public void checkInvariants(@Nonnull CollisionSet<T> collisionSet)
    {
        if (collisionSet.size(values) < 2) {
            throw new IllegalStateException(String.format("expected size greater than one: size=%d", collisionSet.size(values)));
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("(0x");
        sb.append(Integer.toHexString(hashCode));
        sb.append(",");
        sb.append(values);
        sb.append(")");
        return sb.toString();
    }
}
