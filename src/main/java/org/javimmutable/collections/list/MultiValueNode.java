///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2024, Burton Computer Corporation
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

import org.javimmutable.collections.Func0;
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.ToStringHelper;
import org.javimmutable.collections.indexed.IndexedArray;
import org.javimmutable.collections.iterators.GenericIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.function.Consumer;

@Immutable
class MultiValueNode<T>
    extends AbstractNode<T>
    implements ArrayHelper.Allocator<T>
{
    static final int MAX_SIZE = 128;
    static final int SPLIT_SIZE = MAX_SIZE / 2;

    private final T[] values;

    MultiValueNode(T a,
                   T b)
    {
        values = allocate(2);
        values[0] = a;
        values[1] = b;
    }

    /**
     * Builds a leaf node using the provided array directly (i.e. not copied).
     *
     * @param values array to retain and use for leaf node
     */
    private MultiValueNode(T[] values)
    {
        assert values.length > 1;
        assert values.length <= MAX_SIZE;
        this.values = values;
    }

    /**
     * Builds a leaf node using a new array of specified size copied from the provided array.
     *
     * @param values array to copy for use in leaf node
     */
    MultiValueNode(T[] values,
                   int count)
    {
        assert count > 1;
        assert count <= MAX_SIZE;
        this.values = ArrayHelper.prefix(this, values, count);
    }

    /**
     * Builds a leaf node using a new array populated by calling copyTo() on the two nodes.
     * Total size of the two nodes must not exceed MAX_SIZE.
     */
    MultiValueNode(@Nonnull AbstractNode<T> left,
                   @Nonnull AbstractNode<T> right,
                   int size)
    {
        assert size > 1;
        assert size <= MAX_SIZE;
        assert size == (left.size() + right.size());
        values = allocate(size);
        left.copyTo(values, 0);
        right.copyTo(values, left.size());
    }

    @Override
    boolean isEmpty()
    {
        return values.length == 0;
    }

    @Override
    int size()
    {
        return values.length;
    }

    @Override
    int depth()
    {
        return 0;
    }

    @Override
    T get(int index)
    {
        return values[index];
    }

    @Override
    <C> C findImpl(int index,
                   Func0<C> defaultMapping,
                   Func1<T, C> valueMapping)
    {
        try {
            return valueMapping.apply(values[index]);
        } catch (ArrayIndexOutOfBoundsException ignored) {
            return defaultMapping.apply();
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> append(T value)
    {
        if (values.length < MAX_SIZE) {
            T[] newValues = ArrayHelper.append(this, values, value);
            return new MultiValueNode<>(newValues);
        } else {
            return new BranchNode<>(this, new OneValueNode<>(value));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> append(@Nonnull AbstractNode<T> node)
    {
        if (node.isEmpty()) {
            return this;
        } else if (node.depth() > 0) {
            return node.prepend(this);
        } else {
            final int combinedSize = size() + node.size();
            if (combinedSize <= MAX_SIZE) {
                return new MultiValueNode<>(this, node, combinedSize);
            } else {
                return new BranchNode<>(this, node, combinedSize);
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(T value)
    {
        if (values.length < MAX_SIZE) {
            T[] newValues = ArrayHelper.prepend(this, values, value);
            return new MultiValueNode<>(newValues);
        } else {
            return new BranchNode<>(new OneValueNode<>(value), this);
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> prepend(@Nonnull AbstractNode<T> node)
    {
        if (node.isEmpty()) {
            return this;
        } else if (node.depth() > 0) {
            return node.append(this);
        } else {
            final int combinedSize = size() + node.size();
            if (combinedSize <= MAX_SIZE) {
                return new MultiValueNode<>(node, this, combinedSize);
            } else {
                return new BranchNode<>(node, this, combinedSize);
            }
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> assign(int index,
                           T value)
    {
        return new MultiValueNode<>(ArrayHelper.assign(values, index, value));
    }

    @Nonnull
    @Override
    AbstractNode<T> insert(int index,
                           T value)
    {
        if (values.length < MAX_SIZE) {
            return new MultiValueNode<>(ArrayHelper.insert(this, values, index, value));
        } else {
            final T[] left, right;
            if (index <= SPLIT_SIZE) {
                left = ArrayHelper.prefixInsert(this, values, SPLIT_SIZE, index, value);
                right = ArrayHelper.suffix(this, values, SPLIT_SIZE);
            } else {
                left = ArrayHelper.prefix(this, values, SPLIT_SIZE);
                right = ArrayHelper.suffixInsert(this, values, SPLIT_SIZE, index, value);
            }
            return new BranchNode<>(new MultiValueNode<>(left), new MultiValueNode<>(right));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> delete(int index)
    {
        final int length = values.length;
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException();
        } else if (length == 1) {
            ArrayHelper.checkBounds(values, index);
            return EmptyNode.instance();
        } else if (length == 2) {
            return new OneValueNode<>(values[1 - index]);
        } else {
            return new MultiValueNode<>(ArrayHelper.delete(this, values, index));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteFirst()
    {
        return delete(0);
    }

    @Nonnull
    @Override
    AbstractNode<T> deleteLast()
    {
        return delete(values.length - 1);
    }

    @Override
    void copyTo(T[] array,
                int offset)
    {
        System.arraycopy(values, 0, array, offset, values.length);
    }

    @Nonnull
    @Override
    AbstractNode<T> prefix(int limit)
    {
        final int length = values.length;
        if (limit < 0 || limit > length) {
            throw new IndexOutOfBoundsException();
        } else if (limit == 0) {
            return EmptyNode.instance();
        } else if (limit == length) {
            return this;
        } else if (limit == 1) {
            return new OneValueNode<>(values[0]);
        } else {
            return new MultiValueNode<>(ArrayHelper.prefix(this, values, limit));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> suffix(int offset)
    {
        final int length = values.length;
        if (offset < 0 || offset > length) {
            throw new IndexOutOfBoundsException();
        } else if (offset == 0) {
            return this;
        } else if (offset == length - 1) {
            return new OneValueNode<>(values[offset]);
        } else if (offset == length) {
            return EmptyNode.instance();
        } else {
            return new MultiValueNode<>(ArrayHelper.suffix(this, values, offset));
        }
    }

    @Nonnull
    @Override
    AbstractNode<T> reverse()
    {
        return new MultiValueNode<>(ArrayHelper.reverse(this, values));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T[] allocate(int size)
    {
        assert size > 0;
        return (T[])new Object[size];
    }

    @Override
    public void checkInvariants()
    {
        int currentSize = values.length;
        if (currentSize < 1 || currentSize > MAX_SIZE) {
            throw new RuntimeException(String.format("incorrect size: currentSize=%d", currentSize));
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MultiValueNode<?> leafNode = (MultiValueNode<?>)o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(values, leafNode.values);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(values);
    }

    @Override
    public String toString()
    {
        return ToStringHelper.arrayToString(values);
    }

    @Nullable
    @Override
    public GenericIterator.State<T> iterateOverRange(@Nullable GenericIterator.State<T> parent,
                                                     int offset,
                                                     int limit)
    {
        assert offset >= 0 && offset <= limit && limit <= values.length;
        return GenericIterator.multiValueState(parent, IndexedArray.retained(values), offset, limit);
    }

    @Override
    public void forEach(Consumer<? super T> action)
    {
        for (T value : values) {
            action.accept(value);
        }
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        for (T value : values) {
            proc.apply(value);
        }
    }

    @Override
    public <V> V reduce(V sum,
                        Func2<V, T, V> accumulator)
    {
        for (T value : values) {
            sum = accumulator.apply(sum, value);
        }
        return sum;
    }

    @Override
    public <V, E extends Exception> V reduceThrows(V sum,
                                                   Sum1Throws<T, V, E> accumulator)
        throws E
    {
        for (T value : values) {
            sum = accumulator.apply(sum, value);
        }
        return sum;
    }
}
