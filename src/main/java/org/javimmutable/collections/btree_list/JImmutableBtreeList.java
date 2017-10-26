///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.Indexed;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.common.IndexedList;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.cursors.Cursors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;

/**
 * Implementation of JImmutableRandomAccessList that uses a B-Tree for its implementation.
 */
@Immutable
public class JImmutableBtreeList<T>
    implements JImmutableRandomAccessList<T>
{
    private static final JImmutableBtreeList<Object> EMPTY = new JImmutableBtreeList<Object>(BtreeEmptyNode.of());

    private final BtreeNode<T> root;

    @SuppressWarnings("unchecked")
    public static <T> JImmutableBtreeList<T> of()
    {
        return (JImmutableBtreeList<T>)EMPTY;
    }

    public static <T> Builder<T> builder()
    {
        return new Builder<T>();
    }

    @Nonnull
    public static <T> JImmutableBtreeList<T> of(@Nonnull Indexed<? extends T> values)
    {
        int nodeCount = values.size();
        if (nodeCount == 0) {
            return of();
        }

        if (nodeCount <= BtreeNode.MAX_CHILDREN) {
            return new JImmutableBtreeList<T>(BtreeLeafNode.of(values, 0, nodeCount));
        }

        final List<BtreeNode<T>> nodes = new ArrayList<BtreeNode<T>>();
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

        return new JImmutableBtreeList<T>(nodes.get(0));
    }

    private JImmutableBtreeList(BtreeNode<T> root)
    {
        this.root = root;
    }

    private JImmutableBtreeList<T> create(BtreeInsertResult<T> insertResult)
    {
        if (insertResult.type == BtreeInsertResult.Type.INPLACE) {
            return new JImmutableBtreeList<T>(insertResult.newNode);
        } else {
            return new JImmutableBtreeList<T>(new BtreeBranchNode<T>(insertResult.newNode, insertResult.extraNode));
        }
    }

    private JImmutableBtreeList<T> createForDelete(@Nonnull BtreeNode<T> newRoot)
    {
        if (newRoot.valueCount() == 0) {
            return of();
        }
        while (newRoot.childCount() == 1) {
            BtreeNode<T> child = newRoot.firstChild();
            if (child == newRoot) {
                break;
            }
            newRoot = child;
        }
        return new JImmutableBtreeList<T>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> assign(int index,
                                         @Nullable T value)
    {
        if (index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableBtreeList<T>(root.assign(index, value));
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
    public JImmutableBtreeList<T> insertAll(@Nonnull Cursorable<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAll(@Nonnull Collection<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAll(@Nonnull Cursor<? extends T> values)
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
                                            @Nonnull Cursorable<? extends T> values)
    {
        return insertAll(index, values.cursor());
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAll(int index,
                                            @Nonnull Collection<? extends T> values)
    {
        return insertAll(index, values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAll(int index,
                                            @Nonnull Cursor<? extends T> values)
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
        int i = index;
        BtreeNode<T> newRoot = root;
        while (values.hasNext()) {
            BtreeInsertResult<T> insertResult = newRoot.insertAt(i, values.next());
            if (insertResult.type == BtreeInsertResult.Type.INPLACE) {
                newRoot = insertResult.newNode;
            } else {
                newRoot = new BtreeBranchNode<T>(insertResult.newNode, insertResult.extraNode);
            }
            i++;
        }
        return new JImmutableBtreeList<T>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllFirst(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(0, values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllFirst(@Nonnull Collection<? extends T> values)
    {
        return insertAll(0, values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllFirst(@Nonnull Cursor<? extends T> values)
    {
        return insertAll(0, values);

    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        return insertAll(0, values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllLast(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(size(), values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllLast(@Nonnull Collection<? extends T> values)
    {
        return insertAll(size(), values);
    }

    @Nonnull
    @Override
    public JImmutableBtreeList<T> insertAllLast(@Nonnull Cursor<? extends T> values)
    {
        return insertAll(size(), values);
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
    public JImmutableRandomAccessList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        JImmutableRandomAccessList<T> answer = this;
        for (T value : values) {
            answer = answer.insertLast(value);
        }
        return answer;
    }

    @Override
    public boolean isEmpty()
    {
        return root.valueCount() == 0;
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return new ListAdaptor<T>(this);
    }

    @Nonnull
    @Override
    public Cursor<T> cursor()
    {
        return root.cursor();
    }

    @Override
    @Nonnull
    public Iterator<T> iterator()
    {
        return root.iterator();
    }

    @Override
    @Nonnull
    public Spliterator<T> spliterator()
    {
        return root.iterator().spliterator(Spliterator.IMMUTABLE | Spliterator.ORDERED);
    }

    @Override
    public void checkInvariants()
    {
        root.checkInvariants();
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
        implements JImmutableRandomAccessList.Builder<T>
    {
        private final List<T> values = new ArrayList<T>();

        @Nonnull
        @Override
        public Builder<T> add(T value)
        {
            values.add(value);
            return this;
        }

        @Nonnull
        @Override
        public JImmutableBtreeList<T> build()
        {
            return of(IndexedList.retained(values));
        }

        @Nonnull
        @Override
        public Builder<T> add(Cursor<? extends T> source)
        {
            for (Cursor<? extends T> cursor = source.start(); cursor.hasValue(); cursor = cursor.next()) {
                add(cursor.getValue());
            }
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Iterator<? extends T> source)
        {
            while (source.hasNext()) {
                add(source.next());
            }
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Collection<? extends T> source)
        {
            add(source.iterator());
            return this;
        }

        @Nonnull
        @Override
        public <K extends T> Builder<T> add(K... source)
        {
            for (T value : source) {
                add(value);
            }
            return this;
        }

        @Nonnull
        @Override
        public Builder<T> add(Indexed<? extends T> source)
        {
            return add(source, 0, source.size());
        }

        @Nonnull
        @Override
        public Builder<T> add(Indexed<? extends T> source,
                              int offset,
                              int limit)
        {
            for (int i = offset; i < limit; ++i) {
                add(source.get(i));
            }
            return this;
        }
    }
}
