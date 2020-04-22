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
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.javimmutable.collections.common.HamtLongMath.*;

public class ArrayFullBranchNode<T>
    extends ArrayNode<T>
{
    private final int shiftCount;
    private final int baseIndex;
    private final ArrayNode<T>[] children;
    private final int size;

    ArrayFullBranchNode(int shiftCount,
                        int baseIndex,
                        @Nonnull ArrayNode<T>[] children,
                        int size)
    {
        this.shiftCount = shiftCount;
        this.baseIndex = baseIndex;
        this.children = children;
        this.size = size;
        assert children.length == ARRAY_SIZE;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public T getValueOr(int shiftCount,
                        int index,
                        T defaultValue)
    {
        assert shiftCount >= this.shiftCount;
        if (shiftCount != this.shiftCount) {
            if (baseIndexAtShift(this.shiftCount, index) != baseIndex) {
                return defaultValue;
            }
            shiftCount = this.shiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        return children[childIndex].getValueOr(shiftCount - 1, index, defaultValue);
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        assert shiftCount >= this.shiftCount;
        if (shiftCount != this.shiftCount) {
            if (baseIndexAtShift(this.shiftCount, index) != baseIndex) {
                return Holders.of();
            }
            shiftCount = this.shiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        return children[childIndex].find(shiftCount - 1, index);
    }

    @Override
    public ArrayNode<T> assign(int entryBaseIndex,
                               int shiftCount,
                               int index,
                               T value)
    {
        final int thisShiftCount = this.shiftCount;
        final int baseIndex = this.baseIndex;
        assert shiftCount >= thisShiftCount;
        if (shiftCount != thisShiftCount) {
            if (baseIndexAtShift(thisShiftCount, index) != baseIndex) {
                final ArrayNode<T> leaf = ArraySingleLeafNode.forValue(entryBaseIndex, index, value);
                return ArrayBranchNode.forChildren(baseIndex, this, index, leaf);
            }
            shiftCount = thisShiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final ArrayNode<T>[] children = this.children;
        final ArrayNode<T> child = children[childIndex];
        final ArrayNode<T> newChild = child.assign(entryBaseIndex, shiftCount - 1, index, value);
        assert newChild != child;
        final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
        final int newSize = size - child.iterableSize() + newChild.iterableSize();
        return new ArrayFullBranchNode<>(shiftCount, baseIndex, newChildren, newSize);
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        final int thisShiftCount = this.shiftCount;
        final int baseIndex = this.baseIndex;
        assert shiftCount >= thisShiftCount;
        if (shiftCount != thisShiftCount) {
            if (baseIndexAtShift(thisShiftCount, index) != baseIndex) {
                return this;
            }
            shiftCount = thisShiftCount;
        }
        final int childIndex = indexAtShift(shiftCount, index);
        final ArrayNode<T>[] children = this.children;
        final ArrayNode<T> child = children[childIndex];
        final ArrayNode<T> newChild = child.delete(shiftCount - 1, index);
        if (newChild != child) {
            final int newSize = size - child.iterableSize() + newChild.iterableSize();
            if (newChild.isEmpty()) {
                return ArrayBranchNode.fullWithout(shiftCount, baseIndex, childIndex, children, newSize);
            } else {
                final ArrayNode<T>[] newChildren = ArrayHelper.assign(children, childIndex, newChild);
                return new ArrayFullBranchNode<>(shiftCount, baseIndex, newChildren, newSize);
            }
        }
        return this;
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

    @Override
    public void checkInvariants()
    {
        if (!checkChildShifts(shiftCount, children)) {
            throw new IllegalStateException("one or more children invalid for this branch");
        }
        if (computeSize(children) != size) {
            throw new IllegalStateException(String.format("size mismatch: size=%d computed=%d", size, computeSize(children)));
        }
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        final Indexed<ArrayNode<T>> source = IndexedArray.retained(children);
        return GenericIterator.indexedState(parent, source, offset, limit);
    }

    @Override
    public int iterableSize()
    {
        return size;
    }
}
