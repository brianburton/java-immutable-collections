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

package org.javimmutable.collections.list.legacy;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.InsertableSequence;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.common.Subindexed;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.IndexedIterator;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.iterators.SequenceIterator;
import org.javimmutable.collections.sequence.EmptySequenceNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * JImmutableList implementation using 32-way trees.  The underlying trees
 * only allow values to be inserted or deleted from the head or tail of the list
 * so any methods attempting to do so inside the list throw UnsupportedOperationException.
 * Replaced by JImmutableTreeList since direct element access (get) is less common than simple
 * construction and iteration and this class is far less flexible in what it can do than the balanced
 * binary tree implementation.
 *
 * Retained temporarily for extreme backwards compatibility but will definitely be removed in the future.
 *
 * @deprecated will be removed on or after 2020-01-01
 */
@Deprecated
public class JImmutableArrayList<T>
    implements JImmutableList<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableArrayList EMPTY = new JImmutableArrayList(EmptyNode.of());

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
    public JImmutableArrayList<T> select(@Nonnull Predicate<T> predicate)
    {
        final Builder<T> builder = builder();
        for (T value : this) {
            if (predicate.test(value)) {
                builder.add(value);
            }
        }
        return builder.size() == size() ? this : builder.build();
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> reject(@Nonnull Predicate<T> predicate)
    {
        return select(predicate.negate());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> getInsertableSelf()
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(values);
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
    public JImmutableArrayList<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(values);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllFirst(@Nonnull Iterable<? extends T> values)
    {
        final Node<T> newRoot;
        if (values instanceof JImmutableArrayList) {
            final Node<T> other = ((JImmutableArrayList<T>)values).root;
            if (other.size() > root.size()) {
                newRoot = other.insertAll(Integer.MAX_VALUE, true, root.iterator());
            } else {
                newRoot = root.insertAll(Integer.MAX_VALUE, false, IndexedIterator.reverse(other));
            }
        } else if (values instanceof Indexed) {
            final Indexed<T> indexed = (Indexed<T>)values;
            newRoot = root.insertAll(Integer.MAX_VALUE, false, IndexedIterator.reverse(indexed));
        } else if (values instanceof List) {
            final Indexed<T> indexed = IndexedList.retained((List<T>)values);
            newRoot = root.insertAll(Integer.MAX_VALUE, false, IndexedIterator.reverse(indexed));
        } else {
            final Iterator<? extends T> iterator = values.iterator();
            InsertableSequence<T> seq = EmptySequenceNode.of();
            while (iterator.hasNext()) {
                seq = seq.insert(iterator.next());
            }
            newRoot = root.insertAll(Integer.MAX_VALUE, false, SequenceIterator.iterator(seq));
        }
        return (newRoot != root) ? new JImmutableArrayList<>(newRoot) : this;
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        InsertableSequence<T> seq = EmptySequenceNode.of();
        while (values.hasNext()) {
            seq = seq.insert(values.next());
        }
        final Node<T> newRoot = root.insertAll(Integer.MAX_VALUE, false, SequenceIterator.iterator(seq));
        return (newRoot != root) ? new JImmutableArrayList<>(newRoot) : this;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllLast(@Nonnull Iterable<? extends T> values)
    {
        if (values instanceof JImmutableArrayList) {
            final Node<T> other = ((JImmutableArrayList<T>)values).root;
            if (other.size() > root.size()) {
                final Node<T> newRoot = other.insertAll(Integer.MAX_VALUE, false, IndexedIterator.reverse(root));
                return (newRoot != root) ? new JImmutableArrayList<>(newRoot) : this;
            }
        }
        return insertAllLast(values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        final Node<T> newRoot = root.insertAll(Integer.MAX_VALUE, true, values);
        return (newRoot != root) ? new JImmutableArrayList<>(newRoot) : this;
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

    @Override
    @Nonnull
    public SplitableIterator<T> iterator()
    {
        return root.iterator();
    }

    @Override
    public <A> JImmutableArrayList<A> transform(@Nonnull Func1<T, A> transform)
    {
        final Builder<A> builder = builder();
        for (T t : root) {
            builder.add(transform.apply(t));
        }
        return builder.build();
    }

    @Override
    public <A> JImmutableArrayList<A> transformSome(@Nonnull Func1<T, Holder<A>> transform)
    {
        final Builder<A> builder = builder();
        for (T t : root) {
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

    @Nonnull
    @Override
    public JImmutableArrayList<T> insert(int index,
                                         @Nullable T value)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAll(int index,
                                            @Nonnull Iterable<? extends T> values)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAll(int index,
                                            @Nonnull Iterator<? extends T> values)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> prefix(int limit)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> suffix(int offset)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> middle(int offset,
                                         int limit)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public JImmutableList<T> slice(int offset,
                                   int limit)
    {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> delete(int index)
    {
        throw new UnsupportedOperationException();
    }

    public static class Builder<T>
        implements JImmutableList.Builder<T>
    {
        private final TreeBuilder<T> builder;

        private Builder()
        {
            builder = new TreeBuilder<>(true);
        }

        @Nonnull
        @Override
        public JImmutableArrayList<T> build()
        {
            return builder.size() == 0 ? of() : new JImmutableArrayList<>(builder.build());
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
        private Iterator<T> iterator()
        {
            return builder.build().iterator();
        }
    }
}
