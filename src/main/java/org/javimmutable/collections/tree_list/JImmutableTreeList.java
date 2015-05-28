///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.tree_list;

import org.javimmutable.collections.*;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of PersistentRandomAccessList that uses a 2-3 tree for its implementation.
 * Values are stored and traversed in the same order as they are added using add().
 * Performance is slower than PersistentLinkedList so if forward order and/or random
 * access are not required using that class may be a better option.   All operations
 * should be O(logN).
 *
 * @param <T>
 */
@Deprecated
@Immutable
public class JImmutableTreeList<T>
        implements JImmutableRandomAccessList<T>
{
    @SuppressWarnings("unchecked")
    private static final JImmutableTreeList EMPTY = new JImmutableTreeList(null, 0);

    private final TreeNode<T> root;
    private final int size;

    @SuppressWarnings("unchecked")
    public static <T> JImmutableTreeList<T> of()
    {
        return (JImmutableTreeList<T>)EMPTY;
    }

    private JImmutableTreeList(TreeNode<T> root,
                               int size)
    {
        this.root = root;
        this.size = size;
    }

    /**
     * Creates a MutableBuilder instance for efficiently constructing JImmutableTreeLists.
     *
     * @param <T>
     * @return
     */
    public static <T> Builder<T> builder()
    {
        return new Builder<T>();
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insert(int index,
                                        @Nullable T value)
    {
        if ((index < 0) || (index > size)) {
            throw new IndexOutOfBoundsException();
        }

        if (index == size) {
            return insert(value);
        } else {
            UpdateResult<T> result = root.insertBefore(index, value);
            return update(result);
        }
    }

    @Override
    @Nonnull
    public JImmutableTreeList<T> insert(@Nullable T value)
    {
        if (size == 0) {
            return create(new LeafNode<T>(value));
        } else {
            UpdateResult<T> result = root.insertAfter(size - 1, value);
            return update(result);
        }
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        if (size == 0) {
            return JImmutableTreeList.<T>builder().add(values.iterator()).build();
        } else {
            JImmutableTreeList<T> answer = this;
            for (T value : values) {
                answer = answer.insert(value);
            }
            return answer;
        }
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertFirst(@Nullable T value)
    {
        return insert(0, value);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertLast(@Nullable T value)
    {
        return insert(value);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Cursorable<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Collection<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Cursor<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(@Nonnull Iterator<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Cursorable<? extends T> values)
    {
        return insertAll(index, values.cursor());
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Collection<? extends T> values)
    {
        return insertAll(index, values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Cursor<? extends T> values)
    {
        return insertAll(index, values.iterator());
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAll(int index,
                                           @Nonnull Iterator<? extends T> values)
    {
        if ((index < 0) || (index > size)) {
            throw new IndexOutOfBoundsException();
        }
        int i = index;
        JImmutableTreeList<T> answer = this;
        while(values.hasNext()) {
            answer = answer.insert(i, values.next());
            i++;
        }
        return answer;
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(0, values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Collection<? extends T> values)
    {
        return insertAll(0, values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Cursor<? extends T> values)
    {
        return insertAll(0, values);

    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllFirst(@Nonnull Iterator<? extends T> values)
    {
        return insertAll(0, values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Cursorable<? extends T> values)
    {
        return insertAll(size, values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Collection<? extends T> values)
    {
        return insertAll(size, values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Cursor<? extends T> values)
    {
        return insertAll(size, values);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> insertAllLast(@Nonnull Iterator<? extends T> values)
    {
        return insertAll(size, values);
    }


    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteFirst()
    {
        return delete(0);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteLast()
    {
        return delete(size - 1);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> delete(int index)
    {
        if ((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException();
        }

        DeleteResult<T> result = root.delete(index);
        if (result.type == DeleteResult.Type.UNCHANGED) {
            throw new IndexOutOfBoundsException();
        } else if (result.type == DeleteResult.Type.ELIMINATED) {
            return of();
        } else {
            return create(result.node);
        }
    }

    @Override
    public T get(int index)
    {
        if ((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException();
        }

        return root.get(index);
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> assign(int index,
                                        @Nullable T value)
    {
        if ((index < 0) || (index >= size)) {
            throw new IndexOutOfBoundsException();
        }

        UpdateResult<T> result = root.assign(index, value);
        return update(result);
    }

    @Nonnull
    @Override
    public List<T> getList()
    {
        return ListAdaptor.of(this);
    }

    @Override
    public boolean isEmpty()
    {
        return size == 0;
    }

    private JImmutableTreeList<T> update(UpdateResult<T> result)
    {
        switch (result.type) {
        case UNCHANGED:
            return this;

        case INPLACE:
            return create(result.newNode);

        case SPLIT:
            return create(new TwoNode<T>(result.newNode,
                                         result.extraNode,
                                         result.newNode.getSize(),
                                         result.extraNode.getSize()));
        }
        throw new RuntimeException();
    }

    @Override
    public int size()
    {
        return size;
    }

    @Nonnull
    @Override
    public JImmutableTreeList<T> deleteAll()
    {
        return of();
    }

    @Override
    public boolean equals(Object o)
    {
        return (o instanceof JImmutableList) && Cursors.areEqual(cursor(), ((JImmutableList)o).cursor());
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

    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

    @Nonnull
    public Cursor<T> cursor()
    {
        return (size == 0) ? StandardCursor.<T>of() : root.cursor();
    }

    public void verifyDepthsMatch()
    {
        if (root != null) {
            root.verifyDepthsMatch();
        }
    }

    private JImmutableTreeList<T> create(TreeNode<T> root)
    {
        return new JImmutableTreeList<T>(root, root.getSize());
    }

    public static class Builder<T>
            implements JImmutableRandomAccessList.Builder<T>
    {
        private final List<TreeNode<T>> leaves = new ArrayList<TreeNode<T>>();

        @Nonnull
        @Override
        public Builder<T> add(T value)
        {
            leaves.add(new LeafNode<T>(value));
            return this;
        }

        @Nonnull
        @Override
        public JImmutableTreeList<T> build()
        {
            int nodeCount = leaves.size();
            if (nodeCount == 0) {
                return of();
            }

            if (nodeCount == 1) {
                return new JImmutableTreeList<T>(leaves.get(0), 1);
            }

            List<TreeNode<T>> dst = new ArrayList<TreeNode<T>>();
            List<TreeNode<T>> src = leaves;
            while (nodeCount > 1) {
                int dstOffset = 0;
                int srcOffset = 0;
                int remaining = nodeCount;
                while (remaining > 1) {
                    if ((remaining == 3) || (remaining >= 5)) {
                        final TreeNode<T> left = src.get(srcOffset++);
                        final TreeNode<T> middle = src.get(srcOffset++);
                        final TreeNode<T> right = src.get(srcOffset++);
                        set(dst, dstOffset++, new ThreeNode<T>(left, middle, right, left.getSize(), middle.getSize(), right.getSize()));
                        remaining -= 3;
                    } else {
                        final TreeNode<T> left = src.get(srcOffset++);
                        final TreeNode<T> right = src.get(srcOffset++);
                        set(dst, dstOffset++, new TwoNode<T>(left, right, left.getSize(), right.getSize()));
                        remaining -= 2;
                    }
                }
                if (remaining == 1) {
                    set(dst, dstOffset++, src.get(srcOffset));
                }
                nodeCount = dstOffset;
                src = dst;
            }
            TreeNode<T> root = dst.get(0);
            return new JImmutableTreeList<T>(root, root.getSize());
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

        private void set(List<TreeNode<T>> dst,
                         int index,
                         TreeNode<T> node)
        {
            if (index < dst.size()) {
                dst.set(index, node);
            } else {
                assert index == dst.size();
                dst.add(node);
            }
        }
    }
}
