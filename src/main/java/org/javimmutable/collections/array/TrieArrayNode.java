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

import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.HamtLongMath;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.javimmutable.collections.common.HamtLongMath.*;

class TrieArrayNode<T>
    implements InvariantCheckable
{
    static final int LEAF_SHIFT_COUNT = 0;
    static final int ROOT_SHIFT_COUNT = HamtLongMath.maxShiftsForBitCount(30);
    static final int NEGATIVE_BASE_INDEX = Integer.MIN_VALUE;
    static final int POSITIVE_BASE_INDEX = 0;

    private static final Object[] EMPTY_VALUES = new Object[0];
    @SuppressWarnings({"rawtypes"})
    private static final TrieArrayNode[] EMPTY_NODES = new TrieArrayNode[0];
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final TrieArrayNode EMPTY = new TrieArrayNode(ROOT_SHIFT_COUNT, 0, 0L, EMPTY_VALUES, 0L, EMPTY_NODES, 0);

    private final int shiftCount;
    private final int baseIndex;
    private final long valuesBitmask;
    private final T[] values;
    private final long nodesBitmask;
    private final TrieArrayNode<T>[] nodes;
    private final int size;

    TrieArrayNode(int shiftCount,
                  int baseIndex,
                  long valuesBitmask,
                  T[] values,
                  long nodesBitmask,
                  @Nonnull TrieArrayNode<T>[] nodes,
                  int size)
    {
        assert bitCount(valuesBitmask) == values.length;
        assert bitCount(nodesBitmask) == nodes.length;
        this.shiftCount = shiftCount;
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
    @Nonnull
    static <T> TrieArrayNode<T> empty()
    {
        return (TrieArrayNode<T>)EMPTY;
    }

    @Nonnull
    private static <T> TrieArrayNode<T> forValue(int shiftCount,
                                                 int index,
                                                 T value)
    {
        assert isMyValue(shiftCount, index);
        final int baseIndex = baseIndexAtShift(shiftCount, index);
        final long valueBitmask = bitFromIndex(indexAtShift(shiftCount, index));
        final T[] values = ArrayHelper.newArray(value);
        final long nodeBitmask = 0L;
        final TrieArrayNode<T>[] nodes = emptyNodes();
        return new TrieArrayNode<>(shiftCount, baseIndex, valueBitmask, values, nodeBitmask, nodes, 1);
    }

    @Nonnull
    private static <T> TrieArrayNode<T> forNode(int shiftCount,
                                                int nodeBaseIndex,
                                                @Nonnull TrieArrayNode<T> node)
    {
        final int baseIndex = baseIndexAtShift(shiftCount, nodeBaseIndex);
        final long valueBitmask = 0L;
        final T[] values = emptyValues();
        final long nodeBitmask = bitFromIndex(indexAtShift(shiftCount, nodeBaseIndex));
        final TrieArrayNode<T>[] nodes = allocateNodes(1);
        nodes[0] = node;
        return new TrieArrayNode<>(shiftCount, baseIndex, valueBitmask, values, nodeBitmask, nodes, node.size());
    }

    int size()
    {
        return size;
    }

    boolean isEmpty()
    {
        return size == 0;
    }

    T getValueOr(int shiftCount,
                 int index,
                 T defaultValue)
    {
        final int thisShiftCount = this.shiftCount;
        if (shiftCount != thisShiftCount) {
            assert shiftCount >= thisShiftCount;
            if (baseIndexAtShift(thisShiftCount, index) != baseIndex) {
                return defaultValue;
            }
            shiftCount = thisShiftCount;
        }
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        if (isMyValue(shiftCount, index)) {
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

    @Nonnull
    Holder<T> find(int shiftCount,
                   int index)
    {
        final int thisShiftCount = this.shiftCount;
        if (shiftCount != thisShiftCount) {
            assert shiftCount >= thisShiftCount;
            if (baseIndexAtShift(thisShiftCount, index) != baseIndex) {
                return Holders.of();
            }
            shiftCount = thisShiftCount;
        }
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        if (isMyValue(shiftCount, index)) {
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

    @Nonnull
    TrieArrayNode<T> assign(int shiftCount,
                            int index,
                            T value)
    {
        final int thisShiftCount = this.shiftCount;
        final int baseIndex = this.baseIndex;
        if (shiftCount != thisShiftCount) {
            assert shiftCount > thisShiftCount;
            final int valueShiftCount = findMaxCommonShift(ROOT_SHIFT_COUNT, baseIndex, index);
            assert valueShiftCount <= shiftCount;
            if (valueShiftCount > thisShiftCount) {
                final TrieArrayNode<T> ancestor = forNode(valueShiftCount, baseIndex, this);
                return ancestor.assign(valueShiftCount, index, value);
            }
            shiftCount = thisShiftCount;
        }
        assert baseIndexAtShift(shiftCount, index) == baseIndex;
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        if (isMyValue(shiftCount, index)) {
            final T[] values = this.values;
            final long newBitmask = addBit(valuesBitmask, bit);
            final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
            if (bitIsPresent(valuesBitmask, bit)) {
                final T[] newValues = ArrayHelper.assign(values, arrayIndex, value);
                return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size);
            } else {
                final T[] newValues = ArrayHelper.insert(TrieArrayNode::allocateValues, values, arrayIndex, value);
                return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size + 1);
            }
        } else {
            final long bitmask = this.nodesBitmask;
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            if (bitIsPresent(bitmask, bit)) {
                final TrieArrayNode<T> node = nodes[arrayIndex];
                final TrieArrayNode<T> newNode = node.assign(shiftCount - 1, index, value);
                assert newNode != node;
                final TrieArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                final int newSize = size - node.size() + newNode.size();
                return new TrieArrayNode<>(shiftCount, this.baseIndex, valuesBitmask, values, bitmask, newNodes, newSize);
            } else {
                final long newBitmask = addBit(bitmask, bit);
                final int valueShiftCount = findMinimumShiftForZeroBelowHashCode(index);
                assert valueShiftCount < shiftCount;
                final TrieArrayNode<T> newNode = forValue(valueShiftCount, index, value);
                if (valuesBitmask == 0 && bitCount(newBitmask) == 1) {
                    return newNode;
                } else {
                    final TrieArrayNode<T>[] newNodes = ArrayHelper.insert(TrieArrayNode::allocateNodes, nodes, arrayIndex, newNode);
                    return new TrieArrayNode<>(shiftCount, this.baseIndex, valuesBitmask, values, newBitmask, newNodes, size + 1);
                }
            }
        }
    }

    @Nonnull
    TrieArrayNode<T> delete(int shiftCount,
                            int index)
    {
        final int thisShiftCount = this.shiftCount;
        if (shiftCount != thisShiftCount) {
            assert shiftCount >= thisShiftCount;
            if (baseIndexAtShift(thisShiftCount, index) != baseIndex) {
                return this;
            }
            shiftCount = thisShiftCount;
        }
        assert baseIndexAtShift(shiftCount, index) == baseIndex;
        final int myIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(myIndex);
        final long valuesBitmask = this.valuesBitmask;
        if (isMyValue(shiftCount, index)) {
            if (bitIsPresent(valuesBitmask, bit)) {
                if (size == 1) {
                    return empty();
                } else {
                    final long newBitmask = removeBit(valuesBitmask, bit);
                    final int arrayIndex = arrayIndexForBit(valuesBitmask, bit);
                    final T[] newValues = ArrayHelper.delete(TrieArrayNode::allocateValues, values, arrayIndex);
                    return new TrieArrayNode<>(shiftCount, baseIndex, newBitmask, newValues, nodesBitmask, nodes, size - 1);
                }
            }
        } else {
            final long bitmask = this.nodesBitmask;
            if (bitIsPresent(bitmask, bit)) {
                final int arrayIndex = arrayIndexForBit(bitmask, bit);
                final TrieArrayNode<T>[] nodes = this.nodes;
                final TrieArrayNode<T> node = nodes[arrayIndex];
                final TrieArrayNode<T> newNode = node.delete(shiftCount - 1, index);
                if (newNode != node) {
                    final int newSize = size - node.size() + newNode.size();
                    if (newSize == 0) {
                        return empty();
                    } else if (newNode.isEmpty()) {
                        final long newBitmask = removeBit(bitmask, bit);
                        if (valuesBitmask == 0 && bitCount(newBitmask) == 1) {
                            return nodes[arrayIndexForBit(bitmask, newBitmask)];
                        } else {
                            final TrieArrayNode<T>[] newNodes = ArrayHelper.delete(TrieArrayNode::allocateNodes, nodes, arrayIndex);
                            return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, newBitmask, newNodes, newSize);
                        }
                    } else {
                        final TrieArrayNode<T>[] newNodes = ArrayHelper.assign(nodes, arrayIndex, newNode);
                        return new TrieArrayNode<>(shiftCount, baseIndex, valuesBitmask, values, bitmask, newNodes, newSize);
                    }
                }
            }
        }
        return this;
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

    @Nonnull
    GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>> iterable(int rootBaseIndex)
    {
        return new IterableImpl(rootBaseIndex);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    static <T> T[] allocateValues(int size)
    {
        return size == 0 ? emptyValues() : (T[])new Object[size];
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    static <T> TrieArrayNode<T>[] allocateNodes(int size)
    {
        return size == 0 ? emptyNodes() : (TrieArrayNode<T>[])new TrieArrayNode[size];
    }

    @SuppressWarnings("unchecked")
    private static <T> T[] emptyValues()
    {
        return (T[])EMPTY_VALUES;
    }

    @SuppressWarnings("unchecked")
    private static <T> TrieArrayNode<T>[] emptyNodes()
    {
        return (TrieArrayNode<T>[])EMPTY_NODES;
    }

    static int nodeIndex(int userIndex)
    {
        return userIndex < 0 ? userIndex - Integer.MIN_VALUE : userIndex;
    }

    private static <T> boolean checkChildShifts(int shiftCount,
                                                @Nonnull TrieArrayNode<T>[] nodes)
    {
        for (TrieArrayNode<T> node : nodes) {
            if (shiftCount <= node.shiftCount || node.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static <T> int computeSize(@Nonnull TrieArrayNode<T>[] children)
    {
        int total = 0;
        for (TrieArrayNode<T> child : children) {
            total += child.size();
        }
        return total;
    }

    static boolean isMyValue(int shiftCount,
                             int index)
    {
        return index == 0 ? (shiftCount == LEAF_SHIFT_COUNT) : (hashCodeBelowShift(shiftCount, index) == 0);
    }

    private class IterableImpl
        implements GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>>
    {
        private final int rootBaseIndex;

        private IterableImpl(int rootBaseIndex)
        {
            this.rootBaseIndex = rootBaseIndex;
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
                    final int entryIndex = rootBaseIndex + baseIndex + shift(shiftCount, valueIndex);
                    iterables.add(GenericIterator.valueIterable(MapEntry.entry(entryIndex, values[arrayIndex])));
                }
                if (bitIsPresent(nodesBitmask, bit)) {
                    final int nodeIndex = arrayIndexForBit(nodesBitmask, bit);
                    iterables.add(nodes[nodeIndex].iterable(rootBaseIndex));
                }
                combinedBitmask = removeBit(combinedBitmask, bit);
            }
            assert iterables.size() == (values.length + nodes.length);
            return GenericIterator.indexedState(parent, IndexedList.retained(iterables), offset, limit);
        }

        @Override
        public int iterableSize()
        {
            return size;
        }
    }
}
