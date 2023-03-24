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

package org.javimmutable.collections.array;

import static org.javimmutable.collections.common.BitmaskMath.ARRAY_SIZE;
import static org.javimmutable.collections.common.BitmaskMath.addBit;
import static org.javimmutable.collections.common.BitmaskMath.bitCount;
import static org.javimmutable.collections.common.BitmaskMath.bitFromIndex;
import static org.javimmutable.collections.common.BitmaskMath.bitIsAbsent;
import static org.javimmutable.collections.common.BitmaskMath.bitIsPresent;
import static org.javimmutable.collections.common.BitmaskMath.copyToCompactArrayUsingBitmask;
import static org.javimmutable.collections.common.IntArrayMappedTrieMath.baseIndexAtShift;
import static org.javimmutable.collections.common.IntArrayMappedTrieMath.indexAtShift;

import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import org.javimmutable.collections.IMapEntry;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ArrayHelper;

@NotThreadSafe
public class TrieArrayBuilder<T>
{
    private final Node<T> root = new Node<>(TrieArrayNode.ROOT_SHIFT_COUNT, 0);
    private int nextIndex = 0;

    @Nonnull
    public TrieArrayNode<T> buildRoot()
    {
        return root.toNode();
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
        root.put(TrieArrayNode.flip(index), value);
    }

    public <K, V> void assign(@Nonnull ArrayAssignMapper<K, V, T> mapper,
                              @Nonnull K key,
                              V value)
    {
        root.mappedPut(mapper, TrieArrayNode.flip(key.hashCode()), key, value);
    }

    public int size()
    {
        return root.size();
    }

    public void reset()
    {
        nextIndex = 0;
        root.reset();
    }

    @Nonnull
    public SplitableIterator<IMapEntry<Integer, T>> iterator()
    {
        return buildRoot().entries().iterator();
    }

    private static class Node<T>
    {
        private final int shiftCount;
        private final int baseIndex;
        private long valuesBitmask;
        private final T[] values;
        private long nodesBitmask;
        private final Node<T>[] nodes;
        private int size;

        private Node(int shiftCount,
                     int index)
        {
            assert shiftCount <= TrieArrayNode.ROOT_SHIFT_COUNT;
            assert shiftCount >= TrieArrayNode.LEAF_SHIFT_COUNT;
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
            size = 0;
        }

        private void put(int index,
                         T value)
        {
            assert baseIndexAtShift(shiftCount, index) == baseIndex;
            final int myIndex = indexAtShift(shiftCount, index);
            final long bit = bitFromIndex(myIndex);
            if (shiftCount == TrieArrayNode.findShiftForIndex(index)) {
                values[myIndex] = value;
                if (bitIsAbsent(valuesBitmask, bit)) {
                    valuesBitmask = addBit(valuesBitmask, bit);
                    size += 1;
                }
            } else {
                Node<T> node = nodes[myIndex];
                if (node == null) {
                    nodesBitmask = addBit(nodesBitmask, bit);
                    node = new Node<>(shiftCount - 1, index);
                    nodes[myIndex] = node;
                } else {
                    size -= node.size;
                }
                node.put(index, value);
                size += node.size;
            }
        }

        private <K, V> void mappedPut(@Nonnull ArrayAssignMapper<K, V, T> mapper,
                                      int index,
                                      @Nonnull K key,
                                      V value)
        {
            assert baseIndexAtShift(shiftCount, index) == baseIndex;
            final int myIndex = indexAtShift(shiftCount, index);
            final long bit = bitFromIndex(myIndex);
            if (shiftCount == TrieArrayNode.findShiftForIndex(index)) {
                if (bitIsPresent(valuesBitmask, bit)) {
                    size -= mapper.mappedSize(values[myIndex]);
                    values[myIndex] = mapper.mappedAssign(values[myIndex], key, value);
                } else {
                    valuesBitmask = addBit(valuesBitmask, bit);
                    values[myIndex] = mapper.mappedAssign(key, value);
                }
                size += mapper.mappedSize(values[myIndex]);
            } else {
                Node<T> node = nodes[myIndex];
                if (node == null) {
                    nodesBitmask = addBit(nodesBitmask, bit);
                    node = new Node<>(shiftCount - 1, index);
                    nodes[myIndex] = node;
                } else {
                    size -= node.size;
                }
                node.mappedPut(mapper, index, key, value);
                size += node.size;
            }
        }

        @Nonnull
        private TrieArrayNode<T> toNode()
        {
            final T[] answerValues = TrieArrayNode.allocateValues(bitCount(valuesBitmask));
            copyToCompactArrayUsingBitmask(valuesBitmask, values, answerValues, x -> x);
            final TrieArrayNode<T>[] answerNodes = TrieArrayNode.allocateNodes(bitCount(nodesBitmask));
            copyToCompactArrayUsingBitmask(nodesBitmask, nodes, answerNodes, child -> child.toNode());
            return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, answerValues, nodesBitmask, answerNodes, size);
        }

        private int size()
        {
            return size;
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private static <T> Node<T>[] allocate()
    {
        return (Node<T>[])new Node[ARRAY_SIZE];
    }
}
