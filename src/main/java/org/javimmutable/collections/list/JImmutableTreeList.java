///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2019, Burton Computer Corporation
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

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.functional.Each1Throws;
import org.javimmutable.collections.functional.Sum1Throws;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.serialization.JImmutableListProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collector;

import static org.javimmutable.collections.list.TreeBuilder.*;

@Immutable
public class JImmutableTreeList<T>
    implements JImmutableList<T>,
               Serializable
{
    @SuppressWarnings("unchecked")
    private static final JImmutableTreeList EMPTY = new JImmutableTreeList(EmptyNode.instance());
    private static final long serialVersionUID = -121805;

    private final AbstractNode<T> root;

    private JImmutableTreeList(@Nonnull AbstractNode<T> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> JImmutableTreeList<T> of()
    {
        return EMPTY;
    }

    @Nonnull
    public static <T> JImmutableTreeList<T> of(@Nonnull Indexed<? extends T> values)
    {
        return create(nodeFromIndexed(values, 0, values.size()));
    }

    @Nonnull
    public static <T> JImmutableTreeList<T> of(@Nonnull Indexed<? extends T> values,
                                               int offset,
                                               int limit)
    {
        return create(nodeFromIndexed(values, offset, limit));
    }

    @Nonnull
    public static <T> JImmutableTreeList<T> of(@Nonnull Iterator<? extends T> values)
    {
        return create(nodeFromIterator(values));
    }

    @Nonnull
    public static <T> ListBuilder<T> listBuilder()
    {
        return new ListBuilder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableList<T>> createListCollector()
    {
        return Collector.<T, ListBuilder<T>, JImmutableList<T>>of(() -> new ListBuilder<>(),
                                                                  (b, v) -> b.add(v),
                                                                  (b1, b2) -> b1.combineWith(b2),
                                                                  b -> b.build());
    }

    @Nonnull
    static <T> JImmutableTreeList<T> create(@Nonnull AbstractNode<T> root)
    {
        if (root.isEmpty()) {
            return of();
        } else {
            return new JImmutableTreeList<>(root);
        }
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> assign(int index,
                                        @Nullable T value)
    {
        return new JImmutableTreeList<>(root.assign(index, value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insert(@Nullable T value)
    {
        return new JImmutableTreeList<>(root.append(value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        return create(root.append(nodeFromIterable(values)));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insert(int index,
                                        @Nullable T value)
    {
        return new JImmutableTreeList<>(root.insert(index, value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertFirst(@Nullable T value)
    {
        return new JImmutableTreeList<>(root.prepend(value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertLast(@Nullable T value)
    {
        return new JImmutableTreeList<>(root.append(value));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(nodeFromIterator(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Iterable<? extends T> values)
    {
        return insertAll(index, nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Iterator<? extends T> values)
    {
        return insertAll(index, nodeFromIterator(values));
    }

    @Nonnull
    private JImmutableTreeList<T> insertAll(int index,
                                            @Nonnull AbstractNode<T> other)
    {
        return create(root.prefix(index).append(other).append(root.suffix(index)));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Iterable<? extends T> values)
    {
        return insertAllFirst(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        return insertAllFirst(nodeFromIterator(values));
    }

    @Nonnull
    private JImmutableTreeList<T> insertAllFirst(@Nonnull AbstractNode<T> other)
    {
        return create(root.prepend(other));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(nodeFromIterator(values));
    }

    @Nonnull
    private JImmutableTreeList<T> insertAllLast(@Nonnull AbstractNode<T> other)
    {
        return create(root.append(other));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteFirst()
    {
        return create(root.deleteFirst());
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteLast()
    {
        return create(root.deleteLast());
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> delete(int index)
    {
        return create(root.delete(index));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteAll()
    {
        return of();
    }

    @Override
    public <A> JImmutableTreeList<A> transform(@Nonnull Func1<T, A> transform)
    {
        final ListBuilder<A> builder = new ListBuilder<>();
        for (T t : this) {
            builder.add(transform.apply(t));
        }
        return builder.build();
    }

    @Override
    public <A> JImmutableTreeList<A> transformSome(@Nonnull Func1<T, Holder<A>> transform)
    {
        final ListBuilder<A> builder = new ListBuilder<>();
        for (T t : this) {
            final Holder<A> ha = transform.apply(t);
            if (ha.isFilled()) {
                builder.add(ha.getValue());
            }
        }
        return builder.build();
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

    @Override
    public boolean isEmpty()
    {
        return root.isEmpty();
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> select(@Nonnull Predicate<T> predicate)
    {
        final ListBuilder<T> answer = listBuilder();
        for (T value : this) {
            if (predicate.test(value)) {
                answer.add(value);
            }
        }
        return answer.size() == size() ? this : answer.build();
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> reject(@Nonnull Predicate<T> predicate)
    {
        JImmutableTreeList<T> answer = this;
        int index = 0;
        for (T value : this) {
            assert value == answer.get(index);
            if (predicate.test(value)) {
                answer = answer.delete(index);
            } else {
                index += 1;
            }
        }
        return answer.size() == size() ? this : answer;
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> prefix(int limit)
    {
        return create(root.prefix(limit));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> suffix(int offset)
    {
        return create(root.suffix(offset));
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> middle(int offset,
                                        int limit)
    {
        return create(root.prefix(limit).suffix(offset));
    }

    @Nonnull
    @Override
    public JImmutableList<T> slice(int offset,
                                   int limit)
    {
        final int size = root.size();
        if (offset < 0) {
            offset = size + offset;
        }
        if (limit < 0) {
            limit = size + limit + 1;
        }
        if (offset < 0) {
            offset = 0;
        } else if (offset > size) {
            offset = size;
        }
        if (limit < offset) {
            limit = offset;
        } else if (limit > size) {
            limit = size;
        }
        return middle(offset, limit);
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return new ListAdaptor<>(this);
    }

    @Nonnull
    @Override
    public JImmutableList<T> getInsertableSelf()
    {
        return this;
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants();
    }

    @Override
    @Nonnull
    public SplitableIterator<T> iterator()
    {
        return root.iterator();
    }

    @Override
    public int getSpliteratorCharacteristics()
    {
        return StreamConstants.SPLITERATOR_ORDERED;
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

    @SuppressWarnings("unchecked")
    @Nonnull
    private AbstractNode<T> nodeFromIterable(@Nonnull Iterable<? extends T> values)
    {
        AbstractNode<T> otherRoot;
        if (values instanceof JImmutableTreeList) {
            otherRoot = ((JImmutableTreeList<T>)values).root;
        } else if (values instanceof List) {
            otherRoot = TreeBuilder.nodeFromIndexed(IndexedList.retained((List)values));
        } else {
            otherRoot = nodeFromIterator(values.iterator());
        }
        return otherRoot;
    }

    @Override
    public void forEach(Consumer<? super T> action)
    {
        root.forEach(action);
    }

    @Override
    public <E extends Exception> void forEachThrows(@Nonnull Each1Throws<T, E> proc)
        throws E
    {
        root.forEachThrows(proc);
    }

    @Override
    public <V> V inject(V initialValue,
                        Func2<V, T, V> accumulator)
    {
        return root.inject(initialValue, accumulator);
    }

    @Override
    public <V, E extends Exception> V injectThrows(V initialValue,
                                                   Sum1Throws<T, V, E> accumulator)
        throws E
    {
        return root.injectThrows(initialValue, accumulator);
    }

    @ThreadSafe
    public static class ListBuilder<T>
        implements JImmutableList.Builder<T>
    {
        private final TreeBuilder<T> builder = new TreeBuilder<>();

        @Nonnull
        @Override
        public synchronized JImmutableTreeList<T> build()
        {
            return create(builder.build());
        }

        @Nonnull
        public synchronized ListBuilder<T> combineWith(@Nonnull ListBuilder<T> other)
        {
            builder.combineWith(other.builder);
            return this;
        }

        @Override
        public synchronized int size()
        {
            return builder.size();
        }

        @Nonnull
        @Override
        public synchronized ListBuilder<T> add(T value)
        {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public synchronized ListBuilder<T> add(Iterator<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public synchronized ListBuilder<T> add(Iterable<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public synchronized <K extends T> ListBuilder<T> add(K... source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public synchronized ListBuilder<T> add(Indexed<? extends T> source,
                                               int offset,
                                               int limit)
        {
            builder.add(source, offset, limit);
            return this;
        }

        @Nonnull
        @Override
        public synchronized ListBuilder<T> add(Indexed<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        public synchronized void checkInvariants()
        {
            builder.checkInvariants();
        }
    }
}
