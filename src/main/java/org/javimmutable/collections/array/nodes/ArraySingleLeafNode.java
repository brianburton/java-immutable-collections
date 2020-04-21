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
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nullable;

import static org.javimmutable.collections.MapEntry.entry;
import static org.javimmutable.collections.common.HamtLongMath.baseIndexFromHashCode;

public class ArraySingleLeafNode<T>
    extends ArrayNode<T>
{
    private final int iteratorIndex;
    private final int index;
    private final T value;

    ArraySingleLeafNode(int iteratorIndex,
                        int index,
                        T value)
    {
        this.iteratorIndex = iteratorIndex;
        this.index = index;
        this.value = value;
    }

    public static <T> ArrayNode<T> forValue(int entryBaseIndex,
                                            int index,
                                            T value)
    {
        return new ArraySingleLeafNode<>(entryBaseIndex + index, index, value);
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
        if (index == this.index) {
            return value;
        } else {
            return defaultValue;
        }
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        if (index == this.index) {
            return Holders.of(value);
        } else {
            return Holders.of();
        }
    }

    @Override
    public ArrayNode<T> assign(int entryBaseIndex,
                               int shiftCount,
                               int index,
                               T value)
    {
        if (index == this.index) {
            return new ArraySingleLeafNode<>(iteratorIndex, index, value);
        } else if (baseIndexFromHashCode(index) == baseIndexFromHashCode(this.index)) {
            return ArrayLeafNode.forValues(entryBaseIndex, this.index, this.value, index, value);
        } else {
            final ArrayNode<T> leaf = forValue(entryBaseIndex, index, value);
            return ArrayBranchNode.forChildren(this.index, this, index, leaf);
        }
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        if (index == this.index) {
            return ArrayEmptyNode.of();
        }
        return this;
    }

    @Override
    public void checkInvariants()
    {
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        return GenericIterator.valueState(parent, entry(iteratorIndex, value));
    }

    @Override
    public int iterableSize()
    {
        return 1;
    }

    @Override
    boolean isLeaf()
    {
        return true;
    }
}
