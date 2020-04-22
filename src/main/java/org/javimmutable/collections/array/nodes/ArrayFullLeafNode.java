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
import org.javimmutable.collections.MapEntry;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.javimmutable.collections.common.HamtLongMath.*;

public class ArrayFullLeafNode<T>
    extends ArrayNode<T>
{
    private final int iteratorBaseIndex;
    private final int baseIndex;
    private final T[] values;

    ArrayFullLeafNode(int iteratorBaseIndex,
                      int baseIndex,
                      T[] values)
    {
        assert values.length == ARRAY_SIZE;
        this.iteratorBaseIndex = iteratorBaseIndex;
        this.baseIndex = baseIndex;
        this.values = values;
    }

    @Override
    public int iterableSize()
    {
        return ARRAY_SIZE;
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
        final int valueIndex = indexFromHashCode(index);
        if (baseIndex + valueIndex != index) {
            return defaultValue;
        }
        return values[valueIndex];
    }

    @Override
    public Holder<T> find(int shiftCount,
                          int index)
    {
        final int valueIndex = indexFromHashCode(index);
        if (baseIndex + valueIndex != index) {
            return Holders.of();
        }
        return Holders.of(values[valueIndex]);
    }

    @Override
    public ArrayNode<T> assign(int entryBaseIndex,
                               int shiftCount,
                               int index,
                               T value)
    {
        final int valueIndex = indexFromHashCode(index);
        final int baseIndex = this.baseIndex;
        if (baseIndex + valueIndex != index) {
            assert shiftCount > LEAF_SHIFTS;
            final ArrayNode<T> leaf = ArraySingleLeafNode.forValue(entryBaseIndex, index, value);
            return ArrayBranchNode.forChildren(baseIndex, this, index, leaf);
        }
        final T[] newValues = ArrayHelper.assign(values, valueIndex, value);
        return new ArrayFullLeafNode<>(iteratorBaseIndex, baseIndex, newValues);
    }

    @Override
    public ArrayNode<T> delete(int shiftCount,
                               int index)
    {
        final int valueIndex = indexFromHashCode(index);
        final int baseIndex = this.baseIndex;
        if (baseIndex + valueIndex != index) {
            return this;
        }
        final long bit = bitFromIndex(valueIndex);
        final long newBitmask = removeBit(ALL_BITS, bit);
        final T[] newValues = ArrayHelper.delete(values, valueIndex);
        return new ArrayLeafNode<>(iteratorBaseIndex, baseIndex, newBitmask, newValues);
    }

    @Nonnull
    private JImmutableMap.Entry<Integer, T> valueEntry(int valueIndex)
    {
        final T value = values[valueIndex];
        return MapEntry.entry(iteratorBaseIndex + valueIndex, value);
    }

    @Nullable
    @Override
    public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                   int offset,
                                                                                   int limit)
    {
        final Indexed<Integer> indices = IndexedHelper.range(0, ARRAY_SIZE);
        final Indexed<JImmutableMap.Entry<Integer, T>> entries = IndexedHelper.transformed(indices, this::valueEntry);
        return GenericIterator.multiValueState(parent, entries, offset, limit);
    }

    @Override
    public void checkInvariants()
    {
        if (ARRAY_SIZE != values.length) {
            throw new IllegalStateException(String.format("invalid length for array: expected=%s length=%d", ARRAY_SIZE, values.length));
        }
    }

    @Override
    boolean isLeaf()
    {
        return true;
    }

    @Override
    int shiftCount()
    {
        return LEAF_SHIFTS;
    }
}
