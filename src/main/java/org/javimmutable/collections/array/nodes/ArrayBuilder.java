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

package org.javimmutable.collections.array.nodes;

import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Temp;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.LazyMultiIterator;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;

import static org.javimmutable.collections.common.HamtLongMath.*;

@NotThreadSafe
public class ArrayBuilder<T>
{
    private static final int NEGATIVE_BASE_INDEX = rootIndex(-1);
    private static final int POSITIVE_BASE_INDEX = rootIndex(0);

    private final Branch<T> negative = new Branch<>(ArrayNode.ROOT_SHIFTS, 0);
    private final Branch<T> positive = new Branch<>(ArrayNode.ROOT_SHIFTS, 0);
    private int nextIndex = 0;

    public static int rootIndex(int userIndex)
    {
        return userIndex < 0 ? Integer.MIN_VALUE : 0;
    }

    public static int nodeIndex(int userIndex)
    {
        return userIndex < 0 ? userIndex - Integer.MIN_VALUE : userIndex;
    }

    @Nonnull
    public ArrayNode<T> buildNegativeRoot()
    {
        return negative.toNode(NEGATIVE_BASE_INDEX);
    }

    @Nonnull
    public ArrayNode<T> buildPositiveRoot()
    {
        return positive.toNode(POSITIVE_BASE_INDEX);
    }

    public int getNextIndex()
    {
        return nextIndex;
    }

    public void setNextIndex(int nextIndex)
    {
        this.nextIndex = nextIndex;
    }

    public void add(T value)
    {
        put(nextIndex++, value);
    }

    public void put(int index,
                    T value)
    {
        rootForIndex(index).put(nodeIndex(index), value);
    }

    public int size()
    {
        return negative.size() + positive.size();
    }

    public void reset()
    {
        nextIndex = 0;
        negative.reset();
        positive.reset();
    }

    @Nonnull
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return LazyMultiIterator.iterator(IndexedHelper.indexed(buildNegativeRoot(), buildPositiveRoot()));
    }

    @Nonnull
    private BuilderNode<T> rootForIndex(int index)
    {
        return index < 0 ? negative : positive;
    }

    private static abstract class BuilderNode<T>
    {
        abstract void put(int index,
                          T value);

        @Nonnull
        abstract ArrayNode<T> toNode(int rootBaseIndex);

        abstract int size();
    }

    private static class Leaf<T>
        extends BuilderNode<T>
    {
        private final T[] values;
        private final int baseIndex;
        private long bitmask;

        private Leaf(int index)
        {
            values = ArrayHelper.allocate(ARRAY_SIZE);
            baseIndex = baseIndexFromHashCode(index);
            bitmask = 0;
        }

        void put(int index,
                 T value)
        {
            assert baseIndexFromHashCode(index) == baseIndex;
            final int valueIndex = indexFromHashCode(index);
            bitmask = addBit(bitmask, bitFromIndex(valueIndex));
            values[valueIndex] = value;
        }

        @Nonnull
        ArrayNode<T> toNode(int rootBaseIndex)
        {
            assert bitmask != 0;
            final int valueCount = bitCount(bitmask);
            if (valueCount == 0) {
                return ArrayEmptyNode.of();
            } else if (valueCount == 1) {
                final int valueIndex = indexForBit(bitmask);
                final int index = baseIndex + valueIndex;
                return new ArraySingleLeafNode<>(rootBaseIndex + index, index, values[valueIndex]);
            } else {
                final T[] nodeValues = ArrayHelper.allocate(valueCount);
                copyToCompactArrayUsingBitmask(bitmask, values, nodeValues, x -> x);
                if (valueCount == ARRAY_SIZE) {
                    return new ArrayFullLeafNode<>(rootBaseIndex + baseIndex, baseIndex, nodeValues);
                } else {
                    return new ArrayLeafNode<>(rootBaseIndex + baseIndex, baseIndex, bitmask, nodeValues);
                }
            }
        }

        @Override
        int size()
        {
            return bitCount(bitmask);
        }
    }

    private static class Branch<T>
        extends BuilderNode<T>
    {
        private final int shiftCount;
        private final int baseIndex;
        private final BuilderNode<T>[] children;
        private long bitmask;

        private Branch(int shiftCount,
                       int index)
        {
            assert shiftCount > ArrayNode.LEAF_SHIFTS;
            this.shiftCount = shiftCount;
            this.baseIndex = baseIndexAtShift(shiftCount, index);
            children = allocate();
            bitmask = 0;
        }

        private void reset()
        {
            Arrays.fill(children, null);
            bitmask = 0;
        }

        @Override
        void put(int index,
                 T value)
        {
            final int childIndex = indexAtShift(shiftCount, index);
            BuilderNode<T> child = children[childIndex];
            if (child == null) {
                if (shiftCount == ArrayNode.PARENT_SHIFTS) {
                    child = new Leaf<>(index);
                } else {
                    child = new Branch<>(shiftCount - 1, index);
                }
                children[childIndex] = child;
                bitmask = addBit(bitmask, bitFromIndex(childIndex));
            }
            child.put(index, value);
        }

        @Nonnull
        @Override
        ArrayNode<T> toNode(int rootBaseIndex)
        {
            final int childCount = bitCount(bitmask);
            if (childCount == 0) {
                return ArrayEmptyNode.of();
            } else if (childCount == 1) {
                final int childIndex = indexForBit(bitmask);
                return children[childIndex].toNode(rootBaseIndex);
            } else {
                final ArrayNode<T>[] nodes = ArrayNode.allocate(childCount);
                copyToCompactArrayUsingBitmask(bitmask, children, nodes, child -> child.toNode(rootBaseIndex));
                final int size = ArrayNode.computeSize(nodes);
                if (childCount == ARRAY_SIZE) {
                    return new ArrayFullBranchNode<>(shiftCount, baseIndex, nodes, size);
                } else {
                    return new ArrayBranchNode<>(shiftCount, baseIndex, bitmask, nodes, size);
                }
            }
        }

        @Override
        int size()
        {
            final Temp.Int1 sum = Temp.intVar(0);
            forEachIndex(bitmask, i -> sum.a += children[i].size());
            return sum.a;
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> BuilderNode<T>[] allocate()
    {
        return (BuilderNode<T>[])new BuilderNode[ARRAY_SIZE];
    }
}
