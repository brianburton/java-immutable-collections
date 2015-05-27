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

import org.javimmutable.collections.*;
import org.javimmutable.collections.common.IteratorAdaptor;
import org.javimmutable.collections.common.ListAdaptor;
import org.javimmutable.collections.cursors.Cursors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * JImmutableList implementation using 32-way trees.  The underlying trees, like the JImmutableList,
 * only allow values to be inserted or deleted from the head or tail of the list.
 *
 * @param <T>
 */
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
        return new JImmutableArrayList<T>(root.assign(index, value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insert(@Nullable T value)
    {
        return new JImmutableArrayList<T>(root.insertLast(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insert(@Nonnull Iterable<? extends T> values)
    {
        Node<T> newRoot = root;
        for (T value : values) {
            newRoot = newRoot.insertLast(value);
        }
        return new JImmutableArrayList<T>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertFirst(@Nullable T value)
    {
        return new JImmutableArrayList<T>(root.insertFirst(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertLast(@Nullable T value)
    {
        return new JImmutableArrayList<T>(root.insertLast(value));
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAll(@Nonnull Cursorable<? extends T> values)
    {
        return insertAllLast(values);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAll(@Nonnull Collection<? extends T> values)
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
    public JImmutableArrayList<T> insertAllFirst(@Nonnull Cursorable<? extends T> values)
    {
        return insertAllFirst(values.cursor());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllFirst(@Nonnull Collection<? extends T> values)
    {
        ArrayList<T> temp = new ArrayList<T>(values.size());
        temp.addAll(values);
        return insertAllFirstReverse(temp);
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
        ArrayList<T> temp = new ArrayList<T>();
        while (values.hasNext()) {
            temp.add(values.next());
        }
        return insertAllFirstReverse(temp);
    }

    private JImmutableArrayList<T> insertAllFirstReverse(ArrayList<T> temp) {
        Node<T> newRoot = root;
        for(int x = temp.size()-1; x >= 0; x--) {
            newRoot = newRoot.insertFirst(temp.get(x));
        }
        return new JImmutableArrayList<T>(newRoot);
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllLast(@Nonnull Cursorable<? extends T> values)
    {
        return insertAllLast(values.cursor());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> insertAllLast(@Nonnull Collection<? extends T> values)
    {
        return insertAllLast(values.iterator());
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
        while(values.hasNext()) {
            newRoot = newRoot.insertLast(values.next());
        }
        return new JImmutableArrayList<T>(newRoot);
    }


    @Nonnull
    @Override
    public JImmutableArrayList<T> deleteFirst()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableArrayList<T>(root.deleteFirst());
    }

    @Nonnull
    @Override
    public JImmutableArrayList<T> deleteLast()
    {
        if (root.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }
        return new JImmutableArrayList<T>(root.deleteLast());
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
    public Iterator<T> iterator()
    {
        return IteratorAdaptor.of(cursor());
    }

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
        public JImmutableArrayList<T> build()
        {
            final Node<T> node = builder.build();
            return node.isEmpty() ? JImmutableArrayList.<T>of() : new JImmutableArrayList<T>(builder.build());
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
