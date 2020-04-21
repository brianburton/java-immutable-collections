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
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.IterableStreamable;
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.array.nodes.ArrayEmptyNode;
import org.javimmutable.collections.array.nodes.ArrayNode;
import org.javimmutable.collections.common.ArrayHelper;
import org.javimmutable.collections.common.ArrayToMapAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.iterators.TransformStreamable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collector;

import static org.javimmutable.collections.MapEntry.entry;

public class JImmutableNodeArray<T>
    implements Serializable,
               JImmutableArray<T>,
               ArrayHelper.Allocator<ArrayNode<T>>
{
    @SuppressWarnings("rawtypes")
    private static final JImmutableNodeArray EMPTY = new JImmutableNodeArray();

    private final ArrayNode<T> negative;
    private final ArrayNode<T> positive;
    private final int size;

    private JImmutableNodeArray()
    {
        negative = ArrayEmptyNode.of();
        positive = ArrayEmptyNode.of();
        this.size = 0;
    }

    private JImmutableNodeArray(ArrayNode<T> negative,
                                ArrayNode<T> positive,
                                int size)
    {
        this.negative = negative;
        this.positive = positive;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <T> JImmutableArray<T> of()
    {
        return (JImmutableArray<T>)EMPTY;
    }

    public static <T> JImmutableArray.Builder<T> builder()
    {
        return new Builder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableArray<T>> collector()
    {
        return Collector.<T, JImmutableNodeArray.Builder<T>, JImmutableArray<T>>of(() -> new Builder<>(),
                                                                                   (b, v) -> b.add(v),
                                                                                   (b1, b2) -> (Builder<T>)b1.add(b2.iterator()),
                                                                                   b -> b.build());
    }

    @Nonnull
    private ArrayNode<T> root(int userIndex)
    {
        return userIndex < 0 ? negative : positive;
    }

    @Nonnull
    private JImmutableArray<T> withRoot(int userIndex,
                                        @Nonnull ArrayNode<T> newRoot,
                                        int size)
    {
        if (userIndex < 0) {
            return new JImmutableNodeArray<>(newRoot, positive, size);
        } else {
            return new JImmutableNodeArray<>(negative, newRoot, size);
        }
    }

    static int entryBaseIndex(int userIndex)
    {
        return userIndex < 0 ? Integer.MIN_VALUE : 0;
    }

    static int nodeIndex(int userIndex)
    {
        return userIndex < 0 ? userIndex - Integer.MIN_VALUE : userIndex;
    }

    @Nullable
    @Override
    public T get(int index)
    {
        return getValueOr(index, null);
    }

    @Override
    public T getValueOr(int index,
                        @Nullable T defaultValue)
    {
        final int nodeIndex = nodeIndex(index);
        return root(index).getValueOr(ArrayNode.ROOT_SHIFTS, nodeIndex, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<T> find(int index)
    {
        final int nodeIndex = nodeIndex(index);
        return root(index).find(ArrayNode.ROOT_SHIFTS, nodeIndex);
    }

    @Nonnull
    @Override
    public Holder<JImmutableMap.Entry<Integer, T>> findEntry(int index)
    {
        return find(index).map(v -> entry(index, v));
    }

    @Nonnull
    @Override
    public JImmutableArray<T> assign(int index,
                                     @Nullable T value)
    {
        final int entryBaseIndex = entryBaseIndex(index);
        final int nodeIndex = nodeIndex(index);
        final ArrayNode<T> child = root(index);
        final ArrayNode<T> newChild = child.assign(entryBaseIndex, ArrayNode.ROOT_SHIFTS, nodeIndex, value);
        final int newSize = size - child.iterableSize() + newChild.iterableSize();
        return withRoot(index, newChild, newSize);
    }

    @Nonnull
    @Override
    public JImmutableArray<T> delete(int index)
    {
        final int nodeIndex = nodeIndex(index);
        final ArrayNode<T> child = root(index);
        final ArrayNode<T> newChild = child.delete(ArrayNode.ROOT_SHIFTS, nodeIndex);
        if (newChild != child) {
            final int newSize = size - child.iterableSize() + newChild.iterableSize();
            if (newSize == 0) {
                return of();
            } else {
                return withRoot(index, newChild, newSize);
            }
        }
        return this;
    }

    @Override
    public int size()
    {
        return size;
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    @Override
    public boolean isNonEmpty()
    {
        return size != 0;
    }

    @Nonnull
    @Override
    public JImmutableArray<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public Map<Integer, T> getMap()
    {
        return ArrayToMapAdaptor.of(this);
    }

    @Nonnull
    @Override
    public IterableStreamable<Integer> keys()
    {
        return TransformStreamable.ofKeys(this);
    }

    @Nonnull
    @Override
    public IterableStreamable<T> values()
    {
        return TransformStreamable.ofValues(this);
    }

    @Nonnull
    @Override
    public JImmutableArray<T> insert(JImmutableMap.Entry<Integer, T> e)
    {
        return (e == null) ? this : assign(e.getKey(), e.getValue());
    }

    @Nonnull
    @Override
    public JImmutableArray<T> getInsertableSelf()
    {
        return this;
    }

    @Override
    public void checkInvariants()
    {
        negative.checkInvariants();
        positive.checkInvariants();
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return new GenericIterator<>(new IterableChildren(), 0, size);
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ArrayNode<T>[] allocate(int size)
    {
        return (ArrayNode<T>[])new ArrayNode[size];
    }

    @Override
    public boolean equals(Object o)
    {
        return (o == this) || ((o instanceof JImmutableArray) && IteratorHelper.iteratorEquals(iterator(), ((JImmutableArray)o).iterator()));
    }

    @Override
    public int hashCode()
    {
        return IteratorHelper.iteratorHashCode(iterator());
    }

    @Override
    public String toString()
    {
        return IteratorHelper.iteratorToString(iterator());
    }

    private class IterableChildren
        implements GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>>
    {
        @Nullable
        @Override
        public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                       int offset,
                                                                                       int limit)
        {
            final Indexed<ArrayNode<T>> source = IndexedHelper.indexed(negative, positive);
            return GenericIterator.indexedState(parent, source, offset, limit);
        }

        @Override
        public int iterableSize()
        {
            return JImmutableNodeArray.this.size;
        }
    }

    public static class Builder<T>
        implements JImmutableArray.Builder<T>
    {
        private JImmutableArray<T> array;

        private Builder()
        {
            array = of();
        }

        @Override
        public int size()
        {
            return array.size();
        }

        @Nonnull
        @Override
        public JImmutableArray.Builder<T> clear()
        {
            array = of();
            return this;
        }

        @Nonnull
        @Override
        public JImmutableArray.Builder<T> add(T value)
        {
            array = array.assign(array.size(), value);
            return this;
        }

        @Nonnull
        @Override
        public JImmutableArray<T> build()
        {
            return array;
        }

        @Nonnull
        private Iterator<T> iterator()
        {
            return array.values().iterator();
        }
    }
}
