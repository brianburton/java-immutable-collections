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

    private final Node<T> negative = new Node<>(ArrayNode.ROOT_SHIFTS, 0);
    private final Node<T> positive = new Node<>(ArrayNode.ROOT_SHIFTS, 0);
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
    private Node<T> rootForIndex(int index)
    {
        return index < 0 ? negative : positive;
    }

    private static class Node<T>
    {
        private final int shiftCount;
        private final int baseIndex;
        private long valuesBitmask;
        private final T[] values;
        private long nodesBitmask;
        private final Node<T>[] nodes;

        private Node(int shiftCount,
                     int index)
        {
            assert shiftCount <= ArrayNode.ROOT_SHIFTS;
            assert shiftCount >= ArrayNode.LEAF_SHIFTS;
            this.shiftCount = shiftCount;
            baseIndex = baseIndexAtShift(shiftCount, index);
            valuesBitmask = 0;
            values = ArrayHelper.allocate(ARRAY_SIZE);
            nodesBitmask = 0;
            nodes = allocate();
        }

        private void reset()
        {
            Arrays.fill(values, null);
            Arrays.fill(nodes, null);
            valuesBitmask = 0;
            nodesBitmask = 0;
        }

        private void put(int index,
                         T value)
        {
            assert baseIndexAtShift(shiftCount, index) == baseIndex;
            final int myIndex = indexAtShift(shiftCount, index);
            final int remainder = hashCodeBelowShift(shiftCount, index);
            final long bit = bitFromIndex(myIndex);
            if (remainder == 0) {
                valuesBitmask = addBit(valuesBitmask, bit);
                values[myIndex] = value;
            } else {
                Node<T> node = nodes[myIndex];
                if (node == null) {
                    nodesBitmask = addBit(nodesBitmask, bit);
                    node = new Node<>(shiftCount - 1, index);
                    nodes[myIndex] = node;
                }
                node.put(index, value);
            }
        }

        @Nonnull
        private ArrayNode<T> toNode(int rootBaseIndex)
        {
            final T[] answerValues = ArraySuperNode.allocateValues(bitCount(valuesBitmask));
            copyToCompactArrayUsingBitmask(valuesBitmask, values, answerValues, x -> x);
            final ArrayNode<T>[] answerNodes = ArraySuperNode.allocateNodes(bitCount(nodesBitmask));
            copyToCompactArrayUsingBitmask(nodesBitmask, nodes, answerNodes, child -> child.toNode(rootBaseIndex));
            return new ArraySuperNode<>(shiftCount, rootBaseIndex, baseIndex, valuesBitmask, answerValues, nodesBitmask, answerNodes, size());
        }

        private int size()
        {
            final Temp.Int1 sum = Temp.intVar(0);
            forEachIndex(nodesBitmask, i -> sum.a += nodes[i].size());
            return sum.a + bitCount(valuesBitmask);
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> Node<T>[] allocate()
    {
        return (Node<T>[])new Node[ARRAY_SIZE];
    }
}
