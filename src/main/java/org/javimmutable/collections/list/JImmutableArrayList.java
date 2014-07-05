///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2014, Burton Computer Corporation
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
import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.MutableBuilder;
import org.javimmutable.collections.array.trie32.TrieArray;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.cursors.Cursors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Immutable
public class JImmutableArrayList<T>
        implements JImmutableList<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableArrayList EMPTY = new JImmutableArrayList(TrieArray.of(), 0, 0);

    private final JImmutableArray<T> values;
    private final int first;
    private final int next;

    private JImmutableArrayList(JImmutableArray<T> values,
                                int first,
                                int next)
    {
        this.values = values;
        this.first = first;
        this.next = next;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> JImmutableArrayList<T> of()
    {
        return (JImmutableArrayList<T>)EMPTY;
    }

    @Nonnull
    public static <T> JImmutableArrayList<T> of(Indexed<? extends T> source,
                                                int offset,
                                                int limit)
    {
        return JImmutableArrayList.<T>builder().add(source, offset, limit).build();
    }

    @Nonnull
    public static <T> JImmutableArrayList<T> of(Indexed<T> source)
    {
        return JImmutableArrayList.<T>builder().add(source).build();
    }

    @Nonnull
    public static <T> Builder<T> builder()
    {
        return new Builder<T>();
    }

    @Override
    public boolean isEmpty()
    {
        return first == next;
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> assign(int index,
                                         @Nullable T value)
    {
        final int realIndex = calcRealIndex(index);
        return new JImmutableArrayList<T>(values.assign(realIndex, value), first, next);
    }

    @Override
    @Nonnull
    public JImmutableArrayList<T> insert(@Nullable T value)
    {
        if (next == Integer.MAX_VALUE) {
            throw new IndexOutOfBoundsException();
        }
        final int index = next;
        return new JImmutableArrayList<T>(values.assign(index, value), first, index + 1);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        if (first == next) {
            return JImmutableArrayList.<T>builder().add(values.iterator()).build();
        } else {
            int index = next;
            JImmutableArray<T> newValues = this.values;
            for (T value : values) {
                newValues = newValues.assign(index, value);
                index += 1;
            }
            return new JImmutableArrayList<T>(newValues, first, index);
        }
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertFirst(@Nullable T value)
    {
        if (first == Integer.MIN_VALUE) {
            throw new IndexOutOfBoundsException();
        }
        final int index = first - 1;
        return new JImmutableArrayList<T>(values.assign(index, value), index, next);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertLast(@Nullable T value)
    {
        return insert(value);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> deleteFirst()
    {
        if (first == next) {
            throw new IndexOutOfBoundsException();
        }
        final int index = first;
        return new JImmutableArrayList<T>(values.delete(index), first + 1, next);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> deleteLast()
    {
        if (first == next) {
            throw new IndexOutOfBoundsException();
        }
        final int index = next - 1;
        return new JImmutableArrayList<T>(values.delete(index), first, index);
    }

    @Override
    public int size()
    {
        return next - first;
    }

    @Nonnull
    @Override
    public JImmutableList<T> deleteAll()
    {
        return of();
    }

    @Override
    public T get(int index)
    {
        final int realIndex = calcRealIndex(index);
        return values.get(realIndex);
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return ListAdaptor.of(this);
    }

    @Override
    @Nonnull
    public Cursor<T> cursor()
    {
        return values.valuesCursor();
    }

    @Override
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Override
    public boolean equals(Object o)
    {
        return (o == this) || ((o instanceof JImmutableList) && Cursors.areEqual(cursor(), ((JImmutableList)o).cursor()));
    }

    @Override
    public int hashCode()
    {
        return Cursors.computeHashCode(cursor());
    }

    @Override
    public String toString()
    {
        return Cursors.makeString(cursor());
    }

    public static class Builder<T>
            implements MutableBuilder<T, JImmutableArrayList<T>>
    {
        private final TrieArray.Builder<T> builder = TrieArray.builder();

        @Nonnull
        @Override
        public Builder<T> add(T value)
        {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public JImmutableArrayList<T> build()
        {
            final JImmutableArray<T> values = builder.build();
            if (values.isEmpty()) {
                return of();
            } else {
                return new JImmutableArrayList<T>(values, 0, values.size());
            }
        }

        @Nonnull
        @Override
        public Builder<T> add(Cursor<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Iterator<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Collection<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public <K extends T> Builder<T> add(K... source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Indexed<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        public Builder<T> add(Indexed<? extends T> source,
                              int offset,
                              int limit)
        {
            builder.add(source, offset, limit);
            return this;
        }
    }

    private int calcRealIndex(int index)
    {
        final int realIndex = first + index;
        if ((realIndex < first) || (realIndex >= next)) {
            throw new IndexOutOfBoundsException();
        }
        return realIndex;
    }
}
