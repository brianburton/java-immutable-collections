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

import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.Proc1;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.Sum1;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.CollisionSet;
import org.javimmutable.collections.common.ToStringHelper;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.Objects;

@Immutable
public class SetBranchNode<T>
    implements ArrayHelper.Allocator<SetNode<T>>,
               SetNode<T>
{
    @SuppressWarnings("rawtypes")
    private static final SetBranchNode[] EMPTY_NODES = new SetBranchNode[0];

    static final int SHIFT = 6;
    static final int MASK = 0x3f;

    private final long bitmask;
    @Nonnull
    private final CollisionSet.Node value;
    @Nonnull
    private final SetNode<T>[] children;
    private final int size;

    SetBranchNode(long bitmask,
                  @Nonnull CollisionSet.Node value,
                  @Nonnull SetNode<T>[] children,
                  int size)
    {
        assert countBits(bitmask) == children.length;
        this.bitmask = bitmask;
        this.value = value;
        this.children = children;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    static <T> SetNode<T> forLeafExpansion(@Nonnull CollisionSet<T> collisionSet,
                                           int hashCode,
                                           @Nonnull T key)
    {
        if (hashCode == 0) {
            return new SetBranchNode<T>(0, collisionSet.single(key), EMPTY_NODES, 1);
        } else {
            final int index = hashCode & MASK;
            final int remainder = hashCode >>> SHIFT;
            final long bit = 1L << index;
            final SetNode<T>[] children = new SetNode[1];
            children[0] = new SetSingleValueLeafNode<>(remainder, key);
            return new SetBranchNode<>(bit, collisionSet.empty(), children, 1);
        }
    }

    @SuppressWarnings("unchecked")
    static <T> SetNode<T> forLeafExpansion(@Nonnull CollisionSet<T> collisionSet,
                                           int hashCode,
                                           @Nonnull CollisionSet.Node value)
    {
        if (hashCode == 0) {
            return new SetBranchNode<T>(0, value, EMPTY_NODES, collisionSet.size(value));
        } else {
            final int index = hashCode & MASK;
            final int remainder = hashCode >>> SHIFT;
            final long bit = 1L << index;
            final SetNode<T>[] children = new SetNode[1];
            children[0] = SetMultiValueLeafNode.createLeaf(collisionSet, remainder, value);
            return new SetBranchNode<>(bit, collisionSet.empty(), children, collisionSet.size(value));
        }
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public int size(@Nonnull CollisionSet<T> collisionSet)
    {
        return size;
    }

    @Override
    public boolean contains(@Nonnull CollisionSet<T> collisionSet,
                            int hashCode,
                            @Nonnull T hashKey)
    {
        if (hashCode == 0) {
            return collisionSet.contains(value, hashKey);
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final long bit = 1L << index;
        final long bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return false;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return children[childIndex].contains(collisionSet, remainder, hashKey);
        }
    }

    @Override
    @Nonnull
    public SetNode<T> insert(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T hashKey)
    {
        final SetNode<T>[] children = this.children;
        final long bitmask = this.bitmask;
        final CollisionSet.Node thisValue = this.value;
        if (hashCode == 0) {
            final CollisionSet.Node newValue = collisionSet.insert(thisValue, hashKey);
            if (thisValue == newValue) {
                return this;
            } else {
                return new SetBranchNode<>(bitmask, newValue, children, size - collisionSet.size(thisValue) + collisionSet.size(newValue));
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final long bit = 1L << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            final SetNode<T> newChild = new SetSingleValueLeafNode<>(remainder, hashKey);
            final SetNode<T>[] newChildren = ArrayHelper.insert(this, children, childIndex, newChild);
            return new SetBranchNode<>(bitmask | bit, thisValue, newChildren, size + 1);
        } else {
            final SetNode<T> child = children[childIndex];
            final SetNode<T> newChild = child.insert(collisionSet, remainder, hashKey);
            if (newChild == child) {
                return this;
            } else {
                final SetNode<T>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new SetBranchNode<>(bitmask, thisValue, newChildren, size - child.size(collisionSet) + newChild.size(collisionSet));
            }
        }
    }

    @Override
    @Nonnull
    public SetNode<T> delete(@Nonnull CollisionSet<T> collisionSet,
                             int hashCode,
                             @Nonnull T hashKey)
    {
        final long bitmask = this.bitmask;
        final SetNode<T>[] children = this.children;
        final CollisionSet.Node value = this.value;
        if (hashCode == 0) {
            final CollisionSet.Node newValue = collisionSet.delete(value, hashKey);
            final int newSize = this.size - collisionSet.size(value) + collisionSet.size(newValue);
            if (newValue == value) {
                return this;
            } else if (collisionSet.size(newValue) == 0) {
                if (bitmask == 0) {
                    return SetEmptyNode.of();
                } else {
                    return createForDelete(collisionSet, bitmask, newValue, children, newSize);
                }
            } else {
                return new SetBranchNode<>(bitmask, newValue, children, newSize);
            }
        }
        final int index = hashCode & MASK;
        final int remainder = hashCode >>> SHIFT;
        final long bit = 1L << index;
        final int childIndex = realIndex(bitmask, bit);
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final SetNode<T> child = children[childIndex];
            final SetNode<T> newChild = child.delete(collisionSet, remainder, hashKey);
            final int newSize = size - child.size(collisionSet) + newChild.size(collisionSet);
            if (newChild == child) {
                return this;
            } else if (newChild.isEmpty(collisionSet)) {
                if (children.length == 1) {
                    if (collisionSet.size(value) == 0) {
                        return SetEmptyNode.of();
                    } else {
                        return SetMultiValueLeafNode.createLeaf(collisionSet, 0, value);
                    }
                } else {
                    final SetNode<T>[] newChildren = ArrayHelper.delete(this, children, childIndex);
                    return createForDelete(collisionSet, bitmask & ~bit, value, newChildren, newSize);
                }
            } else {
                final SetNode<T>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return createForDelete(collisionSet, bitmask, value, newChildren, newSize);
            }
        }
    }

    private SetNode<T> createForDelete(@Nonnull CollisionSet<T> collisionSet,
                                       long bitmask,
                                       CollisionSet.Node value,
                                       @Nonnull SetNode<T>[] children,
                                       int newSize)
    {
        if (collisionSet.size(value) == 0 && children.length == 1) {
            final SetNode<T> child = children[0];
            if (child.isLeaf()) {
                assert newSize == child.size(collisionSet);
                return child.liftNode(Long.numberOfTrailingZeros(bitmask));
            }
            if (child instanceof SetBranchNode) {
                final SetBranchNode<T> branch = (SetBranchNode<T>)child;
                if (collisionSet.size(branch.value) > 0 && branch.children.length == 0) {
                    assert newSize == collisionSet.size(branch.value);
                    return SetMultiValueLeafNode.createLeaf(collisionSet, Long.numberOfTrailingZeros(bitmask), branch.value);
                }
            }
        }
        return new SetBranchNode<>(bitmask, value, children, newSize);
    }

    @Override
    public boolean isEmpty(@Nonnull CollisionSet<T> collisionSet)
    {
        return bitmask == 0 && collisionSet.size(value) == 0;
    }

    @Nonnull
    @Override
    public SetNode<T> liftNode(int index)
    {
        throw new UnsupportedOperationException();
    }

    private static int realIndex(long bitmask,
                                 long bit)
    {
        return Long.bitCount(bitmask & (bit - 1));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public SetNode<T>[] allocate(int size)
    {
        return new SetNode[size];
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nonnull CollisionSet<T> collisionSet,
                                                     @Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        assert offset >= 0 && offset <= limit && limit <= size;
        return GenericIterator.indexedState(parent, indexedForIterator(collisionSet), offset, limit);
    }

    @Override
    public void forEach(@Nonnull CollisionSet<T> collisionSet,
                        @Nonnull Proc1<T> proc)
    {
        collisionSet.forEach(value, proc);
        for (SetNode<T> child : children) {
            child.forEach(collisionSet, proc);
        }
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull CollisionSet<T> collisionSet,
                                                    @Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        collisionSet.forEachThrows(value, proc);
        for (SetNode<T> child : children) {
            child.forEachThrows(collisionSet, proc);
        }
    }

    @Override
    public <R> R reduce(@Nonnull CollisionSet<T> collisionSet,
                        R sum,
                        @Nonnull Sum1<T, R> proc)
    {
        sum = collisionSet.reduce(value, sum, proc);
        for (SetNode<T> child : children) {
            sum = child.reduce(collisionSet, sum, proc);
        }
        return sum;
    }

    @Override
    public <R, E extends Exception> R reduceThrows(@Nonnull CollisionSet<T> collisionSet,
                                                   R sum,
                                                   @Nonnull Sum1Throws<T, R, E> proc)
        throws E
    {
        sum = collisionSet.reduceThrows(value, sum, proc);
        for (SetNode<T> child : children) {
            sum = child.reduceThrows(collisionSet, sum, proc);
        }
        return sum;
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
        SetBranchNode<?> that = (SetBranchNode<?>)o;
        return bitmask == that.bitmask &&
               size == that.size &&
               value.equals(that.value) &&
               Arrays.equals(children, that.children);
    }

    @Override
    public int hashCode()
    {
        int result = Objects.hash(bitmask, value, size);
        result = 31 * result + Arrays.hashCode(children);
        return result;
    }

    @Override
    public String toString()
    {
        return "(" + size + ",0x" + Long.toHexString(bitmask) + "," + children.length + "," + value + "," + ToStringHelper.arrayToString(children) + ")";
    }

    private int computeSize(@Nonnull CollisionSet<T> collisionSet)
    {
        int answer = collisionSet.size(value);
        for (SetNode<T> child : children) {
            answer += child.size(collisionSet);
        }
        return answer;
    }

    @Override
    public void checkInvariants(@Nonnull CollisionSet<T> collisionSet)
    {
        if (size != computeSize(collisionSet)) {
            throw new IllegalStateException(String.format("incorrect size: expected=%d actual=%d", computeSize(collisionSet), size));
        }
        if (collisionSet.size(value) == 0 && children.length == 1) {
            if (children[0] instanceof SetMultiValueLeafNode || children[0] instanceof SetSingleValueLeafNode) {
                // we should have replaced ourselves with a leaf
                throw new IllegalStateException(String.format("expected leaf but was %s", children[0].getClass().getName()));
            }
        }
        for (SetNode<T> child : children) {
            child.checkInvariants(collisionSet);
        }
    }

    @Nonnull
    private Indexed<GenericIterator.Iterable<T>> indexedForIterator(@Nonnull CollisionSet<T> collisionSet)
    {
        return new Indexed<GenericIterator.Iterable<T>>()
        {
            @Override
            public GenericIterator.Iterable<T> get(int index)
            {
                if (index == 0) {
                    return collisionSet.genericIterable(value);
                } else {
                    return children[index - 1].genericIterable(collisionSet);
                }
            }

            @Override
            public int size()
            {
                return 1 + children.length;
            }
        };
    }

    private static int countBits(long bitmask)
    {
        int count = 0;
        while (bitmask != 0) {
            if ((bitmask & 1L) == 1L) {
                count += 1;
            }
            bitmask >>>= 1;
        }
        return count;
    }
}
