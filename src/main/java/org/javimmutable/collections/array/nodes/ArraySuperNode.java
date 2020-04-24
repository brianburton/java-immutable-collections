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

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.common.HamtLongMath.*;

public class ArraySuperNode<T>
    extends ArrayNode<T>
{
    private static final Object[] EMPTY_VALUES = new Object[0];
    @SuppressWarnings({"rawtypes"})
    private static final ArrayNode[] EMPTY_NODES = new ArrayNode[0];
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final ArraySuperNode EMPTY = new ArraySuperNode(ROOT_SHIFTS, 0, 0, 0L, EMPTY_VALUES, 0L, EMPTY_NODES, 0);

    private final int shiftCount;
    private final int entryBaseIndex;
    private final int baseIndex;
    private final long valuesBitmask;
    private final T[] values;
    private final long nodesBitmask;
    private final ArrayNode<T>[] nodes;
    private final int size;

    private ArraySuperNode(int shiftCount,
                           int entryBaseIndex,
                           int baseIndex,
                           long valuesBitmask,
                           T[] values,
                           long nodesBitmask,
                           @Nonnull ArrayNode<T>[] nodes,
                           int size)
    {
        assert bitCount(valuesBitmask) == values.length;
        assert bitCount(nodesBitmask) == nodes.length;
        this.shiftCount = shiftCount;
        this.entryBaseIndex = entryBaseIndex;
        this.baseIndex = baseIndex;
        this.valuesBitmask = valuesBitmask;
        this.values = values;
        this.nodesBitmask = nodesBitmask;
        this.nodes = nodes;
        this.size = size;
        assert checkChildShifts(shiftCount, nodes);
        assert computeSize(nodes) + values.length == size;
    }

    @SuppressWarnings("unchecked")
    public static <T> ArrayNode<T> empty()
    {
        return (ArrayNode<T>)EMPTY;
    }

    private static <T> ArrayNode<T> forAssign(int shiftCount,
                                              int entryBaseIndex,
                                              int index)
    {
        final int baseIndex = baseIndexAtShift(shiftCount, index);
        return new ArraySuperNode<>(shiftCount, entryBaseIndex, baseIndex, 0L, emptyValues(), 0L, emptyNodes(), 0);
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] emptyValues()
    {
        return (T[])EMPTY_VALUES;
    }

    @SuppressWarnings("unchecked")
    private static <T> ArrayNode<T>[] emptyNodes()
    {
        return (ArrayNode<T>[])EMPTY_NODES;
    }

    @Override
    public int iterableSize()
    {
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public T getValueOr(int shiftCount,
                        int index,
                        T defaultValue)
    {
        assert shiftCount == this.shiftCount;
        final int myIndex = indexAtShift(shiftCount, index);
        final int remainder = hashCodeBelowShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        if (remainder == 0) {
            final long bitmask = this.valuesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return values[arrayIndex];
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return nodes[arrayIndex].getValueOr(shiftCount - 1, index, defaultValue);
            }
        }
        return defaultValue;
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        assert shiftCount == this.shiftCount;
        final int myIndex = indexAtShift(shiftCount, index);
        final int remainder = hashCodeBelowShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        if (remainder == 0) {
            final long bitmask = this.valuesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return Holders.of(values[arrayIndex]);
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                return nodes[arrayIndex].find(shiftCount - 1, index);
            }
        }
        return Holders.of();
    }

    @Override
    public ArrayNode<T> assign(int entryBaseIndex,
                               int shiftCount,
                               int index,
                               T value)
    {
        assert shiftCount == this.shiftCount;
        final int myIndex = indexAtShift(shiftCount, index);
        final int remainder = hashCodeBelowShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        if (remainder == 0) {
            assert baseIndexAtShift(shiftCount, index) == baseIndex;
            final T[] values = this.values;
            final long bitmask = this.valuesBitmask;
            final long newBitmask = addBit(bitmask, bit);
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            if (bitIsPresent(bitmask, bit)) {
                assert entryBaseIndex == this.entryBaseIndex;
                final T[] newValues = ArrayHelper.assign(values, arrayIndex, value);
                return new ArraySuperNode<>(shiftCount, entryBaseIndex, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size);
            } else {
                final T[] newValues = ArrayHelper.insert(ArraySuperNode::allocateValues, values, arrayIndex, value);
                return new ArraySuperNode<>(shiftCount, entryBaseIndex, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size + 1);
            }
        } else {
            final long bitmask = this.nodesBitmask;
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            if (bitIsPresent(bitmask, bit)) {
                final ArrayNode<T> node = nodes[arrayIndex];
                final ArrayNode<T> newNode = node.assign(entryBaseIndex, shiftCount - 1, index, value);
                assert newNode != node;
                final ArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                final int newSize = size - node.iterableSize() + newNode.iterableSize();
                return new ArraySuperNode<>(shiftCount, this.entryBaseIndex, this.baseIndex, valuesBitmask, values, bitmask, newNodes, newSize);
            } else {
                final long newBitmask = addBit(bitmask, bit);
                final ArrayNode<T> newNode = ArraySuperNode.<T>forAssign(shiftCount - 1, entryBaseIndex, index).assign(entryBaseIndex, shiftCount - 1, index, value);
                final ArrayNode<T>[] newNodes = ArrayHelper.insert(ArraySuperNode::allocateNodes, nodes, arrayIndex, newNode);
                return new ArraySuperNode<>(shiftCount, this.entryBaseIndex, this.baseIndex, valuesBitmask, values, newBitmask, newNodes, size + 1);
            }
        }
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        assert shiftCount == this.shiftCount;
        final int myIndex = indexAtShift(shiftCount, index);
        final int remainder = hashCodeBelowShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        if (remainder == 0) {
            final long bitmask = this.valuesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                if (size == 1) {
                    return empty();
                } else {
                    final long newBitmask = removeBit(bitmask, bit);
                    final int arrayIndex = arrayIndexForBit(bitmask, bit);
                    final T[] newValues = ArrayHelper.delete(ArraySuperNode::allocateValues, values, arrayIndex);
                    return new ArraySuperNode<>(shiftCount, entryBaseIndex, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size - 1);
                }
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                final ArrayNode<T> node = nodes[arrayIndex];
                final ArrayNode<T> newNode = node.delete(shiftCount - 1, index);
                if (newNode != node) {
                    final int newSize = size - node.iterableSize() + newNode.iterableSize();
                    if (newSize == 0) {
                        return empty();
                    } else if (newNode.isEmpty()) {
                        final long newBitmask = removeBit(bitmask, bit);
                        final ArrayNode<T>[] newNodes = ArrayHelper.delete(ArraySuperNode::allocateNodes, nodes, arrayIndex);
                        return new ArraySuperNode<>(shiftCount, entryBaseIndex, baseIndex, valuesBitmask, values, newBitmask, newNodes, newSize);
                    } else {
                        final ArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                        return new ArraySuperNode<>(shiftCount, entryBaseIndex, baseIndex, valuesBitmask, values, bitmask, newNodes, newSize);
                    }
                }
            }
        }
        return this;
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        final List<GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>>> iterables = new ArrayList<>(values.length + nodes.length);
        long combinedBitmask = addBit(valuesBitmask, nodesBitmask);
        while (combinedBitmask != 0) {
            final long bit = leastBit(combinedBitmask);
            if (bitIsPresent(valuesBitmask, bit)) {
                final int valueIndex = indexForBit(bit);
                final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                final int entryIndex = entryBaseIndex + baseIndex + shift(shiftCount, valueIndex);
                iterables.add(GenericIterator.valueIterable(MapEntry.entry(entryIndex, values[arrayIndex])));
            }
            if (bitIsPresent(nodesBitmask, bit)) {
                final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                iterables.add(nodes[nodeIndex]);
            }
            combinedBitmask = removeBit(combinedBitmask, bit);
        }
        assert iterables.size() == (values.length + nodes.length);
        return GenericIterator.indexedState(parent, IndexedList.retained(iterables), offset, limit);
    }

    @Override
    public void checkInvariants()
    {
        if (bitCount(valuesBitmask) != values.length) {
            throw new IllegalStateException(String.format("invalid bitmask for values array: bitmask=%s length=%d", Long.toBinaryString(valuesBitmask), values.length));
        }
        if (bitCount(nodesBitmask) != nodes.length) {
            throw new IllegalStateException(String.format("invalid bitmask for nodes array: bitmask=%s length=%d", Long.toBinaryString(nodesBitmask), nodes.length));
        }
        if (!checkChildShifts(shiftCount, nodes)) {
            throw new IllegalStateException("one or more nodes invalid for this branch");
        }
        final int computedSize = computeSize(nodes) + values.length;
        if (computedSize != size) {
            throw new IllegalStateException(String.format("size mismatch: size=%d computed=%d", size, computedSize));
        }
    }

    @Override
    boolean isLeaf()
    {
        return false;
    }

    @Override
    int shiftCount()
    {
        return shiftCount;
    }

    @Nonnull
    private static <T> T[] allocateValues(int size)
    {
        return size == 0 ? emptyValues() : ArrayHelper.allocate(size);
    }

    @Nonnull
    private static <T> ArrayNode<T>[] allocateNodes(int size)
    {
        return size == 0 ? emptyNodes() : allocate(size);
    }

    static <T> boolean checkChildShifts(int shiftCount,
                                        @Nonnull ArrayNode<T>[] children)
    {
        for (ArrayNode<T> child : children) {
            if (shiftCount <= child.shiftCount() && !child.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
