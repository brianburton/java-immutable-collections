///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.btree_list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Tuple2;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.IndexedIterator;

import javax.annotation.Nonnull;
import java.util.Arrays;

class BtreeLeafNode<T>
    implements BtreeNode<T>,
               ArrayHelper.Allocator<T>
{
    private final T[] values;

    @SuppressWarnings("unchecked")
    BtreeLeafNode(T value)
    {
        this.values = (T[])(new Object[]{value});
    }

    @SuppressWarnings("unchecked")
    static <T> BtreeLeafNode<T> of(Indexed<? extends T> source,
                                   int offset,
                                   int limit)
    {
        final int length = limit - offset;
        assert (length > 0) && (length <= MAX_CHILDREN);
        assert limit <= source.size();
        final T[] values = (T[])new Object[length];
        for (int i = 0; i < length; ++i) {
            values[i] = source.get(offset + i);
        }
        return new BtreeLeafNode<>(values);
    }

    private BtreeLeafNode(T[] values)
    {
        this.values = values;
    }

    @Override
    public int childCount()
    {
        return values.length;
    }

    @Override
    public int valueCount()
    {
        return values.length;
    }

    @Override
    public T get(int index)
    {
        return values[index];
    }

    @Nonnull
    @Override
    public BtreeNode<T> assign(int index,
                               T value)
    {
        return new BtreeLeafNode<>(ArrayHelper.assign(values, index, value));
    }

    @Nonnull
    @Override
    public BtreeInsertResult<T> insertAt(int index,
                                         T value)
    {
        final T[] newValues = ArrayHelper.insert(this, values, index, value);
        if (values.length == MAX_CHILDREN) {
            if (index == values.length) {
                return BtreeInsertResult.createSplit(new BtreeLeafNode<>(ArrayHelper.subArray(this, newValues, 0, MIN_CHILDREN + 1)),
                                                     new BtreeLeafNode<>(ArrayHelper.subArray(this, newValues, MIN_CHILDREN + 1, newValues.length)));
            } else {
                return BtreeInsertResult.createSplit(new BtreeLeafNode<>(ArrayHelper.subArray(this, newValues, 0, MIN_CHILDREN)),
                                                     new BtreeLeafNode<>(ArrayHelper.subArray(this, newValues, MIN_CHILDREN, newValues.length)));
            }
        } else {
            return BtreeInsertResult.createInPlace(new BtreeLeafNode<>(newValues));
        }
    }

    @Nonnull
    @Override
    public BtreeInsertResult<T> append(T value)
    {
        return insertAt(values.length, value);
    }

    @Override
    public boolean containsIndex(int index)
    {
        return index < values.length;
    }

    @Override
    public void checkInvariants()
    {
        if (values.length > MAX_CHILDREN) {
            throw new IllegalStateException();
        }
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return StandardCursor.of(IndexedArray.retained(values));
    }

    @Nonnull
    @Override
    public SplitableIterator<T> iterator()
    {
        return IndexedIterator.iterator(IndexedArray.retained(values));
    }

    @Nonnull
    @Override
    public BtreeNode<T> delete(int index)
    {
        if ((values.length == 1) && (index == 0)) {
            return BtreeEmptyNode.of();
        } else {
            return new BtreeLeafNode<>(ArrayHelper.delete(this, values, index));
        }
    }

    @Nonnull
    @Override
    public BtreeNode<T> mergeChildren(BtreeNode<T> sibling)
    {
        final BtreeLeafNode<T> leaf = (BtreeLeafNode<T>)sibling;
        assert (leaf.values.length + values.length) <= MAX_CHILDREN;
        return new BtreeLeafNode<>(ArrayHelper.concat(this, values, leaf.values));
    }

    @Nonnull
    @Override
    public Tuple2<BtreeNode<T>, BtreeNode<T>> distributeChildren(BtreeNode<T> sibling)
    {
        final BtreeLeafNode<T> leaf = (BtreeLeafNode<T>)sibling;
        assert (leaf.values.length + values.length) >= MAX_CHILDREN;
        assert (leaf.values.length + values.length) <= (2 * MAX_CHILDREN);
        return Tuple2.of(new BtreeLeafNode<>(ArrayHelper.subArray(this, values, leaf.values, 0, MIN_CHILDREN)),
                         new BtreeLeafNode<>(ArrayHelper.subArray(this, values, leaf.values, MIN_CHILDREN, values.length + leaf.values.length)));
    }

    @Nonnull
    @Override
    public BtreeNode<T> firstChild()
    {
        return this;
    }

    @Override
    public int depth()
    {
        return 1;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T[] allocate(int size)
    {
        return (T[])(new Object[size]);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        BtreeLeafNode that = (BtreeLeafNode)o;

        if (!Arrays.equals(values, that.values)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return (values != null) ? Arrays.hashCode(values) : 0;
    }
}
