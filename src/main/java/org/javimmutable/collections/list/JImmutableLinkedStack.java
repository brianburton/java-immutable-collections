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

package org.javimmutable.collections.list;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.cursors.Cursors;
import org.javimmutable.collections.cursors.SequenceCursor;
import org.javimmutable.collections.cursors.SingleValueCursor;
import org.javimmutable.collections.cursors.StandardCursor;
import org.javimmutable.collections.iterators.EmptyIterator;
import org.javimmutable.collections.iterators.SequenceIterator;
import org.javimmutable.collections.iterators.SingleValueIterator;
import org.javimmutable.collections.iterators.SplitableIterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;

/**
 * Singly linked list implementation of JImmutableStack that stores and retrieves values
 * in the reverse order of the corresponding add() method calls.  If forward or random
 * access to stored values is required use JImmutableArrayList or JImmutableTreeList
 * instead, but this class is significantly faster when its limitations are acceptable.
 */
@Immutable
public abstract class JImmutableLinkedStack
{
    private static final EmptySequence EMPTY = new EmptySequence();

    @SuppressWarnings("unchecked")
    public static <T> JImmutableStack<T> of()
    {
        return (JImmutableStack<T>)EMPTY;
    }

    public static <T> JImmutableStack<T> of(T value)
    {
        return new Single<>(value);
    }

    public static <T> JImmutableStack<T> of(List<T> values)
    {
        JImmutableStack<T> list = of();
        for (T value : values) {
            list = list.insert(value);
        }
        return list;
    }

    public static <T> JImmutableStack<T> of(T... values)
    {
        JImmutableStack<T> list = of();
        for (T value : values) {
            list = list.insert(value);
        }
        return list;
    }

    private static abstract class Base<V>
        implements JImmutableStack<V>
    {
        @Nonnull
        public abstract JImmutableStack<V> insert(@Nullable V value);

        @Nonnull
        public abstract JImmutableStack<V> getTail();

        @Nonnull
        public abstract SplitableIterator<V> iterator();

        @Nonnull
        @Override
        public JImmutableStack<V> remove()
        {
            return getTail();
        }

        @Nonnull
        @Override
        public Spliterator<V> spliterator()
        {
            return iterator().spliterator(Spliterator.IMMUTABLE | Spliterator.ORDERED);
        }

        public List<V> makeList()
        {
            final List<V> answer = new ArrayList<>();
            JImmutableStack<V> next = this;
            while (!next.isEmpty()) {
                answer.add(next.getHead());
                next = next.getTail();
            }
            return answer;
        }

        @Override
        public boolean equals(Object o)
        {
            return (o instanceof JImmutableStack) && Cursors.areEqual(cursor(), ((JImmutableStack)o).cursor());
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
    }

    private static class EmptySequence<V>
        extends Base<V>
    {
        public boolean isEmpty()
        {
            return true;
        }

        public V getHead()
        {
            throw new UnsupportedOperationException();
        }

        @Nonnull
        @Override
        public JImmutableStack<V> getTail()
        {
            return this;
        }

        @Nonnull
        @Override
        public JImmutableStack<V> insert(@Nullable V value)
        {
            return new Single<>(value);
        }

        @Nonnull
        public Cursor<V> cursor()
        {
            return StandardCursor.of();
        }

        @Nonnull
        @Override
        public SplitableIterator<V> iterator()
        {
            return EmptyIterator.of();
        }

        @Override
        public void checkInvariants()
        {
            //no invariants
        }
    }

    private static class Single<V>
        extends Base<V>
    {
        private final V value;

        private Single(V value)
        {
            this.value = value;
        }

        public boolean isEmpty()
        {
            return false;
        }

        public V getHead()
        {
            return value;
        }

        @Nonnull
        @Override
        public JImmutableStack<V> getTail()
        {
            return of();
        }

        @Nonnull
        @Override
        public JImmutableStack<V> insert(@Nullable V value)
        {
            return new Chain<>(value, this);
        }

        @Nonnull
        public Cursor<V> cursor()
        {
            return SingleValueCursor.of(value);
        }

        @Nonnull
        @Override
        public SplitableIterator<V> iterator()
        {
            return SingleValueIterator.of(value);
        }

        @Override
        public void checkInvariants()
        {
            //no invariants
        }
    }

    private static class Chain<V>
        extends Base<V>
    {
        private final V value;
        private final JImmutableStack<V> next;

        private Chain(V value,
                      JImmutableStack<V> next)
        {
            this.value = value;
            this.next = next;
        }

        public boolean isEmpty()
        {
            return false;
        }

        public V getHead()
        {
            return value;
        }

        @Nonnull
        @Override
        public JImmutableStack<V> getTail()
        {
            return next;
        }

        @Override
        @Nonnull
        public JImmutableStack<V> insert(@Nullable V value)
        {
            return new Chain<>(value, this);
        }

        @Override
        @Nonnull
        public Cursor<V> cursor()
        {
            return SequenceCursor.of(this);
        }

        @Nonnull
        public SplitableIterator<V> iterator()
        {
            return SequenceIterator.iterator(this);
        }

        @Override
        public void checkInvariants()
        {
            //no invariants
        }
    }
}
