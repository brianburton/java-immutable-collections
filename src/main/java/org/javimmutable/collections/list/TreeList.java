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

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Func2;
import org.javimmutable.collections.IList;
import org.javimmutable.collections.IListBuilder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.Maybe;
import org.javimmutable.collections.Proc1Throws;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.Sum1Throws;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.common.MutableDelta;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.iterators.IteratorHelper;
import static org.javimmutable.collections.list.TreeBuilder.nodeFromIndexed;
import static org.javimmutable.collections.list.TreeBuilder.nodeFromIterator;
import org.javimmutable.collections.serialization.TreeListProxy;

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

@Immutable
public class TreeList<T>
    implements IList<T>,
               Serializable
{
    @SuppressWarnings("unchecked")
    private static final TreeList EMPTY = new TreeList(EmptyNode.instance());
    private static final long serialVersionUID = -121805;

    private final AbstractNode<T> root;

    private TreeList(@Nonnull AbstractNode<T> root)
    {
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static <T> TreeList<T> of()
    {
        return EMPTY;
    }

    @Nonnull
    public static <T> TreeList<T> of(@Nonnull Indexed<? extends T> values)
    {
        return create(nodeFromIndexed(values, 0, values.size()));
    }

    @Nonnull
    public static <T> TreeList<T> of(@Nonnull Indexed<? extends T> values,
                                     int offset,
                                     int limit)
    {
        return create(nodeFromIndexed(values, offset, limit));
    }

    @Nonnull
    public static <T> TreeList<T> of(@Nonnull Iterator<? extends T> values)
    {
        return create(nodeFromIterator(values));
    }

    @Nonnull
    public static <T> ListBuilder<T> listBuilder()
    {
        return new ListBuilder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, IList<T>> createListCollector()
    {
        return Collector.<T, ListBuilder<T>, IList<T>>of(() -> new ListBuilder<>(),
                                                         (b, v) -> b.add(v),
                                                         (b1, b2) -> b1.combineWith(b2),
                                                         b -> b.build());
    }

    @Nonnull
    static <T> TreeList<T> create(@Nonnull AbstractNode<T> root)
    {
        if (root.isEmpty()) {
            return of();
        } else {
            return new TreeList<>(root);
        }
    }

    @Nonnull
    @Override
    public TreeList<T> assign(int index,
                              @Nullable T value)
    {
        return new TreeList<>(root.assign(index, value));
    }

    @Nonnull
    @Override
    public TreeList<T> insert(@Nullable T value)
    {
        return new TreeList<>(root.append(value));
    }

    @Nonnull
    @Override
    public TreeList<T> insert(int index,
                              @Nullable T value)
    {
        AbstractNode<T> newRoot;
        if (index == 0) {
            newRoot = root.prepend(value);
        } else if (index == root.size()) {
            newRoot = root.append(value);
        } else {
            newRoot = root.insert(index, value);
        }
        return new TreeList<>(newRoot);
    }

    @Nonnull
    @Override
    public TreeList<T> insertFirst(@Nullable T value)
    {
        return new TreeList<>(root.prepend(value));
    }

    @Nonnull
    @Override
    public TreeList<T> insertLast(@Nullable T value)
    {
        return new TreeList<>(root.append(value));
    }

    @Nonnull
    @Override
    public TreeList<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public TreeList<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(nodeFromIterator(values));
    }

    @Nonnull
    @Override
    public TreeList<T> insertAll(int index,
                                 @Nonnull Iterable<? extends T> values)
    {
        return insertAll(index, nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public TreeList<T> insertAll(int index,
                                 @Nonnull Iterator<? extends T> values)
    {
        return insertAll(index, nodeFromIterator(values));
    }

    @Nonnull
    private TreeList<T> insertAll(int index,
                                  @Nonnull AbstractNode<T> other)
    {
        return create(root.prefix(index).append(other).append(root.suffix(index)));
    }

    @Nonnull
    @Override
    public TreeList<T> insertAllFirst(@Nonnull Iterable<? extends T> values)
    {
        return insertAllFirst(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public TreeList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        return insertAllFirst(nodeFromIterator(values));
    }

    @Nonnull
    private TreeList<T> insertAllFirst(@Nonnull AbstractNode<T> other)
    {
        return create(root.prepend(other));
    }

    @Nonnull
    @Override
    public TreeList<T> insertAllLast(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(nodeFromIterable(values));
    }

    @Nonnull
    @Override
    public TreeList<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(nodeFromIterator(values));
    }

    @Nonnull
    private TreeList<T> insertAllLast(@Nonnull AbstractNode<T> other)
    {
        return create(root.append(other));
    }

    @Nonnull
    @Override
    public TreeList<T> deleteFirst()
    {
        return create(root.deleteFirst());
    }

    @Nonnull
    @Override
    public TreeList<T> deleteLast()
    {
        return create(root.deleteLast());
    }

    @Nonnull
    @Override
    public TreeList<T> delete(int index)
    {
        return create(root.delete(index));
    }

    @Nonnull
    @Override
    public TreeList<T> deleteAll()
    {
        return of();
    }

    @Nonnull
    @Override
    public IList<T> reverse()
    {
        final AbstractNode<T> newRoot = root.reverse();
        if (newRoot == root) {
            return this;
        } else {
            return new TreeList<>(newRoot);
        }
    }

    @Override
    public <A> TreeList<A> transform(@Nonnull Func1<T, A> transform)
    {
        final ListBuilder<A> builder = new ListBuilder<>();
        root.forEach(t -> builder.add(transform.apply(t)));
        return builder.build();
    }

    @Override
    public <A> TreeList<A> transformSome(@Nonnull Func1<T, Maybe<A>> transform)
    {
        final ListBuilder<A> builder = new ListBuilder<>();
        root.forEach(t -> transform.apply(t).apply(tt -> builder.add(tt)));
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

    @Nonnull
    @Override
    public Maybe<T> find(int index)
    {
        return root.findImpl(index, () -> Maybe.empty(), value -> Maybe.of(value));
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
    public TreeList<T> select(@Nonnull Predicate<T> predicate)
    {
        final ListBuilder<T> answer = listBuilder();
        root.forEach(value -> {
            if (predicate.test(value)) {
                answer.add(value);
            }
        });
        return answer.size() == size() ? this : answer.build();
    }

    @Nonnull
    @Override
    public TreeList<T> reject(@Nonnull Predicate<T> predicate)
    {
        final MutableDelta index = new MutableDelta();
        final AbstractNode<T> newRoot = root.reduce(root, (answer, value) -> {
            assert value == answer.get(index.getValue());
            if (predicate.test(value)) {
                answer = answer.delete(index.getValue());
            } else {
                index.add(1);
            }
            return answer;
        });
        if (newRoot.isEmpty()) {
            return of();
        } else if (newRoot == root) {
            return this;
        } else {
            return new TreeList<>(newRoot);
        }
    }

    @Nonnull
    @Override
    public TreeList<T> prefix(int limit)
    {
        return create(root.prefix(limit));
    }

    @Nonnull
    @Override
    public TreeList<T> suffix(int offset)
    {
        return create(root.suffix(offset));
    }

    @Nonnull
    @Override
    public TreeList<T> middle(int offset,
                              int limit)
    {
        return create(root.prefix(limit).suffix(offset));
    }

    @Nonnull
    @Override
    public IList<T> slice(int offset,
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
        return (o == this) || ((o instanceof IList) && IteratorHelper.iteratorEquals(iterator(), ((IList)o).iterator()));
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
        return new TreeListProxy(this);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    private AbstractNode<T> nodeFromIterable(@Nonnull Iterable<? extends T> values)
    {
        AbstractNode<T> otherRoot;
        if (values instanceof TreeList) {
            otherRoot = ((TreeList<T>)values).root;
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
    public <E extends Exception> void forEachThrows(@Nonnull Proc1Throws<T, E> proc)
        throws E
    {
        root.forEachThrows(proc);
    }

    @Override
    public <V> V reduce(V initialValue,
                        Func2<V, T, V> accumulator)
    {
        return root.reduce(initialValue, accumulator);
    }

    @Override
    public <V, E extends Exception> V reduceThrows(V initialValue,
                                                   Sum1Throws<T, V, E> accumulator)
        throws E
    {
        return root.reduceThrows(initialValue, accumulator);
    }

    @ThreadSafe
    public static class ListBuilder<T>
        implements IListBuilder<T>
    {
        private final TreeBuilder<T> builder = new TreeBuilder<>();

        @Nonnull
        @Override
        public TreeList<T> build()
        {
            return create(builder.build());
        }

        @Nonnull
        public ListBuilder<T> combineWith(@Nonnull ListBuilder<T> other)
        {
            AbstractNode<T> myRoot = builder.build();
            AbstractNode<T> theirRoot = other.builder.build();
            builder.rebuild(myRoot.append(theirRoot));
            return this;
        }

        @Override
        public int size()
        {
            return builder.size();
        }

        @Nonnull
        @Override
        public ListBuilder<T> add(T value)
        {
            builder.add(value);
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> addAll(Iterator<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> addAll(Iterable<? extends T> source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public <K extends T> ListBuilder<T> addAll(K... source)
        {
            builder.add(source);
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> addAll(Indexed<? extends T> source,
                                     int offset,
                                     int limit)
        {
            builder.add(source, offset, limit);
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> addAll(Indexed<? extends T> source)
        {
            builder.add(source, 0, source.size());
            return this;
        }

        @Nonnull
        @Override
        public ListBuilder<T> clear()
        {
            builder.clear();
            return this;
        }

        public void checkInvariants()
        {
            builder.checkInvariants();
        }
    }
}
