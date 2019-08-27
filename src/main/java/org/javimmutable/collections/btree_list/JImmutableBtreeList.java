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

package org.javimmutable.collections.btree_list;

import org.javimmutable.collections.Func1;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.SplitableIterator;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.common.StreamConstants;
import org.javimmutable.collections.indexed.IndexedList;
import org.javimmutable.collections.iterators.IteratorHelper;
import org.javimmutable.collections.serialization.JImmutableRandomAccessListProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;

/**
 * Implementation of JImmutableRandomAccessList that uses a B-Tree for its implementation.
 */
@Immutable
public class JImmutableBtreeList<T>
    implements JImmutableRandomAccessList<T>,
               Serializable
{
    private static final JImmutableBtreeList<Object> EMPTY = new JImmutableBtreeList<>(BtreeEmptyNode.of());
    private static final long serialVersionUID = -121805;

    private final BtreeNode<T> root;

    @SuppressWarnings("unchecked")
    public static <T> JImmutableBtreeList<T> of()
    {
        return (JImmutableBtreeList<T>)EMPTY;
    }

    public static <T> Builder<T> builder()
    {
        return new Builder<>();
    }

    @Nonnull
    public static <T> Collector<T, ?, JImmutableRandomAccessList<T>> collector()
    {
        return Collector.<T, Builder<T>, JImmutableRandomAccessList<T>>of(() -> new Builder<>(),
                                                                          (b, v) -> b.add(v),
                                                                          (b1, b2) -> b1.combineWith(b2),
                                                                          b -> b.build());
    }

    @Nonnull
    public static <T> JImmutableBtreeList<T> of(@Nonnull Indexed<? extends T> values)
    {
        int nodeCount = values.size();
        if (nodeCount == 0) {
            return of();
        }

        if (nodeCount <= BtreeNode.MAX_CHILDREN) {
            return new JImmutableBtreeList<>(BtreeLeafNode.of(values, 0, nodeCount));
        }

        final List<BtreeNode<T>> nodes = new ArrayList<>();
        int remaining = nodeCount;
        int offset = 0;
        while (remaining > 0) {
            BtreeNode<T> node;
            if (remaining <= BtreeNode.MAX_CHILDREN) {
                node = BtreeLeafNode.of(values, offset, Math.min(offset + BtreeNode.MAX_CHILDREN, nodeCount));
                remaining = 0;
                offset = nodeCount;
            } else {
                node = BtreeLeafNode.of(values, offset, offset + BtreeNode.MIN_CHILDREN);
                remaining -= BtreeNode.MIN_CHILDREN;
                offset += BtreeNode.MIN_CHILDREN;
            }
            nodes.add(node);
        }
        nodeCount = nodes.size();

        final Indexed<BtreeNode<T>> indexed = IndexedList.retained(nodes);
        while (nodeCount > 1) {
            remaining = nodeCount;
            offset = 0;
            int branchCount = 0;
            while (remaining > 0) {
                BtreeNode<T> node;
                if (remaining <= BtreeNode.MAX_CHILDREN) {
                    node = BtreeBranchNode.of(indexed, offset, Math.min(offset + BtreeNode.MAX_CHILDREN, nodeCount));
                    remaining = 0;
                    offset = nodeCount;
                } else {
                    node = BtreeBranchNode.of(indexed, offset, offset + BtreeNode.MIN_CHILDREN);
                    remaining -= BtreeNode.MIN_CHILDREN;
                    offset += BtreeNode.MIN_CHILDREN;
                }
                nodes.set(branchCount, node);
                branchCount += 1;
            }
            nodeCount = branchCount;
        }

        return new JImmutableBtreeList<>(nodes.get(0));
    }

    private JImmutableBtreeList(BtreeNode<T> root)
    {
        this.root = root;
    }

    private static <T> JImmutableBtreeList<T> create(BtreeInsertResult<T> insertResult)
    {
        if (insertResult.type == BtreeInsertResult.Type.INPLACE) {
            return new JImmutableBtreeList<>(insertResult.newNode);
        } else {
            return new JImmutableBtreeList<>(new BtreeBranchNode<>(insertResult.newNode, insertResult.extraNode));
        }
    }

    private JImmutableBtreeList<T> createForDelete(@Nonnull BtreeNode<T> newRoot)
    {
        if (newRoot.valueCount() == 0) {
            return of();
        }
        return new JImmutableBtreeList<>(newRoot.compress());
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> assign(int index,
                                         @Nullable T value)
    {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableBtreeList<>(root.assign(index, value));
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insert(@Nullable T value)
    {
        return create(root.append(value));
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insert(int index,
                                         @Nullable T value)
    {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return create(root.insertAt(index, value));
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> getInsertableSelf()
    {
        return this;
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertFirst(@Nullable T value)
    {
        return create(root.insertAt(0, value));
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertLast(@Nullable T value)
    {
        return create(root.append(value));
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAll(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAll(int index,
                                            @Nonnull Iterable<? extends T> values)
    {
        return insertAll(index, values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAll(int index,
                                            @Nonnull Iterator<? extends T> values)
    {
        if ((index < 0) || (index > size())) {
            throw new IndexOutOfBoundsException();
        }
        BtreeNode<T> newRoot;
        if (index == size()) {
            final BtreeNodeBuilder<T> builder = new BtreeNodeBuilder<>();
            if (root.depth() == 1) {
                for (T t : root) {
                    builder.add(t);
                }
            } else {
                builder.rebuild(root);
            }
            while (values.hasNext()) {
                builder.add(values.next());
            }
            newRoot = builder.build();
        } else {
            int i = index;
            newRoot = root;
            while (values.hasNext()) {
                BtreeInsertResult<T> insertResult = newRoot.insertAt(i, values.next());
                if (insertResult.type == BtreeInsertResult.Type.INPLACE) {
                    newRoot = insertResult.newNode;
                } else {
                    newRoot = new BtreeBranchNode<>(insertResult.newNode, insertResult.extraNode);
                }
                i++;
            }
        }
        return (root == newRoot) ? this : new JImmutableBtreeList<>(newRoot);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllFirst(@Nonnull Iterable<? extends T> values)
    {
        if (values instanceof JImmutableBtreeList) {
            return combine((JImmutableBtreeList<T>)values, this);
        } else {
            return insertAll(0, values.iterator());
        }
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        return insertAll(0, values);
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllLast(@Nonnull Iterable<? extends T> values)
    {
        if (values instanceof JImmutableBtreeList) {
            return combine(this, (JImmutableBtreeList<T>)values);
        } else {
            return insertAll(size(), values.iterator());
        }
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        return insertAll(size(), values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> deleteFirst()
    {
        return createForDelete(root.delete(0));
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> deleteLast()
    {
        return createForDelete(root.delete(root.valueCount() - 1));
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> delete(int index)
    {
        return createForDelete(root.delete(index));
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> deleteAll()
    {
        return of();
    }

    @Override
    public int size()
    {
        return root.valueCount();
    }

    @Override
    public T get(int index)
    {
        return root.get(index);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Override
    public boolean isEmpty()
    {
        return root.valueCount() == 0;
    }

    @Override
    public <A> JImmutableRandomAccessList<A> transform(@Nonnull Func1<T, A> transform)
    {
        final Builder<A> builder = builder();
        for (T t : this) {
            builder.add(transform.apply(t));
        }
        return builder.build();
    }

    @Override
    public <A> JImmutableRandomAccessList<A> transformSome(@Nonnull Func1<T, Holder<A>> transform)
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

    @Nonnull
    @Override
    public List<T> getList()
    {
        return new ListAdaptor<>(this);
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
    public void checkInvariants()
    {
        root.checkInvariants(true);
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
        return new JImmutableRandomAccessListProxy(this);
    }

    private static <T> JImmutableBtreeList<T> combine(JImmutableBtreeList<T> left,
                                                      JImmutableBtreeList<T> right)
    {
        final BtreeNode<T> leftRoot = left.root;
        final BtreeNode<T> rightRoot = right.root;
        final int leftDepth = leftRoot.depth();
        final int rightDepth = rightRoot.depth();
        if (leftDepth == 1) {
            return right.insertAll(0, left.iterator());
        } else if (rightDepth == 1) {
            return left.insertAll(left.size(), right.iterator());
        } else if (leftDepth < rightDepth) {
            final BtreeInsertResult<T> insertResult = rightRoot.insertNode(rightDepth - leftDepth, false, leftRoot);
            return create(insertResult);
        } else {
            final BtreeInsertResult<T> insertResult = leftRoot.insertNode(leftDepth - rightDepth, true, rightRoot);
            return create(insertResult);
        }
    }

    BtreeNode<T> root()
    {
        return root;
    }

    public static class Builder<T>
        implements JImmutableRandomAccessList.Builder<T>
    {
        private final BtreeNodeBuilder<T> builder;

        private Builder()
        {
            builder = new BtreeNodeBuilder<>();
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
        public JImmutableBtreeList<T> build()
        {
            return builder.size() == 0 ? of() : new JImmutableBtreeList<>(builder.build());
        }

        @Nonnull
        public Builder<T> combineWith(@Nonnull Builder<T> other)
        {
            final JImmutableBtreeList<T> a = build();
            final JImmutableBtreeList<T> b = other.build();
            final JImmutableBtreeList<T> ab = combine(a, b);
            builder.rebuild(ab.root);
            return this;
        }

        void checkInvariants()
        {
            builder.checkInvariants();
        }
    }
}
