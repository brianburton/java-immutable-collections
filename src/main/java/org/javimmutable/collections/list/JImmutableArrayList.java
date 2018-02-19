///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2018, Burton Computer Corporation
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
import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.InsertableSequence;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.Sequence;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.common.Subindexed;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.sequence.EmptySequenceNode;
import org.javimmutable.collections.serialization.JImmutableListProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;

/**
 * JImmutableList implementation using 32-way trees.  The underlying trees, like the JImmutableList,
 * only allow values to be inserted or deleted from the head or tail of the list.
 */
public class JImmutableArrayList<T>
    implements JImmutableList<T>,
               Serializable
{
    @SuppressWarnings("unchecked")
    private static final JImmutableArrayList EMPTY = new JImmutableArrayList(EmptyNode.of());
    private static final long serialVersionUID = -121805;

    private final Node<T> root;

    private JImmutableArrayList(Node<T> root)
    {
        this.root = root;
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
        return of(Subindexed.of(source, offset, limit));
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> JImmutableArrayList<T> of(Indexed<? extends T> source)
    {
        final Node<T> root = BranchNode.of(source);
        return root.isEmpty() ? (JImmutableArrayList<T>)EMPTY : new JImmutableArrayList<>(root);
    }

    @Nonnull
    public static <T> Builder<T> builder()
    {
        return new Builder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableList<T>> collector()
    {
        return Collector.<T, Builder<T>, JImmutableList<T>>of(() -> new Builder<>(),
                                                              (b, v) -> b.add(v),
                                                              (b1, b2) -> (Builder<T>)b1.add(b2.iterator()),
                                                              b -> b.build());
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
    public JImmutableArrayList<T> assign(int index,
                                         @Nullable T value)
    {
        return new JImmutableArrayList<>(root.assign(index, value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insert(@Nullable T value)
    {
        return new JImmutableArrayList<>(root.insertLast(value));
    }

    @Nonnull
    @Override
    public JImmutableList<T> getInsertableSelf()
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        Node<T> newRoot = root;
        for (T value : values) {
            newRoot = newRoot.insertLast(value);
        }
        return new JImmutableArrayList<>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertFirst(@Nullable T value)
    {
        return new JImmutableArrayList<>(root.insertFirst(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertLast(@Nullable T value)
    {
        return new JImmutableArrayList<>(root.insertLast(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAll(@Nonnull Cursor<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllFirst(@Nonnull Iterable<? extends T> values)
    {
        return insertAllFirst(values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllFirst(@Nonnull Cursor<? extends T> values)
    {
        return insertAllFirst(values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        InsertableSequence<T> seq = EmptySequenceNode.of();
        while (values.hasNext()) {
            seq = seq.insert(values.next());
        }
        return insertAllFirstImpl(seq);
    }

    private JImmutableArrayList<T> insertAllFirstImpl(Sequence<T> seq)
    {
        Node<T> newRoot = root;
        while (!seq.isEmpty()) {
            newRoot = newRoot.insertFirst(seq.getHead());
            seq = seq.getTail();
        }
        return new JImmutableArrayList<>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllLast(@Nonnull Iterable<? extends T> values)
    {
        Node<T> newRoot = root.insertAll(Integer.MAX_VALUE, true, values.iterator());
        newRoot.checkInvariants();
        return new JImmutableArrayList<>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllLast(@Nonnull Cursor<? extends T> values)
    {
        return insertAllLast(values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        Node<T> newRoot = root;
        while (values.hasNext()) {
            newRoot = newRoot.insertLast(values.next());
        }
        return new JImmutableArrayList<>(newRoot);
    }


    @Nonnull
    @Override
    public JImmutableArrayList<T> deleteFirst()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableArrayList<>(root.deleteFirst());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> deleteLast()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableArrayList<>(root.deleteLast());
    }

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> deleteAll()
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
    @Nonnull
    public SplitableIterator<T> iterator()
    {
        return root.iterator();
    }

    @Override
    public <A> JImmutableList<A> transform(@Nonnull Func1<T, A> transform)
    {
        final Builder<A> builder = builder();
        for (T t : this) {
            builder.add(transform.apply(t));
        }
        return builder.build();
    }

    @Override
    public <A> JImmutableList<A> transformSome(@Nonnull Func1<T, Holder<A>> transform)
    {
        final Builder<A> builder = builder();
        for (T t : this) {
            final Holder<A> ha = transform.apply(t);
            if (ha.isFilled()) {
                builder.add(ha.getValue());
            }
        }
        return builder.build();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants();
    }

    @Override
    public boolean equals(Object o)
    {
        return (o == this) || ((o instanceof JImmutableList) && IteratorHelper.iteratorEquals(iterator(), ((JImmutableList)o).iterator()));
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

    private Object writeReplace()
    {
        return new JImmutableListProxy(this);
    }

    public static class Builder<T>
        implements JImmutableList.Builder<T>
    {
        private final TreeBuilder<T> builder;

        private Builder()
        {
            builder = new TreeBuilder<>(true);
        }

        @Override
        public int size()
        {
            return builder.size();
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
        public JImmutableArrayList<T> build()
        {
            return builder.size() == 0 ? of() : new JImmutableArrayList<>(builder.build());
        }

        @Nonnull
        private Iterator<T> iterator()
        {
            return builder.build().iterator();
        }
    }
}
