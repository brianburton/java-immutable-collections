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
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nullable;

import static org.javimmutable.collections.common.HamtLongMath.*;

public class ArrayBranchNode<T>
    extends ArrayNode<T>
{
    private final int shiftCount;
    private final int baseIndex;
    private final long bitmask;
    private final ArrayNode<T>[] children;
    private final int size;

    ArrayBranchNode(int shiftCount,
                    int baseIndex,
                    long bitmask,
                    ArrayNode<T>[] children,
                    int size)
    {
        assert bitCount(bitmask) == children.length;
        assert children.length >= 2;
        this.shiftCount = shiftCount;
        this.baseIndex = baseIndex;
        this.bitmask = bitmask;
        this.children = children;
        this.size = size;
        assert checkChildShifts(shiftCount, children);
        assert computeSize(children) == size;
    }

    static <T> ArrayNode<T> fullWithout(int shiftCount,
                                        int baseIndex,
                                        int removeIndex,
                                        ArrayNode<T>[] children,
                                        int size)
    {
        final long bit = bitFromIndex(removeIndex);
        final long bitmask = removeBit(-1L, bit);
        final ArrayNode<T>[] newChildren = ArrayHelper.delete(ArrayNode::allocate, children, removeIndex);
        return new ArrayBranchNode<>(shiftCount, baseIndex, bitmask, newChildren, size);
    }

    static <T> ArrayNode<T> forChildren(int index1,
                                        ArrayNode<T> child1,
                                        int index2,
                                        ArrayNode<T> child2)
    {
        final int shiftCount = findMaxCommonShift(ROOT_SHIFTS, index1, index2);
        assert shiftCount > LEAF_SHIFTS;
        assert shiftCount == ROOT_SHIFTS || baseIndexAtShift(shiftCount, index1) == baseIndexAtShift(shiftCount, index2);
        final int baseIndex = baseIndexAtShift(shiftCount, index1);
        final int childIndex1 = indexAtShift(shiftCount, index1);
        final int childIndex2 = indexAtShift(shiftCount, index2);
        final long bitmask = addBit(bitFromIndex(childIndex1), bitFromIndex(childIndex2));
        final int size = child1.iterableSize() + child2.iterableSize();
        final ArrayNode<T>[] children = allocate(2);
        if (childIndex1 < childIndex2) {
            children[0] = child1;
            children[1] = child2;
        } else {
            children[0] = child2;
            children[1] = child1;
        }
        return new ArrayBranchNode<>(shiftCount, baseIndex, bitmask, children, size);
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
        if (shiftCount != this.shiftCount) {
            assert shiftCount >= this.shiftCount;
            if (baseIndexAtShift(this.shiftCount, index) != baseIndex) {
                return defaultValue;
            }
            shiftCount = this.shiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return children[arrayIndex].getValueOr(shiftCount - 1, index, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        if (shiftCount != this.shiftCount) {
            assert shiftCount >= this.shiftCount;
            if (baseIndexAtShift(this.shiftCount, index) != baseIndex) {
                return Holders.of();
            }
            shiftCount = this.shiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(childIndex);
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            return children[arrayIndex].find(shiftCount - 1, index);
        }
        return Holders.of();
    }

    @Override
    public ArrayNode<T> assign(int entryBaseIndex,
                               int shiftCount,
                               int index,
                               T value)
    {
        final int thisShiftCount = this.shiftCount;
        final int baseIndex = this.baseIndex;
        if (shiftCount != thisShiftCount) {
            assert shiftCount >= thisShiftCount;
            if (baseIndexAtShift(thisShiftCount, index) != baseIndex) {
                final ArrayNode<T> leaf = ArraySingleLeafNode.forValue(entryBaseIndex, index, value);
                return ArrayBranchNode.forChildren(baseIndex, this, index, leaf);
            }
            shiftCount = thisShiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(childIndex);
        final long bitmask = this.bitmask;
        final int arrayIndex = arrayIndexForBit(bitmask, bit);
        final ArrayNode<T>[] children = this.children;
        if (bitIsPresent(bitmask, bit)) {
            final ArrayNode<T> child = children[arrayIndex];
            final ArrayNode<T> newChild = child.assign(entryBaseIndex, shiftCount - 1, index, value);
            assert newChild != child;
            final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, arrayIndex, newChild);
            final int newSize = size - child.iterableSize() + newChild.iterableSize();
            return new ArrayBranchNode<>(shiftCount, baseIndex, bitmask, newChildren, newSize);
        } else {
            final ArrayNode<T> newChild = ArraySingleLeafNode.forValue(entryBaseIndex, index, value);
            final ArrayNode<T>[] newChildren = ArrayHelper.insert(ArrayNode::allocate, children, arrayIndex, newChild);
            if (newChildren.length == ARRAY_SIZE) {
                return new ArrayFullBranchNode<>(shiftCount, baseIndex, newChildren, size + 1);
            } else {
                return new ArrayBranchNode<>(shiftCount, baseIndex, addBit(bitmask, bit), newChildren, size + 1);
            }
        }
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        final int thisShiftCount = this.shiftCount;
        final int baseIndex = this.baseIndex;
        if (shiftCount != thisShiftCount) {
            assert shiftCount >= thisShiftCount;
            if (baseIndexAtShift(thisShiftCount, index) != baseIndex) {
                return this;
            }
            shiftCount = thisShiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final long bit = bitFromIndex(childIndex);
        final long bitmask = this.bitmask;
        if (bitIsPresent(bitmask, bit)) {
            final int arrayIndex = arrayIndexForBit(bitmask, bit);
            final ArrayNode<T>[] children = this.children;
            final ArrayNode<T> child = children[arrayIndex];
            final ArrayNode<T> newChild = child.delete(shiftCount - 1, index);
            if (newChild != child) {
                final int newSize = size - child.iterableSize() + newChild.iterableSize();
                if (newSize == 0) {
                    return ArrayEmptyNode.of();
                } else if (newChild.isEmpty()) {
                    final ArrayNode<T>[] newChildren = ArrayHelper.delete(ArrayNode::allocate, children, arrayIndex);
                    if (newChildren.length == 1) {
                        return newChildren[0];
                    } else {
                        return new ArrayBranchNode<>(shiftCount, baseIndex, removeBit(bitmask, bit), newChildren, newSize);
                    }
                } else {
                    final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, arrayIndex, newChild);
                    return new ArrayBranchNode<>(shiftCount, baseIndex, bitmask, newChildren, newSize);
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
        return GenericIterator.indexedState(parent, IndexedArray.retained(children), offset, limit);
    }

    @Override
    public void checkInvariants()
    {
        if (bitCount(bitmask) != children.length) {
            throw new IllegalStateException(String.format("invalid bitmask for array: bitmask=%s length=%d", Long.toBinaryString(bitmask), children.length));
        }
        if (children.length < 2) {
            throw new IllegalStateException(String.format("fewer than 2 children in branch: length=%d", children.length));
        }
        if (!checkChildShifts(shiftCount, children)) {
            throw new IllegalStateException("one or more children invalid for this branch");
        }
        if (computeSize(children) != size) {
            throw new IllegalStateException(String.format("size mismatch: size=%d computed=%d", size, computeSize(children)));
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
}
