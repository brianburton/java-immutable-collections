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

package org.javimmutable.collections.array;

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
class TrieArrayBuilder<T>
{

    private final Node<T> negative = new Node<>(TrieArrayNode.ROOT_SHIFTS, 0);
    private final Node<T> positive = new Node<>(TrieArrayNode.ROOT_SHIFTS, 0);
    private int nextIndex = 0;

    @Nonnull
    TrieArrayNode<T> buildNegativeRoot()
    {
        return negative.toNode();
    }

    @Nonnull
    TrieArrayNode<T> buildPositiveRoot()
    {
        return positive.toNode();
    }

    int getNextIndex()
    {
        return nextIndex;
    }

    void setNextIndex(int nextIndex)
    {
        this.nextIndex = nextIndex;
    }

    void add(T value)
    {
        put(nextIndex++, value);
    }

    void put(int index,
             T value)
    {
        rootForIndex(index).put(TrieArrayNode.nodeIndex(index), value);
    }

    int size()
    {
        return negative.size() + positive.size();
    }

    void reset()
    {
        nextIndex = 0;
        negative.reset();
        positive.reset();
    }

    @Nonnull
    SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return LazyMultiIterator.iterator(IndexedHelper.indexed(buildNegativeRoot().iterable(TrieArrayNode.NEGATIVE_BASE_INDEX), buildPositiveRoot().iterable(TrieArrayNode.POSITIVE_BASE_INDEX)));
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
            assert shiftCount <= TrieArrayNode.ROOT_SHIFTS;
            assert shiftCount >= TrieArrayNode.LEAF_SHIFTS;
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
            final long bit = bitFromIndex(myIndex);
            if (TrieArrayNode.isMyValue(shiftCount, index)) {
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
        private TrieArrayNode<T> toNode()
        {
            final T[] answerValues = TrieArrayNode.allocateValues(bitCount(valuesBitmask));
            copyToCompactArrayUsingBitmask(valuesBitmask, values, answerValues, x -> x);
            final TrieArrayNode<T>[] answerNodes = TrieArrayNode.allocateNodes(bitCount(nodesBitmask));
            copyToCompactArrayUsingBitmask(nodesBitmask, nodes, answerNodes, child -> child.toNode());
            return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, answerValues, nodesBitmask, answerNodes, size());
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
