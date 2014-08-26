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
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.ListAdaptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class JImmutableArrayList2<T>
        implements JImmutableList<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableArrayList2 EMPTY = new JImmutableArrayList2(EmptyNode.of());

    private final Node<T> root;

    private JImmutableArrayList2(Node<T> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> JImmutableArrayList2<T> of()
    {
        return (JImmutableArrayList2<T>)EMPTY;
    }

    @Nonnull
    public static <T> JImmutableArrayList2<T> of(Indexed<? extends T> source,
                                                 int offset,
                                                 int limit)
    {
        return JImmutableArrayList2.<T>builder().add(source, offset, limit).build();
    }

    @Nonnull
    public static <T> JImmutableArrayList2<T> of(Indexed<T> source)
    {
        return JImmutableArrayList2.<T>builder().add(source).build();
    }

    @Nonnull
    public static <T> Builder<T> builder()
    {
        return new Builder<T>();
    }

    @Override
    public int size()
    {
        return root.size();
    }

    @Override
    public T get(int index)
    {
        return root.get(index);
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> assign(int index,
                                          @Nullable T value)
    {
        return new JImmutableArrayList2<T>(root.assign(index, value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> insert(@Nullable T value)
    {
        return new JImmutableArrayList2<T>(root.insertLast(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> insert(@Nonnull Iterable<? extends T> values)
    {
        Node<T> newRoot = root;
        for (T value : values) {
            newRoot = newRoot.insertLast(value);
        }
        return new JImmutableArrayList2<T>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> insertFirst(@Nullable T value)
    {
        return new JImmutableArrayList2<T>(root.insertFirst(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> insertLast(@Nullable T value)
    {
        return new JImmutableArrayList2<T>(root.insertLast(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> deleteFirst()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableArrayList2<T>(root.deleteFirst());
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> deleteLast()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableArrayList2<T>(root.deleteLast());
    }

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Nonnull
    @Override
    public JImmutableArrayList2<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return ListAdaptor.of(this);
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return root.cursor();
    }

    @Override
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    void checkInvariants()
    {
        root.checkInvariants();
    }

    public static class Builder<T>
            implements JImmutableList.Builder<T>
    {
        private final BranchNode.Builder<T> builder;

        public Builder()
        {
            this.builder = BranchNode.builder();
        }

        @Nonnull
        @Override
        public Builder<T> add(T value)
        {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public JImmutableArrayList2<T> build()
        {
            final Node<T> node = builder.build();
            return node.isEmpty() ? JImmutableArrayList2.<T>of() : new JImmutableArrayList2<T>(builder.build());
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
        @Override
        public Builder<T> add(Indexed<? extends T> source,
                              int offset,
                              int limit)
        {
            builder.add(source, offset, limit);
            return this;
        }
    }
}
