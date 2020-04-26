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
import org.javimmutable.collections.common.ArrayToMapAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.indexed.IndexedHelper;
import org.javimmutable.collections.iterators.GenericIterator;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.iterators.TransformIterator;
import org.javimmutable.collections.iterators.TransformStreamable;
import org.javimmutable.collections.serialization.JImmutableArrayProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collector;

import static org.javimmutable.collections.MapEntry.entry;

public class JImmutableTrieArray<T>
    implements Serializable,
               JImmutableArray<T>
{
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final JImmutableTrieArray EMPTY = new JImmutableTrieArray(TrieArrayNode.empty(), TrieArrayNode.empty(), 0);

    private final TrieArrayNode<T> negative;
    private final TrieArrayNode<T> positive;
    private final int size;

    private JImmutableTrieArray(@Nonnull TrieArrayNode<T> negative,
                                @Nonnull TrieArrayNode<T> positive,
                                int size)
    {
        this.negative = negative;
        this.positive = positive;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> JImmutableArray<T> of()
    {
        return (JImmutableArray<T>)EMPTY;
    }

    @Nonnull
    public static <T> JImmutableArray.Builder<T> builder()
    {
        return new Builder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableArray<T>> collector()
    {
        return Collector.<T, JImmutableTrieArray.Builder<T>, JImmutableArray<T>>of(() -> new Builder<>(),
                                                                                   (b, v) -> b.add(v),
                                                                                   (b1, b2) -> (Builder<T>)b1.add(b2.iterator()),
                                                                                   b -> b.build());
    }

    @Nonnull
    private TrieArrayNode<T> root(int userIndex)
    {
        return userIndex < 0 ? negative : positive;
    }

    @Nonnull
    private JImmutableArray<T> withRoot(int userIndex,
                                        @Nonnull TrieArrayNode<T> newRoot,
                                        int size)
    {
        if (userIndex < 0) {
            return new JImmutableTrieArray<>(newRoot, positive, size);
        } else {
            return new JImmutableTrieArray<>(negative, newRoot, size);
        }
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
        final int nodeIndex = TrieArrayNode.nodeIndex(index);
        return root(index).getValueOr(TrieArrayNode.ROOT_SHIFT_COUNT, nodeIndex, defaultValue);
    }

    @Nonnull
    @Override
    public Holder<T> find(int index)
    {
        final int nodeIndex = TrieArrayNode.nodeIndex(index);
        return root(index).find(TrieArrayNode.ROOT_SHIFT_COUNT, nodeIndex);
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
        final int nodeIndex = TrieArrayNode.nodeIndex(index);
        final TrieArrayNode<T> child = root(index);
        final TrieArrayNode<T> newChild = child.assign(TrieArrayNode.ROOT_SHIFT_COUNT, nodeIndex, value);
        final int newSize = size - child.size() + newChild.size();
        return withRoot(index, newChild, newSize);
    }

    @Nonnull
    @Override
    public JImmutableArray<T> delete(int index)
    {
        final int nodeIndex = TrieArrayNode.nodeIndex(index);
        final TrieArrayNode<T> child = root(index);
        final TrieArrayNode<T> newChild = child.delete(TrieArrayNode.ROOT_SHIFT_COUNT, nodeIndex);
        if (newChild != child) {
            final int newSize = size - child.size() + newChild.size();
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
        final int computedSize = negative.size() + positive.size();
        if (computedSize != size) {
            throw new IllegalStateException(String.format("size mismatch: expected=%d actual=%d", computedSize, this.size));
        }
    }

    @Nonnull
    @Override
    public SplitableIterator<JImmutableMap.Entry<Integer, T>> iterator()
    {
        return new GenericIterator<>(new IterableImpl(), 0, size);
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @SuppressWarnings("rawtypes")
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

    @Nonnull
    @Override
    public JImmutableArray.Builder<T> arrayBuilder()
    {
        return new Builder<>();
    }

    private Object writeReplace()
    {
        return new JImmutableArrayProxy(this);
    }

    private class IterableImpl
        implements GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>>
    {
        @Nullable
        @Override
        public GenericIterator.State<JImmutableMap.Entry<Integer, T>> iterateOverRange(@Nullable GenericIterator.State<JImmutableMap.Entry<Integer, T>> parent,
                                                                                       int offset,
                                                                                       int limit)
        {
            final Indexed<GenericIterator.Iterable<JImmutableMap.Entry<Integer, T>>> source = IndexedHelper.indexed(negative.iterable(TrieArrayNode.NEGATIVE_BASE_INDEX),
                                                                                                                    positive.iterable(TrieArrayNode.POSITIVE_BASE_INDEX));
            return GenericIterator.indexedState(parent, source, offset, limit);
        }

        @Override
        public int iterableSize()
        {
            return JImmutableTrieArray.this.size;
        }
    }

    @ThreadSafe
    public static class Builder<T>
        implements JImmutableArray.Builder<T>
    {
        private final TrieArrayBuilder<T> builder;

        private Builder()
        {
            builder = new TrieArrayBuilder<>();
        }

        @Override
        public synchronized int size()
        {
            return builder.size();
        }

        @Nonnull
        @Override
        public synchronized JImmutableArray.Builder<T> clear()
        {
            builder.reset();
            return this;
        }

        @Nonnull
        @Override
        public synchronized JImmutableArray.Builder<T> add(T value)
        {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public synchronized JImmutableArray.Builder<T> put(int index,
                                                           T value)
        {
            builder.put(index, value);
            return this;
        }

        @Override
        public synchronized JImmutableArray.Builder<T> setNextIndex(int index)
        {
            builder.setNextIndex(index);
            return this;
        }

        @Nonnull
        @Override
        public synchronized JImmutableArray<T> build()
        {
            return builder.size() == 0 ? of() : new JImmutableTrieArray<>(builder.buildNegativeRoot(), builder.buildPositiveRoot(), builder.size());
        }

        @Nonnull
        private synchronized Iterator<T> iterator()
        {
            return TransformIterator.of(builder.iterator(), JImmutableMap.Entry::getValue);
        }
    }
}
