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

package org.javimmutable.collections.deque;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.IDeque;
import org.javimmutable.collections.IDequeBuilder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.InvariantCheckable;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.DequeListAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.serialization.ArrayDequeProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * IDeque implementation using 32-way trees.  The underlying trees
 * only allow values to be inserted or deleted from the head or tail.
 */
public class ArrayDeque<T>
    implements IDeque<T>
{
    @SuppressWarnings("unchecked")
    private static final ArrayDeque EMPTY = new ArrayDeque(EmptyNode.of());

    private final Node<T> root;

    private ArrayDeque(Node<T> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> ArrayDeque<T> of()
    {
        return (ArrayDeque<T>)EMPTY;
    }

    @Nonnull
    public static <T> IDeque<T> of(Indexed<? extends T> source)
    {
        return new Builder<T>().addAll(source).build();
    }

    @Nonnull
    public static <T> Builder<T> builder()
    {
        return new Builder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, IDeque<T>> collector()
    {
        return Collector.<T, Builder<T>, IDeque<T>>of(() -> new Builder<>(),
                                                      (b, v) -> b.add(v),
                                                      (b1, b2) -> (Builder<T>)b1.addAll(b2.iterator()),
                                                      b -> b.build());
    }

    @Nonnull
    static <T> ArrayDeque<T> create(@Nonnull Node<T> root)
    {
        if (root.isEmpty()) {
            return of();
        } else {
            return new ArrayDeque<>(root);
        }
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
    public Maybe<T> find(int index)
    {
        return root.find(index);
    }

    @Nonnull
    @Override
    public ArrayDeque<T> assign(int index,
                                @Nullable T value)
    {
        return create(root.assign(index, value));
    }

    @Nonnull
    @Override
    public ArrayDeque<T> insert(@Nullable T value)
    {
        return create(root.insertLast(value));
    }

    @Nonnull
    @Override
    public ArrayDeque<T> select(@Nonnull Predicate<T> predicate)
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
    public ArrayDeque<T> reject(@Nonnull Predicate<T> predicate)
    {
        return select(predicate.negate());
    }

    @Nonnull
    @Override
    public ArrayDeque<T> insertFirst(@Nullable T value)
    {
        return create(root.insertFirst(value));
    }

    @Nonnull
    @Override
    public ArrayDeque<T> insertLast(@Nullable T value)
    {
        return create(root.insertLast(value));
    }

    @Nonnull
    @Override
    public ArrayDeque<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public ArrayDeque<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(values);
    }

    private static <T> Node<T> insertAllFirstImpl(Node<T> root,
                                                  Indexed<? extends T> values)
    {
        ReverseBuilder<T> builder = ReverseBuilder.insertAtBeginning(root);
        builder.addAll(values.reversed());
        return builder.build();
    }

    private static <T> Node<T> insertAllFirstImpl(Node<T> root,
                                                  Iterator<? extends T> values)
    {
        ReverseBuilder<T> builder = ReverseBuilder.insertAtBeginning(root);
        builder.addAll(IteratorHelper.<T>reverse(values));
        return builder.build();
    }

    private static <T> Node<T> insertAllLastImpl(Node<T> root,
                                                 Indexed<? extends T> values)
    {
        ForwardBuilder<T> builder = ForwardBuilder.insertAtEnd(root);
        builder.addAll(values);
        return builder.build();
    }

    private static <T> Node<T> insertAllLastImpl(Node<T> root,
                                                 Iterator<? extends T> values)
    {
        ForwardBuilder<T> builder = ForwardBuilder.insertAtEnd(root);
        builder.addAll(values);
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ArrayDeque<T> insertAllFirst(@Nonnull Iterable<? extends T> values)
    {
        final Node<T> newRoot;
        if (values instanceof ArrayDeque) {
            final Node<T> other = ((ArrayDeque<T>)values).root;
            if (other.size() > root.size()) {
                newRoot = insertAllLastImpl(other, root);
            } else {
                newRoot = insertAllFirstImpl(root, other);
            }
        } else if (values instanceof Indexed) {
            final Indexed<T> indexed = (Indexed<T>)values;
            newRoot = insertAllFirstImpl(root, indexed);
        } else if (values instanceof List) {
            final Indexed<T> indexed = IndexedList.retained((List<T>)values);
            newRoot = insertAllFirstImpl(root, indexed);
        } else {
            newRoot = insertAllFirstImpl(root, values.iterator());
        }
        return (newRoot != root) ? create(newRoot) : this;
    }

    @Nonnull
    @Override
    public ArrayDeque<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        final Node<T> newRoot = insertAllFirstImpl(root, values);
        return (newRoot != root) ? create(newRoot) : this;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public ArrayDeque<T> insertAllLast(@Nonnull Iterable<? extends T> values)
    {
        if (values instanceof ArrayDeque) {
            final Node<T> other = ((ArrayDeque<T>)values).root;
            if (other.size() > root.size()) {
                final Node<T> newRoot = insertAllFirstImpl(other, root);
                return (newRoot != root) ? create(newRoot) : this;
            }
        }
        return insertAllLast(values.iterator());
    }

    @Nonnull
    @Override
    public ArrayDeque<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        final Node<T> newRoot = insertAllLastImpl(root, values);
        return (newRoot != root) ? create(newRoot) : this;
    }

    @Nonnull
    @Override
    public ArrayDeque<T> deleteFirst()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return create(root.deleteFirst());
    }

    @Nonnull
    @Override
    public ArrayDeque<T> deleteLast()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return create(root.deleteLast());
    }

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Override
    public boolean isNonEmpty()
    {
        return !root.isEmpty();
    }

    @Nonnull
    @Override
    public ArrayDeque<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return DequeListAdaptor.of(this);
    }

    @Nonnull
    @Override
    public IDeque<T> reverse()
    {
        return create(root.reverse());
    }

    @Override
    @Nonnull
    public SplitableIterator<T> iterator()
    {
        return root.iterator();
    }

    @Override
    public <A> ArrayDeque<A> transform(@Nonnull Func1<T, A> transform)
    {
        final Builder<A> builder = builder();
        for (T t : root) {
            builder.add(transform.apply(t));
        }
        return builder.build();
    }

    @Override
    public <A> ArrayDeque<A> transformSome(@Nonnull Func1<T, Maybe<A>> transform)
    {
        final Builder<A> builder = builder();
        for (T t : root) {
            transform.apply(t).apply(v -> builder.add(v));
        }
        return builder.build();
    }

    @Nonnull
    @Override
    public IDeque<T> prefix(int limit)
    {
        final Node<T> newRoot = root.prefix(limit);
        return (newRoot == root) ? this : create(newRoot);
    }

    @Nonnull
    @Override
    public IDeque<T> suffix(int offset)
    {
        final Node<T> newRoot = root.suffix(offset);
        return (newRoot == root) ? this : create(newRoot);
    }

    @Nonnull
    public IDeque<T> middle(int offset,
                            int limit)
    {
        return prefix(limit).suffix(offset);
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
        return (o == this) || ((o instanceof IDeque) && IteratorHelper.iteratorEquals(iterator(), ((IDeque)o).iterator()));
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
        return new ArrayDequeProxy(this);
    }

    public static class Builder<T>
        implements IDequeBuilder<T>,
                   InvariantCheckable
    {
        private final ForwardBuilder<T> builder;

        private Builder()
        {
            builder = ForwardBuilder.insertAtEnd(EmptyNode.of());
        }

        @Nonnull
        @Override
        public synchronized ArrayDeque<T> build()
        {
            return create(builder.build());
        }

        @Override
        public synchronized int size()
        {
            return builder.size();
        }

        @Nonnull
        @Override
        public synchronized Builder<T> add(T value)
        {
            builder.add(value);
            return this;
        }

        @Nonnull
        private synchronized Iterator<T> iterator()
        {
            return builder.build().iterator();
        }

        @Nonnull
        @Override
        public synchronized IDequeBuilder<T> clear()
        {
            builder.clear();
            return this;
        }

        @Override
        public void checkInvariants()
        {
            builder.checkInvariants();
        }
    }
}
