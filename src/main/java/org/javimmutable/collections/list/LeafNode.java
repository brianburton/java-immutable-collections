///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.common.IndexedArray;
import org.javimmutable.collections.common.IndexedList;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;

/**
 * Node that forms the bottom of the 32-way tree and contains up to 32 values.
 *
 * @param <T>
 */
@Immutable
class LeafNode<T>
        implements Node<T>
{
    @Nonnull
    private final T[] values;

    private LeafNode(@Nonnull T[] values)
    {
        assert values.length > 0;
        assert values.length <= 32;
        this.values = values;
    }

    LeafNode(T value)
    {
        values = ListHelper.allocateValues(1);
        values[0] = value;
    }

    static <T> LeafNode<T> fromList(List<T> values,
                                    int offset,
                                    int limit)
    {
        return fromList(IndexedList.retained(values), offset, limit);
    }

    static <T> LeafNode<T> fromList(Indexed<? extends T> values,
                                    int offset,
                                    int limit)
    {
        T[] array = ListHelper.allocateValues(limit - offset);
        for (int i = offset; i < limit; ++i) {
            array[i - offset] = values.get(i);
        }
        return new LeafNode<T>(array);
    }

    static <T> LeafNode<T> forTesting(T[] values)
    {
        return new LeafNode<T>(values.clone());
    }

    @Override
    public boolean isEmpty()
    {
        return values.length == 0;
    }

    @Override
    public boolean isFull()
    {
        return values.length == 32;
    }

    @Override
    public int size()
    {
        return values.length;
    }

    @Override
    public int getDepth()
    {
        return 1;
    }

    @Override
    public Node<T> deleteFirst()
    {
        if (values.length == 1) {
            return EmptyNode.of();
        }
        T[] newValues = ListHelper.allocateValues(values.length - 1);
        System.arraycopy(values, 1, newValues, 0, newValues.length);
        return new LeafNode<T>(newValues);
    }

    @Override
    public Node<T> deleteLast()
    {
        if (values.length == 1) {
            return EmptyNode.of();
        }
        T[] newValues = ListHelper.allocateValues(values.length - 1);
        System.arraycopy(values, 0, newValues, 0, newValues.length);
        return new LeafNode<T>(newValues);
    }

    @Override
    public Node<T> insertFirst(T value)
    {
        if (isFull()) {
            return new BranchNode<T>(value, this);
        }
        T[] newValues = ListHelper.allocateValues(values.length + 1);
        System.arraycopy(values, 0, newValues, 1, values.length);
        newValues[0] = value;
        return new LeafNode<T>(newValues);
    }

    @Override
    public Node<T> insertLast(T value)
    {
        if (isFull()) {
            return new BranchNode<T>(this, value);
        }
        T[] newValues = ListHelper.allocateValues(values.length + 1);
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[values.length] = value;
        return new LeafNode<T>(newValues);
    }

    @Override
    public boolean containsIndex(int index)
    {
        return (index >= 0) && (index < values.length);
    }

    @Override
    public T get(int index)
    {
        return values[index];
    }

    @Override
    public Node<T> assign(int index,
                          T value)
    {
        T[] newValues = values.clone();
        newValues[index] = value;
        return new LeafNode<T>(newValues);
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of(IndexedArray.retained(values));
    }

    @Override
    public void checkInvariants()
    {
        if ((values.length == 0) || (values.length > 32)) {
            throw new IllegalStateException();
        }
        //TODO: review checkInvariants()
    }
}
